package com.bluestacks.bugzy.ui.home;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.DimenRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupMenu;

import com.bluestacks.bugzy.data.model.Status;
import com.bluestacks.bugzy.ui.common.AppliedSortAdapter;
import com.bluestacks.bugzy.ui.common.CaseAdapter;
import com.bluestacks.bugzy.ui.common.ErrorView;
import com.bluestacks.bugzy.ui.common.Injectable;
import com.bluestacks.bugzy.ui.common.HomeActivityCallbacks;
import com.bluestacks.bugzy.R;
import com.bluestacks.bugzy.data.model.Case;
import com.bluestacks.bugzy.utils.OnItemClickListener;
import com.xiaofeng.flowlayoutmanager.FlowLayoutManager;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MyCasesFragment extends Fragment implements Injectable, OnItemClickListener {
    private static final String TAG = MyCasesFragment.class.getName();
    private static final String PARAM_FILTER = "filter";
    private static final String PARAM_FILTER_TEXT = "filter_text";
    private MyCasesViewModel mViewModel;
    private List<Case> mCases;
    private String mFilter;
    private String mFilterText;
    private HomeActivityCallbacks mHomeActivityCallbacks;
    private CaseAdapter mAdapter;
    private AppliedSortAdapter mAppliedSortingsAdapter;

    @Inject
    protected ViewModelProvider.Factory mViewModelFactory;

    @BindView(R.id.recyclerView)
    protected RecyclerView mRecyclerView;

    @BindView(R.id.viewError)
    protected ErrorView mErrorView;

    @BindView(R.id.containerSorting)
    protected LinearLayout mSortingContainer;

    @BindView(R.id.recyclerViewSorting)
    protected RecyclerView mSortingRecyclerView;

    public static MyCasesFragment getInstance(String filter, String filterText) {
        MyCasesFragment fragment = new MyCasesFragment();
        Bundle args = new Bundle();
        args.putString(PARAM_FILTER, filter);
        args.putString(PARAM_FILTER_TEXT, filterText);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFilter = getArguments().getString(PARAM_FILTER);
        mFilterText = getArguments().getString(PARAM_FILTER_TEXT);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof HomeActivityCallbacks) {
            mHomeActivityCallbacks = (HomeActivityCallbacks) context;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_mycases, null);
        ButterKnife.bind(this, v);
        return v;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setupSortingView();
        mViewModel = ViewModelProviders.of(this, mViewModelFactory).get(MyCasesViewModel.class);

        if (mHomeActivityCallbacks != null) {
            mHomeActivityCallbacks.onFragmentsActivityCreated(this, mFilterText, getTag());
        }
        this.subscribeToViewModel();


        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity().getApplicationContext(), DividerItemDecoration.VERTICAL));
        mAdapter = new CaseAdapter(mCases, this);
        mRecyclerView.setAdapter(mAdapter);

        mViewModel.loadCases(mFilter);  // Load cases
    }

    public void setupSortingView() {
        mAppliedSortingsAdapter = new AppliedSortAdapter(3);
        mAppliedSortingsAdapter.setItemClickListener((position, view) -> {
            PopupMenu popupMenu = new PopupMenu(getActivity(), view);
            popupMenu.setOnMenuItemClickListener(item -> {
                return true;
            });
            popupMenu.getMenu().add("Replace");
            popupMenu.getMenu().add("Remove");
            popupMenu.show();
        });
        FlowLayoutManager manager = new FlowLayoutManager();
        manager.setAutoMeasureEnabled(true);
        mSortingRecyclerView.setLayoutManager(manager);
        mSortingRecyclerView.addItemDecoration(new ItemOffsetDecoration(
                (int)TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, 2f, getResources().getDisplayMetrics()
                )
        ));
        mSortingRecyclerView.setAdapter(mAppliedSortingsAdapter);
    }

    @Override
    public void onItemClick(int position) {
        if (mCases == null) {
            return;
        }
        if (mHomeActivityCallbacks != null) {
            mHomeActivityCallbacks.onCaseSelected(mCases.get(position));
        }
    }

    private void subscribeToViewModel() {
        mViewModel.getCasesState().observe(this, resourceState -> {
            if (resourceState.data != null) {
                showCases(resourceState.data);
            }
            if (resourceState.status == Status.LOADING) {
                showLoading();
                return;
            }
            if (resourceState.status == Status.ERROR) {
                showError(resourceState.message);
                return;
            }
            if (resourceState.status == Status.SUCCESS) {
                this.hideLoading();
            }
        });

        mViewModel.getAppliedSorting().observe(this, value -> {
            mAppliedSortingsAdapter.setData(value);
        });
    }

    @UiThread
    protected void showCases(List<Case> cases) {
        mCases = cases;
        showContent();
        mAdapter.setData(mCases);
        mAdapter.notifyDataSetChanged();
    }

    @UiThread
    private void hideLoading() {
        mErrorView.hide();
    }

    @UiThread
    protected void showLoading() {
        if (mCases == null) {
            mRecyclerView.setVisibility(View.GONE);
            mErrorView.showProgress("Fetching " + mFilterText + "..." );
            return;
        }
        // TODO: Show some modern way of fetching
    }

    @UiThread
    protected void showContent() {
        mRecyclerView.setVisibility(View.VISIBLE);
        mErrorView.hide();
    }

    @UiThread
    private void showError(String message) {
        if (mCases == null) {
            mRecyclerView.setVisibility(View.GONE);
            mErrorView.showError(message);
            return;
        }
        Snackbar.make(getView(), message, Snackbar.LENGTH_SHORT).show();
    }

    public static class ItemOffsetDecoration extends RecyclerView.ItemDecoration {

        private int mItemOffset;

        public ItemOffsetDecoration(int itemOffset) {
            mItemOffset = itemOffset;
        }

        public ItemOffsetDecoration(@NonNull Context context, @DimenRes int itemOffsetId) {
            this(context.getResources().getDimensionPixelSize(itemOffsetId));
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                                   RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            outRect.set(mItemOffset, mItemOffset, mItemOffset, mItemOffset);
        }
    }
}
