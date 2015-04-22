package com.almasosorio.sharethatbill;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.Locale;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    private FragmentManager fm;
    private Context context;
    private String userName;
    private String groupName;
    private enum PagerType {GROUP_PAGER, NEWBILL_PAGER};
    private PagerType pagerType;
    private ArrayList<String> userList;

    public ViewPagerAdapter (FragmentManager fm, Context context, String userName,
                             String groupName) {
        super(fm);
        this.fm = fm;
        this.context = context;
        this.userName = userName;
        this.groupName = groupName;
        this.pagerType = PagerType.GROUP_PAGER;
    }

    public ViewPagerAdapter (FragmentManager fm, Context context, ArrayList<String> userList) {
        super(fm);
        this.userList = userList;
        this.pagerType = PagerType.NEWBILL_PAGER;
    }

    @Override
    public Fragment getItem(int position) {

        if (pagerType == PagerType.GROUP_PAGER) {
            switch (position) {

                case 0:
                    return FragmentGroupNotifications.newInstance(context, userName, groupName);
                case 1:
                    return FragmentGroupBills.newInstance(context, userName, groupName);
                case 2:
                    return FragmentGroupMembers.newInstance(context, userName, groupName);


                default:
                    break;
            }
        } else if (pagerType == PagerType.NEWBILL_PAGER) {
            switch (position) {
                case 0:
                case 1:
                default:
                    break;
            }
        }
        return null;
    }

    @Override
    public int getCount() {
        switch (pagerType) {
            case GROUP_PAGER: return 3;
            case NEWBILL_PAGER: return 2;
            default: break;
        }
        return 0;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        Locale l = Locale.getDefault();
        String name = "";
        if (pagerType == PagerType.GROUP_PAGER) {
            switch (position) {
                case 0:
                    name = context.getResources().getString(R.string.notifications);
                    break;
                case 1:
                    name = context.getResources().getString(R.string.bills);
                    break;
                case 2:
                    name = context.getResources().getString(R.string.users);
                    break;

                default:
                    break;
            }
        } else if (pagerType == PagerType.NEWBILL_PAGER) {
            switch (position) {
                case 0:
                    name = context.getResources().getString(R.string.who_paid);
                    break;
                case 1:
                    name = context.getResources().getString(R.string.split_options);
                    break;
                default:
                    break;
            }
        }
        return name.toUpperCase(l);
    }
}