package in.bugzy.ui.splash;


import in.bugzy.BugzyApp;
import in.bugzy.R;
import in.bugzy.common.Const;
import in.bugzy.ui.BaseActivity;
import in.bugzy.ui.home.HomeActivity;
import in.bugzy.ui.login.LoginActivity;
import com.gauravbhola.ripplepulsebackground.RipplePulseLayout;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LayoutAnimationController;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SplashActivity extends BaseActivity {
    SplashViewModel mViewModel;
    Handler mHandler;
    @Inject
    ViewModelProvider.Factory mVmFactory;

    @BindView(R.id.layout_ripplepulse)
    RipplePulseLayout mRipplePulseLayout;

    @BindView(R.id.iv_bug)
    ImageView mBugView;

    @BindView(R.id.tv_appname)
    TextView mAppNameView;

    @BindView(R.id.container_anim_items)
    LinearLayout mAnimItemsContainer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setAppliedTheme();
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);
        mHandler = new Handler();

        mAnimItemsContainer.setLayoutAnimation(getListAnimationController(1000));
        mRipplePulseLayout.startRippleAnimation();
        mViewModel = ViewModelProviders.of(this, mVmFactory).get(SplashViewModel.class);
        mViewModel.splashDisplayed();
        subscribeToViewmodel();
    }

    private void setAppliedTheme() {
        if(((BugzyApp)getApplication()).getAppliedTheme() == Const.DARK_THEME)  {
            setTheme(R.style.LoginTheme_Dark);
        } else {
            // Light Theme
            setTheme(R.style.LoginTheme);
        }
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
                startLoginActivity();
            } else {
//                startLoginActivity();
                startHomeActivity();
            }
            finishDelayed();
        });

    }


    void finishDelayed() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 1000);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }


    public void startLoginActivity() {
        Pair<View, String> bug = new Pair<>(mBugView, "bug");
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, bug);
        Intent intent = new Intent(this, LoginActivity.class);
        ActivityCompat.startActivity(this, intent, options.toBundle());
    }

    public void startHomeActivity() {
        AnimatorSet set = new AnimatorSet();
        Animator a1 = ObjectAnimator.ofFloat(mAppNameView, "alpha", 1f, 0f);
        Animator a2 = ObjectAnimator.ofFloat(mBugView, "alpha", 1f, 0f);
        Animator a3 = ObjectAnimator.ofFloat(mRipplePulseLayout, "alpha", 1f, 0f);
        set.setDuration(200);

        set.playTogether(a1, a2, a3);
        set.start();

        set.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                startActivity(new Intent(SplashActivity.this, HomeActivity.class));
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });

    }

}
