package com.almasosorio.sharethatbill;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentGroupBills extends Fragment {

    private String userName;
    private String groupName;

    private ArrayList<Map<String, ?>> dataSet;

    public FragmentGroupBills() {
        // Required empty public constructor
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
        groupName = getArguments().getString(getActivity().getString(R.string.bundle_user_name));

        

        return v;
    }


}
