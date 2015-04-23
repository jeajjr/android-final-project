package com.almasosorio.sharethatbill;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;


public class DrawerRecyclerViewAdapter extends RecyclerView.Adapter<DrawerRecyclerViewAdapter.ViewHolder> {
    private static String TAG = "DrawerRecyclerViewAdapter";

    private ArrayList<String> groupNames;
    private OnDrawerItemClickListener onDrawerItemClickListener;
    
    private int currentItem;
    private Context context;

    public DrawerRecyclerViewAdapter(Context context, ArrayList<String> groupNames) {
        this.context = context;
        this.groupNames = groupNames;
        currentItem = 0;
    }

    public interface OnDrawerItemClickListener {
        public void onGroupItemClick(int index);
        public void onCreateGroupClick();
    }

    public void setOnDrawerItemClickListener(final OnDrawerItemClickListener mItemClickListener) {
        this.onDrawerItemClickListener = mItemClickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView icon;
        public TextView name;
        public View thisView;

        public ViewHolder(View v) {
            super(v);

            thisView = v;

            icon = (ImageView) v.findViewById(R.id.itemIcon);
            name = (TextView) v.findViewById(R.id.itemName);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int itemPosition = getPosition();


                    if (itemPosition < groupNames.size()) {
                        currentItem = itemPosition;
                        notifyDataSetChanged();
                    }

                    if (onDrawerItemClickListener != null) {
                        if (itemPosition < groupNames.size() && groupNames.size() != 0) {
                            Log.d(TAG, "clicked group " + itemPosition);
                            onDrawerItemClickListener.onGroupItemClick(itemPosition);
                        }
                        else {
                            Log.d(TAG, "clicked add group item");
                            onDrawerItemClickListener.onCreateGroupClick();
                        }
                    }
                }
            });
        }

        public void bindData (String groupName) {
            this.name.setText(groupName);

            if (currentItem == getPosition()) {
                thisView.setBackgroundColor(0xff272c2b);
                name.setTextColor(0xffDFDFE0);
            }
            else {
                thisView.setBackgroundColor(0x00000000);
                name.setTextColor(0xff272c2b);
            }
        }

        public void bindCreateGroup() {
            name.setText(context.getString(R.string.create_new_group));
            icon.setImageResource(R.drawable.gear_dark);
        }
    }

    public void dataSetChanged(ArrayList<String> groupNames) {
        this.groupNames.clear();
        this.groupNames.addAll(groupNames);
        this.notifyDataSetChanged();
    }

    public void setCurrentGroup(int currentGroupPosition) {
        currentItem = currentGroupPosition;
    }
    
    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public DrawerRecyclerViewAdapter.ViewHolder onCreateViewHolder (ViewGroup parent, int viewType) {
        View v;
        Log.d(TAG, "received viewType " + viewType + " groupNames.size(): " + groupNames.size());

        if (viewType < groupNames.size()) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.drawer_item_group, parent, false);
            Log.d(TAG, "creating drawer_item_group");
        }
        else {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.drawer_item_option, parent, false);
            Log.d(TAG, "creating drawer_item_option");
        }

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder (ViewHolder holder, int position) {
        Log.d(TAG, "binding position " + position + " groupNames.size(): " + groupNames.size());
        if (position < groupNames.size())
            holder.bindData(groupNames.get(position));
        else
            holder.bindCreateGroup();
    }

    @Override
    public int getItemCount() {
        return groupNames.size() + 1;
    }


}