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

    private static final int ADD_MEMBER_REQUEST = 0;
    private static final int EDIT_MEMBER_REQUEST = 1;
    private boolean mIsFirstGroup;
    private RecyclerView mRecyclerView;
    private RecyclerViewAdapter mRecyclerAdapter;
    private EditText mGroupName;
    private int mLastEditPosition;
    private String mUserName;
    ArrayList<HashMap<RecyclerViewAdapter.MapItemKey, String>> dataSet;

    static public FragmentCreateGroup newInstance(boolean isFirstGroup, String userName) {
        FragmentCreateGroup f = new FragmentCreateGroup();
        f.mIsFirstGroup = isFirstGroup;
        f.mUserName = userName;
        return f;
    }

    static public FragmentCreateGroup newInstance(String userName) {
        FragmentCreateGroup f = new FragmentCreateGroup();
        f.mIsFirstGroup = false;
        f.mUserName = userName;
        return f;
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

                        db.addUserToGroup(mUserName, params[0], mUserName);

                        for (int i = 0; i < dataSet.size(); i++) {
                            String addedUser = (String)dataSet.get(i).get(RecyclerViewAdapter.MapItemKey.TEXT_1);

                            if (!db.userExists(addedUser))
                                continue;

                            if (!db.addUserToGroup(addedUser, params[0], mUserName)) {

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
                            getActivity().finish();
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode != Activity.RESULT_OK) {
            super.onActivityResult(requestCode, resultCode, data);
            return;
        }

        if (requestCode == ADD_MEMBER_REQUEST && dataSet != null) {
            String[] emails = data.getStringExtra(MemberDialog.EXTRA_ENTRY_EMAIL).toString().split("\n");

            if (emails == null)
                return;

            for (int i = 0; i < emails.length; i++) {

                if (emails[i].isEmpty())
                    continue;

                HashMap<RecyclerViewAdapter.MapItemKey, String> item = new HashMap<>();
                item.put(RecyclerViewAdapter.MapItemKey.TEXT_1, emails[i]);
                dataSet.add(item);
                mRecyclerAdapter.notifyItemInserted(dataSet.size() - 1);
            }

        } else if (requestCode == EDIT_MEMBER_REQUEST && dataSet != null) {

            String newEmail = data.getStringExtra(MemberDialog.EXTRA_ENTRY_EMAIL).toString();

            if (!newEmail.isEmpty() && !newEmail.contains("\n")) {
                dataSet.get(mLastEditPosition).put(RecyclerViewAdapter.MapItemKey.TEXT_1, data.getStringExtra(MemberDialog.EXTRA_ENTRY_EMAIL));
                mRecyclerAdapter.notifyItemChanged(mLastEditPosition);
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
