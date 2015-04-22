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
                    Log.d(TAG, "clicked item " + itemPosition);

                    if (itemPosition < groupNames.size()) {
                        currentItem = itemPosition;
                        notifyDataSetChanged();
                    }

                    if (onDrawerItemClickListener != null) {
                        if (itemPosition < groupNames.size()) {
                            onDrawerItemClickListener.onGroupItemClick(itemPosition);
                        }
                        else
                            onDrawerItemClickListener.onCreateGroupClick();
                    }
                }
            });
        }

        public void bindData (String groupName) {
            this.name.setText(groupName);

            if (currentItem == getPosition()) {
                thisView.setBackgroundColor(0xffffffff);
                name.setTextColor(0xff000000);
            }
            else {
                thisView.setBackgroundColor(0x00000000);
                name.setTextColor(0xffffffff);
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

        if (viewType < groupNames.size())
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.drawer_item_group, parent, false);
        else
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.drawer_item_option, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder (ViewHolder holder, int position) {
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