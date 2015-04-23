package com.almasosorio.sharethatbill;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    private FragmentManager fm;
    private Context context;
    private String userName;
    private String groupName;
    private enum PagerType {GROUP_PAGER, NEWBILL_PAGER};
    private PagerType pagerType;
    private FragmentWhoPaid mWhoPaidFrag;
    private FragmentSplitOptions mSplitOptionsFrag;

    private FragmentGroupNotifications fragNotifications;
    private FragmentGroupBills fragBills;
    private FragmentGroupMembers fragMembers;

    public ViewPagerAdapter (FragmentManager fm, Context context, String userName,
                             String groupName) {
        super(fm);
        this.fm = fm;
        this.context = context;
        this.userName = userName;
        this.groupName = groupName;
        this.pagerType = PagerType.GROUP_PAGER;
    }

    public ViewPagerAdapter (FragmentManager fm, Context context,
                             ArrayList<HashMap<FragmentNewBill.KeyType, ?>> userList,
                             TextView totalPaidLabel) {
        super(fm);
        this.fm = fm;
        this.context = context;
        this.pagerType = PagerType.NEWBILL_PAGER;
        mSplitOptionsFrag = FragmentSplitOptions.newInstance(userList);
        mWhoPaidFrag = FragmentWhoPaid.newInstance(userList, totalPaidLabel, new FragmentWhoPaid.totalPaidListener() {
            @Override
            public void onTotalPaidChanged(Double value) {
                mSplitOptionsFrag.onUpdateTotalPaid(value);
            }
        });
    }

    public void updateTotalPaidLabel(TextView totalPaidLabel) {
        mWhoPaidFrag.setTotalPaidLabel(totalPaidLabel);
    }

    public void updateGroupFragments(String groupName) {
        this.groupName = groupName;
        if (fragNotifications != null)
            fragNotifications.updateGroup(groupName);
        if (fragBills != null)
            fragBills.updateGroup(groupName);
        if (fragMembers != null)
            fragMembers.updateGroup(groupName);
    }

    @Override
    public Fragment getItem(int position) {

        if (pagerType == PagerType.GROUP_PAGER) {
            switch (position) {

                case 0:
                    return (fragNotifications = FragmentGroupNotifications.newInstance(context, userName, groupName));
                case 1:
                    return (fragBills = FragmentGroupBills.newInstance(context, userName, groupName));
                case 2:
                    return (fragMembers = FragmentGroupMembers.newInstance(context, userName, groupName));

                default:
                    break;
            }
        } else if (pagerType == PagerType.NEWBILL_PAGER) {
            switch (position) {
                case 0: return mWhoPaidFrag;
                case 1: return mSplitOptionsFrag;
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
                    name = context.getResources().getString(R.string.members);
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