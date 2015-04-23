package com.almasosorio.sharethatbill;

import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.PagerTabStrip;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;


public class ActivityNewBill extends ActionBarActivity {

    ImageButton mDoneButton;
    FragmentNewBill mFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_bill);

        if (mFragment == null)
            mFragment = FragmentNewBill.newInstance(null);

        if (savedInstanceState == null) {

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, mFragment, "FragmentNewBill")
                    .commit();

        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.menu_activity_new_bill);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        mDoneButton = (ImageButton) findViewById(R.id.done);
        mDoneButton.setBackgroundColor(Color.alpha(0));
        mDoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mFragment != null) {

                }
            }
        });

        //TextView title = (TextView) toolbar.findViewById(R.id.textViewToolboxTitle);
        //title.setText(getString(R.string.new_bill));
    }
}
