package com.bluestacks.bugzy.ui.casedetails;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.bluestacks.bugzy.BugzyApp;
import com.bluestacks.bugzy.common.Const;
import com.bluestacks.bugzy.ui.BaseActivity;
import com.bluestacks.bugzy.R;
import com.bluestacks.bugzy.data.model.Case;
import com.bluestacks.bugzy.ui.editcase.CaseEditActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CaseDetailsActivity extends BaseActivity implements CaseDetailsFragment.CaseDetailsFragmentContract {
    protected String mFogBugzId;

    @BindView(R.id.toolbar)
    protected Toolbar mToolbar;

    @BindView(R.id.fab)
    protected FloatingActionButton fab;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setAppliedTheme();
        setContentView(R.layout.activity_case_details);
        ButterKnife.bind(this);

        // Parse arguments
        Bundle extras = getIntent().getExtras();
        mFogBugzId = extras.getString("bug_id");
        Case aCase = (Case) extras.getSerializable("bug");

        setupToolbar();
        setupViews();

        if (savedInstanceState == null) {
            CaseDetailsFragment fragment = CaseDetailsFragment.getInstance(mFogBugzId, aCase);
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

    public void setupViews() {
        fab.setOnClickListener(viewRef -> {
            Intent i = new Intent(this, CaseEditActivity.class);
            i.putExtra(CaseEditActivity.PARAM_MODE, CaseEditActivity.MODE_EDIT);
            i.putExtra(CaseEditActivity.PARAM_CASE_ID, Integer.parseInt(mFogBugzId));
            startActivity(i);
        });
    }

    private void setupToolbar() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Case: " + mFogBugzId);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void openImageActivity(String imagePath) {
        Bundle arg = new Bundle();
        arg.putString("img_path", imagePath);
        Intent i  = new Intent(this, FullScreenImageActivity.class);
        i.putExtras(arg);
        this.startActivity(i);
    }
}
