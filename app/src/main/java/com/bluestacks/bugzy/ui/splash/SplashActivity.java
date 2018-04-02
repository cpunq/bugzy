package com.bluestacks.bugzy.ui.splash;


import com.bluestacks.bugzy.R;
import com.bluestacks.bugzy.ui.BaseActivity;
import com.bluestacks.bugzy.ui.home.HomeActivity;
import com.bluestacks.bugzy.ui.login.LoginActivity;
import com.gauravbhola.ripplepulsebackground.RipplePulseLayout;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LayoutAnimationController;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SplashActivity extends BaseActivity {
    SplashViewModel mViewModel;
    @Inject
    ViewModelProvider.Factory mVmFactory;

    @BindView(R.id.layout_ripplepulse)
    RipplePulseLayout mRipplePulseLayout;

    @BindView(R.id.container_anim_items)
    LinearLayout mAnimItemsContainer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);

        mAnimItemsContainer.setLayoutAnimation(getListAnimationController(1000));
        mRipplePulseLayout.startRippleAnimation();
        mViewModel = ViewModelProviders.of(this, mVmFactory).get(SplashViewModel.class);
        mViewModel.splashDisplayed();
        subscribeToViewmodel();
    }

    public LayoutAnimationController getListAnimationController(long animationDuration) {
        AnimationSet set = new AnimationSet(true);
        Animation animation = new AlphaAnimation(0.0f, 1.0f);
        animation.setDuration(animationDuration);
        set.addAnimation(animation);

        animation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0.0f,Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.ABSOLUTE, 300.0f,Animation.ABSOLUTE, 0.0f
        );
        animation.setDuration(animationDuration);
        set.setInterpolator(new AccelerateDecelerateInterpolator());
        set.addAnimation(animation);

        LayoutAnimationController controller = new LayoutAnimationController(set, 0.2f);
        return controller;
    }

    public void subscribeToViewmodel() {
        mViewModel.getTokenLiveData().observe(this, token -> {
            if (TextUtils.isEmpty(token)) {
                startActivity(new Intent(this, LoginActivity.class));
            } else {
                startActivity(new Intent(this, HomeActivity.class));
            }
            finish();
        });
    }
}
