package com.almasosorio.sharethatbill;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.Locale;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    private FragmentManager fm;
    private Context context;
    private String userName;
    private String groupName;

    public ViewPagerAdapter (FragmentManager fm, Context context, String userName,
                             String groupName) {
        super(fm);
        this.fm = fm;
        this.context = context;
        this.userName = userName;
        this.groupName = groupName;
    }



    @Override
    public Fragment getItem(int position) {
        switch (position) {

            case 0:
                return FragmentGroupBills.newInstance(context, userName, groupName);
            /*
            case 1:
                return new FragmentThreeHoursForecast();
            case 2:
                return new FragmentDailyForecast();
                */
        }
        return null;
    }

    @Override
    public int getCount() {
        return 1;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        Locale l = Locale.getDefault();
        String name = "";

        switch (position) {
            case 0:
                name = context.getResources().getString(R.string.bills);
                break;
            /*
            case 1:
                name = context.getResources().getString(R.string.tab_title_three_hours_forecast);
                break;
            case 2:
                name = context.getResources().getString(R.string.tab_title_daily_forecast);
                break;
                */
        }
        return name.toUpperCase(l);
    }
}