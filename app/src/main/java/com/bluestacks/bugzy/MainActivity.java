package com.bluestacks.bugzy;


import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bluestacks.bugzy.models.resp.Case;
import com.bluestacks.bugzy.models.resp.ListCasesResponse;
import com.bluestacks.bugzy.models.resp.User;
import com.bluestacks.bugzy.net.ConnectivityInterceptor;
import com.bluestacks.bugzy.net.FogbugzApiFactory;
import com.bluestacks.bugzy.net.FogbugzApiService;
import com.bluestacks.bugzy.utils.PrefHelper_;


import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

@EActivity
public class MainActivity extends AppCompatActivity {

    @ViewById(R.id.recyclerView)
    protected RecyclerView mRecyclerView;

    private LinearLayoutManager mLinearLayoutManager;

    private Call<User> me;
    private Call<ListCasesResponse> mCases;
    private ListCasesResponse myCases;
    private String mAccessToken;


    @Pref
    PrefHelper_ mPrefs;


    private FogbugzApiService mApiClient;
    private RecyclerAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



    }


    @AfterViews
    protected void onViewsReady() {
        mLinearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mApiClient = FogbugzApiFactory.getApiClient(this);
        getToken();
    }


    @Background
    protected void getToken() {

        if(TextUtils.isEmpty(mPrefs.accessToken().get())) {
            me =  mApiClient.loginWithEmail("manish@bluestacks.com","junQfood_2708");
            try{
                String result = me.execute().body().getAuthToken();
                Log.d("Token : " , result);
                mPrefs.accessToken().put(result);
                mPrefs.isUserLoggedIn().put(true);
                mAccessToken = result;
            }
            catch (IOException e) {

            }
        }
        else{
            mAccessToken = mPrefs.accessToken().get();

            mCases = mApiClient.listCases(mAccessToken,"sTitle,ixPriority");

            try {
                Response<ListCasesResponse> resp = mCases.execute();

                if(resp.isSuccessful()) {
                    myCases = resp.body();
                    for(Case s : myCases.getCases()) {
                        Log.d("Bug id",String.valueOf(s.getIxBug()));
                    }
                    updateToken(myCases.getCases());
                    Log.d("Cases List " , myCases.toString());
                }
                else {
                    Log.d("Call Failed ", resp.errorBody().toString());
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
        mAdapter = new RecyclerAdapter(cases);
        mRecyclerView.setAdapter(mAdapter);
    }

    @UiThread
    protected void showConnectivityError() {
        Toast.makeText(this,"No internet",Toast.LENGTH_LONG).show();
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
            return new BugHolder(inflatedView);
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

        //4
        public BugHolder (View v) {
            super(v);
            mItemDate = (TextView) v.findViewById(R.id.item_id);
            mItemDescription = (TextView) v.findViewById(R.id.item_description);
            mPriority = (LinearLayout) v.findViewById(R.id.priority);
            v.setOnClickListener(this);
        }

        //5
        @Override
        public void onClick(View v) {
            Log.d("RecyclerView", "CLICK!");
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
        }
    }
}
