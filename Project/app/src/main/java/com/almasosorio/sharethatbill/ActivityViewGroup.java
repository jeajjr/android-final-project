package com.almasosorio.sharethatbill;

import android.graphics.Color;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;


public class ActivityViewGroup extends ActionBarActivity {
    private static final String TAG ="ActivityViewGroup";

    private String userName;
    private String groupName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_group);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            userName = extras.getString("user_name");
            groupName = extras.getString("group_name");
        }
        else {
            //TODO: remove
            userName = "user1";
            groupName = "group1";
        }

        Log.d(TAG, "got bundle: " + userName + ", " + groupName);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView title = (TextView) toolbar.findViewById(R.id.textViewToolboxTitle);
        title.setText(groupName);

        PagerTabStrip pagerTabStrip = (PagerTabStrip) findViewById(R.id.pager_tab_strip);
        pagerTabStrip.setDrawFullUnderline(true);
        pagerTabStrip.setTabIndicatorColor(Color.WHITE);
        pagerTabStrip.setTextColor(Color.WHITE);

        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.bill,
                R.string.bill) {
            // Only lock drawer when it is closed
            public void onDrawerOpened(View view){
            }

            public void onDrawerClosed(View view) {
            }
        };

        drawerLayout.setDrawerListener(drawerToggle);
        // only open navigation drawer via button on toolbar
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);

        ArrayList<HashMap<RecyclerViewAdapter.MapItemKey, String>> dataSet = new ArrayList<>();
        HashMap<RecyclerViewAdapter.MapItemKey, String> item = new HashMap<>();
        item.put(RecyclerViewAdapter.MapItemKey.TEXT_1, "blah1");
        dataSet.add(item);
        item = new HashMap<>();
        item.put(RecyclerViewAdapter.MapItemKey.TEXT_1, "blah2");
        dataSet.add(item);
        item = new HashMap<>();
        item.put(RecyclerViewAdapter.MapItemKey.TEXT_1, "blah3");
        dataSet.add(item);
        item = new HashMap<>();
        item.put(RecyclerViewAdapter.MapItemKey.TEXT_1, "blah4");
        dataSet.add(item);

        RecyclerView drawerList = (RecyclerView) findViewById(R.id.recyclerView1);
        drawerList.setLayoutManager(new LinearLayoutManager(this));
        RecyclerViewAdapter recyclerViewAdapter =
                new RecyclerViewAdapter(this, dataSet, RecyclerViewAdapter.ItemType.PICTURE_WITH_TEXT);
        drawerList.setAdapter(recyclerViewAdapter);

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), this,
                userName, groupName);

        ViewPager mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mViewPager.setAdapter(viewPagerAdapter);
    }
}
