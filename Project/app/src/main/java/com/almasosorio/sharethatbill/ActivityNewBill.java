package com.almasosorio.sharethatbill;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class ActivityNewBill extends ActionBarActivity {

    Button mDoneButton;
    FragmentNewBill mFragment;

    public static final String INTENT_VALUES_PAID = "InValuesPaid";
    public static final String INTENT_VALUES_OWED = "InValuesOwed";
    public static final String INTENT_USERS_EMAIL = "InUserEmails";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_bill);

        String groupName = "", userName = "";
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            groupName = extras.getString(getString(R.string.bundle_group_name));
            userName = extras.getString(getString(R.string.bundle_user_name));
        }
        else {
            //TODO: remove
            groupName = "group1";
            userName = "user1";
        }

        if (mFragment == null)
            mFragment = FragmentNewBill.newInstance(groupName, userName);

        if (savedInstanceState == null) {

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, mFragment)
                    .commit();

        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.menu_activity_new_bill);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mDoneButton = (Button) findViewById(R.id.done);
        mDoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mFragment != null) {
                    //setResult(RESULT_OK, mFragment.getIntent());
                    if (!mFragment.createBill())
                        Log.d("ActivityNewBill", "DoneButton - creating bill returned false");
                    finish();
                }
            }
        });
    }
}
