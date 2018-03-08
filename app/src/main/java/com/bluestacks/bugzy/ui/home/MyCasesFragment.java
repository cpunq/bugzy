package com.bluestacks.bugzy.ui.home;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bluestacks.bugzy.models.Status;
import com.bluestacks.bugzy.ui.common.ErrorView;
import com.bluestacks.bugzy.ui.common.Injectable;
import com.bluestacks.bugzy.ui.common.HomeActivityCallbacks;
import com.bluestacks.bugzy.R;
import com.bluestacks.bugzy.models.resp.Case;
import com.bluestacks.bugzy.utils.OnItemClickListener;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MyCasesFragment extends Fragment implements Injectable, OnItemClickListener {
    private static final String PARAM_FILTER = "filter";
    private static final String PARAM_FILTER_TEXT = "filter_text";
    private MyCasesViewModel mViewModel;
    private List<Case> mCases;
    private String mFilter;
    private String mFilterText;
    private HomeActivityCallbacks mHomeActivityCallbacks;
    private RecyclerAdapter mAdapter;

    @Inject
    protected ViewModelProvider.Factory mViewModelFactory;

    @BindView(R.id.recyclerView)
    protected RecyclerView mRecyclerView;

    @BindView(R.id.viewError)
    protected ErrorView mErrorView;

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
        View v = inflater.inflate(R.layout.fragment_recyclerview, null);
        ButterKnife.bind(this, v);
        return v;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this, mViewModelFactory).get(MyCasesViewModel.class);

        if (mHomeActivityCallbacks != null) {
            mHomeActivityCallbacks.onFragmentsActivityCreated(this, mFilterText, getTag());
        }
        this.subscribeToViewModel();


        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mViewModel.loadCases(mFilter);  // Load cases
    }

    //5
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
    }

    @UiThread
    protected void showCases(List<Case> cases) {
        mCases = cases;
        showContent();
        mAdapter = new RecyclerAdapter(mCases, this);
        mRecyclerView.setAdapter(mAdapter);
    }

    @UiThread
    private void hideLoading() {
        mErrorView.hide();
    }

    @UiThread
    protected void showLoading() {
        if (mCases == null) {
            mRecyclerView.setVisibility(View.GONE);
        }
        mErrorView.showProgress("Fetching " + mFilterText + "..." );
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
        }
        mErrorView.showError(message);
    }

    public class RecyclerAdapter extends RecyclerView.Adapter<BugHolder> {
        private OnItemClickListener mItemClickListener;
        private List<Case> mBugs;
        public RecyclerAdapter(List<Case> bugs, OnItemClickListener listener) {
            mBugs = bugs ;
            mItemClickListener = listener;
        }
        @Override
        public BugHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View inflatedView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.bug_item_row, parent, false);
            final BugHolder holder = new BugHolder(inflatedView, mHomeActivityCallbacks);
            inflatedView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = holder.getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        if (mItemClickListener != null) {
                            mItemClickListener.onItemClick(pos);
                        }
                    }
                }
            });
            return holder;
        }

        @Override
        public void onBindViewHolder(BugHolder holder, int position) {
            Case bug = mBugs.get(position);
            holder.bindData(bug);
        }

        @Override
        public int getItemCount() {
            return mBugs.size();
        }
    }

    public static class BugHolder extends RecyclerView.ViewHolder{
        private TextView mItemDate;
        private TextView mItemDescription;
        private LinearLayout mPriority;

        public BugHolder (View v, HomeActivityCallbacks a) {
            super(v);
            mItemDate = (TextView) v.findViewById(R.id.item_id);
            mItemDescription = (TextView) v.findViewById(R.id.item_description);
            mPriority = (LinearLayout) v.findViewById(R.id.priority);
        }

        public void bindData(Case bug) {
            mItemDate.setText(String.valueOf(bug.getIxBug()));
            mItemDescription.setText(bug.getTitle());

            if(bug.getPriority() == 3){
                mPriority.setBackgroundColor(Color.parseColor("#e74c3c"));
            }
            else if(bug.getPriority() == 5) {
                mPriority.setBackgroundColor(Color.parseColor("#ddb65b"));
            }
            else if(bug.getPriority() == 4) {
                mPriority.setBackgroundColor(Color.parseColor("#95a5a6"));
            }
            else if(bug.getPriority() == 7) {
                mPriority.setBackgroundColor(Color.parseColor("#bdc3c7"));
            }
            else {
                mPriority.setBackgroundColor(Color.parseColor("#ecf0f1"));
            }
        }
    }


}
