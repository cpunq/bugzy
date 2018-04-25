package in.bugzy.ui.login;


import android.app.Application;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.UiThread;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;

import in.bugzy.BugzyApp;
import in.bugzy.common.Const;
import in.bugzy.data.model.Status;
import in.bugzy.ui.home.HomeActivity;
import in.bugzy.ui.BaseActivity;
import in.bugzy.R;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends BaseActivity {
    private LoginViewModel mLoginViewModel;
    private LoginPagerAdapter mPagerAdapter;

    @Inject
    Application mApp;

    @Inject
    ViewModelProvider.Factory mViewModelFactory;

    @BindView(R.id.view_pager)
    protected ViewPager mViewPager;

    @BindView(R.id.next_button)
    protected Button mNextButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setAppliedTheme();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        mLoginViewModel = ViewModelProviders.of(this, mViewModelFactory).get(LoginViewModel.class);
        setupViewPager();
        onViewsReady();
    }

    private void setAppliedTheme() {
        if(((BugzyApp)getApplication()).getAppliedTheme() == Const.DARK_THEME)  {
            setTheme(R.style.LoginTheme_Dark);
        } else {
            // Light Theme
            setTheme(R.style.LoginTheme);
        }
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

        mLoginViewModel.getNextButtonText().observe(this, nextButtonText -> {
            mNextButton.setText(nextButtonText);
        });

        mLoginViewModel.getHomeScreenCommand().observe(this, v -> {
            redirectToHome();
        });

        mLoginViewModel.getChangeThemeCommand().observe(this, v -> {
            recreate();
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
    private void redirectToHome() {
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
