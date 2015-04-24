package com.almasosorio.sharethatbill;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;


public class FragmentCreateGroup extends Fragment {
    public static final String TAG = "FragmentCreateGroup";
    private static final String EXTRA_ENTRY_EMAIL = "ENTRY_EMAIL";
    private static final int ADD_MEMBER_REQUEST = 0;
    private static final int EDIT_MEMBER_REQUEST = 1;
    private boolean mIsFirstGroup;
    private RecyclerView mRecyclerView;
    private RecyclerViewAdapter mRecyclerAdapter;
    private EditText mGroupName;
    private int mLastEditPosition;
    private String mUserEmail;
    ArrayList<HashMap<RecyclerViewAdapter.MapItemKey, String>> dataSet;

    static public FragmentCreateGroup newInstance(boolean isFirstGroup, String userEmail) {
        FragmentCreateGroup f = new FragmentCreateGroup();
        f.mIsFirstGroup = isFirstGroup;
        f.mUserEmail = userEmail;
        Preferences.getInstance().setUserEmail(userEmail);
        return f;
    }

    static public FragmentCreateGroup newInstance(String userName) {
        return newInstance(false, userName);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_creategroup, container, false);

        mGroupName = (EditText) v.findViewById(R.id.groupName);

        if (mIsFirstGroup)
            ((TextView)v.findViewById(R.id.topTitle)).setText(R.string.create_first_group);

        dataSet = new ArrayList<>();

        mRecyclerView = (RecyclerView) v.findViewById(R.id.memberList);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerAdapter = new RecyclerViewAdapter(getActivity(), dataSet, RecyclerViewAdapter.ItemType.CREATE_GROUP_MEMBER_ENTRY);
        mRecyclerAdapter.setParentFragment(this);
        mRecyclerView.setAdapter(mRecyclerAdapter);

