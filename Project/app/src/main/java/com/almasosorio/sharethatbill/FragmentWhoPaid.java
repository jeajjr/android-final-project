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
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;


public class FragmentWhoPaid extends Fragment {

    public static final String TAG = "FragmentWhoPaid";
    private static final String EXTRA_AMOUNT_PAID = "AmountPaid";
    private static final int EDIT_USER_AMOUNT_REQUEST = 0;

    private int mLastPositionEdited;
    RecyclerView mRecyclerView;
    RecyclerViewAdapter mRecyclerAdapter;
    ArrayList<HashMap<RecyclerViewAdapter.MapItemKey, String>> dataSet;

    static public FragmentWhoPaid newInstance() {
        FragmentWhoPaid f = new FragmentWhoPaid();
        f.dataSet = new ArrayList<>();
        // TODO: pass members of the group as parameter for dataSet
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_who_paid, container, false);

        mRecyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerAdapter = new RecyclerViewAdapter(getActivity(), dataSet, RecyclerViewAdapter.ItemType.WHO_PAID_LIST_ITEM);
        mRecyclerAdapter.setParentFragment(this);
        mRecyclerAdapter.setOnListItemClickListener(new RecyclerViewAdapter.OnListItemClickListener() {
            @Override
            public void onItemClick(int position) {
                onEditMemberValue(position);
            }
        });
        mRecyclerView.setAdapter(mRecyclerAdapter);

        return v;
    }

    public void onEditMemberValue(int position) {

        mLastPositionEdited = position;

        ValuePaidDialog dialog = new ValuePaidDialog("Edit Value Paid",
                "Enter value paid by " + dataSet.get(position).get(RecyclerViewAdapter.MapItemKey.TEXT_1),
                dataSet.get(position).get(RecyclerViewAdapter.MapItemKey.TEXT_2));
        dialog.setTargetFragment(FragmentWhoPaid.this, EDIT_USER_AMOUNT_REQUEST);
        dialog.show(getFragmentManager(), "EditMemberValue");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode != Activity.RESULT_OK) {
            super.onActivityResult(requestCode, resultCode, data);
            return;
        }

        if (requestCode == EDIT_USER_AMOUNT_REQUEST) {
            dataSet.get(mLastPositionEdited).put(RecyclerViewAdapter.MapItemKey.TEXT_2,
                    data.getStringExtra(EXTRA_AMOUNT_PAID));
            mRecyclerAdapter.notifyItemChanged(mLastPositionEdited);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    public class ValuePaidDialog extends DialogFragment {

        private String title, message, defaultText;

        public ValuePaidDialog(String title, String message, String defaultText) {
            super();
            this.title = title;
            this.message = message;
            this.defaultText = defaultText;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            final EditText textEdit = new EditText(getActivity());
            textEdit.setText(defaultText);
            textEdit.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
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
                                    i.putExtra(EXTRA_AMOUNT_PAID, textEdit.getText());
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
