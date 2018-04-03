package com.bluestacks.bugzy.ui.login;


import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.UiThread;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;

import com.bluestacks.bugzy.data.model.Status;
import com.bluestacks.bugzy.ui.home.HomeActivity;
import com.bluestacks.bugzy.ui.BaseActivity;
import com.bluestacks.bugzy.R;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends BaseActivity {
    private LoginViewModel mLoginViewModel;
    private LoginPagerAdapter mPagerAdapter;

    @Inject
    ViewModelProvider.Factory mViewModelFactory;

    @BindView(R.id.view_pager)
    protected ViewPager mViewPager;

    @BindView(R.id.next_button)
    protected Button mNextButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        mLoginViewModel = ViewModelProviders.of(this, mViewModelFactory).get(LoginViewModel.class);
        setupViewPager();
        onViewsReady();
    }

    void setupViewPager() {
        mPagerAdapter = new LoginPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mPagerAdapter);
    }

    protected void onViewsReady() {
        mLoginViewModel.getSnackBarText().observe(this, s -> Snackbar.make(mViewPager, s, Snackbar.LENGTH_LONG).show());

        mNextButton.setOnClickListener(view -> mLoginViewModel.nextClicked());

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
            // Something to do?
        });

        mLoginViewModel.getLoginStepLiveData().observe(this, step -> {
            switch (step) {
                case ORG:
                    mViewPager.setCurrentItem(0);
                    break;
                case CREDENTIALS:
                    mViewPager.setCurrentItem(1);
                    break;
                case THEME:
                    mViewPager.setCurrentItem(2);
                    break;
                case INFO:
                    mViewPager.setCurrentItem(3);
                    break;
            }

        });
    }

    private void setInteractionEnabled(boolean set) {
        mNextButton.setEnabled(set);
    }

    @UiThread
    private void showMessage(String message) {
        // Send mNextButton as the view to find parent from
        showMessage(mNextButton, message);
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

    @Override
    public void onBackPressed() {
        if (mLoginViewModel.backPressed()) {
            return;
        }
        finish();
    }
}
