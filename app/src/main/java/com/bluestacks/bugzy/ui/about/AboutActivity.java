package com.bluestacks.bugzy.ui.about;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bluestacks.bugzy.BugzyApp;
import com.bluestacks.bugzy.BuildConfig;
import com.bluestacks.bugzy.R;
import com.bluestacks.bugzy.common.Const;
import com.bluestacks.bugzy.ui.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AboutActivity extends BaseActivity implements AppBarLayout.OnOffsetChangedListener {

    @BindView(R.id.appbar_layout)
    AppBarLayout mAppBarLayout;

    @BindView(R.id.imageView)
    ImageView mImageView;

    @BindView(R.id.tv_appname)
    TextView mAppNameView;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.tv_version)
    TextView mVersionTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setAppliedTheme();
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);
        setupToolbar();
        setupViews();

        if (savedInstanceState == null) {
            AboutActivityFragment fragment = AboutActivityFragment.newInstance();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container_frame, fragment);
            ft.commit();
        }
    }

    private void setAppliedTheme() {
        if(((BugzyApp)getApplication()).getAppliedTheme() == Const.DARK_THEME)  {
            setTheme(R.style.AppTheme_Dark);
        } else {
            // Light Theme
            setTheme(R.style.AppTheme);
        }
    }

    private void setupToolbar() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mAppBarLayout.addOnOffsetChangedListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        int maxScroll = appBarLayout.getTotalScrollRange();
        float percentage = (float) (maxScroll - Math.abs(verticalOffset)) / (float) (maxScroll);
        mImageView.setAlpha(percentage);
        mAppNameView.setAlpha(percentage);
    }

    private void setupViews(){
        mVersionTextView.setText("Version " + BuildConfig.VERSION_NAME);
    }
}
