package com.almasosorio.sharethatbill;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class FragmentNewBill extends Fragment {

    public static final String TAG = "FragmentNewBill";

    static public FragmentNewBill newInstance() {
        FragmentNewBill f = new FragmentNewBill();
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
        View v = inflater.inflate(R.layout.fragment_newbill, container, false);

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(
                getActivity().getSupportFragmentManager(), getActivity(),
                userName, groupName);

        ViewPager mViewPager = (ViewPager) v.findViewById(R.id.view_pager);
        mViewPager.setAdapter(viewPagerAdapter);

        return v;
    }
}
