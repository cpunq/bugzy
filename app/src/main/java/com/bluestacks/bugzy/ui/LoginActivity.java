package com.bluestacks.bugzy.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.bluestacks.bugzy.BaseActivity;
import com.bluestacks.bugzy.HomeActivity_;
import com.bluestacks.bugzy.R;
import com.bluestacks.bugzy.common.Const;
import com.bluestacks.bugzy.models.resp.User;
import com.bluestacks.bugzy.net.FogbugzApiService;
import com.bluestacks.bugzy.utils.PrefsHelper;
import com.bluestacks.bugzy.utils.Utils;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.io.IOException;

import javax.inject.Inject;

import retrofit2.Call;

@EActivity
public class LoginActivity extends BaseActivity {

    @Inject PrefsHelper mPrefs;
    @Inject FogbugzApiService mApiClient;

    @ViewById(R.id.edittext_user_email)
    protected EditText mUserEmail;

    @ViewById(R.id.edittext_user_password)
    protected EditText mPassWord;

    @ViewById(R.id.login_button)
    protected Button mLoginButton;

    private Call<User> me;
    private String mAccessToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    @AfterViews
    protected void onViewsReady() {
        mAccessToken = getAccessToken();
        if(isLoggedIn()) {
            redirectHome();
        }
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
                    attemptLogin(mUserEmail.getText().toString(),mPassWord.getText().toString());
                }
            }
        });
    }

    @Background
    protected void attemptLogin(String email,String password) {

        if(TextUtils.isEmpty(mPrefs.getString(PrefsHelper.Key.ACCESS_TOKEN, ""))) {
            me =  mApiClient.loginWithEmail(email,password);
            try{
                String result = me.execute().body().getAuthToken();
                Log.d("Token : " , result);
                mPrefs.setString(PrefsHelper.Key.ACCESS_TOKEN, result);
                mPrefs.setBoolean(PrefsHelper.Key.USER_LOGGED_IN, true);
                mAccessToken = result;
                redirectHome();
            }
            catch (IOException e) {
                Log.d(Const.TAG,"Error logging in ");
            }
        }
    }

    private void redirectHome() {
        Intent mHome  = new Intent(LoginActivity.this, HomeActivity_.class);
        startActivity(mHome);
        this.finish();
    }

    private String getAccessToken() {
        return mPrefs.getString(PrefsHelper.Key.ACCESS_TOKEN, "");
    }

    private boolean isLoggedIn() {
        return !TextUtils.isEmpty(mAccessToken);
    }
}
