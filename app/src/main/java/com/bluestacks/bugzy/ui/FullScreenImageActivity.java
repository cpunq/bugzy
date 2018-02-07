package com.bluestacks.bugzy.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.bluestacks.bugzy.BaseActivity;
import com.bluestacks.bugzy.R;
import com.bumptech.glide.Glide;
import com.jsibbold.zoomage.ZoomageView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FullScreenImageActivity extends BaseActivity {

    @BindView(R.id.full_image)
    protected ZoomageView mFullImage;

    @BindView(R.id.toolbar)
    protected Toolbar mToolbar;

    private String mImagePath;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen_image);
        ButterKnife.bind(this);
        setupToolbar();

        mImagePath = getIntent().getExtras().getString("img_path");
        Glide.with(getApplicationContext())
                .load(mImagePath)
                .into(mFullImage);
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


}
