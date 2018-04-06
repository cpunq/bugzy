package com.bluestacks.bugzy.ui.search;

import com.bluestacks.bugzy.BugzyApp;
import com.bluestacks.bugzy.R;
import com.bluestacks.bugzy.common.Const;
import com.bluestacks.bugzy.data.model.Case;
import com.bluestacks.bugzy.data.model.RecentSearch;
import com.bluestacks.bugzy.data.model.Resource;
import com.bluestacks.bugzy.data.model.SearchSuggestion;
import com.bluestacks.bugzy.data.model.Status;
import com.bluestacks.bugzy.data.remote.model.ListCasesData;
import com.bluestacks.bugzy.ui.BaseActivity;
import com.bluestacks.bugzy.ui.casedetails.CaseDetailsActivity;
import com.bluestacks.bugzy.ui.common.CaseAdapter;
import com.bluestacks.bugzy.ui.common.ErrorView;
import com.bluestacks.bugzy.utils.OnItemClickListener;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
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
    public static final String TAG = SearchSuggestion.class.getName();
    private SearchActivityViewModel mViewModel;
    private CaseAdapter mAdapter;
    private SearchSuggestionAdapter mSearchSuggestionAdapter;
    private List<Case> mCases;
    private List<SearchSuggestion> mSearchSuggestions;
    private List<RecentSearch> mRecentSearches;

    private SearchHistoryAdapter mSearchHistoryAdapter;
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

    @BindView(R.id.searchRecyclerView)
    RecyclerView mSearchSuggestionView;

    @BindView(R.id.search_history_recycler_view)
    RecyclerView mSearchHistoryRecyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setAppliedTheme();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);
        mViewModel = ViewModelProviders.of(this, mViewModelFactory).get(SearchActivityViewModel.class);

        setupViews();
        subscribeToViewModel();
        mSearchEditText.clearFocus();
        // Triggers the search change event, which inturn triggers the search history
        mSearchEditText.setText("");
    }

    private void setAppliedTheme() {
        if(((BugzyApp)getApplication()).getAppliedTheme() == Const.DARK_THEME)  {
            setTheme(R.style.SearchActivityTheme_Dark);
        } else {
            // Light Theme
            setTheme(R.style.SearchActivityTheme);
        }
    }


    private void setupViews() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        prepareSearchEditText();

        mSearchSuggestionAdapter = new SearchSuggestionAdapter(position -> {
            if (mSearchSuggestions == null) {
                return;
            }
            mViewModel.searchSuggestionSelected(mSearchSuggestions.get(position));
        });
        mSearchSuggestionView.setLayoutManager(new LinearLayoutManager(this));
        mSearchSuggestionView.setAdapter(mSearchSuggestionAdapter);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL));
        mAdapter = new CaseAdapter(mCases, this);
        mRecyclerView.setAdapter(mAdapter);
        prepareSearchHistoryRecyclerView();
    }

    private void prepareSearchHistoryRecyclerView() {
        mSearchHistoryAdapter = new SearchHistoryAdapter(position -> {
            if (mRecentSearches == null) {
                return;
            }
            mSearchEditText.setText(mRecentSearches.get(position).getText());
            mSearchEditText.setSelection(mSearchEditText.getText().length());   // Important, as setText doesn't shift the cursor
            mSearchSuggestionView.setVisibility(View.GONE);    // Important, as setting text will trigger searchChange which will show the searchSuggestions
            mViewModel.search(mRecentSearches.get(position).getText());     // This does the actual search
            dismissKeyboard(mSearchEditText.getWindowToken());
        });
        mSearchHistoryRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mSearchHistoryRecyclerView.setAdapter(mSearchHistoryAdapter);
    }

    private void prepareSearchEditText() {
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
                    // Hide suggestions
                    mSearchSuggestionView.setVisibility(View.GONE);
                    mViewModel.search(v.getText().toString());
                    dismissKeyboard(v.getWindowToken());
                    return true;
                }
                return false;
            }
        });

        mSearchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Show suggestions
                mSearchSuggestionView.setVisibility(View.VISIBLE);
                mViewModel.searchTextChanged(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
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
            Resource<ListCasesData> resource = searchResource.getResource();
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

        mViewModel.getSearchSuggestions().observe(this, list -> {
            // Note: list can be null
            mSearchSuggestions = list;
            mSearchSuggestionAdapter.setData(mSearchSuggestions);
            mSearchSuggestionAdapter.notifyDataSetChanged();
        });

        mViewModel.getSearchChangeEvent().observe(this, updateQuery -> {
            mSearchEditText.setText(updateQuery);
            mSearchEditText.setSelection(mSearchEditText.getText().length());

            // Hide suggestions
            mSearchSuggestionView.setVisibility(View.GONE);
        });

        mViewModel.getRecentSearches().observe(this, searches -> {
            if (searches == null || searches.size() == 0) {
                mSearchHistoryRecyclerView.setVisibility(View.GONE);
            } else {
                mSearchHistoryRecyclerView.setVisibility(View.VISIBLE);
                mRecentSearches = searches;
                mSearchHistoryAdapter.setData(mRecentSearches);
                mSearchHistoryAdapter.notifyDataSetChanged();
            }
        });
    }

    private Spannable getHeaderText(ListCasesData casesData) {
        String casesCountString = casesData.getCount() +" cases";
        String finalString = "Showing " + casesCountString +" of " + casesData.getTotalHits() + " total";
        Spannable spannable = new SpannableString(finalString);

        TypedArray a = getTheme().obtainStyledAttributes(new int[]{R.attr.headerTextColor});
        int color = a.getColor(0, 0);

        spannable.setSpan(new ForegroundColorSpan(color),
                finalString.indexOf(casesCountString),
                finalString.indexOf(casesCountString) + casesCountString.length() ,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new RelativeSizeSpan(1.2f),
                finalString.indexOf(casesCountString),
                finalString.indexOf(casesCountString) + casesCountString.length() ,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannable;
    }

    @UiThread
    protected void showCases(ListCasesData caseData) {
        List<Case> cases = caseData.getCases();
        mAdapter.setHeaderText(getHeaderText(caseData));

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
