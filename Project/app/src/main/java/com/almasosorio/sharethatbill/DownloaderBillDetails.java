package com.almasosorio.sharethatbill;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;

public class DownloaderBillDetails extends AsyncTask<String, Void, Void> {
    private static final String TAG = "DownloaderBillDetails";

    WeakReference<Activity> activity;
    WeakReference<TextView> billName;
    WeakReference<TextView> billValue;
    WeakReference<ArrayList> dataSet;
    WeakReference<RecyclerViewAdapter> adapter;
    WeakReference<ProgressDialog> progressDialog;

    Bill bill;
    ArrayList<TwoStringsClass> groupMembers;
    ArrayList<TwoStringsClass> whoOwes;
    ArrayList<TwoStringsClass> whoPaid;

    public DownloaderBillDetails (Activity activity, TextView billName, TextView billValue,
                                  ArrayList<HashMap<RecyclerViewAdapter.MapItemKey, String>> dataSet,
                                  RecyclerViewAdapter adapter, ProgressDialog progressDialog) {

        this.activity = new WeakReference<>(activity);
        this.billName = new WeakReference<>(billName);
        this.billValue = new WeakReference<>(billValue);
        this.dataSet = new WeakReference<ArrayList>(dataSet);
        this.adapter = new WeakReference<>(adapter);
        this.progressDialog = new WeakReference<>(progressDialog);
    }

    @Override
    protected void onPreExecute() {
        final ProgressDialog progressDialog = this.progressDialog.get();

        if (progressDialog != null)
            progressDialog.show();
    }

    @Override
    protected Void doInBackground(String[] params) {
        Log.d(TAG, "doInBackground");

        DBHandler db = new DBHandler();

        Log.d(TAG, "getting bill: " + params[0] + ", " + params[1]);
        bill = db.getBill(params[0], params[1]);
        Log.d(TAG, "bill: " + bill);
        whoOwes = db.getWhoOwesBill(params[0], params[1]);
        Log.d(TAG, "whoOwes: " + whoOwes);
        whoPaid = db.getWhoPaidBill(params[0], params[1]);
        Log.d(TAG, "whoPaid : " + whoPaid);
        groupMembers = db.getGroupMembersNames(params[0]);
        Log.d(TAG, "groupMembers: " + groupMembers);


        return null;
    }

    public float getValue (ArrayList<TwoStringsClass> set, String key) {
        for (TwoStringsClass t : set)
            if (t.string1.equals(key))
                return Float.parseFloat(t.string2);

        return 0f;
    }

    @Override
    protected void onPostExecute(Void result) {
        Log.d(TAG, "onPostExecute");

        Activity activity = this.activity.get();
        TextView billName = this.billName.get();
        TextView billValue = this.billValue.get();
        ArrayList<HashMap<RecyclerViewAdapter.MapItemKey, String>> dataSet = this.dataSet.get();
        RecyclerViewAdapter adapter = this.adapter.get();

        if (activity != null &&
            billName != null &&
            billValue != null &&
            dataSet != null &&
            adapter != null) {

            dataSet.clear();

            billName.setText(bill.billName);
            billValue.setText(String.format("$ %.2f", bill.billValue));

            for (TwoStringsClass item : groupMembers) {
                HashMap<RecyclerViewAdapter.MapItemKey, String> map = new HashMap<>();
                String text = item.string2;

                float valuePaid = getValue(whoPaid, item.string1);
                float valueOwed = getValue(whoOwes, item.string1);

                if (valuePaid == 0 && valueOwed == 0)
                    text += " " + activity.getText(R.string.did_not_participate);
                if (valuePaid != 0 && valueOwed != 0)
                    text += String.format(" %s $%.2f %s %s $%.2f",
                            activity.getText(R.string.paid),
                            valuePaid,
                            activity.getText(R.string.and),
                            activity.getText(R.string.owes),
                            valueOwed);
                else if (valuePaid != 0)
                    text += String.format(" %s $%.2f",
                            activity.getText(R.string.paid),
                            valuePaid);
                else if (valueOwed != 0)
                    text += String.format(" %s $%.2f",
                            activity.getText(R.string.owes),
                            valueOwed);

                map.put(RecyclerViewAdapter.MapItemKey.TEXT_1, text);
                dataSet.add(map);
            }

            adapter.notifyDataSetChanged();
        }

        final ProgressDialog progressDialog = this.progressDialog.get();

        if (progressDialog != null)
            progressDialog.dismiss();
    }
}
