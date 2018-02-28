package com.bluestacks.bugzy.ui.login;

import com.google.gson.Gson;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.design.widget.Snackbar;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.bluestacks.bugzy.models.Resource;
import com.bluestacks.bugzy.models.Status;
import com.bluestacks.bugzy.ui.home.HomeActivity;
import com.bluestacks.bugzy.utils.AppExecutors;
import com.bluestacks.bugzy.ui.BaseActivity;
import com.bluestacks.bugzy.R;
import com.bluestacks.bugzy.models.Response;
import com.bluestacks.bugzy.models.resp.LoginData;
import com.bluestacks.bugzy.data.remote.FogbugzApiService;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends BaseActivity {

    @Inject FogbugzApiService mApiClient;
    @Inject AppExecutors mAppExecutors;
    @Inject Gson gson;
    @Inject
    ViewModelProvider.Factory mViewModelFactory;

    @BindView(R.id.edittext_user_email)
    protected EditText mUserEmail;

    @BindView(R.id.edittext_user_password)
    protected EditText mPassWord;

    @BindView(R.id.login_button)
    protected Button mLoginButton;
    private LoginViewModel mLoginViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        mLoginViewModel = ViewModelProviders.of(this, mViewModelFactory).get(LoginViewModel.class);

        onViewsReady();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    protected void onViewsReady() {
        mLoginViewModel.getSnackBarText().observe(this, s -> Snackbar.make(mUserEmail,"Please enter a correct email", Snackbar.LENGTH_LONG).show());
        mLoginButton.setOnClickListener(view -> mLoginViewModel.onLoginButtonClicked(mUserEmail.getText().toString(), mPassWord.getText().toString()));
        mLoginViewModel.getCredentialsLiveData().observe(this, stringStringPair -> {
        });
        mLoginViewModel.getLoginState().observe(this, responseResource -> {
            if (responseResource.status == Status.LOADING) {
                setInteractionEnabled(false);
                return;
            }
            setInteractionEnabled(true);
            if(responseResource.status == Status.ERROR) {
                // ViewModel should show error in this case may be?
                showMessage(responseResource.message);
                return;
            }
            if (responseResource.status == Status.SUCCESS) {
                // SUCCESS
                showMessage("Log in success");
            }
        });

        mLoginViewModel.getIsLoggedIn().observe(this, loggedIn -> {
            if (loggedIn) {
                // Jump to Homeactivity
                redirectHome();
            }
        });
    }

    private void setInteractionEnabled(boolean set) {
        mLoginButton.setEnabled(set);
        mPassWord.setEnabled(set);
        mUserEmail.setEnabled(set);
    }

    @UiThread
    private void showMessage(String message) {
        // Send mLoginButton as the view to find parent from
        showMessage(mLoginButton, message);
    }

    @UiThread
    private void showMessage(View v, String message) {
        Snackbar.make(v,message, Snackbar.LENGTH_LONG).show();
    }

    @UiThread
    private void redirectHome() {
        Intent mHome  = new Intent(this, HomeActivity.class);
        startActivity(mHome);
        this.finish();
    }
}
