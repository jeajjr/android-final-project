package com.almasosorio.sharethatbill;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
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
import android.widget.TextView;
import android.widget.Toast;

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
    public static final String TOTAL_PAID_FORMAT = "$ %.2f";
    public enum KeyType {UserEmail, UserName, AmountPaid, AmountToPay, OldAmountToPay};
    public static final int ColorPositive = Color.rgb(66, 255, 23);
    public static final int ColorNegative = Color.rgb(255, 66, 23);
    private ArrayList<HashMap<KeyType, ?>> userList;
    private Button mDateButton;
    private Calendar mDate;
    private EditText mBillNameEditText;
    private ViewPagerAdapter mViewPagerAdapter;
    private String groupName, userName;

    static public FragmentNewBill newInstance(String groupName, String sessionUserName) {
        final FragmentNewBill f = new FragmentNewBill();
        f.userList = new ArrayList<>();
        f.groupName = groupName;
        f.userName = sessionUserName;
        return f;
    }

    private interface onDownloadGroupMembers{
        public void onDownloadGroupMembers(ArrayList<TwoStringsClass> data);
    }

    private void downloadGroupMembers(final onDownloadGroupMembers listener) {
        AsyncTask task = new AsyncTask<String, Void, ArrayList>() {
            private boolean success = false;
            @Override
            protected ArrayList doInBackground(String... params) {
                DBHandler db = new DBHandler();
                try {
                    ArrayList arr = db.getGroupMembersNames(params[0]);
                    success = true;
                    return arr;
                } catch (Exception ex) {

                }
                return null;
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                mViewPagerAdapter.setLoadingNewBill(true, false);
            }

            @Override
            protected void onPostExecute(ArrayList data) {
                mViewPagerAdapter.setLoadingNewBill(false, success);

                if (data != null)
                    listener.onDownloadGroupMembers(data);
            }
        };
        task.execute(groupName);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

            }
        }, 0);
    }

    public void loadGroupMembers() {
        downloadGroupMembers(new onDownloadGroupMembers() {
            @Override
            public void onDownloadGroupMembers(ArrayList<TwoStringsClass> data) {
                userList.clear();

                for (int i = 0; i < data.size(); i++) {
                    HashMap<KeyType, ?> newEntry = new HashMap<>();
                    ((HashMap<KeyType, String>) newEntry).put(KeyType.UserEmail, data.get(i).string1);
                    ((HashMap<KeyType, String>) newEntry).put(KeyType.UserName, data.get(i).string2);
                    ((HashMap<KeyType, Double>) newEntry).put(KeyType.AmountPaid, 0.0);
                    ((HashMap<KeyType, Double>) newEntry).put(KeyType.AmountToPay, 0.0);
                    userList.add(newEntry);
                }

                if (mViewPagerAdapter != null)
                    mViewPagerAdapter.onUpdateUserList();
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mViewPagerAdapter.updateTotalPaidLabel((TextView)getView().findViewById(R.id.totalPaid));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_newbill, container, false);

        mViewPagerAdapter = new ViewPagerAdapter(
                getActivity().getSupportFragmentManager(), getActivity(), userList,
                (TextView)v.findViewById(R.id.totalPaid));

        ViewPager mViewPager = (ViewPager) v.findViewById(R.id.view_pager);
        mViewPager.setAdapter(mViewPagerAdapter);

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

        mBillNameEditText = (EditText)v.findViewById(R.id.billName);
        mBillNameEditText.setHint(getString(R.string.untitled));
        mBillNameEditText.setHintTextColor(Color.LTGRAY);

        loadGroupMembers();

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
                mDate = new GregorianCalendar();
                mDate.setTime(date);
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

    public Intent getIntent() {
        Intent intent = new Intent();

        Double[] valuesPaid = new Double[userList.size()];
        Double[] valuesOwed = new Double[userList.size()];
        String[] emailsList = new String[userList.size()];

        for (int index = 0; index < userList.size(); index++) {
            valuesPaid[index] = (Double)userList.get(index).get(KeyType.AmountPaid);
            valuesOwed[index] = (Double)userList.get(index).get(KeyType.AmountToPay);
            emailsList[index] = (String)userList.get(index).get(KeyType.UserEmail);
        }

        intent.putExtra(ActivityNewBill.INTENT_VALUES_PAID, valuesPaid);
        intent.putExtra(ActivityNewBill.INTENT_VALUES_OWED, valuesOwed);
        intent.putExtra(ActivityNewBill.INTENT_USERS_EMAIL, emailsList);
        return intent;
    }

    public boolean createBill() {

        final Bill bill = new Bill();

        if (mDate == null) {
            mDate = new GregorianCalendar();
            mDate.setTimeInMillis(System.currentTimeMillis());
        }

        bill.billDate = mDate;
        bill.billName = mBillNameEditText.getText().toString();
        bill.groupName = groupName;

        try {
            String str = ((TextView)getView().findViewById(R.id.totalPaid)).getText().toString().substring(2);
            bill.billValue = Float.valueOf(str);
        } catch (NumberFormatException ex) {
            return false;
        }

        if (userList.isEmpty() || bill.billValue == 0.0)
            return false;

        new AsyncTask<Void, Void, Boolean>() {

            private boolean success = true;

            @Override
            protected Boolean doInBackground(Void... params) {
                DBHandler db = new DBHandler();

                if (!db.createBill(bill, userName)) {
                    success = false;
                    Log.d("FragmentNewBill - createBill", "Failed to create bill.");
                    return null;
                }

                for (int index = 0; index < userList.size(); index++) {
                    if (!db.createUserBillRelation(
                            (String)userList.get(index).get(KeyType.UserEmail),
                            groupName, bill.billName, (Double)userList.get(index).get(KeyType.AmountToPay),
                            (Double)userList.get(index).get(KeyType.AmountPaid)
                    )) {
                        success = false;
                        Log.d("FragmentNewBill - createBill", "Failed to create relation.");
                    }
                }

                return success;
            }

            @Override
            protected void onPostExecute(Boolean s) {
                super.onPostExecute(s);
                if (!success)
                    new AlertDialog.Builder(getActivity())
                        .setTitle(getActivity().getString(R.string.bill_creation_failed))
                        .setMessage(getActivity().getString(R.string.bill_creation_failed_message))
                        .setPositiveButton(android.R.string.ok, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
                else
                    Toast.makeText(getActivity(), "Bill \"" + bill.billName + "\" created.", Toast.LENGTH_SHORT).show();
            }
        }.execute();

        return true;
    }
}
