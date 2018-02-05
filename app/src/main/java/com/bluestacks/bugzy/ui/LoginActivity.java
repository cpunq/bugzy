package com.bluestacks.bugzy.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.UiThread;
import android.support.annotation.WorkerThread;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.bluestacks.bugzy.utils.AppExecutors;
import com.bluestacks.bugzy.BaseActivity;
import com.bluestacks.bugzy.R;
import com.bluestacks.bugzy.common.Const;
import com.bluestacks.bugzy.models.Response;
import com.bluestacks.bugzy.models.resp.LoginData;
import com.bluestacks.bugzy.models.resp.LoginRequest;
import com.bluestacks.bugzy.net.FogbugzApiService;
import com.bluestacks.bugzy.utils.PrefsHelper;
import com.bluestacks.bugzy.utils.Utils;

import java.io.IOException;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;

public class LoginActivity extends BaseActivity {

    @Inject PrefsHelper mPrefs;
    @Inject FogbugzApiService mApiClient;
    @Inject AppExecutors mAppExecutors;

    @BindView(R.id.edittext_user_email)
    protected EditText mUserEmail;

    @BindView(R.id.edittext_user_password)
    protected EditText mPassWord;

    @BindView(R.id.login_button)
    protected Button mLoginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        onViewsReady();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(isLoggedIn()) {
            redirectHome();
        }
    }

    protected void onViewsReady() {
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!Utils.isValidInput(mUserEmail.getText().toString())) {
                    Snackbar.make(mUserEmail,"Please enter a correct email", Snackbar.LENGTH_LONG).show();
                    return;
                }
                else if(!Utils.isValidPassword(mPassWord.getText().toString())) {
                    Snackbar.make(mPassWord,"Invalid Password",Snackbar.LENGTH_LONG).show();
                }
                else {
                    setInteractionEnabled(false);
                    mAppExecutors.networkIO().execute(new Runnable() {
                        @Override
                        public void run() {
                            attemptLogin(mUserEmail.getText().toString(),mPassWord.getText().toString());
                        }
                    });
                }
            }
        });
    }

    private void setInteractionEnabled(boolean set) {
        mLoginButton.setEnabled(set);
        mPassWord.setEnabled(set);
        mUserEmail.setEnabled(set);
    }

    @WorkerThread
    protected void attemptLogin(String email,String password) {
        if(TextUtils.isEmpty(mPrefs.getString(PrefsHelper.Key.ACCESS_TOKEN, ""))) {
            Call<Response<LoginData>> response = mApiClient.loginWithEmail(new LoginRequest(email, password));
            try{
                String token = response.execute().body().getData().getToken();
                Log.d("Token : " , token);
                mPrefs.setString(PrefsHelper.Key.ACCESS_TOKEN, token);
                mPrefs.setBoolean(PrefsHelper.Key.USER_LOGGED_IN, true);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        redirectHome();
                    }
                });
            }
            catch (IOException e) {
                Log.d(Const.TAG,"Error logging in ");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setInteractionEnabled(true);
                    }
                });
            }
        }
    }


    @UiThread
    private void redirectHome() {
        Intent mHome  = new Intent(LoginActivity.this, HomeActivity.class);
        startActivity(mHome);
        this.finish();
    }

    private String getAccessToken() {
        return mPrefs.getString(PrefsHelper.Key.ACCESS_TOKEN, "");
    }

    private boolean isLoggedIn() {
        return !TextUtils.isEmpty(getAccessToken());
    }
}
