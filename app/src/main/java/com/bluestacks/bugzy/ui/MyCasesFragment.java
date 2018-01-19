package com.bluestacks.bugzy.ui;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bluestacks.bugzy.HomeActivity;
import com.bluestacks.bugzy.R;
import com.bluestacks.bugzy.models.resp.Case;
import com.bluestacks.bugzy.models.resp.ListCasesResponse;
import com.bluestacks.bugzy.models.resp.User;
import com.bluestacks.bugzy.net.ConnectivityInterceptor;
import com.bluestacks.bugzy.net.FogbugzApiFactory;
import com.bluestacks.bugzy.net.FogbugzApiService;
import com.bluestacks.bugzy.utils.PrefsHelper;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by msharma on 22/06/17.
 */
@EFragment(R.layout.activity_main)
public class MyCasesFragment extends Fragment implements Injectable {



    @ViewById(R.id.recyclerView)
    protected RecyclerView mRecyclerView;

    @ViewById(R.id.progressBar)
    protected ProgressBar mProgress;

    private LinearLayoutManager mLinearLayoutManager;

    private Call<User> me;
    private Call<ListCasesResponse> mCases;
    private ListCasesResponse myCases;
    private String mAccessToken;
    private static MyCasesFragment mFragment;
    private HomeActivity mParentActivity;


    public static MyCasesFragment getInstance() {
        if(mFragment == null) {
            mFragment = new MyCasesFragment_();
            return mFragment;
        }
        else {
            return mFragment;
        }
    }

    @Inject PrefsHelper mPrefs;
    @Inject FogbugzApiService mApiClient;
    private RecyclerAdapter mAdapter;

    @AfterViews
    protected void onViewsReady() {
        mParentActivity = (HomeActivity)getActivity();
        mParentActivity.hideActionIcons();
        mParentActivity.showFab();
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        if(myCases == null) {
            getToken();
        }
        else {
            showContent();
            updateToken(myCases.getCases());
        }
    }


    @Background
    protected void getToken() {

        if(TextUtils.isEmpty(mPrefs.getString(PrefsHelper.Key.ACCESS_TOKEN))) {
            mParentActivity.redirectLogin();
        }
        else{
            mAccessToken = mPrefs.getString(PrefsHelper.Key.ACCESS_TOKEN);

            mCases = mApiClient.listCases(mAccessToken,"sTitle,ixPriority,sStatus,sProject,sFixFor,sArea,sPersonAssignedTo,sPersonOpenedBy,events");


            try {
                showLoading();
                Response<ListCasesResponse> resp = mCases.execute();

                if(resp.isSuccessful()) {
                    myCases = resp.body();
                    for(Case s : myCases.getCases()) {
                        Log.d("Bug id" ,String.valueOf(s.getIxBug()));
                    }
                    updateToken(myCases.getCases());
                    Log.d("Cases List " , myCases.toString());
                }
                else {
                    Log.d("Call Failed " , resp.errorBody().toString());
                }

            }
            catch(ConnectivityInterceptor.NoConnectivityException e){
                showConnectivityError();
            }
            catch (IOException e) {
                Log.d("Cases","Call Failed");
            }
        }


    }

    @UiThread
    protected void updateToken(List<Case> cases) {
        showContent();
        mAdapter = new RecyclerAdapter(cases);
        mRecyclerView.setAdapter(mAdapter);
    }

    @UiThread
    protected void showConnectivityError() {
        Toast.makeText(getActivity(),"No internet",Toast.LENGTH_LONG).show();
    }

    @UiThread
    protected void showLoading() {
        mProgress.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.GONE);
    }

    @UiThread
    protected void showContent() {
        mProgress.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }



    public class RecyclerAdapter extends RecyclerView.Adapter<BugHolder> {

        private List<Case> mBugs;
        public RecyclerAdapter(List<Case> bugs) {
            mBugs = bugs ;
        }
        @Override
        public BugHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View inflatedView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.bug_item_row, parent, false);
            return new BugHolder(inflatedView,mParentActivity);
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

    public static class BugHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mItemDate;
        private TextView mItemDescription;
        private LinearLayout mPriority;
        private Case mBug;
        private HomeActivity mActivity;

        //4
        public BugHolder (View v,HomeActivity a) {
            super(v);
            mItemDate = (TextView) v.findViewById(R.id.item_id);
            mItemDescription = (TextView) v.findViewById(R.id.item_description);
            mPriority = (LinearLayout) v.findViewById(R.id.priority);
            mActivity = a;
            v.setOnClickListener(this);
        }

        //5
        @Override
        public void onClick(View v) {
                Fragment d = CaseDetailsFragment.getInstance();
                Bundle arg = new Bundle();
                arg.putString("bug_id",String.valueOf(mBug.getIxBug()));
                arg.putSerializable("bug",mBug);
                d.setArguments(arg);
                mActivity.setFragment(d);
        }

        public void bindData(Case bug) {
            mBug = bug;
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
