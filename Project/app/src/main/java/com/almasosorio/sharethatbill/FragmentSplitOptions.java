package com.almasosorio.sharethatbill;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.HashMap;


public class FragmentSplitOptions extends Fragment implements Spinner.OnItemSelectedListener {

    public static final String TAG = "FragmentSplitOptions";

    RecyclerView mRecyclerView;
    RecyclerViewAdapter mRecyclerAdapter;
    Spinner mSpinner;
    ArrayList<HashMap<RecyclerViewAdapter.MapItemKey, String>> dataSet;

    static public FragmentSplitOptions newInstance() {
        FragmentSplitOptions f = new FragmentSplitOptions();
        f.dataSet = new ArrayList<>();
        // TODO: pass members of the group as parameter for dataSet
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_split_options, container, false);

        mSpinner = (Spinner) v.findViewById(R.id.spinner);
        mSpinner.setOnItemSelectedListener(this);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.split_method_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(adapter);

        mRecyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerAdapter = new RecyclerViewAdapter(getActivity(), dataSet, RecyclerViewAdapter.ItemType.WHO_PAID_LIST_ITEM);
        mRecyclerView.setAdapter(mRecyclerAdapter);

        return v;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
