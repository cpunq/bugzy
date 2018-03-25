package com.bluestacks.bugzy.ui.casedetails;

import com.bluestacks.bugzy.data.model.Attachment;
import com.bluestacks.bugzy.data.model.CaseStatus;
import com.bluestacks.bugzy.data.model.Resource;
import com.bluestacks.bugzy.data.model.Status;
import com.bluestacks.bugzy.ui.caseevents.CaseEventsAdapter;
import com.bluestacks.bugzy.ui.common.Injectable;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bluestacks.bugzy.R;
import com.bluestacks.bugzy.data.model.Case;
import com.bluestacks.bugzy.data.model.CaseEvent;
import com.bluestacks.bugzy.ui.editcase.CaseEditActivity;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CaseDetailsFragment extends Fragment implements Injectable {
    public static final String TAG = CaseDetailsFragment.class.getName();
    public interface CaseDetailsFragmentContract {
        void openImageActivity(String imagePath);
    }
    private CaseDetailsFragmentViewModel mViewModel;
    private Case mCase;
    private CaseEventsAdapter mAdapter;
    private CaseDetailsFragmentContract mParentActivity;
    private String mToken;
    LinearLayoutManager mlinearLayoutManager;
    private Snackbar mSyncSnackbar;
    private Snackbar mRetrySnackbar;


    @Inject
    ViewModelProvider.Factory mViewModelFactory;

    @BindView(R.id.main_container)
    protected LinearLayout mContainer;

    @BindView(R.id.priority_indicator)
    protected LinearLayout mPriorityIndicator;

    @BindView(R.id.recyclerView)
    protected RecyclerView mRecyclerView;

    @BindView(R.id.progressBar)
    protected ProgressBar mProgress;

    @BindView(R.id.textview_bug_id)
    protected TextView mBugId;

    @BindView(R.id.textview_bug_title)
    protected TextView mBugTitle;

    @BindView(R.id.textview_active_status)
    protected TextView mActiveStatus;

    @BindView(R.id.textview_assigned_to)
    protected TextView mAssignedTo;

    @BindView(R.id.textview_milestone)
    protected TextView mMileStone;

    @BindView(R.id.textview_required_merge)
    protected TextView mRequiredMerge;

    @BindView(R.id.button_assign)
    protected ImageButton mAssignButton;

    @BindView(R.id.button_reactivate)
    protected ImageButton mReactivateButton;

    @BindView(R.id.button_close_case)
    protected ImageButton mCloseCaseButton;

    @BindView(R.id.button_reopen)
    protected Button mReopenButton;

    @BindView(R.id.button_resolve)
    protected ImageButton mResolveButton;

    public static CaseDetailsFragment getInstance(String bugId, Case aCase) {
        CaseDetailsFragment fragment = new CaseDetailsFragment();
        Bundle args = new Bundle();
        args.putSerializable("bug", aCase);
        args.putString("bug_id",bugId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof CaseDetailsFragmentContract) {
            mParentActivity = (CaseDetailsFragmentContract)context;
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getArguments();
        mCase = (Case) extras.getSerializable("bug");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.case_details, null);
        ButterKnife.bind(this, v);
        return v;
    }
    private void setupViews() {
        mlinearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mlinearLayoutManager);
        mAdapter = new CaseEventsAdapter(getContext());
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnAttachmentClickListener((view, event, attachmentPosition) -> {
            // TODO: Move this logic to ViewModel
            Attachment attachment = event.getsAttachments().get(attachmentPosition);
            String filename = attachment.getFilename().toLowerCase();
            if (filename.endsWith("png") || filename.endsWith("jpg") || filename.endsWith("jpeg")) {
                final String img_path = ("https://bluestacks.fogbugz.com/" + attachment.getUrl() + "&token=" + mToken)
                        .replaceAll("&amp;","&");
                mParentActivity.openImageActivity(img_path);
                return;
            }
            // For other attachments leave things to browser
            String url = ("https://bluestacks.fogbugz.com/" + attachment.getUrl() + "&token=" + mToken)
                    .replaceAll("&amp;","&");
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        });
        prepareActionsButtons();
        prepareRecyclerScrollListener();
    }

    private void prepareActionsButtons() {
        setButtonDrawableColorFilter(mAssignButton.getDrawable());
        setButtonDrawableColorFilter(mResolveButton.getDrawable());
        setButtonDrawableColorFilter(mReactivateButton.getDrawable());
        setButtonDrawableColorFilter(mReopenButton.getCompoundDrawables()[0]);
        setButtonDrawableColorFilter(mCloseCaseButton.getDrawable());
        mReopenButton.setTextColor(getResources().getColor(R.color.textColorSecondary));
    }

    public Snackbar getSyncSnackbar() {
        Snackbar bar = Snackbar.make(getView(), "Syncing case details..", Snackbar.LENGTH_INDEFINITE);
        ViewGroup contentLay = (ViewGroup) bar.getView().findViewById(android.support.design.R.id.snackbar_text).getParent();
        ProgressBar item = new ProgressBar(getContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(inDp(24), inDp(24));
        params.gravity = Gravity.CENTER_VERTICAL;
        item.setLayoutParams(params);
        contentLay.addView(item,0);
        return bar;
    }


    int mScrollY =  0;
    int mMaxScroll = 0;

    private void prepareRecyclerScrollListener() {
        mContainer.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mRecyclerView.setPadding(mRecyclerView.getPaddingLeft(),
                        mContainer.getHeight() + inDp(8),
                        mRecyclerView.getPaddingRight(),
                        mRecyclerView.getPaddingBottom());
                mlinearLayoutManager.scrollToPosition(0);
                mMaxScroll = -mContainer.getHeight() + inDp(56);
                mContainer.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                mScrollY += dy;
//                Log.d(TAG, String.format("%d, %d", mScrollY, mRecyclerView.computeVerticalScrollOffset()));
                int requiredScroll = Math.min(0, Math.max(mMaxScroll, -mScrollY));
                if (requiredScroll == mMaxScroll && mMaxScroll != 0) {
                    ViewCompat.setElevation(mContainer, inDp(4));
                } else {
                    ViewCompat.setElevation(mContainer, inDp(0));
                }
                mContainer.setTranslationY(requiredScroll);
            }
        });
    }

    private int inDp(int dps) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dps, getResources().getDisplayMetrics());
    }

    private void setButtonDrawableColorFilter(Drawable d) {
        d.mutate().setColorFilter(getResources().getColor(R.color.textColorSecondary), PorterDuff.Mode.SRC_ATOP);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setupViews();

        mViewModel = ViewModelProviders.of(this, mViewModelFactory).get(CaseDetailsFragmentViewModel.class);

        showCaseDetails(mCase);

        mViewModel.getCaseState().observe(this, pair -> {
            Resource<Case> caseState = pair.first;
            mToken = pair.second;
            if (mToken == null) {
                // Clear stuff and navigate to parent activity
            }
            mAdapter.setToken(mToken);

            if (caseState.data != null) {
                showCaseDetails(caseState.data);
            }
            if (caseState.status == Status.LOADING) {
                showLoading();
                return;
            }
            if (caseState.status == Status.ERROR) {
                showError(caseState.message);
                return;
            }
            if (caseState.status == Status.SUCCESS) {
                this.hideLoading();
                return;
            }
        });
        mViewModel.loadCaseDetails(mCase);
        mViewModel.getSnackBarText().observe(this, text -> Snackbar.make(getView(), text, Snackbar.LENGTH_LONG).show());
    }

    @OnClick(R.id.button_resolve)
    public void onResolveClicked() {
        startActivity(new Intent(getActivity(), CaseEditActivity.class));
    }

    @OnClick(R.id.button_reopen)
    public void onReopenClicked() {
        startActivity(new Intent(getActivity(), CaseEditActivity.class));
    }

    @OnClick(R.id.button_reactivate)
    public void onReActivateClicked() {
        startActivity(new Intent(getActivity(), CaseEditActivity.class));
    }

    @OnClick(R.id.button_close_case)
    public void onCloseCaseClicked() {
        startActivity(new Intent(getActivity(), CaseEditActivity.class));
    }

    @OnClick(R.id.button_assign)
    public void onAssignClicked() {
        startActivity(new Intent(getActivity(), CaseEditActivity.class));
    }

    private void showActionButtons(Case kase) {
        String status = kase.getStatus().toLowerCase();
        if(status.startsWith(CaseStatus.RESOLVED) || status.startsWith(CaseStatus.VERIFIED)) {
            mAssignButton.setVisibility(View.VISIBLE);
            mResolveButton.setVisibility(View.VISIBLE);
            mReactivateButton.setVisibility(View.VISIBLE);
            mReopenButton.setVisibility(View.GONE);
            mCloseCaseButton.setVisibility(View.VISIBLE);
            return;
        }
        if(status.startsWith(CaseStatus.OPEN) || status.startsWith(CaseStatus.ACTIVE)) {
            mAssignButton.setVisibility(View.VISIBLE);
            mResolveButton.setVisibility(View.VISIBLE);
            mReactivateButton.setVisibility(View.GONE);
            mReopenButton.setVisibility(View.GONE);
            mCloseCaseButton.setVisibility(View.GONE);
            return;
        }
        if(status.startsWith(CaseStatus.CLOSED)) {
            mAssignButton.setVisibility(View.GONE);
            mResolveButton.setVisibility(View.GONE);
            mReactivateButton.setVisibility(View.GONE);
            mReopenButton.setVisibility(View.VISIBLE);
            mCloseCaseButton.setVisibility(View.GONE);
            return;
        }
    }

    @UiThread
    protected void showCaseDetails(Case aCase) {
        mCase = aCase;
        showContent();
        showActionButtons(mCase);

        List<CaseEvent> evs = mCase.getCaseevents();
        if (evs != null) {
            mAdapter.setData(evs);
            mAdapter.notifyDataSetChanged();
        }

        mBugId.setText(String.valueOf(mCase.getIxBug()));
        mBugTitle.setText(String.valueOf(mCase.getTitle()));
        mAssignedTo.setText(String.valueOf(mCase.getPersonAssignedTo()));
        mRequiredMerge.setText(mCase.getRequiredMergeIn());

        if (!TextUtils.isEmpty(mCase.getFixFor())) {
            mMileStone.setText(String.valueOf(mCase.getFixFor()));
        }
        mActiveStatus.setText(String.valueOf(mCase.getStatus()));
        Case bug = mCase;
        if(bug.getPriority() <= 3){
            mPriorityIndicator.setBackgroundColor(Color.parseColor("#e74c3c"));
        } else if(bug.getPriority() == 4) {
            mPriorityIndicator.setBackgroundColor(Color.parseColor("#95a5a6"));
        }
        else if(bug.getPriority() == 5) {
            mPriorityIndicator.setBackgroundColor(Color.parseColor("#ddb65b"));
        }
        else if(bug.getPriority() <= 7) {
            mPriorityIndicator.setBackgroundColor(Color.parseColor("#bdc3c7"));
        }
        else {
            mPriorityIndicator.setBackgroundColor(Color.parseColor("#ecf0f1"));
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mRetrySnackbar != null && mRetrySnackbar.isShown()) {
            mRetrySnackbar.dismiss();
        }
        if (mSyncSnackbar != null && mSyncSnackbar.isShown()) {
            mSyncSnackbar.dismiss();
        }
    }


    public Snackbar getRetrySnackbar(String message) {
        Snackbar snackbar = Snackbar
                .make(getView(), message, Snackbar.LENGTH_INDEFINITE)
                .setAction("RETRY", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mViewModel.loadCaseDetails(mCase);
                    }
                });
        return snackbar;
    }

    private void showRetrySnackbar(String message) {
        mRetrySnackbar = getRetrySnackbar(message);
        mRetrySnackbar.show();
    }

    private void hideRetrySnackbar() {
        if (mRetrySnackbar == null) {
            return;
        }
        mRetrySnackbar.dismiss();
        mRetrySnackbar = null;
    }


    private void showSyncProgress() {
        mSyncSnackbar = getSyncSnackbar();
        mSyncSnackbar.show();
    }

    private void hideSyncProgress() {
        if (mSyncSnackbar == null) {
            return;
        }
        mSyncSnackbar.dismiss();
        mSyncSnackbar = null;
    }

    protected void hideLoading() {
        mProgress.setVisibility(View.GONE);
        hideSyncProgress();
    }

    @UiThread
    protected void showContent() {
        mProgress.setVisibility(View.GONE);
        mContainer.setVisibility(View.VISIBLE);
    }

    protected void showLoading() {
        hideRetrySnackbar();
        if (mCase.getCaseevents() == null || mCase.getCaseevents().size() == 0) {
            // Show full screen loading
            mProgress.setVisibility(View.VISIBLE);
            return;
        }
        showSyncProgress();
    }

    protected void showError(String message) {
        showRetrySnackbar(message);
    }
}
