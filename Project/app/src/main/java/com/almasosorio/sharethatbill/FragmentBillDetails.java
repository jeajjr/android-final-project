package com.almasosorio.sharethatbill;


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
import java.util.Map;


public class FragmentBillDetails extends Fragment {
    private static final String TAG = "FragmentBillDetails";

    RecyclerView recyclerView;
    RecyclerViewAdapter adapter;
    ArrayList<Map<RecyclerViewAdapter.MapItemKey, String>> dataSet;

    public FragmentBillDetails() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_bill_details, container, false);

        View deleteButton = v.findViewById(R.id.layout_delete_button);
        TextView buttonText = (TextView) v.findViewById(R.id.delete_button_text);
        buttonText.setText(buttonText.getText().toString().toUpperCase());
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "clicked on deleteButton");
            }
        });

        recyclerView = (RecyclerView) v.findViewById(R.id.cardList);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        // RecyclerViewAdapter(Context context, ArrayList<Map<String, ?>> dataSet, ItemType listType)
        dataSet = new ArrayList<>();
        Map<RecyclerViewAdapter.MapItemKey, String> item = new HashMap<>();
        item.put(RecyclerViewAdapter.MapItemKey.TEXT_1, "Michael paid $6.00 and owes $3.30");
        dataSet.add(item);

        adapter = new RecyclerViewAdapter(getActivity(), dataSet, RecyclerViewAdapter.ItemType.PICTURE_WITH_TEXT);
        recyclerView.setAdapter(adapter);

        return v;
    }


}
