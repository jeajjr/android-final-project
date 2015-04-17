package com.almasosorio.sharethatbill;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DownloaderBillDetails extends AsyncTask<String, Void, ArrayList> {
    private static final String TAG = "DownloaderBillDetails";

    WeakReference<Activity> activity;
    WeakReference<TextView> billName;
    WeakReference<TextView> billValue;
    WeakReference<ArrayList> dataSet;

    Bill bill;

    public DownloaderBillDetails (Activity activity, TextView billName, TextView billValue,
                                  ArrayList<HashMap<RecyclerViewAdapter.MapItemKey, String>> dataSet) {

        this.activity = new WeakReference<>(activity);
        this.billName = new WeakReference<>(billName);
        this.billValue = new WeakReference<>(billValue);
        this.dataSet = new WeakReference<ArrayList>(dataSet);
    }

    @Override
    protected ArrayList doInBackground(String[] params) {
        Log.d(TAG, "doInBackground");

        DBHandler db = new DBHandler();

        Log.d(TAG, "getting bill: " + params[0] + ", " + params[1]);
        bill = db.getBill(params[0], params[1]);
        ArrayList
        return null;
    }

    @Override
    protected void onPostExecute(ArrayList result) {
        Log.d(TAG, "onPostExecute");

        TextView billName = this.billName.get();
        TextView billValue = this.billValue.get();

        if (billName != null && billValue != null) {
            billName.setText(bill.billName);
            billValue.setText(String.format("$ %.2f", bill.billValue));
        }
    }
}
