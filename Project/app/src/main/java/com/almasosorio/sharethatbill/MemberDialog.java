package com.almasosorio.sharethatbill;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.EditText;

public class MemberDialog extends DialogFragment {
    public static final String EXTRA_ENTRY_EMAIL = "ENTRY_EMAIL";

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
        if (!defaultText.isEmpty()) {
            textEdit.setText(defaultText);
            textEdit.setSelection(defaultText.length());
            textEdit.setSingleLine();
        }
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
                                    i.putExtra(EXTRA_ENTRY_EMAIL, textEdit.getText().toString());
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