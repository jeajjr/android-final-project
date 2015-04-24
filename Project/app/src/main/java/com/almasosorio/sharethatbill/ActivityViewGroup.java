package com.almasosorio.sharethatbill;

import android.content.Intent;
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
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;


public class ActivityViewGroup extends ActionBarActivity {
    private static final String TAG ="ActivityViewGroup";

    private String userName;
    private String groupName;

    private ViewPagerAdapter viewPagerAdapter;
    private DrawerRecyclerViewAdapter recyclerViewAdapter;

    private ArrayList<String> groupsList;

    private TextView toolbarTitle;

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
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        toolbarTitle = (TextView) toolbar.findViewById(R.id.textViewToolboxTitle);
        toolbarTitle.setText(groupName);

        PagerTabStrip pagerTabStrip = (PagerTabStrip) findViewById(R.id.pager_tab_strip);
        pagerTabStrip.setDrawFullUnderline(true);
        pagerTabStrip.setTabIndicatorColor(Color.WHITE);
        pagerTabStrip.setTextColor(Color.WHITE);

        final DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.drawer_opened,
                R.string.drawer_closed) {
            //TODO: remove if not used
            public void onDrawerOpened(View view) {
            }

            public void onDrawerClosed(View view) {
            }
        };
        drawerToggle.syncState();

        (new UserNameDownloader((TextView) drawerLayout.findViewById(R.id.name))).execute(userName);

        drawerLayout.setDrawerListener(drawerToggle);
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);

        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), this,
                userName, groupName);
        final ViewPager mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mViewPager.setOffscreenPageLimit(2);
        mViewPager.setAdapter(viewPagerAdapter);
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0)
                    drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                else
                    drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        toolbar.findViewById(R.id.imageViewToolboxRightButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPagerAdapter.updateGroupFragments(groupName);
            }
        });

        groupsList = new ArrayList<>();

        RecyclerView drawerList = (RecyclerView) findViewById(R.id.recyclerView);
        drawerList.setHasFixedSize(true);
        drawerList.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewAdapter = new DrawerRecyclerViewAdapter(this, groupsList);
        drawerList.setAdapter(recyclerViewAdapter);
        recyclerViewAdapter.setOnDrawerItemClickListener(new DrawerRecyclerViewAdapter.OnDrawerItemClickListener() {
            @Override
            public void onGroupItemClick(int index) {
                if (!groupName.equals(groupsList.get(index))) {
                    groupName = groupsList.get(index);
                    viewPagerAdapter.updateGroupFragments(groupName);
                    toolbarTitle.setText(groupName);
                    drawerLayout.closeDrawer(Gravity.LEFT);

                    drawerLayout.closeDrawer(Gravity.LEFT);
                }
            }

            @Override
            public void onCreateGroupClick() {
                Intent intent = new Intent(ActivityViewGroup.this, ActivityCreateGroup.class);
                intent.putExtra(getString(R.string.bundle_user_name), userName);
                intent.putExtra(getString(R.string.bundle_first_group_create), false);
                startActivity(intent);

                drawerLayout.closeDrawer(Gravity.LEFT);
            }
        });

        (new GroupNamesDownloader(groupsList, recyclerViewAdapter)).execute(userName);
    }

    @Override
    protected void onResume() {
        super.onResume();
/*
        if (groupsList.size() > 0)
            toolbarTitle.setText(groupsList.get(0));
        viewPagerAdapter.updateGroupFragments(groupName);
        */
        (new GroupNamesDownloader(groupsList, recyclerViewAdapter)).execute(userName);
    }

    private class UserNameDownloader extends AsyncTask<String, Void , String> {

        private WeakReference<TextView> textView;

        public UserNameDownloader (TextView textView) {
            this.textView = new WeakReference<>(textView);
        }

        @Override
        protected String doInBackground(String... params) {
            DBHandler db = new DBHandler();
            return db.getUserNamesByEmail(params[0]);
        }

        @Override
        protected void onPostExecute(String name) {
            TextView textView = this.textView.get();

            if (textView != null) {
                textView.setText(name);
            }
        }
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

            final ArrayList<String> dataSet = this.dataSet.get();
            final DrawerRecyclerViewAdapter adapter = this.adapter.get();

            if (dataSet != null && adapter != null) {
                dataSet.clear();
                dataSet.addAll(results);
                adapter.notifyDataSetChanged();

                toolbarTitle.setText(dataSet.get(0));
                groupName = dataSet.get(0);

                if (viewPagerAdapter != null)
                    viewPagerAdapter.updateGroupFragments(groupName);
            }
        }
    }
}
