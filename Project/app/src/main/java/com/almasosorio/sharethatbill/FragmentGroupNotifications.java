package com.almasosorio.sharethatbill;


import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;


public class FragmentGroupNotifications extends Fragment {
    private static final String TAG = "FragmentGroupNot";

    private String userName;
    private String groupName;

    private ArrayList<HashMap<RecyclerViewAdapter.MapItemKey, String>> dataSet;

    private RecyclerView recyclerView;
    private RecyclerViewAdapter adapter;

    public FragmentGroupNotifications() {
        // Required empty public constructor
    }

    public void updateGroup(String groupName) {
        Log.d(TAG, "received update request: " + groupName);
        dataSet.clear();
        this.groupName = groupName;
        (new NotificationsDownloader(dataSet, adapter)).execute(groupName, userName);
    }

    public static FragmentGroupNotifications newInstance(Context context, String userName, String groupName) {
        FragmentGroupNotifications fragment = new FragmentGroupNotifications();

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
        View v = inflater.inflate(R.layout.fragment_group_notifications, container, false);

        userName = getArguments().getString(getActivity().getString(R.string.bundle_user_name));
        groupName = getArguments().getString(getActivity().getString(R.string.bundle_group_name));

        recyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        adapter = new RecyclerViewAdapter(getActivity(), dataSet, RecyclerViewAdapter.ItemType.NOTIFICATION_LIST_ITEM);
        recyclerView.setAdapter(adapter);
        adapter.setOnListItemClickListener(new RecyclerViewAdapter.OnListItemClickListener() {
            @Override
            public void onItemClick(int position) {
                handleClick(position);
            }
        });

        (new NotificationsDownloader(dataSet, adapter)).execute(groupName, userName);

        return v;
    }

    private void handleClick(int position) {
        HashMap<RecyclerViewAdapter.MapItemKey, String> itemClicked = dataSet.get(position);

        String billName = itemClicked.get(RecyclerViewAdapter.MapItemKey.CLICKABLE_BILL_NAME);
        if (billName != null) {
            Intent intent = new Intent(getActivity(), ActivityBillDetails.class);
            intent.putExtra(getString(R.string.bundle_user_name), userName);
            intent.putExtra(getString(R.string.bundle_group_name), groupName);
            intent.putExtra(getString(R.string.bundle_bill_name), billName);
            startActivity(intent);
        }
    }

    private class NotificationsDownloader extends AsyncTask<String, Void , ArrayList> {
        private static final String TAG = "NotificationsDownloader";

        private WeakReference<ArrayList> dataSet;
        private WeakReference<RecyclerViewAdapter> adapter;

        public NotificationsDownloader(ArrayList dataSet, RecyclerViewAdapter adapter) {
            this.dataSet = new WeakReference<>(dataSet);
            this.adapter = new WeakReference<>(adapter);
        }

        @Override
        protected ArrayList doInBackground(String... params) {
            Log.d(TAG, "group: " + params[0]);

            ArrayList<HashMap<RecyclerViewAdapter.MapItemKey, String>> results = new ArrayList<>();

            DBHandler db = new DBHandler();

            ArrayList<Notification> notifications = db.getGroupNotifications(params[0]);

            Log.d(TAG, params[0] + " notifications: " + notifications);

            for (int i=0; i<notifications.size(); i++) {
                HashMap<RecyclerViewAdapter.MapItemKey, String> userItem = new HashMap<>();

                String middleString = "";
                String endString = "";

                switch (notifications.get(i).type) {
                    case Notification.BILL_CREATED:
                        middleString = getActivity().getString(R.string.created_the_bill);
                        endString = "";
                        break;

                    case Notification.BILL_EDITED:
                        middleString = getActivity().getString(R.string.edited_the_bill);
                        endString = "";
                        break;

                    case Notification.BILL_DELETED:
                        middleString = getActivity().getString(R.string.deleted_the_bill);
                        endString = "";
                        break;

                    case Notification.USER_ADDED:
                        middleString = getActivity().getString(R.string.added);
                        endString = getActivity().getString(R.string.to_the_group);
                        break;
                }

                String notificationUserName = "";
                if (params[1].equals(notifications.get(i).owner))
                    notificationUserName = getString(R.string.you);
                else
                    notificationUserName = db.getUserNamesByEmail(notifications.get(i).owner);

                String notificationDescription = "";
                if (notifications.get(i).type == Notification.USER_ADDED)
                    notificationDescription = db.getUserNamesByEmail(notifications.get(i).description);
                else
                    notificationDescription = notifications.get(i).description;

                userItem.put(RecyclerViewAdapter.MapItemKey.TEXT_1,
                        notificationUserName + " " + middleString + " " +
                        notificationDescription + " " + endString);

                Calendar now = Calendar.getInstance();

                long difference =  now.getTimeInMillis() - notifications.get(i).date.getTimeInMillis();

                difference /= 1000; // difference is now in seconds

                String timeStamp = "";

                if (difference < 60)
                    timeStamp = getActivity().getString(R.string.less_than_one_minute_ago);
                else if ((difference /= 60) < 60) // difference is now in minutes
                    timeStamp = difference + " " + getActivity().getString(R.string.minutes_ago);
                else if ((difference /= 60) < 24) // difference is now in hours
                    timeStamp = difference +  " " + getActivity().getString(R.string.hours_ago);
                else if ((difference /= 24) < 7) // difference is now in days
                    timeStamp = difference +  " " + getActivity().getString(R.string.days_ago);
                else {
                    difference /= 7; // difference is now in weeks
                    timeStamp = difference +  " " + getActivity().getString(R.string.weeks_ago);
                }

                userItem.put(RecyclerViewAdapter.MapItemKey.TEXT_2, timeStamp);

                if (notifications.get(i).type == Notification.BILL_CREATED ||
                    notifications.get(i).type == Notification.BILL_EDITED)
                    userItem.put(RecyclerViewAdapter.MapItemKey.CLICKABLE_BILL_NAME, notifications.get(i).description);

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
        }
    }

}
