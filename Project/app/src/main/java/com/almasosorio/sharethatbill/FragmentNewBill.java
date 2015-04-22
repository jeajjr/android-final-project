package com.almasosorio.sharethatbill;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;


public class FragmentNewBill extends Fragment {

    public static final String TAG = "FragmentNewBill";
    private static final String EXTRA_DATE = "ExtraDatePicked";
    private static final int SET_DATE_REQUEST = 0;
    private ArrayList<String> userList;
    private Button mDateButton;

    static public FragmentNewBill newInstance(ArrayList<String> userList) {
        FragmentNewBill f = new FragmentNewBill();
        f.userList = userList;
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
                getActivity().getSupportFragmentManager(), getActivity(), userList);

        ViewPager mViewPager = (ViewPager) v.findViewById(R.id.view_pager);
        mViewPager.setAdapter(viewPagerAdapter);

        PagerTabStrip pagerTabStrip = (PagerTabStrip) v.findViewById(R.id.pager_tab_strip);
        pagerTabStrip.setDrawFullUnderline(true);
        pagerTabStrip.setTabIndicatorColor(Color.WHITE);
        pagerTabStrip.setTextColor(Color.WHITE);

        mDateButton = (Button)v.findViewById(R.id.setDate);
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateDialog dialog = new DateDialog();
                dialog.setTargetFragment(FragmentNewBill.this, SET_DATE_REQUEST);
                dialog.show(getFragmentManager(), "SetDateDialog");
            }
        });

        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode != Activity.RESULT_OK) {
            super.onActivityResult(requestCode, resultCode, data);
            return;
        }

        if (requestCode == SET_DATE_REQUEST) {
            Date date = (Date)data.getSerializableExtra(EXTRA_DATE);
            if (date != null) {
                mDateButton.setText(new SimpleDateFormat("MMMM dd/yyyy (EEEE)", Locale.getDefault()).format(date));
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    public static class DateDialog extends DialogFragment {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            final DatePicker datePicker = new DatePicker(getActivity());
            final Date currentDate = new Date(System.currentTimeMillis());

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(currentDate);

            datePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {
                        @Override
                        public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            currentDate.setTime(new GregorianCalendar(year, monthOfYear, dayOfMonth).getTimeInMillis());
                        }
                    });

            AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
            adb.setView(datePicker)
                    .setTitle("Select Date")
                    .setPositiveButton(android.R.string.ok,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (getTargetFragment() != null) {
                                        Intent i = new Intent();
                                        i.putExtra(EXTRA_DATE, currentDate);
                                        getTargetFragment().onActivityResult(getTargetRequestCode(),
                                                Activity.RESULT_OK, i);
                                    }
                                }
                            })
                    .setNegativeButton(android.R.string.cancel, null)
                    .setIcon(android.R.drawable.ic_dialog_info);

            return adb.create();
        }
    }
}