        ((Button)v.findViewById(R.id.addMember)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MemberDialog dialog = new MemberDialog("Add Member", "Enter member email", "");
                dialog.setTargetFragment(FragmentCreateGroup.this, ADD_MEMBER_REQUEST);
                dialog.show(getFragmentManager(), "AddMemberDialog");
            }
        });

        ((Button)v.findViewById(R.id.createGroup)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mGroupName.getText().toString().isEmpty()) {
                    new AlertDialog.Builder(getActivity())
                            .setTitle(getActivity().getString(R.string.cant_create_group))
                            .setMessage(getActivity().getString(R.string.group_name_invalid))
                            .setPositiveButton(android.R.string.ok, null)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                    return;
                }

                new AsyncTask<String, Void, Void>() {
                    private String error = "";
                    private String groupName;
                    @Override
                    protected Void doInBackground(String... params) {
                        DBHandler db = new DBHandler();
                        groupName = params[0];
                        if (db.groupExists(params[0])) {
                            error = getString(R.string.group_already_exists);
                            return null;
                        }

                        if (!db.createGroup(params[0])) {
                            error = getString(R.string.failed_to_create_group);
                            return null;
                        }

                        db.addUserToGroup(mUserEmail, params[0], mUserEmail);

                        for (int i = 0; i < dataSet.size(); i++) {
                            String addedUser = (String)dataSet.get(i).get(RecyclerViewAdapter.MapItemKey.TEXT_1);

                            if (!db.userExists(addedUser))
                                continue;

                            if (!db.addUserToGroup(addedUser, params[0], mUserEmail)) {

                            }
                        }

                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        super.onPostExecute(aVoid);

                        if (!error.isEmpty()) {
                            new AlertDialog.Builder(getActivity())
                                    .setTitle(getActivity().getString(R.string.failed_to_create_group))
                                    .setMessage(error)
                                    .setPositiveButton(android.R.string.ok, null)
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .show();
                        } else {
                            Toast.makeText(getActivity(), "Group \"" + groupName + "\" created.", Toast.LENGTH_SHORT).show();
                            if (mIsFirstGroup)
                                getActivity().finish();
                            else {
                                Intent intent = new Intent(getActivity(), ActivityViewGroup.class);
                                intent.putExtra(getString(R.string.bundle_user_name), mUserEmail);
                                intent.putExtra(getString(R.string.bundle_first_group_create), false);
                                startActivity(intent);
                            }
                        }

                    }
                }.execute(mGroupName.getText().toString());
            }
        });

        return v;
    }

    public void editMember(int position) {

        if (position < 0 || position >= dataSet.size())
            return;

        mLastEditPosition = position;

        MemberDialog dialog = new MemberDialog(getString(R.string.edit_member),
                getString(R.string.enter_new_email),
                dataSet.get(position).get(RecyclerViewAdapter.MapItemKey.TEXT_1));
        dialog.setTargetFragment(FragmentCreateGroup.this, EDIT_MEMBER_REQUEST);
        dialog.show(getFragmentManager(), "EditMemberDialog");
    }

    private interface userExistListener {
        public void onUserExist(boolean exist);
    }

    private void checkNewUserEntry(String userEmail, final userExistListener listener) {
        new AsyncTask<String, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(String... params) {
                DBHandler db = new DBHandler();
                return db.userExists(params[0]);
            }
            @Override
            protected void onPostExecute(Boolean aBoolean) {
                super.onPostExecute(aBoolean);
                if (listener != null)
                    listener.onUserExist(aBoolean);
            }
        }.execute(userEmail);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode != Activity.RESULT_OK) {
            super.onActivityResult(requestCode, resultCode, data);
            return;
        }

        if (requestCode == ADD_MEMBER_REQUEST && dataSet != null) {

            final String[] emails = data.getStringExtra(EXTRA_ENTRY_EMAIL).toString().split("\n");

            if (emails == null)
                return;

            for (int i = 0; i < emails.length; i++) {

                final String newEmail = emails[i];

                if (newEmail.isEmpty()) {
                    if (emails.length == 1)
                        Toast.makeText(getActivity(), "Email can't be empty.",
                            Toast.LENGTH_SHORT).show();
                    continue;
                }

                if (newEmail.equals(Preferences.getInstance().getUserEmail())) {
                    if (emails.length == 1)
                        Toast.makeText(getActivity(), "You don't have to add yourself.",
                            Toast.LENGTH_SHORT).show();
                    continue;
                }

                if (mRecyclerAdapter.getEntryByString(newEmail) != -1) {
                    if (emails.length == 1)
                        Toast.makeText(getActivity(), "User's email already added.",
                            Toast.LENGTH_SHORT).show();
                    continue;
                }

                HashMap<RecyclerViewAdapter.MapItemKey, String> item = new HashMap<>();
                item.put(RecyclerViewAdapter.MapItemKey.TEXT_1, emails[i]);
                dataSet.add(item);
                mRecyclerAdapter.notifyItemInserted(dataSet.size() - 1);

                checkNewUserEntry(emails[i], new userExistListener() {
                    @Override
                    public void onUserExist(boolean exist) {
                        mRecyclerAdapter.setEntryIsValid(mRecyclerAdapter.getEntryByString(newEmail), exist);
                        if (!exist && emails.length == 1)
                            Toast.makeText(getActivity(), "User's email \"" + newEmail + "\" couldn't be found.",
                                    Toast.LENGTH_SHORT).show();
                    }
                });
            }

        } else if (requestCode == EDIT_MEMBER_REQUEST && dataSet != null) {

            final String newEmail = data.getStringExtra(EXTRA_ENTRY_EMAIL).toString();

            if (newEmail.isEmpty()) {
                Toast.makeText(getActivity(), "Email can't be empty.",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            if (newEmail.contains("\n")) {
                Toast.makeText(getActivity(), "Email must be single line.",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            if (newEmail.equals(Preferences.getInstance().getUserEmail())) {
                Toast.makeText(getActivity(), "You don't have to add yourself.",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            if (mRecyclerAdapter.getEntryByString(newEmail) == -1) {
                dataSet.get(mLastEditPosition).put(RecyclerViewAdapter.MapItemKey.TEXT_1, data.getStringExtra(EXTRA_ENTRY_EMAIL));
                mRecyclerAdapter.notifyItemChanged(mLastEditPosition);
                checkNewUserEntry(newEmail, new userExistListener() {
                    @Override
                    public void onUserExist(boolean exist) {
                        mRecyclerAdapter.setEntryIsValid(mRecyclerAdapter.getEntryByString(newEmail), exist);
                        if (!exist)
                            Toast.makeText(getActivity(), "User's email \"" + newEmail + "\" couldn't be found.",
                                    Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    public static class MemberDialog extends DialogFragment {

        private String title, message, defaultText;

        public MemberDialog(String title, String message, String defaultText) {
            super();
            this.title = title;
            this.message = message;
            this.defaultText = defaultText;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            final EditText textEdit = new EditText(getActivity());

            if (!defaultText.isEmpty()) {
                textEdit.setText(defaultText);
                textEdit.setSelection(defaultText.length());
                textEdit.setSingleLine();
            }

            AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
            adb.setView(textEdit)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (getTargetFragment() != null) {
                                    Intent i = new Intent();
                                    i.putExtra(EXTRA_ENTRY_EMAIL, textEdit.getText().toString());
                                    getTargetFragment().onActivityResult(getTargetRequestCode(),
                                            Activity.RESULT_OK, i);
                                }
                            }
                        })
                .setNegativeButton(android.R.string.cancel, null)
                .setIcon(android.R.drawable.ic_input_add);

            return adb.create();
        }
    }

}
