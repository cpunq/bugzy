package com.bluestacks.bugzy;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.UiThread;
import android.support.annotation.WorkerThread;
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

import com.bluestacks.bugzy.models.resp.ListPeopleResponse;
import com.bluestacks.bugzy.models.resp.Person;
import com.bluestacks.bugzy.models.resp.User;
import com.bluestacks.bugzy.net.ConnectivityInterceptor;
import com.bluestacks.bugzy.net.FogbugzApiService;
import com.bluestacks.bugzy.utils.PrefsHelper;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Response;

public class PeopleActivity extends BaseActivity{

    @BindView(R.id.recyclerView)
    protected RecyclerView mRecyclerView;

    private LinearLayoutManager mLinearLayoutManager;
    private Call<ListPeopleResponse> mCases;
    private ListPeopleResponse myCases;
    private String mAccessToken;
    private RecyclerAdapter mAdapter;

    @Inject
    PrefsHelper mPrefs;

    @Inject
    FogbugzApiService mApiClient;

    @Inject
    AppExecutors mAppExecutors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        onViewsReady();
    }

    protected void onViewsReady() {
        mLinearLayoutManager = new LinearLayoutManager(this);
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
            Log.d("no","not Logged in");
        }
        else{
            mAccessToken = mPrefs.getString(PrefsHelper.Key.ACCESS_TOKEN);

            mCases = mApiClient.listPeople(mAccessToken);

            try {
                Response<ListPeopleResponse> resp = mCases.execute();

                if(resp.isSuccessful()) {
                    myCases = resp.body();
                    for(Person s : myCases.getPersons()) {
                        Log.d("Bug id",String.valueOf(s.getPersonid()));
                    }
                    mAppExecutors.mainThread().execute(new Runnable() {
                        @Override
                        public void run() {
                            updateToken(myCases.getPersons());
                        }
                    });
                    Log.d("Cases List " , myCases.toString());
                }
                else {
                    Log.d("Call Failed ", resp.errorBody().toString());
                }

            }
            catch(ConnectivityInterceptor.NoConnectivityException e){
                mAppExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        showConnectivityError();
                    }
                });
            }
            catch (IOException e) {
                Log.d("Cases","Call Failed");
            }
        }
    }

    @UiThread
    protected void updateToken(List<Person> persons) {
        mAdapter = new RecyclerAdapter(persons);
        mRecyclerView.setAdapter(mAdapter);
    }

    @UiThread
    protected void showConnectivityError() {
        Toast.makeText(this,"No internet",Toast.LENGTH_LONG).show();
    }

    public class RecyclerAdapter extends RecyclerView.Adapter<BugHolder> {

        private List<Person> mPersons;
        public RecyclerAdapter(List<Person> persons) {
            mPersons = persons ;
        }
        @Override
        public BugHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View inflatedView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.bug_item_row, parent, false);
            return new BugHolder(inflatedView);
        }

        @Override
        public void onBindViewHolder(BugHolder holder, int position) {
            Person person = mPersons.get(position);
            holder.bindData(person);
        }

        @Override
        public int getItemCount() {
            return mPersons.size();
        }
    }

    public static class BugHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mItemDate;
        private TextView mItemDescription;
        private LinearLayout mPriority;
        private Person mPerson;

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

        public void bindData(Person person) {
            mPerson = person;
            mItemDate.setText(String.valueOf(person.getFullname()));
            mItemDescription.setText(person.getEmail());
            mPriority.setBackgroundColor(Color.parseColor("#ddb65b"));
        }
    }
}
