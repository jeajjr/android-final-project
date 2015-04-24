package com.almasosorio.sharethatbill;


import android.content.Context;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private static String TAG = "RecyclerViewAdapter";

    public enum ItemType {PICTURE_WITH_TEXT, NOTIFICATION_LIST_ITEM, BILL_LIST_ITEM,
        GROUP_MEMBERS_LIST_ITEM, CREATE_GROUP_MEMBER_ENTRY, WHO_PAID_LIST_ITEM,
        SPLIT_OPTIONS_LIST_ITEM};
    public enum MapItemKey {TEXT_1, TEXT_2, TEXT_3, TEXT_4, CLICKABLE_BILL_NAME,
        CREATE_GROUP_MEMBER_ENTRY_IS_VALID};

    private Context context;
    ArrayList<HashMap<MapItemKey, String>> dataSet;
    private ItemType listType;

    private Fragment parentFragment;

    private OnListItemClickListener onListItemClickListener;

    public RecyclerViewAdapter(Context context, ArrayList<HashMap<MapItemKey, String>> dataSet, ItemType listType) {
        this.context = context;
        this.dataSet = dataSet;
        this.listType = listType;
    }

    public void setParentFragment(Fragment f) {
        parentFragment = f;
    }

    public void setEntryIsValid(int index, boolean isValid) {

        if (listType != ItemType.CREATE_GROUP_MEMBER_ENTRY)
            return;

        if (isValid) {
            dataSet.get(index).put(MapItemKey.CREATE_GROUP_MEMBER_ENTRY_IS_VALID, Boolean.TRUE.toString());
            notifyItemChanged(index);
        } else {
            dataSet.remove(index);
            notifyItemRemoved(index);
        }
    }

    public int getEntryByString(String s) {
        for (int i = 0; i < dataSet.size(); i++) {
            if (dataSet.get(i).get(MapItemKey.TEXT_1).equals(s)) {
                return i;
            }
        }
        return -1;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView icon;
        public TextView textView1;
        public TextView textView2;
        public TextView textView3;
        public TextView textView4;
        public ImageButton imageButton1, imageButton2;
        public ProgressBar progressBar1;

        public ViewHolder(View v) {
            super(v);

            textView1 = (TextView) v.findViewById(R.id.item_text1);

            switch (listType) {
                case PICTURE_WITH_TEXT:
                    icon = (ImageView) v.findViewById(R.id.item_picture);
                    break;

                case NOTIFICATION_LIST_ITEM:
                    textView2 = (TextView) v.findViewById(R.id.item_text2);
                    break;

                case BILL_LIST_ITEM:
                    textView2 = (TextView) v.findViewById(R.id.item_text2);
                    textView3 = (TextView) v.findViewById(R.id.item_text3);
                    textView4 = (TextView) v.findViewById(R.id.item_text4);
                    break;

                case GROUP_MEMBERS_LIST_ITEM:
                    textView2 = (TextView) v.findViewById(R.id.item_text2);
                    textView3 = (TextView) v.findViewById(R.id.item_text3);
                    break;

                case CREATE_GROUP_MEMBER_ENTRY:

                    progressBar1 = (ProgressBar) v.findViewById(R.id.progressBar);

                    imageButton1 = (ImageButton) v.findViewById(R.id.editEntry);
                    imageButton2 = (ImageButton) v.findViewById(R.id.removeEntry);

                    v.setClickable(false);

                    imageButton1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ((FragmentCreateGroup)parentFragment).editMember(getPosition());
                        }
                    });

                    imageButton2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dataSet.remove(getPosition());
                            notifyItemRemoved(getPosition());
                        }
                    });
                    break;

                case WHO_PAID_LIST_ITEM:
                case SPLIT_OPTIONS_LIST_ITEM:
                    textView2 = (TextView) v.findViewById(R.id.valuePaid);
                    break;

            }

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "adapter received click on item " + getPosition());

                    if (onListItemClickListener != null)
                        onListItemClickListener.onItemClick(getPosition());
                }
            });
        }

        public void bindItemData (Map<MapItemKey, String> data) {

            textView1.setText(data.get(MapItemKey.TEXT_1));

            switch (listType) {
                case PICTURE_WITH_TEXT:
                    icon.setImageResource(R.drawable.group_member);
                    break;

                case NOTIFICATION_LIST_ITEM:
                    textView2.setText(data.get(MapItemKey.TEXT_2));
                    break;

                case BILL_LIST_ITEM:
                    textView2.setText(data.get(MapItemKey.TEXT_2));
                    textView3.setText(data.get(MapItemKey.TEXT_3));
                    textView4.setText(data.get(MapItemKey.TEXT_4));
                    break;

                case GROUP_MEMBERS_LIST_ITEM:
                    textView2.setText(data.get(MapItemKey.TEXT_2));
                    textView3.setText(data.get(MapItemKey.TEXT_3));
                    break;

                case CREATE_GROUP_MEMBER_ENTRY:
                    try {
                        if (Boolean.valueOf(data.get(MapItemKey.CREATE_GROUP_MEMBER_ENTRY_IS_VALID)))
                            progressBar1.setVisibility(View.INVISIBLE);
                        else
                            progressBar1.setVisibility(View.VISIBLE);
                    } catch (Exception ex) {

                    }
                    break;

                case WHO_PAID_LIST_ITEM:
                case SPLIT_OPTIONS_LIST_ITEM:

                    String email = data.get(MapItemKey.TEXT_4);

                    Log.d("RecyclerViewAdapter", "Comparing " + (email == null ? "<null>" : email) + " with " +Preferences.getInstance().getUserEmail() );

                    if (email != null && email.equals(Preferences.getInstance().getUserEmail())) {
                        textView1.setTextColor(Color.rgb(255, 165, 55));
                        textView1.setText(textView1.getText() + " (You)");
                    } else
                        textView1.setTextColor(Color.WHITE);

                    textView2.setText(data.get(MapItemKey.TEXT_2));
                    break;
            }
        }
    }

    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder (ViewGroup parent, int viewType) {
        View v = null;

        switch (listType) {
            case PICTURE_WITH_TEXT:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image_with_text, parent, false);
                break;

            case NOTIFICATION_LIST_ITEM:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false);
                break;

            case BILL_LIST_ITEM:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bills_list, parent, false);
                break;

            case GROUP_MEMBERS_LIST_ITEM:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_members_list, parent, false);
                break;

            case CREATE_GROUP_MEMBER_ENTRY:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_creategroup_addmember, parent, false);
                break;

            case WHO_PAID_LIST_ITEM:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_whopaid_entry, parent, false);
                break;

            case SPLIT_OPTIONS_LIST_ITEM:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_whopaid_entry, parent, false);
                ((TextView)v.findViewById(R.id.valuePaid)).setTextColor(FragmentNewBill.ColorNegative);
                break;
        }

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder (ViewHolder holder, int position) {
        Map<MapItemKey, String> data = dataSet.get(position);
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