package com.bluestacks.bugzy.ui.home;

import com.google.gson.Gson;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bluestacks.bugzy.BugzyApp;
import com.bluestacks.bugzy.models.Status;
import com.bluestacks.bugzy.ui.login.LoginActivity;
import com.bluestacks.bugzy.ui.common.ErrorView;
import com.bluestacks.bugzy.ui.common.Injectable;
import com.bluestacks.bugzy.ui.common.HomeActivityCallbacks;
import com.bluestacks.bugzy.utils.AppExecutors;
import com.bluestacks.bugzy.R;
import com.bluestacks.bugzy.models.resp.Person;
import com.bluestacks.bugzy.data.remote.FogbugzApiService;
import com.bluestacks.bugzy.data.local.PrefsHelper;

import java.util.List;
import java.util.concurrent.Executor;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PeopleFragment extends Fragment implements Injectable {
    private PeopleViewModel mViewModel;

    @Inject
    ViewModelProvider.Factory mViewModelFactory;

    @BindView(R.id.recyclerView)
    protected RecyclerView mRecyclerView;

    @BindView(R.id.viewError)
    protected ErrorView mErrorView;

    private LinearLayoutManager mLinearLayoutManager;
    private List<Person> people;
    private static PeopleFragment mFragment;
    private RecyclerAdapter mAdapter;
    protected Executor mMainExecutor;
    private HomeActivityCallbacks mHomeActivityCallbacks;

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
        if (context instanceof HomeActivityCallbacks) {
            mHomeActivityCallbacks = (HomeActivityCallbacks)context;
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
        mViewModel = ViewModelProviders.of(this, mViewModelFactory).get(PeopleViewModel.class);

        if (mHomeActivityCallbacks != null) {
            mHomeActivityCallbacks.onFragmentsActivityCreated(this, "People", getTag());
        }
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        subscribeToViewModel();

    }

    protected void subscribeToViewModel() {
        mViewModel.getPeopleState().observe(this, peopleResource -> {
            if (peopleResource.data != null) {
                updatePeople(peopleResource.data);
            }

            if (peopleResource.status == Status.LOADING) {
                showLoading();
                return;
            }
            if (peopleResource.status == Status.ERROR) {
                showError(peopleResource.message);
                return;
            }
            if (peopleResource.status == Status.SUCCESS) {
                hideLoading();
                return;
            }
        });
    }

    @UiThread
    protected void updatePeople(List<Person> persons) {
        people = persons;
        mAdapter = new RecyclerAdapter(persons);
        mRecyclerView.setAdapter(mAdapter);
        showContent();
    }

    @UiThread
    private void hideLoading() {
        mErrorView.hide();
    }

    @UiThread
    protected void showLoading() {
        if (people == null) {
            // Hiding content only when the people are null
            mRecyclerView.setVisibility(View.GONE);
        }
        mErrorView.showProgress("Fetching people..." );
    }

    @UiThread
    protected void showContent() {
        mRecyclerView.setVisibility(View.VISIBLE);
        mErrorView.hide();
    }

    protected void showError(String message) {
        if (people == null) {
            // Hiding content only when the people are null
            mRecyclerView.setVisibility(View.GONE);
        }
        mErrorView.showError(message);
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
