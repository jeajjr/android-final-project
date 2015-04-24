package com.almasosorio.sharethatbill;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;


public class FragmentCreateAccount extends Fragment {
    private static final String TAG = "FragmentCreateAccount";

    private ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_create_account, container, false);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(getResources().getString(R.string.loading));
        progressDialog.setCancelable(false);

        Button createAccountButton = (Button) v.findViewById(R.id.button_create_account);
        createAccountButton.setText(createAccountButton.getText().toString().toUpperCase());
        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processTextFields();
            }
        });

        return v;
    }

    private void processTextFields() {
        String firstName = ((EditText) getView().findViewById(R.id.firstName)).getText().toString();
        String lastName = ((EditText) getView().findViewById(R.id.lastName)).getText().toString();
        String email = ((EditText) getView().findViewById(R.id.email)).getText().toString();
        String password = ((EditText) getView().findViewById(R.id.password)).getText().toString();
        String passwordConfirm = ((EditText) getView().findViewById(R.id.passwordConfirm)).getText().toString();

        if (    firstName.length() == 0 ||
                lastName.length() == 0 ||
                email.length() == 0 ||
                password.length() == 0 ||
                passwordConfirm.length() == 0)
            createAlertDialog(getString(R.string.error), getString(R.string.did_not_complete_all_fields));
        /* TODO: uncomment
        else if (!isValidEmail(email)) {
            createAlertDialog(getString(R.string.invalid_email), getString(R.string.invalid_email_text));
        }
        */
        else if (!password.equals(passwordConfirm)) {
            createAlertDialog(getString(R.string.error), getString(R.string.passwords_dont_match));
        }
        else {
            (new AccountCreator()).execute(email, firstName, lastName, password);
        }
    }

    private void accountCreationSuccessful(String userName) {
        Log.d(TAG, "accountCreationSuccessful");

        Intent intent = new Intent(getActivity(), ActivityCreateGroup.class);
        intent.putExtra(getString(R.string.bundle_user_name), userName);
        startActivity(intent);
    }

    private boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    protected void createAlertDialog(String title, String message) {
        Log.d(TAG, "createAccountFailed");

        new AlertDialog.Builder(getActivity())
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private class AccountCreator extends AsyncTask<String, Void, Boolean> {
        private static final String TAG = "LoginValidator";

        private String userName;

        @Override
        protected Boolean doInBackground(String... params) {
            Log.d(TAG, "doInBackground");

            userName = params[0];

            DBHandler db = new DBHandler();

            return db.createUserAccount(params[0], params[1], params[2], params[3]);
        }

        @Override
        protected void onPreExecute() {
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(Boolean result) {
            Log.d(TAG, "Received for login: " + result);

            progressDialog.dismiss();

            if (result) {
                accountCreationSuccessful(userName);
            }
            else {
                createAlertDialog(getString(R.string.error), getString(R.string.account_creation_failed));
            }
        }
    }
}
