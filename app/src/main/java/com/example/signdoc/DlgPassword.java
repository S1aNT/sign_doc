package com.example.signdoc;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.EditText;

import java.lang.ref.Reference;

/**
 * Created by andy on 11.12.14.
 */
public class DlgPassword extends DialogFragment {
    private EditText edPassword;
    private GetPassInterface eventPassword;

    public DlgPassword(GetPassInterface eventPass) {
        eventPassword = eventPass;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        builder.setView(inflater.inflate(R.layout.dlg_password, null))
                // Add action buttons
                .setPositiveButton(R.string.btn_ready, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        //
                        edPassword = (EditText) DlgPassword.this.getDialog().findViewById(R.id.edPassword);
                        String password = edPassword.getText().toString();
                        eventPassword.onPassword(password);
                    }
                })
                .setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        DlgPassword.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }
}