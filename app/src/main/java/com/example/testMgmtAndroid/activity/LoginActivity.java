package com.example.testMgmtAndroid.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.testMgmtAndroid.R;
import com.example.testMgmtAndroid.helper.ApiCallHandler;
import com.example.testMgmtAndroid.helper.OnApiTaskCompleted;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

public class LoginActivity extends AppCompatActivity implements OnApiTaskCompleted {
    EditText email, Password;
    Button buttonlogIn;
    Context context;
    SharedPreferences preferences;
    private TextInputLayout inputLayoutEmail, inputLayoutPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        email = findViewById(R.id.input_email);
        Password = findViewById(R.id.input_password);
        buttonlogIn = findViewById(R.id.btn_login);
        email.addTextChangedListener(new MyTextWatcher(email));
        Password.addTextChangedListener(new MyTextWatcher(Password));
        inputLayoutEmail = findViewById(R.id.input_layout_email);
        inputLayoutPassword = findViewById(R.id.input_layout_password);
        context = this;
        buttonlogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateLogin();
            }
        });
    }
    private void validateLogin() {
        try {
            if (!validateEmail()) {
                return;
            }
            if (!validatePassword()) {
                return;
            }
            String email1 = email.getText().toString().trim();
            String password = Password.getText().toString().trim();
            JSONObject data = new JSONObject();
            data.put("firstName", "Parent");
            data.put("lastName", email1);
            data.put("emailId", password);
            data.put("password", password);
            data.put("emailId\": \"rathodswapnil9@gmail.com\",\n" +
                    "    \"phone", "eYlhftdSQB6vN1U5nz6-Sa:APA91bHHi-Now3-0xqDX7PRM7J8dlkvII_v-dsD_xkIqkmQS6YDneJ003GvaO5terwtjGLKrxlgsjPVGpDO9pSV68_sb2hfygg-RjtJ_8oDnRRt6UQM_GXaKL_qwz_yMHTMod7Y0quzA");

            Log.e("afterReceivingData", "Postdata ==== " + data);
            ApiCallHandler loginHandler = new ApiCallHandler("POST",
                    "http://localhost:8888/apiDev/add-customer", context,
                    1);
            loginHandler.execute(data.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setPreference(JSONObject response) throws JSONException {
        SharedPreferences preferences = getSharedPreferences("user_data", MODE_PRIVATE);
        SharedPreferences.Editor edit = preferences.edit();
        Iterator<?> keys = response.keys();
        while (keys.hasNext()) {
            String key = (String) keys.next();
            if (!new String("success").equals(key)

                    && !new String("responseCode").equals(key)

                    && !new String("statusMsg").equals(key)) {
                edit.putString(key, response.getString(key));
            }
        }
        String email1 = email.getText().toString().trim();
        String password = Password.getText().toString().trim();
        edit.putString("loggedInUserType", "Parent");
        edit.putString("loggedInUserName", email1);
        edit.putString("loggedInPassword", password);
        edit.putString("loggedInEmployeId", "clientId");
        edit.commit();//saving to disk
        Log.e("afterReceivingData"," responce ==== " + response);

    }




    @Override
    public void afterReceivingData(int callback, JSONObject response) {
        try {

            if (response.optString("success").equalsIgnoreCase("true")) {
                if (callback == 1) {
                    this.setPreference(response);
                    this.gotoUpcoming();
                }
            } else {
                Toast.makeText(context, "Enter Valid Details", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void gotoUpcoming() {
        Intent dintent = new Intent(context, MainActivity.class);
        Intent intent = getIntent();
        startActivity(dintent);
        finish();
    }

    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.input_email:
                    validateEmail();

                    break;
                case R.id.input_password:
                    validatePassword();
                    break;
            }
        }
    }

    private boolean validatePassword() {
        if (Password.getText().toString().trim().isEmpty()) {
            inputLayoutPassword.setError(getString(R.string.err_msg_password));
            requestFocus(Password);
            return false;
        } else {
            inputLayoutPassword.setErrorEnabled(false);
        }

        return true;
    }

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private boolean validateEmail() {
        String email1 = email.getText().toString().trim();
        if (email1.isEmpty() || !isValidEmail(email1)) {
            inputLayoutEmail.setError(getString(R.string.err_msg_email));
            requestFocus(email);
            return false;
        } else {
            inputLayoutEmail.setErrorEnabled(false);

        }

        return true;
    }

}