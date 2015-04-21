package com.almasosorio.sharethatbill;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class FragmentBillDetails extends Fragment {
    private static final String TAG = "FragmentBillDetails";

    private RecyclerView recyclerView;
    private RecyclerViewAdapter adapter;
    private ArrayList<HashMap<RecyclerViewAdapter.MapItemKey, String>> dataSet;

    private String userName;
    private String groupName;
    private String billName;

    public FragmentBillDetails() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        dataSet = new ArrayList<>();
    }

    public static FragmentBillDetails newInstance(Context context, String userName,
                                                  String groupName, String billName) {
        FragmentBillDetails fragment = new FragmentBillDetails();

        Bundle args = new Bundle();
        args.putSerializable(context.getString(R.string.bundle_user_name), userName);
        args.putSerializable(context.getString(R.string.bundle_group_name), groupName);
        args.putSerializable(context.getString(R.string.bundle_bill_name), billName);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_bill_details, container, false);

        this.userName = getArguments().getString(getActivity().getString(R.string.bundle_user_name));
        this.groupName = getArguments().getString(getActivity().getString(R.string.bundle_group_name));
        this.billName = getArguments().getString(getActivity().getString(R.string.bundle_bill_name));

        View deleteButton = v.findViewById(R.id.layout_delete_button);
        TextView buttonText = (TextView) v.findViewById(R.id.delete_button_text);
        buttonText.setText(buttonText.getText().toString().toUpperCase());
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "clicked on deleteButton");
            }
        });

        recyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        adapter = new RecyclerViewAdapter(getActivity(), dataSet, RecyclerViewAdapter.ItemType.BILL_LIST_ITEM);
        recyclerView.setAdapter(adapter);

        TextView billName = (TextView) v.findViewById(R.id.textViewBillName);
        TextView billValue = (TextView) v.findViewById(R.id.textViewBillValue);

        (new DownloaderBillDetails(getActivity(), billName, billValue, dataSet, adapter))
                .execute(this.groupName, this.billName);

        return v;
    }


}
