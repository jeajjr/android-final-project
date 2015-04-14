package com.almasosorio.sharethatbill;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentLogin extends Fragment {


    public FragmentLogin() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_login, container, false);

        Button loginButton = (Button) v.findViewById(R.id.button_login);
        loginButton.setText(loginButton.getText().toString().toUpperCase());

        Button createAccountButton = (Button) v.findViewById(R.id.button_create_account);
        createAccountButton.setText(createAccountButton.getText().toString().toUpperCase());

        return v;
    }


}
