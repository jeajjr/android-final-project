package com.almasosorio.sharethatbill;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentLogin extends Fragment {
    private static final String TAG = "FragmentLogin";

    public FragmentLogin() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_login, container, false);

        final EditText userName = (EditText) v.findViewById(R.id.email);
        final EditText password = (EditText) v.findViewById(R.id.password);

        Button loginButton = (Button) v.findViewById(R.id.button_login);
        loginButton.setText(loginButton.getText().toString().toUpperCase());
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "clicked loginButton");

                final String userNameString = userName.getText().toString();
                final String passwordString = password.getText().toString();

                (new LoginValidator()).execute(userNameString, passwordString);
            }
        });

        Button createAccountButton = (Button) v.findViewById(R.id.button_create_account);
        createAccountButton.setText(createAccountButton.getText().toString().toUpperCase());
        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "clicked createAccountButton");

                getActivity().getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.abc_fade_in, R.anim.abc_fade_out,
                                R.anim.abc_fade_in, R.anim.abc_fade_out)
                        .replace(R.id.container, new FragmentCreateAccount())
                        .addToBackStack(null)
                        .commit();
            }
        });
        return v;
    }

    protected void loginSuccessful() {
        Log.d(TAG, "loginSuccessful");

        
    }

    protected void loginFailed() {
        Log.d(TAG, "loginFailed");

        new AlertDialog.Builder(getActivity())
                .setTitle(getActivity().getString(R.string.login_failed))
                .setMessage(getActivity().getString(R.string.login_failed_message))
                .setPositiveButton(android.R.string.ok, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private class LoginValidator extends AsyncTask<String, Void, Boolean> {
        private static final String TAG = "LoginValidator";

        @Override
        protected Boolean doInBackground(String... params) {
            Log.d(TAG, "doInBackground");

            Log.d(TAG, "Login received: " + params[0] + ", " + params[1]);

            return (Boolean) (new DBHandler()).checkLogin(params[0], params[1]);
        }

        @Override
        protected void onPostExecute(Boolean result) {
            Log.d(TAG, "Received for login: " + result);

            if (result)
                loginSuccessful();
            else
                loginFailed();
        }
    }
}
