package com.almasosorio.sharethatbill;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;


public class ActivityBillDetails extends ActionBarActivity {

    String userName;
    String groupName;
    String billName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_details);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            userName = extras.getString(getString(R.string.bundle_user_name));
            groupName = extras.getString(getString(R.string.bundle_group_name));
            billName = extras.getString(getString(R.string.bundle_bill_name));
        }

        getSupportFragmentManager().beginTransaction()
                .add(R.id.container, FragmentBillDetails.newInstance(this,
                        userName, groupName, billName))
                .commit();
    }
}
