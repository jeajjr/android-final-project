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
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;

public class FragmentGroupBills extends Fragment {
    private static final String TAG = "FragmentGroupBills";

    private String userName;
    private String groupName;

    private ArrayList<HashMap<RecyclerViewAdapter.MapItemKey, String>> dataSet;

    private RecyclerView recyclerView;
    private RecyclerViewAdapter adapter;

    //private ProgressBar progressBar;
    private ViewLoading viewLoader;
    private TextView listEmptyText;

    public FragmentGroupBills() {
        // Required empty public constructor
    }

    public void updateGroup(String groupName) {
        Log.d(TAG, "received update request: " + groupName);
        dataSet.clear();
        adapter.notifyDataSetChanged();
        this.groupName = groupName;
        if (isAdded())
            (new GroupBillsDownloader(getActivity().getApplicationContext(), dataSet, adapter, viewLoader, listEmptyText)).execute(groupName, userName);
    }

    public static FragmentGroupBills newInstance(Context context, String userName, String groupName) {
        FragmentGroupBills fragment = new FragmentGroupBills();

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
        View v = inflater.inflate(R.layout.fragment_group_bills, container, false);

        userName = getArguments().getString(getActivity().getString(R.string.bundle_user_name));
        groupName = getArguments().getString(getActivity().getString(R.string.bundle_group_name));


        View addButton = v.findViewById(R.id.layout_add_button);
        TextView buttonText = (TextView) v.findViewById(R.id.add_button_text);
        buttonText.setText(buttonText.getText().toString().toUpperCase());
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "clicked on addButton");

                Intent intent = new Intent(getActivity(), ActivityNewBill.class);
                intent.putExtra(getString(R.string.bundle_user_name), userName);
                intent.putExtra(getString(R.string.bundle_group_name), groupName);
                startActivity(intent);
            }
        });

        //recyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);

        recyclerView = new RecyclerView(getActivity());
        int padding = (int)getActivity().getResources().getDisplayMetrics().density;
        recyclerView.setPadding(padding, padding, padding, padding);
        //mRecyclerView.setBackground(getActivity().getDrawable(R.drawable.rectangle_outline));
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);//(RelativeLayout.LayoutParams)mRecyclerView.getLayoutParams();
        params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        recyclerView.setLayoutParams(params);

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        adapter = new RecyclerViewAdapter(getActivity(), dataSet, RecyclerViewAdapter.ItemType.BILL_LIST_ITEM);
        recyclerView.setAdapter(adapter);
        adapter.setOnListItemClickListener(new RecyclerViewAdapter.OnListItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(getActivity(), ActivityBillDetails.class);
                intent.putExtra(getString(R.string.bundle_user_name), userName);
                intent.putExtra(getString(R.string.bundle_group_name), groupName);
                intent.putExtra(getString(R.string.bundle_bill_name),
                        dataSet.get(position).get(RecyclerViewAdapter.MapItemKey.TEXT_1));
                startActivity(intent);
            }
        });

        viewLoader = (ViewLoading) v.findViewById(R.id.viewLoader);
        viewLoader.setLoadedView(recyclerView);
        listEmptyText = (TextView) v.findViewById(R.id.text_list_empty);

        (new GroupBillsDownloader(getActivity().getApplicationContext(), dataSet, adapter, viewLoader, listEmptyText)).execute(groupName, userName);

        return v;
    }

    private class GroupBillsDownloader extends AsyncTask<String, Void , ArrayList> {
        private static final String TAG = "GroupBillsDownloader";

        private WeakReference<ArrayList> dataSet;
        private WeakReference<RecyclerViewAdapter> adapter;
        private WeakReference<ViewLoading> viewLoader;
        private WeakReference<TextView> emptyListText;

        private Context context;


        public GroupBillsDownloader(Context context, ArrayList dataSet, RecyclerViewAdapter adapter,
                                    ViewLoading viewLoader, TextView emptyListText) {
            this.context = context;
            this.dataSet = new WeakReference<>(dataSet);
            this.adapter = new WeakReference<>(adapter);
            this.viewLoader = new WeakReference<>(viewLoader);
            this.emptyListText = new WeakReference<>(emptyListText);
        }

        @Override
        protected void onPreExecute() {
            final ViewLoading viewLoader = this.viewLoader.get();
            final TextView emptyListText = this.emptyListText.get();

            if (viewLoader != null)
                viewLoader.setState(true, false);

            if (emptyListText != null)
                emptyListText.setVisibility(View.INVISIBLE);
        }

        @Override
        protected ArrayList doInBackground(String... params) {
            Log.d(TAG, "group: " + params[0]);

            ArrayList<HashMap<RecyclerViewAdapter.MapItemKey, String>> results = new ArrayList<>();

            DBHandler db = new DBHandler();

            ArrayList<TwoStringsClass> bills = db.getGroupBillNamesValues(params[0]);

            Log.d(TAG, params[0] + " bills: " + bills);

            for (int i=0; i<bills.size(); i++) {
                HashMap<RecyclerViewAdapter.MapItemKey, String> billItem = new HashMap<>();

                Float userBalanceInBill = db.getUserParticipationInBill(params[0],
                        bills.get(i).string1, params[1]);

                billItem.put(RecyclerViewAdapter.MapItemKey.TEXT_1, bills.get(i).string1);
                billItem.put(RecyclerViewAdapter.MapItemKey.TEXT_2,
                        String.format("$ %.2f", Float.parseFloat(bills.get(i).string2)));

                if (userBalanceInBill > 0) {
                    billItem.put(RecyclerViewAdapter.MapItemKey.TEXT_3,
                            context.getString(R.string.you_borrowed));

                    billItem.put(RecyclerViewAdapter.MapItemKey.TEXT_4,
                            String.format("$ %.2f", userBalanceInBill));
                }
                else if (userBalanceInBill < 0) {
                    billItem.put(RecyclerViewAdapter.MapItemKey.TEXT_3,
                            context.getString(R.string.you_borrowed));

                    billItem.put(RecyclerViewAdapter.MapItemKey.TEXT_4,
                            String.format("$ %.2f", -userBalanceInBill));
                }
                else {
                    billItem.put(RecyclerViewAdapter.MapItemKey.TEXT_3,
                            context.getString(R.string.not_involved));

                    billItem.put(RecyclerViewAdapter.MapItemKey.TEXT_4, "");
                }

                results.add(billItem);
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

                if (dataSet.size() == 0) {
                    final TextView emptyListText = this.emptyListText.get();

                    if (emptyListText != null)
                        emptyListText.setVisibility(View.VISIBLE);
                }
            }

            final ViewLoading viewLoader = this.viewLoader.get();

            if (viewLoader != null)
                viewLoader.setState(false, false);
        }
    }
}
