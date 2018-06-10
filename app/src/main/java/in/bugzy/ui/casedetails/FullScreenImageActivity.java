package in.bugzy.ui.casedetails;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import in.bugzy.ui.BaseActivity;
import in.bugzy.R;
import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FullScreenImageActivity extends BaseActivity {
    @BindView(R.id.full_image)
    protected PhotoView photoView;

    @BindView(R.id.toolbar)
    protected Toolbar mToolbar;

    private String mImagePath;
    private Animation mSlideUp;
    private Animation mSlideDown;
    private boolean mToolbarVisible = true;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen_image);
        ButterKnife.bind(this);
        setupToolbar();

        mSlideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up);
        mSlideDown = AnimationUtils.loadAnimation(this, R.anim.slide_down);

        mImagePath = getIntent().getExtras().getString("img_path");
        Glide.with(getApplicationContext())
                .load(mImagePath)
                .into(photoView);

        mSlideUp.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                getSupportActionBar().hide();

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        photoView.setOnPhotoTapListener((view, x, y) -> toggleToolbarVisibility());
    }

    private void setupToolbar() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void toggleToolbarVisibility() {
        this.mToolbarVisible = !this.mToolbarVisible;
        if (mToolbarVisible) {
            mToolbar.startAnimation(mSlideDown);
            getSupportActionBar().show();
        } else {
            mToolbar.startAnimation(mSlideUp);
        }
    }
}
