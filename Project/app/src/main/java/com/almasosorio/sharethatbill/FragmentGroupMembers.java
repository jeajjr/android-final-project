package com.almasosorio.sharethatbill;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;


public class FragmentGroupMembers extends Fragment {
    private static final String TAG = "FragmentGroupMembers";

    private static final int ADD_MEMBER_REQUEST = 0;

    private String userName;
    private String groupName;

    private ArrayList<HashMap<RecyclerViewAdapter.MapItemKey, String>> dataSet;

    private RecyclerView recyclerView;
    private RecyclerViewAdapter adapter;

    private ProgressBar progressBar;

    public FragmentGroupMembers() {
        // Required empty public constructor
    }

    public void updateGroup(String groupName) {
        Log.d(TAG, "received update request: " + groupName);
        dataSet.clear();
        adapter.notifyDataSetChanged();
        this.groupName = groupName;
        if (isAdded())
            (new GroupMembersDownloader(getActivity().getApplicationContext(), dataSet, adapter, progressBar))
                    .execute(groupName, userName);
    }

    public static FragmentGroupMembers newInstance(Context context, String userName, String groupName) {
        FragmentGroupMembers fragment = new FragmentGroupMembers();

        Bundle args = new Bundle();
        args.putSerializable(context.getString(R.string.bundle_user_name), userName);
        args.putSerializable(context.getString(R.string.bundle_group_name), groupName);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        dataSet = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_group_members, container, false);

        userName = getArguments().getString(getActivity().getString(R.string.bundle_user_name));
        groupName = getArguments().getString(getActivity().getString(R.string.bundle_group_name));

        TextView buttonText = (TextView) v.findViewById(R.id.add_button_text);
        buttonText.setText(buttonText.getText().toString().toUpperCase());

        View addButton = v.findViewById(R.id.layout_add_button);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "clicked on addButton");

                MemberDialog dialog = new MemberDialog(getString(R.string.add_member), getString(R.string.enter_member_email), "");
                dialog.setTargetFragment(FragmentGroupMembers.this, ADD_MEMBER_REQUEST);
                dialog.show(getFragmentManager(), "AddMemberDialog");
            }
        });

        recyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        adapter = new RecyclerViewAdapter(getActivity(), dataSet, RecyclerViewAdapter.ItemType.GROUP_MEMBERS_LIST_ITEM);
        recyclerView.setAdapter(adapter);

        progressBar = (ProgressBar) v.findViewById(R.id.progressBar);

        (new GroupMembersDownloader(getActivity().getApplicationContext(), dataSet, adapter, progressBar))
                .execute(groupName, userName);

        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode != Activity.RESULT_OK) {
            super.onActivityResult(requestCode, resultCode, data);
            return;
        }

        if (requestCode == ADD_MEMBER_REQUEST && dataSet != null) {
            String email = data.getStringExtra(MemberDialog.EXTRA_ENTRY_EMAIL);

            if (email == null)
                return;

            Log.d(TAG, "onActivityResult: add email " + email);

            if (email.length() != 0 && isValidEmail(email))
                (new AddUserToGroupTask(getActivity().getApplicationContext(), progressBar))
                        .execute(email, groupName, userName);
            else {
                new AlertDialog.Builder(getActivity())
                        .setTitle(getActivity().getString(R.string.invalid_email))
                        .setMessage(getActivity().getString(R.string.invalid_email_text))
                        .setPositiveButton(android.R.string.ok, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private boolean isValidEmail(CharSequence target) {
        return true;
        //TODO: return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    private class GroupMembersDownloader extends AsyncTask<String, Void , ArrayList> {
        private static final String TAG = "GroupMembersDownloader";

        private WeakReference<ArrayList> dataSet;
        private WeakReference<RecyclerViewAdapter> adapter;
        private WeakReference<ProgressBar> progressBar;

        private Context context;

        public GroupMembersDownloader(Context context, ArrayList dataSet, RecyclerViewAdapter adapter,
                                      ProgressBar progressBar) {
            this.context = context;
            this.dataSet = new WeakReference<>(dataSet);
            this.adapter = new WeakReference<>(adapter);
            this.progressBar = new WeakReference<>(progressBar);
        }

        @Override
        protected void onPreExecute() {
            final ProgressBar progressBar = this.progressBar.get();

            if (progressBar != null)
                progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected ArrayList doInBackground(String... params) {
            Log.d(TAG, "group: " + params[0]);

            ArrayList<HashMap<RecyclerViewAdapter.MapItemKey, String>> results = new ArrayList<>();

            DBHandler db = new DBHandler();

            ArrayList<TwoStringsClass> members = db.getGroupUsersBalances(params[0]);

            Log.d(TAG, params[0] + " members: " + members);

            for (int i=0; i<members.size(); i++) {
                HashMap<RecyclerViewAdapter.MapItemKey, String> userItem = new HashMap<>();

                Float userBalanceInGroup = Float.parseFloat(members.get(i).string2);

                userItem.put(RecyclerViewAdapter.MapItemKey.TEXT_1,
                        db.getUserNamesByEmail(members.get(i).string1));

                if (userBalanceInGroup >= 0) {
                    userItem.put(RecyclerViewAdapter.MapItemKey.TEXT_2,
                            context.getString(R.string.lent_in_total));

                    userItem.put(RecyclerViewAdapter.MapItemKey.TEXT_3,
                            String.format("$ %.2f", userBalanceInGroup));
                }
                else {
                    userItem.put(RecyclerViewAdapter.MapItemKey.TEXT_2,
                            context.getString(R.string.borrowed_in_total));

                    userItem.put(RecyclerViewAdapter.MapItemKey.TEXT_3,
                            String.format("$ %.2f", userBalanceInGroup));
                }

                results.add(userItem);
            }

            return results;
        }

        @Override
        protected void onPostExecute(ArrayList results) {
            final ArrayList dataSet = this.dataSet.get();
            final RecyclerViewAdapter adapter = this.adapter.get();

            if (dataSet != null && adapter != null) {
                dataSet.clear();
                dataSet.addAll(results);
                adapter.notifyDataSetChanged();
            }

            final ProgressBar progressBar = this.progressBar.get();

            if (progressBar != null)
                progressBar.setVisibility(View.INVISIBLE);
        }
    }

    private class AddUserToGroupTask extends AsyncTask<String, Void , Boolean> {
        private static final String TAG = "AddUserToGroupTask";

        private WeakReference<ProgressBar> progressBar;

        private Context context;

        public AddUserToGroupTask(Context context, ProgressBar progressBar) {
            this.context = context;
            this.progressBar = new WeakReference<>(progressBar);
        }

        @Override
        protected void onPreExecute() {
            final ProgressBar progressBar = this.progressBar.get();

            if (progressBar != null)
                progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Boolean doInBackground(String... params) {

            DBHandler db = new DBHandler();

            return db.addUserToGroup(params[0], params[1], params[2]);
        }

        @Override
        protected void onPostExecute(Boolean result) {

            if (isAdded()) {
                if (result) {
                    Toast.makeText(context, getString(R.string.user_added_to_group), Toast.LENGTH_LONG);
                    updateGroup(groupName);
                }
                else
                    new AlertDialog.Builder(getActivity())
                            .setTitle(getActivity().getString(R.string.error))
                            .setMessage(getActivity().getString(R.string.not_possible_add_user))
                            .setPositiveButton(android.R.string.ok, null)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
            }

            final ProgressBar progressBar = this.progressBar.get();

            if (progressBar != null)
                progressBar.setVisibility(View.INVISIBLE);
        }
    }
}
