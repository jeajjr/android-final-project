package com.almasosorio.sharethatbill;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class FragmentBillDetails extends Fragment {
    private static final String TAG = "FragmentBillDetails";

    public FragmentBillDetails() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_bill_details, container, false);

        View deleteButton = v.findViewById(R.id.layout_delete_button);
        TextView buttonText = (TextView) v.findViewById(R.id.delete_button_text);
        buttonText.setText(buttonText.getText().toString().toUpperCase());
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "clicked on deleteButton");
            }
        });

        return v;
    }


}
