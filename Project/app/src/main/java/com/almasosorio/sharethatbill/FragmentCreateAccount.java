package com.almasosorio.sharethatbill;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


public class FragmentCreateAccount extends Fragment {
    private static final String TAG = "FragmentCreateAccount";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_create_account, container, false);

        Button createAccountButton = (Button) v.findViewById(R.id.button_create_account);
        createAccountButton.setText(createAccountButton.getText().toString().toUpperCase());


        return v;
    }
}
