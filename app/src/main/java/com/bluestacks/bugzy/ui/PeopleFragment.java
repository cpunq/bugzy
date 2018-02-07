package com.bluestacks.bugzy.ui;

import com.google.gson.Gson;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.annotation.WorkerThread;
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

import com.bluestacks.bugzy.BugzyApp;
import com.bluestacks.bugzy.ui.common.ErrorView;
import com.bluestacks.bugzy.utils.AppExecutors;
import com.bluestacks.bugzy.R;
import com.bluestacks.bugzy.models.resp.ListPeopleData;
import com.bluestacks.bugzy.models.resp.ListPeopleRequest;
import com.bluestacks.bugzy.models.resp.Person;
import com.bluestacks.bugzy.net.ConnectivityInterceptor;
import com.bluestacks.bugzy.net.FogbugzApiService;
import com.bluestacks.bugzy.utils.PrefsHelper;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executor;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Response;

public class PeopleFragment extends Fragment implements Injectable{
    @BindView(R.id.recyclerView)
    protected RecyclerView mRecyclerView;

    @BindView(R.id.progressBar)
    protected ProgressBar mProgressBar;

    @BindView(R.id.viewError)
    protected ErrorView mErrorView;

    private LinearLayoutManager mLinearLayoutManager;
    private List<Person> people;
    private String mAccessToken;
    private static PeopleFragment mFragment;
    private RecyclerAdapter mAdapter;
    protected Executor mMainExecutor;
    private NavigationActivityBehavior mNavigationBehavior;

    @Inject
    PrefsHelper mPrefs;

    @Inject
    FogbugzApiService mApiClient;

    @Inject
    Gson mGson;

    @Inject
    AppExecutors mAppExecutors;

    public static PeopleFragment getInstance() {
        if(mFragment == null) {
            mFragment = new PeopleFragment();
            return mFragment;
        }
        else {
            return mFragment;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof NavigationActivityBehavior) {
            mNavigationBehavior = (NavigationActivityBehavior)context;
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMainExecutor = mAppExecutors.mainThread();
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
        if (mNavigationBehavior != null) {
            mNavigationBehavior.onContentFragmentsActivityCreated(this, "People");
        }
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mAppExecutors.networkIO().execute(new Runnable() {
            @Override
            public void run() {
                fetchPeople();
            }
        });
    }

    @WorkerThread
    protected void fetchPeople() {
        if(TextUtils.isEmpty(mPrefs.getString(PrefsHelper.Key.ACCESS_TOKEN))) {
           redirectLogin();
           return;
        }
        Call<com.bluestacks.bugzy.models.Response<ListPeopleData>> call = mApiClient.listPeople(new ListPeopleRequest());

        try {
            mMainExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    showLoading();
                }
            });
            Response<com.bluestacks.bugzy.models.Response<ListPeopleData>> resp = call.execute();
            final com.bluestacks.bugzy.models.Response<ListPeopleData> body;
            if(resp.isSuccessful()) {
                body = resp.body();
            } else {
                Log.d("Call Failed ", resp.errorBody().toString());
                String stringbody = resp.errorBody().string();
                body = mGson.fromJson(stringbody, com.bluestacks.bugzy.models.Response.class);
            }
            mMainExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    onPeopleResponse(body);
                }
            });
        } catch(ConnectivityInterceptor.NoConnectivityException e){
            mMainExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    onPeopleResponse(null);
                }
            });
        } catch (IOException e) {
            Log.d("Cases","Call Failed");
            mMainExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    onPeopleResponse(null);
                }
            });
        }
    }

    protected void onPeopleResponse(com.bluestacks.bugzy.models.Response<ListPeopleData> response) {
        hideLoading();
        if (response == null) {
            showError("Could not fetch people");
            return;
        }
        if (response.getErrors().size() > 0) {
            showError(response.getErrors().get(0).getMessage());
            return;
        }
        people = response.getData().getPersons();
        ((BugzyApp)getActivity().getApplication()).persons = people;
        updatePeople(people);
        Log.d("Cases List " , people.toString());
    }

    @UiThread
    protected void updatePeople(List<Person> persons) {
        mAdapter = new RecyclerAdapter(persons);
        mRecyclerView.setAdapter(mAdapter);
        showContent();
    }

    @UiThread
    private void hideLoading() {
        mProgressBar.setVisibility(View.GONE);
    }

    @UiThread
    protected void showLoading() {
        mProgressBar.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.GONE);
        mErrorView.setVisibility(View.GONE);
    }

    @UiThread
    protected void showContent() {
        mProgressBar.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
        mErrorView.setVisibility(View.GONE);
    }

    protected void showError(String message) {
        mProgressBar.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.GONE);
        mErrorView.setVisibility(View.VISIBLE);
        mErrorView.setErrorText(message);
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

    private void redirectLogin() {
        Intent mLogin = new Intent(getActivity(),LoginActivity.class);
        startActivity(mLogin);
        getActivity().finish();
    }
}
