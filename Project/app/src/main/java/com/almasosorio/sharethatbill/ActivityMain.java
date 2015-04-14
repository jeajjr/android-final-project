package com.almasosorio.sharethatbill;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;


public class ActivityMain extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.container, new FragmentLogin())
                .commit();
    }
}
