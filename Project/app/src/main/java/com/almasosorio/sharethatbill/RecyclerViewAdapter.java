package com.almasosorio.sharethatbill;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Map;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private static String TAG = "RecyclerViewAdapter";

    public enum ItemType {PICTURE_WITH_TEXT, UPDATES_LIST_ITEM, BILL_LIST_ITEM, GROUP_MEMBERS_LIST_ITEM};

    private Context context;
    ArrayList<Map<String, ?>> dataSet;
    private ItemType listType;

    private OnListItemClickListener onListItemClickListener;

    public RecyclerViewAdapter(Context context, ArrayList<Map<String, ?>> dataSet, ItemType listType) {
        this.context = context;
        this.dataSet = dataSet;
        this.listType = listType;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView icon;
        public TextView textView1;
        public TextView textView2;
        public TextView textView3;
        public TextView textView4;

        public ViewHolder(View v) {
            super(v);
            switch (listType) {
                case PICTURE_WITH_TEXT:
                    //icon = (ImageView) v.findViewById(R.id.imageViewIcon);
                    break;

                case UPDATES_LIST_ITEM:
                    break;

                case BILL_LIST_ITEM:
                    break;

                case GROUP_MEMBERS_LIST_ITEM:
                    break;
            }

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "adapter received click on item " + getPosition());
                    
                    onListItemClickListener.onItemClick(getPosition());
                }
            });
        }

        public void bindItemData (Map<String, ?> data) {
            switch (listType) {
                case PICTURE_WITH_TEXT:
                    //icon.setImageResource(DataParser.getIconResource(iconString));
                    break;

                case UPDATES_LIST_ITEM:
                    break;

                case BILL_LIST_ITEM:
                    break;

                case GROUP_MEMBERS_LIST_ITEM:
                    break;
            }
        }
    }

    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder (ViewGroup parent, int viewType) {
        View v = null;

        switch (listType) {
            case PICTURE_WITH_TEXT:
                //v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_3_hour_item, parent, false);
                break;

            case UPDATES_LIST_ITEM:
                break;

            case BILL_LIST_ITEM:
                break;

            case GROUP_MEMBERS_LIST_ITEM:
                break;
        }

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder (ViewHolder holder, int position) {
        Map<String, ?> data = dataSet.get(position);
        holder.bindItemData(data);
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    /**
     * OnListItemClickListener interface for clicks on a list item.
     * This design pattern allows the listener actions to be defined
     * outside the adapter.
     */
    public interface OnListItemClickListener {
        public void onItemClick(int position);
    }

    public void setOnListItemClickListener(final OnListItemClickListener mItemClickListener) {
        this.onListItemClickListener = mItemClickListener;
    }
}