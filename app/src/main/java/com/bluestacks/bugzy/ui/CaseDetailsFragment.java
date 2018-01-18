package com.bluestacks.bugzy.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
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

import com.bluestacks.bugzy.HomeActivity;
import com.bluestacks.bugzy.R;
import com.bluestacks.bugzy.common.Const;
import com.bluestacks.bugzy.common.RealmController;
import com.bluestacks.bugzy.models.resp.Case;
import com.bluestacks.bugzy.models.resp.CaseEvent;
import com.bluestacks.bugzy.models.resp.ListCasesResponse;
import com.bluestacks.bugzy.models.resp.User;
import com.bluestacks.bugzy.net.ConnectivityInterceptor;
import com.bluestacks.bugzy.net.FogbugzApiFactory;
import com.bluestacks.bugzy.net.FogbugzApiService;
import com.bluestacks.bugzy.utils.PrefHelper_;
import com.bumptech.glide.Glide;
import com.guardanis.imageloader.ImageRequest;
import com.squareup.picasso.Picasso;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Response;

import static android.R.attr.width;

/**
 * Created by msharma on 12/07/17.
 */
@EFragment(R.layout.case_details)
public class CaseDetailsFragment extends Fragment {

    @ViewById(R.id.main_container)
    protected LinearLayout mContainer;

    @ViewById(R.id.priority_indicator)
    protected LinearLayout mPriorityIndicator;

    @ViewById(R.id.recyclerView)
    protected RecyclerView mRecyclerView;

    @ViewById(R.id.progressBar)
    protected ProgressBar mProgress;

    @ViewById(R.id.textview_bug_id)
    protected TextView mBugId;

    @ViewById(R.id.textview_bug_title)
    protected TextView mBugTitle;

    @ViewById(R.id.textview_active_status)
    protected TextView mActiveStatus;

    @ViewById(R.id.textview_assigned_to)
    protected TextView mAssignedTo;

    @ViewById(R.id.textview_milestone)
    protected TextView mMileStone;

    @ViewById(R.id.textview_required_merge)
    protected TextView mRequiredMerge;


    private LinearLayoutManager mLinearLayoutManager;

    private Call<User> me;
    private Call<ListCasesResponse> mCases;
    private ListCasesResponse myCases;
    private String mAccessToken;
    private Case mCase;
    private static CaseDetailsFragment mFragment;
    private HomeActivity mParentActivity;
    private String mFogBugzId;
    private Realm mRealm;
    public static String token;


    public static CaseDetailsFragment getInstance() {
        if(mFragment == null) {
            mFragment = new CaseDetailsFragment_();
            return mFragment;
        }
        else {
            return mFragment;
        }
    }

    @Pref
    PrefHelper_ mPrefs;


    private FogbugzApiService mApiClient;
    private RecyclerAdapter mAdapter;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getArguments();
        mFogBugzId = extras.getString("bug_id");
        mCase = (Case) extras.getSerializable("bug");
    }

    @AfterViews
    protected void onViewsReady() {

        token = mPrefs.accessToken().get();
        mRealm = Realm.getDefaultInstance();
        showLoading();
        mParentActivity = (HomeActivity)getActivity();
        mParentActivity.hideFab();
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mApiClient = FogbugzApiFactory.getApiClient(getActivity());
        getToken();
    }

    @Background
    protected void getToken() {
        mRealm = Realm.getDefaultInstance();
        if(TextUtils.isEmpty(mPrefs.accessToken().get())) {
            mParentActivity.redirectLogin();
        }
        else{
            mAccessToken = mPrefs.accessToken().get();

            mCases = mApiClient.listCases(mAccessToken,"sTitle,ixPriority,sStatus,sProject,sFixFor,sArea,sPersonAssignedTo,sPersonOpenedBy,events");
            updateToken(mCase);
//            try {
//                showLoading();
//                Response<ListCasesResponse> resp = mCases.execute();
//                if(resp.isSuccessful()) {
//                    myCases = resp.body();
//                    for(com.bluestacks.bugzy.models.resp.Case s : myCases.getCases()) {
//                        Log.d("Bug id" ,String.valueOf(s.getIxBug()));
//                    }
//                    updateToken(myCases.getCases());
//                    Log.d("Cases List " , myCases.toString());
//                }
//                else {
//                    Log.d("Call Failed " , resp.errorBody().toString());
//                }
//                //Case bug_info = mRealm.where(Case.class).equalTo("bugId", Integer.parseInt(mFogBugzId)).findFirst();
//                //List<CaseEvent> caseEvents = mRealm.where(CaseEvent.class).findAll();
//                //updateToken(bug_info,caseEvents);
//            }
//            catch(ConnectivityInterceptor.NoConnectivityException e){
//                showConnectivityError();
//            }
//            catch (IOException e) {
//                Log.d("Cases","Call Failed");
//            }
        }
    }

    @UiThread
    protected void updateToken(Case caseEvents) {
        showContent();
//        for(Case s : caseEvents) {
//            if(mFogBugzId.equals(String.valueOf(s.getIxBug()))) {
//                mCase = s;
//            }
//        }
        mParentActivity.showActionIcons();
        mParentActivity.setTitle(String.valueOf(mCase.getIxBug()));
        List<CaseEvent> evs = mCase.getCaseevents().getCaseEvents();
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
