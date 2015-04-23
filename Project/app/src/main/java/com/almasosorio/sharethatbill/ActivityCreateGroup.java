package com.almasosorio.sharethatbill;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;


public class ActivityCreateGroup extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Bundle extras = getIntent().getExtras();
        boolean isFirstGroup = true;
        String userName;

        if (extras != null) {
            isFirstGroup = extras.getBoolean(getString(R.string.bundle_first_group_create));
            userName = extras.getString(getString(R.string.bundle_user_name));
        } else {
            //TODO: remove
            userName = "user1";
        }

        getSupportFragmentManager().beginTransaction()
                .add(R.id.container, FragmentCreateGroup.newInstance(isFirstGroup, userName))
                .addToBackStack(null)
                .commit();
    }
}
