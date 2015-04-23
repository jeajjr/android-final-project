package com.almasosorio.sharethatbill;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;


public class FragmentSplitOptions extends Fragment implements Spinner.OnItemSelectedListener {

    public static final String TAG = "FragmentSplitOptions";
    private static final int EDIT_USER_AMOUNT_REQUEST = 0;

    private int mLastPositionEdited;
    private int mSpinnerSelectedIndex;
    private boolean mLoading = false;
    ProgressBar mLoadingBar;
    Double mTotalSplitValue, mTotalPaid;
    RecyclerView mRecyclerView;
    RecyclerViewAdapter mRecyclerAdapter;
    TextView mTotalSplitLabel;
    Spinner mSpinner;
    ArrayList<HashMap<RecyclerViewAdapter.MapItemKey, String>> dataSet;
    ArrayList<HashMap<FragmentNewBill.KeyType, ?>> mUserList;

    static public FragmentSplitOptions newInstance(ArrayList<HashMap<FragmentNewBill.KeyType, ?>> userList) {
        FragmentSplitOptions f = new FragmentSplitOptions();
        f.dataSet = new ArrayList<>();
        f.mUserList = userList;
        return f;
    }

    public void setLoading(boolean l) {
        mLoading = l;
        if (mLoadingBar != null)
            mLoadingBar.setVisibility(l ? View.VISIBLE : View.INVISIBLE);
    }

    public void updateDataSet() {

        mTotalSplitValue = 0.0;

        for (int i = 0; i < mUserList.size(); i++) {
            Double value = 0.0;
            value = (Double) mUserList.get(i).get(FragmentNewBill.KeyType.AmountToPay);
            dataSet.get(i).put(RecyclerViewAdapter.MapItemKey.TEXT_2,
                    String.format(FragmentNewBill.TOTAL_PAID_FORMAT, value));
            mTotalSplitValue += value;
        }

        if (mTotalSplitLabel != null) {
            mTotalSplitLabel.setText(String.format(getString(R.string.total) + ": " + FragmentNewBill.TOTAL_PAID_FORMAT, mTotalSplitValue));
            mRecyclerAdapter.notifyDataSetChanged();
        }
    }

    public void updateDataSet(int index, Double old) {

        Double value = (Double) mUserList.get(index).get(FragmentNewBill.KeyType.AmountToPay);
        dataSet.get(index).put(RecyclerViewAdapter.MapItemKey.TEXT_2,
                String.format(FragmentNewBill.TOTAL_PAID_FORMAT, value));
        mTotalSplitValue += value - old;

        mTotalSplitLabel.setText(String.format(getString(R.string.total) + ": " + FragmentNewBill.TOTAL_PAID_FORMAT, mTotalSplitValue));
        mRecyclerAdapter.notifyDataSetChanged();
    }

    public void onUpdateUserList() {

        dataSet.clear();

        for (int i = 0; i < mUserList.size(); i++) {
            HashMap<RecyclerViewAdapter.MapItemKey, String> item = new HashMap<>();
            item.put(RecyclerViewAdapter.MapItemKey.TEXT_1, (String)mUserList.get(i).get(FragmentNewBill.KeyType.UserName));
            dataSet.add(item);
        }

        updateDataSet();

    }

    public void onUpdateTotalPaid(Double newValue) {

        if (newValue.equals(mTotalPaid))
            return;

        mTotalPaid = newValue;
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
        View v = inflater.inflate(R.layout.fragment_split_options, container, false);

        mLoadingBar = (ProgressBar)v.findViewById(R.id.progressBar);
        setLoading(mLoading);

        mTotalSplitLabel = (TextView)v.findViewById(R.id.totalValue);
        mTotalSplitValue = 0.0;

        mSpinner = (Spinner) v.findViewById(R.id.spinner);
        mSpinner.setOnItemSelectedListener(this);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.split_method_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(adapter);

        mRecyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            private Drawable mDivider = getResources().getDrawable(R.drawable.line_divider);
            @Override
            public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
                int left = parent.getPaddingLeft();
                int right = parent.getWidth() - parent.getPaddingRight();
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
        mRecyclerAdapter = new RecyclerViewAdapter(getActivity(), dataSet, RecyclerViewAdapter.ItemType.SPLIT_OPTIONS_LIST_ITEM);
        mRecyclerAdapter.setOnListItemClickListener(new RecyclerViewAdapter.OnListItemClickListener() {
            @Override
            public void onItemClick(int position) {
                onEditMemberValue(position);
            }
        });
        mRecyclerView.setAdapter(mRecyclerAdapter);

        updateDataSet();

        return v;
    }

    public void onEditMemberValue(int position) {

        if (mSpinnerSelectedIndex == 1)
            return;

        mLastPositionEdited = position;

        FragmentWhoPaid.ValuePaidDialog dialog = new FragmentWhoPaid.ValuePaidDialog(getString(R.string.edit_value_paid),
                getString(R.string.enter_value_paid_by) + " " + dataSet.get(position).get(RecyclerViewAdapter.MapItemKey.TEXT_1),
                (Double)mUserList.get(position).get(FragmentNewBill.KeyType.AmountToPay));
        dialog.setTargetFragment(FragmentSplitOptions.this, EDIT_USER_AMOUNT_REQUEST);
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
                    FragmentNewBill.KeyType.AmountToPay, data.getDoubleExtra(FragmentWhoPaid.NEW_AMOUNT_EXTRA, 0.0));
            updateDataSet(mLastPositionEdited, data.getDoubleExtra(FragmentWhoPaid.OLD_AMOUNT_EXTRA, 0.0));
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        if (mSpinnerSelectedIndex != position) {

            mSpinnerSelectedIndex = position;

            if (mSpinnerSelectedIndex == 1) {
                for (int i = 0; i < mUserList.size(); i++) {

                    ((HashMap<FragmentNewBill.KeyType, Double>)mUserList.get(i)).
                            put(FragmentNewBill.KeyType.OldAmountToPay,
                                    (Double)mUserList.get(i).get(FragmentNewBill.KeyType.AmountToPay));

                    ((HashMap<FragmentNewBill.KeyType, Double>)mUserList.get(i)).
                            put(FragmentNewBill.KeyType.AmountToPay, mTotalPaid / mUserList.size());
                }
            } else if (mSpinnerSelectedIndex == 0) {

                for (int i = 0; i < mUserList.size(); i++) {

                    ((HashMap<FragmentNewBill.KeyType, Double>)mUserList.get(i)).
                            put(FragmentNewBill.KeyType.AmountToPay, (Double)mUserList.get(i).get(FragmentNewBill.KeyType.OldAmountToPay));
                }

            }

            updateDataSet();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
