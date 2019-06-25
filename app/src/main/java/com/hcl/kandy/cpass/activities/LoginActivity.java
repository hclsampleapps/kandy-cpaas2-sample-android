package com.hcl.kandy.cpass.activities;

import android.Manifest;
import android.content.Intent;
import android.content.PeriodicSync;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.hcl.kandy.cpass.R;
import com.hcl.kandy.cpass.remote.RestApiClient;
import com.hcl.kandy.cpass.remote.RestApiInterface;
import com.hcl.kandy.cpass.remote.models.LoginResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by Ashish Goel on 2/1/2019.
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private RestApiInterface mRestApiInterface;
    private TextView mEtUserName;
    private TextView mEtUserPassword;
    private TextView mEtClient;
    private EditText mBaseUrl;
    public static String access_token = "access_token";
    public static String id_token = "id_token";
    public static String base_url = "base_url";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.button_login).setOnClickListener(this);
        mEtUserName = findViewById(R.id.et_user_name);
        mEtUserPassword = findViewById(R.id.et_user_password);
        mEtClient = findViewById(R.id.et_user_client);
        mBaseUrl = findViewById(R.id.et_url);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("cpass", "onstart mainactivity");
    }

    private boolean validate() {
        if (TextUtils.isEmpty(mBaseUrl.getText().toString()))
            return false;
        else if (TextUtils.isEmpty(mEtUserName.getText().toString()))
            return false;
        else if (TextUtils.isEmpty(mEtUserPassword.getText().toString()))
            return false;
        else if (TextUtils.isEmpty(mEtClient.getText().toString()))
            return false;
        else
            return true;
    }

    private void OnLoginClick() {

        if (!checkPermission()) {
            getPerMission();
            return;
        }


        if (!validate()) {
            Toast.makeText(LoginActivity.this, "All fields are mandatory", Toast.LENGTH_SHORT).show();
            return;

        }

        Retrofit client = RestApiClient.getClient("https://" + mBaseUrl.getText().toString());
        if (client == null) {
            Toast.makeText(LoginActivity.this, "Please enter correct Fields", Toast.LENGTH_SHORT).show();
            return;
        }
        mRestApiInterface = client.create(RestApiInterface.class);
        if (mRestApiInterface == null) {
            Toast.makeText(LoginActivity.this, "Please enter correct Fields", Toast.LENGTH_SHORT).show();
            return;
        }
        showProgressBar("Login..");
        Call<LoginResponse> responseCall = mRestApiInterface.loginAPI(
                mEtUserName.getEditableText().toString(),
                mEtUserPassword.getEditableText().toString(),
                mEtClient.getEditableText().toString(),
                "password",
                "openid");

        responseCall.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(@NonNull Call<LoginResponse> call, @NonNull Response<LoginResponse> response) {
                LoginResponse body = response.body();

                if (body != null) {
                    Toast.makeText(LoginActivity.this, "Login Success", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                    intent.putExtra(access_token, body.getAccessToken());
                    intent.putExtra(id_token, body.getIdToken());
                    intent.putExtra(base_url, mBaseUrl.getText().toString());

                    if (!isFinishing()) {
                        hideProgressBAr();
                        startActivity(intent);
                        finish();
                    } else {
                        hideProgressBAr();
                        Log.d("HCL", "login failed");
                        Toast.makeText(LoginActivity.this, "Try again..", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    hideProgressBAr();
                    Log.d("HCL", "login failed");
                    Toast.makeText(LoginActivity.this, "Try again..", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<LoginResponse> call, @NonNull Throwable t) {
                call.cancel();
                if (!isFinishing()) {
                    hideProgressBAr();
                    Log.d("HCL", "login failed");
                    Toast.makeText(LoginActivity.this, "Try again..", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_login:
                OnLoginClick();
                break;
        }
    }
 
    String[] PERMISSIONS = {
            android.Manifest.permission.MODIFY_AUDIO_SETTINGS,
            android.Manifest.permission.RECORD_AUDIO,
            android.Manifest.permission.CAMERA
    };

    private boolean checkPermission() {
        for (String permission : PERMISSIONS) {
            if (ActivityCompat.checkSelfPermission(LoginActivity.this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    int PERMISSION_ALL = 1;

    private void getPerMission() {

        if (!checkPermission()) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_ALL) {
            if (grantResults.length > 0) {
                String permissionsDenied = "";
                for (String per : PERMISSIONS) {
                    if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                        permissionsDenied += "\n" + per;
                    }
                }
            }
        }
    }
}
