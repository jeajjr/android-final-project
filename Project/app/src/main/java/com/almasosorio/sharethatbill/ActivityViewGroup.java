package com.almasosorio.sharethatbill;

import android.graphics.Color;
import android.os.AsyncTask;
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
import android.view.View;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;


public class ActivityViewGroup extends ActionBarActivity {
    private static final String TAG ="ActivityViewGroup";

    private String userName;
    private String groupName;

    private ArrayList<String> groupsList;

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
            public void onDrawerOpened(View view) {
            }

            public void onDrawerClosed(View view) {
            }
        };

        drawerLayout.setDrawerListener(drawerToggle);
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);

        //TODO: close drawer when clicked

        groupsList = new ArrayList<>();

        RecyclerView drawerList = (RecyclerView) findViewById(R.id.recyclerView);
        drawerList.setHasFixedSize(true);
        drawerList.setLayoutManager(new LinearLayoutManager(this));
        DrawerRecyclerViewAdapter recyclerViewAdapter =
                new DrawerRecyclerViewAdapter(this, groupsList);
        drawerList.setAdapter(recyclerViewAdapter);
        recyclerViewAdapter.setOnDrawerItemClickListener(new DrawerRecyclerViewAdapter.OnDrawerItemClickListener() {
            @Override
            public void onGroupItemClick(int index) {

            }

            @Override
            public void onCreateGroupClick() {

            }
        });

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), this,
                userName, groupName);

        ViewPager mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mViewPager.setAdapter(viewPagerAdapter);

        (new GroupNamesDownloader(groupsList, recyclerViewAdapter)).execute(userName);
    }

    private class GroupNamesDownloader extends AsyncTask<String, Void , ArrayList> {
        private static final String TAG = "GroupNamesDownloader";

        private WeakReference<ArrayList<String>> dataSet;
        private WeakReference<DrawerRecyclerViewAdapter> adapter;

        public GroupNamesDownloader(ArrayList<String> dataSet, DrawerRecyclerViewAdapter adapter) {
            this.dataSet = new WeakReference<>(dataSet);
            this.adapter = new WeakReference<>(adapter);
        }

        @Override
        protected ArrayList doInBackground(String... params) {

            DBHandler db = new DBHandler();

            return db.getUserGroups(params[0]);
        }

        @Override
        protected void onPostExecute(ArrayList results) {
            Log.d(TAG, "onPostExecute");

            final ArrayList dataSet = this.dataSet.get();
            final DrawerRecyclerViewAdapter adapter = this.adapter.get();

            if (dataSet != null && adapter != null) {
                dataSet.clear();
                dataSet.addAll(results);
                adapter.notifyDataSetChanged();
                //TODO: force layout update on item 0
            }
        }
    }
}
