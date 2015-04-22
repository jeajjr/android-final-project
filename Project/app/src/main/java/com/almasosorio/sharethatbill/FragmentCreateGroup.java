package com.almasosorio.sharethatbill;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;


public class FragmentCreateGroup extends Fragment {

    public static final String TAG = "FragmentCreateGroup";
    private static final String EXTRA_ENTRY_EMAIL = "ENTRY_EMAIL";
    private static final int ADD_MEMBER_REQUEST = 0;
    private static final int EDIT_MEMBER_REQUEST = 1;
    private boolean mIsFirstGroup;
    private RecyclerView mRecyclerView;
    private RecyclerViewAdapter mRecyclerAdapter;
    private int mLastEditPosition;
    ArrayList<HashMap<RecyclerViewAdapter.MapItemKey, String>> dataSet;

    static public FragmentCreateGroup newInstance(boolean isFirstGroup) {
        FragmentCreateGroup f = new FragmentCreateGroup();
        f.mIsFirstGroup = isFirstGroup;
        return f;
    }

    static public FragmentCreateGroup newInstance() {
        FragmentCreateGroup f = new FragmentCreateGroup();
        f.mIsFirstGroup = false;
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_creategroup, container, false);

        if (mIsFirstGroup)
            ((TextView)v.findViewById(R.id.topTitle)).setText(R.string.create_first_group);

        dataSet = new ArrayList<>();

        mRecyclerView = (RecyclerView) v.findViewById(R.id.memberList);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerAdapter = new RecyclerViewAdapter(getActivity(), dataSet, RecyclerViewAdapter.ItemType.CREATE_GROUP_MEMBER_ENTRY);
        mRecyclerAdapter.setParentFragment(this);
        mRecyclerView.setAdapter(mRecyclerAdapter);

        ((Button)v.findViewById(R.id.addMember)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MemberDialog dialog = new MemberDialog("Add Member", "Enter member email", "");
                dialog.setTargetFragment(FragmentCreateGroup.this, ADD_MEMBER_REQUEST);
                dialog.show(getFragmentManager(), "AddMemberDialog");
            }
        });

        ((Button)v.findViewById(R.id.createGroup)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO:
                // Code to go to other activity where groups are displayed
                // This should be the group being displayed
                // Add members to group/db that are inside ArrayList "dataSet".
            }
        });

        return v;
    }

    public void editMember(int position) {

        if (position < 0 || position >= dataSet.size())
            return;

        mLastEditPosition = position;

        MemberDialog dialog = new MemberDialog("Edit Member", "Enter member's new email",
                dataSet.get(position).get(RecyclerViewAdapter.MapItemKey.TEXT_1));
        dialog.setTargetFragment(FragmentCreateGroup.this, EDIT_MEMBER_REQUEST);
        dialog.show(getFragmentManager(), "EditMemberDialog");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode != Activity.RESULT_OK) {
            super.onActivityResult(requestCode, resultCode, data);
            return;
        }

        if (requestCode == ADD_MEMBER_REQUEST && dataSet != null) {
            HashMap<RecyclerViewAdapter.MapItemKey, String> item = new HashMap<>();
            item.put(RecyclerViewAdapter.MapItemKey.TEXT_1, data.getStringExtra(EXTRA_ENTRY_EMAIL));
            dataSet.add(item);
            mRecyclerAdapter.notifyItemInserted(dataSet.size() - 1);
        } else if (requestCode == EDIT_MEMBER_REQUEST && dataSet != null) {
            dataSet.get(mLastEditPosition).put(RecyclerViewAdapter.MapItemKey.TEXT_1, data.getStringExtra(EXTRA_ENTRY_EMAIL));
            mRecyclerAdapter.notifyItemChanged(mLastEditPosition);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    public static class MemberDialog extends DialogFragment {

        private String title, message, defaultText;

        public MemberDialog(String title, String message, String defaultText) {
            super();
            this.title = title;
            this.message = message;
            this.defaultText = defaultText;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            final EditText textEdit = new EditText(getActivity());
            textEdit.setText(defaultText);
            AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
            adb.setView(textEdit)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (getTargetFragment() != null) {
                                    Intent i = new Intent();
                                    i.putExtra(EXTRA_ENTRY_EMAIL, textEdit.getText());
                                    getTargetFragment().onActivityResult(getTargetRequestCode(),
                                            Activity.RESULT_OK, i);
                                }
                            }
                        })
                .setNegativeButton(android.R.string.cancel, null)
                .setIcon(android.R.drawable.ic_input_add);

            return adb.create();
        }
    }

}
