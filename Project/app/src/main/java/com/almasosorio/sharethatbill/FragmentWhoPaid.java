package com.almasosorio.sharethatbill;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;


public class FragmentWhoPaid extends Fragment {

    public static final String TAG = "FragmentWhoPaid";
    public static final String NEW_AMOUNT_EXTRA = "NewAmount";
    public static final String OLD_AMOUNT_EXTRA = "OldAmount";
    private static final int EDIT_USER_AMOUNT_REQUEST = 0;

    private ViewLoading mViewLoading;
    private int mLastPositionEdited;
    private boolean mLoading = false;
    RecyclerView mRecyclerView;
    RecyclerViewAdapter mRecyclerAdapter;
    TextView mTotalPaidLabel;
    Double mTotalPaidValue;
    ArrayList<HashMap<RecyclerViewAdapter.MapItemKey, String>> dataSet;
    ArrayList<HashMap<FragmentNewBill.KeyType, ?>> mUserList;

    private totalPaidListener mTotalPaidListener;

    public interface totalPaidListener {
        public void onTotalPaidChanged(Double value);
    }

    static public FragmentWhoPaid newInstance(ArrayList<HashMap<FragmentNewBill.KeyType, ?>> userList,
                                              TextView totalPaidLabel, totalPaidListener listener) {
        FragmentWhoPaid f = new FragmentWhoPaid();
        f.dataSet = new ArrayList<>();
        f.mUserList = userList;
        f.mTotalPaidLabel = totalPaidLabel;
        f.mTotalPaidListener = listener;
        return f;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    public void setLoading(boolean l, boolean success) {

        mLoading = l;

        if (mViewLoading != null)
            mViewLoading.setState(l, success);
    }

    public void setTotalPaidLabel(TextView totalPaidLabel) {
        mTotalPaidLabel = totalPaidLabel;
    }

    public void updateDataSet(int index, Double oldValue) {
        dataSet.get(index).put(RecyclerViewAdapter.MapItemKey.TEXT_2,
                String.format(FragmentNewBill.TOTAL_PAID_FORMAT, (Double) mUserList.get(index).get(FragmentNewBill.KeyType.AmountPaid)));
        mTotalPaidValue += (Double) mUserList.get(index).get(FragmentNewBill.KeyType.AmountPaid) - oldValue;
        mTotalPaidLabel.setText(String.format(FragmentNewBill.TOTAL_PAID_FORMAT, mTotalPaidValue));
        mTotalPaidLabel.setTextColor(mTotalPaidValue >= 0.0 ? FragmentNewBill.ColorPositive : FragmentNewBill.ColorNegative);

        if (mTotalPaidListener != null)
            mTotalPaidListener.onTotalPaidChanged(mTotalPaidValue);

        mRecyclerAdapter.notifyItemChanged(index);
    }

    public void updateDataSet() {

        mTotalPaidValue = 0.0;

        for (int i = 0; i < mUserList.size(); i++) {

            HashMap<FragmentNewBill.KeyType, ?> user = mUserList.get(i);
            dataSet.get(i).put(RecyclerViewAdapter.MapItemKey.TEXT_2,
                    String.format(FragmentNewBill.TOTAL_PAID_FORMAT,
                            (Double)user.get(FragmentNewBill.KeyType.AmountPaid)));
            mTotalPaidValue += (Double)user.get(FragmentNewBill.KeyType.AmountPaid);
        }

        if (mTotalPaidListener != null)
            mTotalPaidListener.onTotalPaidChanged(mTotalPaidValue);

        mRecyclerAdapter.notifyDataSetChanged();
        mTotalPaidLabel.setText(String.format(FragmentNewBill.TOTAL_PAID_FORMAT, mTotalPaidValue));
        mTotalPaidLabel.setTextColor(mTotalPaidValue >= 0.0 ? FragmentNewBill.ColorPositive : FragmentNewBill.ColorNegative);
    }

    public void onUpdateUserList() {
        dataSet.clear();
        for (int i = 0; i < mUserList.size(); i++) {
            HashMap<RecyclerViewAdapter.MapItemKey, String> item = new HashMap<>();
            item.put(RecyclerViewAdapter.MapItemKey.TEXT_1, (String)mUserList.get(i).get(FragmentNewBill.KeyType.UserName));
            item.put(RecyclerViewAdapter.MapItemKey.TEXT_4, (String)mUserList.get(i).get(FragmentNewBill.KeyType.UserEmail));
            Log.d("FragmentWhoPaid", "Added - " + (String)mUserList.get(i).get(FragmentNewBill.KeyType.UserEmail));
            dataSet.add(item);
        }
        updateDataSet();
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

        mTotalPaidValue = 0.0;

        mRecyclerView = new RecyclerView(getActivity());
        int padding = (int)getActivity().getResources().getDisplayMetrics().density;
        mRecyclerView.setPadding(padding, padding, padding, padding);
        //mRecyclerView.setBackground(getActivity().getDrawable(R.drawable.rectangle_outline));
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);//(RelativeLayout.LayoutParams)mRecyclerView.getLayoutParams();
        params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        mRecyclerView.setLayoutParams(params);

        mViewLoading = (ViewLoading)v.findViewById(R.id.viewLoader);
        mViewLoading.setLoadedView(mRecyclerView);
        mViewLoading.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mViewLoading.hasFailed()) {
                    FragmentNewBill f = (FragmentNewBill)getActivity().getSupportFragmentManager().findFragmentById(R.id.container);
                    if (f != null) {
                        f.loadGroupMembers();
                    }
                }
            }
        });

        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            private Drawable mDivider = getResources().getDrawable(R.drawable.line_divider);
            @Override
            public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
                int left = parent.getPaddingLeft() + 35;
                int right = parent.getWidth() - parent.getPaddingRight() - 35;
                int childCount = parent.getChildCount();
                for (int i = 0; i < childCount - 1; i++) {
                    View child = parent.getChildAt(i);
                    RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
                    int top = child.getBottom() + params.bottomMargin;
                    int bottom = top + mDivider.getIntrinsicHeight();
                    mDivider.setBounds(left, top, right, bottom);
                    mDivider.draw(c);
                }
                super.onDrawOver(c, parent, state);
            }
        });
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

        ValuePaidDialog dialog = new ValuePaidDialog(getString(R.string.edit_value_paid),
                getString(R.string.enter_value_paid_by) + " " + dataSet.get(position).get(RecyclerViewAdapter.MapItemKey.TEXT_1),
                (Double)mUserList.get(position).get(FragmentNewBill.KeyType.AmountPaid));
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
            ((HashMap<FragmentNewBill.KeyType, Double>)mUserList.get(mLastPositionEdited)).put(
                        FragmentNewBill.KeyType.AmountPaid, data.getDoubleExtra(NEW_AMOUNT_EXTRA, 0.0));
            updateDataSet(mLastPositionEdited, data.getDoubleExtra(OLD_AMOUNT_EXTRA, 0.0));
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    public static class ValuePaidDialog extends DialogFragment {

        private String title, message;
        private Double value;

        public ValuePaidDialog(String title, String message, Double defaultValue) {
            super();
            this.title = title;
            this.message = message;
            this.value = defaultValue;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            final EditText textEdit = new EditText(getActivity());
            textEdit.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            textEdit.setText(value.toString());
            textEdit.setSelection(0, value.toString().length());
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
                                    Double valuePaid;
                                    String text = textEdit.getText().toString();

                                    if (text.isEmpty())
                                        text = "0";

                                    try {
                                        valuePaid = Double.valueOf(text);
                                        i.putExtra(NEW_AMOUNT_EXTRA, valuePaid);
                                        i.putExtra(OLD_AMOUNT_EXTRA, value);
                                        getTargetFragment().onActivityResult(getTargetRequestCode(),
                                                Activity.RESULT_OK, i);
                                    } catch (NumberFormatException ex) {
                                    }
                                }
                            }
                        })
                .setNegativeButton(android.R.string.cancel, null)
                .setIcon(android.R.drawable.ic_input_add);

            return adb.create();
        }
    }

}
