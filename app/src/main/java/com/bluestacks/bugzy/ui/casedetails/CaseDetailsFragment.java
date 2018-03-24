package com.bluestacks.bugzy.ui.casedetails;

import com.bluestacks.bugzy.data.model.Attachment;
import com.bluestacks.bugzy.ui.caseevents.CaseEventsAdapter;
import com.bluestacks.bugzy.ui.common.Injectable;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bluestacks.bugzy.R;
import com.bluestacks.bugzy.data.model.Case;
import com.bluestacks.bugzy.data.model.CaseEvent;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CaseDetailsFragment extends Fragment implements Injectable {
    public static final String TAG = CaseDetailsFragment.class.getName();
    public interface CaseDetailsFragmentContract {
        void openImageActivity(String imagePath);
    }
    private CaseDetailsFragmentViewModel mViewModel;
    private Case mCase;
    private CaseDetailsFragmentContract mParentActivity;
    private String mToken;

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
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(linearLayoutManager);
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
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setupViews();

        mViewModel = ViewModelProviders.of(this, mViewModelFactory).get(CaseDetailsFragmentViewModel.class);

        showCaseDetails(mCase);
        mViewModel.getToken().observe(this, token -> {
            if (token == null) {
                // Not logged in
                return;
            }
            mToken = token;
            mAdapter.setToken(mToken);
            // If token is there, only then start observing cases
            mViewModel.getCaseState().observe(this, caseState -> {
                if (caseState.data != null) {
                    showCaseDetails(caseState.data);
                }
            });
            // Load cases only when token is gotten
            mViewModel.loadCaseDetails(mCase);
        });

        mViewModel.getSnackBarText().observe(this, text -> Snackbar.make(getView(), text, Snackbar.LENGTH_LONG).show());
    }
    private CaseEventsAdapter mAdapter;

    @UiThread
    protected void showCaseDetails(Case aCase) {
        mCase = aCase;
        showContent();

        List<CaseEvent> evs = mCase.getCaseevents();
        if (evs != null) {
            mAdapter.setData(evs);
            mAdapter.notifyDataSetChanged();
        }

        mBugId.setText(String.valueOf(mCase.getIxBug()));
        mBugTitle.setText(String.valueOf(mCase.getTitle()));
        mAssignedTo.setText(String.valueOf(mCase.getPersonAssignedTo()));

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

    @UiThread
    protected void showContent() {
        mProgress.setVisibility(View.GONE);
        mContainer.setVisibility(View.VISIBLE);
    }
}
