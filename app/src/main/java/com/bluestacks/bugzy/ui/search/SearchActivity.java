package com.bluestacks.bugzy.ui.search;

import com.bluestacks.bugzy.R;
import com.bluestacks.bugzy.data.model.Case;
import com.bluestacks.bugzy.data.model.Resource;
import com.bluestacks.bugzy.data.model.Status;
import com.bluestacks.bugzy.ui.BaseActivity;
import com.bluestacks.bugzy.ui.casedetails.CaseDetailsActivity;
import com.bluestacks.bugzy.ui.common.CaseAdapter;
import com.bluestacks.bugzy.ui.common.ErrorView;
import com.bluestacks.bugzy.utils.OnItemClickListener;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;


public class SearchActivity extends BaseActivity implements OnItemClickListener {
    private SearchActivityViewModel mViewModel;
    private CaseAdapter mAdapter;
    private List<Case> mCases;
    @Inject
    ViewModelProvider.Factory mViewModelFactory;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.et_search)
    EditText mSearchEditText;

    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;

    @BindView(R.id.viewError)
    ErrorView mErrorView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);
        mViewModel = ViewModelProviders.of(this, mViewModelFactory).get(SearchActivityViewModel.class);

        setupViews();
        subscribeToViewModel();
        mSearchEditText.clearFocus();
    }

    private void setupViews() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mSearchEditText.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        mSearchEditText.setRawInputType(InputType.TYPE_CLASS_TEXT);
        mSearchEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    mSearchEditText.post(new Runnable() {
                        @Override
                        public void run() {
                            SearchActivity.this.showKeyboard(mSearchEditText);
                        }
                    });
                }
            }
        });

        mSearchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    mViewModel.search(v.getText().toString());
                    dismissKeyboard(v.getWindowToken());
                    return true;
                }
                return false;
            }
        });

        // Initialize adapter and recyclerView
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL));
        mAdapter = new CaseAdapter(mCases, this);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onItemClick(int position) {
        Case cas = mCases.get(position);
        Intent i = new Intent(this, CaseDetailsActivity.class);
        Bundle arg = new Bundle();
        arg.putString("bug_id", String.valueOf(cas.getIxBug()));
        arg.putSerializable("bug", cas);
        i.putExtras(arg);
        this.startActivity(i);
    }

    private void dismissKeyboard(IBinder windowToken) {
        InputMethodManager imm = (InputMethodManager) this.getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(windowToken, 0);
    }

    private void showKeyboard(View v) {
        InputMethodManager imm = (InputMethodManager) this.getSystemService(INPUT_METHOD_SERVICE);
        imm.showSoftInput(v, InputMethodManager.SHOW_IMPLICIT);
    }

    private void subscribeToViewModel() {
        mViewModel.getSearchResponse().observe(this, searchResource -> {
            Resource<List<Case>> resource = searchResource.getResource();
            if (resource.status == Status.ERROR) {
                showError(resource.message);
                return;
            }
            if (resource.status == Status.LOADING) {
                showLoading();
                return;
            }
            if (resource.status == Status.SUCCESS) {
                showCases(resource.data);
                return;
            }
        });

        mViewModel.getClearSearchEvent().observe(this, v -> {
            mSearchEditText.setText("");
            mCases = null;
            mAdapter.setData(mCases);
            mAdapter.notifyDataSetChanged();
            showContent();

            mSearchEditText.requestFocus();
            showKeyboard(mSearchEditText);
        });
    }

    @UiThread
    protected void showCases(List<Case> cases) {
        if (cases.size() == 0) {
            mCases = null;
            mErrorView.showMessage("No cases found");
            return;
        }
        mCases = cases;
        showContent();
        mAdapter.setData(mCases);
        mAdapter.notifyDataSetChanged();
    }

    @UiThread
    protected void showLoading() {
        mCases = null;
        mRecyclerView.setVisibility(View.GONE);
        mErrorView.showProgress("Searching ...");
    }

    @UiThread
    protected void showContent() {
        mRecyclerView.setVisibility(View.VISIBLE);
        mErrorView.hide();
    }

    @UiThread
    private void showError(String message) {
        mCases = null;
        mRecyclerView.setVisibility(View.GONE);
        mErrorView.showError(message);
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
            SearchActivity.this.onBackPressed();
            return true;
        }
        if (item.getItemId() == R.id.action_clear) {
            mViewModel.clearSearch();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        dismissKeyboard(mSearchEditText.getWindowToken());
        super.onBackPressed();
    }
}
