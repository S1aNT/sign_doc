package com.example.signdoc;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;

public class RegisterSign extends FragmentActivity implements OnClickListener, GetPassInterface {
    static final String PREF_ENC_PRIVATE_KEY = MainActivity.PREF_ENC_PRIVATE_KEY;
    static final String PREF_PUBLIC_KEY = MainActivity.PREF_PUBLIC_KEY;
    static final String RSA_KEYS_TAG = "RSA";
    static final String AES_KEYS_TAG = "AES";
    static final String AES_HASH_TAG = "SHA1PRNG";

    private static Settings settings;

    private EditText edSite;
    private EditText edCode;
    private Button btnReady;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.register_sign);

        settings = Settings.getInstance();

        edSite = (EditText) findViewById(R.id.edSite);
        edCode = (EditText) findViewById(R.id.edCode);
        btnReady = (Button) findViewById(R.id.btnReady); btnReady.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        // Check input fields
        String errors = "";
        String sep = "";
        if (edSite.getText().toString().trim().isEmpty()) {
            errors = getString(R.string.err_site_required);
            sep = "\n";
        };
        if (edCode.getText().toString().trim().isEmpty()) {
            errors += sep + getString(R.string.err_code_required);
        };

        if (!errors.isEmpty()) {
            alert(errors);
        } else {
            DialogFragment dlgPassword = new DlgPassword(this);
            dlgPassword.show(getSupportFragmentManager(), "missiles");
        };
    }

    public void onPassword(String password) {
        // Формируем документ для регистрации подписи
        DocSignRegistration doc = new DocSignRegistration();
        doc.site = edSite.getText().toString().trim();
        doc.code = edCode.getText().toString().trim();
        doc.public_key = settings.get(PREF_PUBLIC_KEY);

        // Расшифровываем приватный ключ
        Sign sign = new Sign(password);

        // Формируем ЭЦП документа
        String sign_data = doc.code;
        byte[] b_sign = sign.create(sign_data.getBytes());

        if (b_sign == null) {
            alert(getString(R.string.err_wrong_password));
        } else {
            doc.sign = Base64.encodeToString(b_sign, Base64.NO_WRAP);
        };

        // Ставим документ в очередь на отправку
        Log.d("SIGN", "Sign: "+doc.sign);









    }

    private void alert(String text) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.title_error)
                .setMessage(text)
                .setCancelable(false)
                .setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
}
