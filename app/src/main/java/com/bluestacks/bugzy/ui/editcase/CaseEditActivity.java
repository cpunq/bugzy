package com.bluestacks.bugzy.ui.editcase;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.bluestacks.bugzy.R;
import com.bluestacks.bugzy.ui.BaseActivity;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CaseEditActivity extends BaseActivity {
    private CaseEditViewModel mCaseEditViewModel;
    @Inject
    ViewModelProvider.Factory mFactory;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.enter_slide_up, 0);
        setContentView(R.layout.activity_case_edit);
        ButterKnife.bind(this);
        setupViews();
        mCaseEditViewModel = ViewModelProviders.of(this, mFactory).get(CaseEditViewModel.class);
        subscribeToViewModel();
    }

    public void subscribeToViewModel() {
    }

    public void setupViews() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_black_24px);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // Emulate a back press
            this.onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        overridePendingTransition(R.anim.enter_slide_up, 0);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.exit_slide_down);
    }



}
