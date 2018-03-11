package com.bluestacks.bugzy.ui.search;

import com.bluestacks.bugzy.R;
import com.bluestacks.bugzy.ui.BaseActivity;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.arch.persistence.room.Insert;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;


public class SearchActivity extends BaseActivity {
    private SearchActivityViewModel mViewModel;
    @Inject
    ViewModelProvider.Factory mViewModelFactory;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.et_search)
    EditText mSearchEditText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);
        setupViews();
        mViewModel = ViewModelProviders.of(this, mViewModelFactory).get(SearchActivityViewModel.class);
        subscribeToViewModel();
    }

    private void setupViews() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mSearchEditText.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        mSearchEditText.setRawInputType(InputType.TYPE_CLASS_TEXT);
        mSearchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    return true;
                }
                return false;
            }
        });
    }

    private void subscribeToViewModel() {
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    SearchActivity.this.onBackPressed();
                }
            }, 200);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
