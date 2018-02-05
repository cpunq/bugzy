package com.bluestacks.bugzy.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.annotation.WorkerThread;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bluestacks.bugzy.utils.AppExecutors;
import com.bluestacks.bugzy.R;
import com.bluestacks.bugzy.common.Const;
import com.bluestacks.bugzy.models.resp.Case;
import com.bluestacks.bugzy.models.resp.CaseEvent;
import com.bluestacks.bugzy.models.resp.ListCasesData;
import com.bluestacks.bugzy.net.FogbugzApiService;
import com.bluestacks.bugzy.utils.PrefsHelper;
import com.bumptech.glide.Glide;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CaseDetailsFragment extends Fragment implements Injectable{

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

    private LinearLayoutManager mLinearLayoutManager;
    private ListCasesData myCases;
    private String mAccessToken;
    private Case mCase;
    private static CaseDetailsFragment mFragment;
    private HomeActivity mParentActivity;
    private String mFogBugzId;
    public static String token;
    private RecyclerAdapter mAdapter;

    @Inject
    PrefsHelper mPrefs;

    @Inject
    FogbugzApiService mApiClient;

    @Inject
    AppExecutors mAppExecutors;

    public static CaseDetailsFragment getInstance() {
        if(mFragment == null) {
            mFragment = new CaseDetailsFragment();
            return mFragment;
        }
        else {
            return mFragment;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mParentActivity = (HomeActivity)getActivity();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getArguments();
        mFogBugzId = extras.getString("bug_id");
        mCase = (Case) extras.getSerializable("bug");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.case_details, null);
        ButterKnife.bind(this, v);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        token = mPrefs.getString(PrefsHelper.Key.ACCESS_TOKEN);
        showLoading();
        mParentActivity.hideFab();
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mAppExecutors.networkIO().execute(new Runnable() {
            @Override
            public void run() {
                getToken();
            }
        });
    }

    @WorkerThread
    protected void getToken() {
        if(TextUtils.isEmpty(mPrefs.getString(PrefsHelper.Key.ACCESS_TOKEN))) {
            mParentActivity.redirectLogin();
        }
        else{
            mAccessToken = mPrefs.getString(PrefsHelper.Key.ACCESS_TOKEN);

            mAppExecutors.mainThread().execute(new Runnable() {
                @Override
                public void run() {
                    updateToken(mCase);
                }
            });
        }
    }

    @UiThread
    protected void updateToken(Case caseEvents) {
        showContent();
        mParentActivity.showActionIcons();
        mParentActivity.setTitle(String.valueOf(mCase.getIxBug()));
        List<CaseEvent> evs = mCase.getCaseevents();
        Collections.reverse(evs);
        mAdapter = new RecyclerAdapter(evs);
        mRecyclerView.setAdapter(mAdapter);
        mBugId.setText(String.valueOf(mCase.getIxBug()));
        mBugTitle.setText(String.valueOf(mCase.getTitle()));
        mAssignedTo.setText(String.valueOf(mCase.getPersonAssignedTo()));
        mMileStone.setText(String.valueOf(mCase.getFixFor()));
        mActiveStatus.setText(String.valueOf(mCase.getStatus()));
        Case bug = mCase;
        Log.d(Const.TAG," " + mCase.getFixFor());
        Log.d(Const.TAG," " + mCase.getProjectArea());
        if(bug.getPriority() == 3){
            mPriorityIndicator.setBackgroundColor(Color.parseColor("#e74c3c"));
        }
        else if(bug.getPriority() == 5) {
            mPriorityIndicator.setBackgroundColor(Color.parseColor("#ddb65b"));
        }
        else if(bug.getPriority() == 4) {
            mPriorityIndicator.setBackgroundColor(Color.parseColor("#95a5a6"));
        }
        else if(bug.getPriority() == 7) {
            mPriorityIndicator.setBackgroundColor(Color.parseColor("#bdc3c7"));
        }
        else {
            mPriorityIndicator.setBackgroundColor(Color.parseColor("#ecf0f1"));
        }
    }

    @UiThread
    protected void showConnectivityError() {
        Toast.makeText(getActivity(),"No internet",Toast.LENGTH_LONG).show();
    }

    @UiThread
    protected void showLoading() {
        mProgress.setVisibility(View.VISIBLE);
        mContainer.setVisibility(View.GONE);
    }

    @UiThread
    protected void showContent() {
        mProgress.setVisibility(View.GONE);
        mContainer.setVisibility(View.VISIBLE);
    }



    public class RecyclerAdapter extends RecyclerView.Adapter<BugHolder> {

        private List<CaseEvent> mBugs;
        public RecyclerAdapter(List<CaseEvent> bugs) {
            mBugs = bugs ;
        }
        @Override
        public BugHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View inflatedView;
            switch(viewType) {
                case 0:
                    inflatedView =LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.bug_event_row_begin, parent, false);
                    break;

                case 2:
                    inflatedView = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.bug_event_row_end, parent, false);
                    break;

                default:
                    inflatedView = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.bug_event_row, parent, false);
                    break;
            }

            return new BugHolder(inflatedView,mParentActivity,mParentActivity);

        }

        @Override
        public void onBindViewHolder(BugHolder holder, int position) {
            CaseEvent bug = mBugs.get(position);
            holder.bindData(bug);
        }

        @Override
        public int getItemCount() {
            return mBugs.size();
        }

        @Override
        public int getItemViewType(int position) {
            if(position == 0) {
                return 0;
            }
            else if(position == mBugs.size()-1) {
                return 2;
            }
            return 1;
        }
    }

    public static class BugHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mItemDate;
        private TextView mItemDescription;
        private TextView mChanges;
        private TextView mChangesContent;
        private ImageView mImageAttachment;
        private CaseEvent mBug;
        private Context mContext;
        private HomeActivity homeActivity;

        //4
        public BugHolder (View v,Context context,HomeActivity activity) {
            super(v);
            mItemDate = (TextView) v.findViewById(R.id.item_id);
            mItemDescription = (TextView) v.findViewById(R.id.item_description);
            mChanges = (TextView) v.findViewById(R.id.changes);
            mChangesContent = (TextView) v.findViewById(R.id.change_content);
            mImageAttachment = (ImageView) v.findViewById(R.id.attachment);
            mContext = context;
            homeActivity = activity;
            v.setOnClickListener(this);
        }

        //5
        @Override
        public void onClick(View v) {
            Log.d("RecyclerView", "CLICK!");
        }

        public void bindData(CaseEvent bug) {
            mBug = bug;
            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            DateFormat format2 = new SimpleDateFormat("MMMM dd, yyyy, hh:mm a", Locale.US);
            try {
                Date d = formatter.parse(String.valueOf(bug.getDate()));
                mItemDate.setText(format2.format(d));
            }catch(ParseException e) {
                mItemDate.setText(String.valueOf(bug.getDate()));
                Log.d(Const.TAG,"Failed to parse tags");
            }

            mItemDescription.setText(Html.fromHtml( bug.getEventDescription()));
            if(!TextUtils.isEmpty(bug.getContentHtml())) {
                mChangesContent.setText(Html.fromHtml(bug.getContentHtml()));
            }
            else if(!TextUtils.isEmpty(bug.getContent())) {
                mChangesContent.setText(Html.fromHtml(bug.getContent()));
            }
            else {
                mChangesContent.setVisibility(View.GONE);
            }

            if(!TextUtils.isEmpty(bug.getsChanges())) {
                mChanges.setText(Html.fromHtml(bug.getsChanges()));
            }
            else {
                mChanges.setVisibility(View.GONE);
            }

            if(bug.getsAttachments().size()>0) {
                Log.d(Const.TAG,bug.getsAttachments().get(0).getUrl());
                if(bug.getsAttachments().get(0).getFilename().endsWith(".png") || bug.getsAttachments().get(0).getFilename().endsWith(".jpg") ) {
                    mImageAttachment.setVisibility(View.VISIBLE);
                    final String img_path = ("https://bluestacks.fogbugz.com/" + bug.getsAttachments().get(0).getUrl() + "&token=" + token).replaceAll("&amp;","&");
                    Log.d(Const.TAG,img_path);
                    Glide.with(mContext).load(img_path)
                            .thumbnail(Glide.with(mContext).load(R.drawable.loading_ring))
                            .into(mImageAttachment);
                    mImageAttachment.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Fragment d = FullScreenImageFragment.getInstance();
                            Bundle arg = new Bundle();
                            Bitmap bitmap = ((BitmapDrawable)mImageAttachment.getDrawable()).getBitmap();
                            arg.putParcelable("img_src",bitmap);
                            d.setArguments(arg);

                            homeActivity.setFragment(d);
                        }
                    });
                }
            }
            else {
                mImageAttachment.setVisibility(View.GONE);
            }
        }
    }
}
