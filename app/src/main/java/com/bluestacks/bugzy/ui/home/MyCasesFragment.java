package com.bluestacks.bugzy.ui.home;

import com.google.gson.Gson;

import android.content.Context;
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
import android.widget.TextView;

import com.bluestacks.bugzy.ui.common.ErrorView;
import com.bluestacks.bugzy.ui.common.Injectable;
import com.bluestacks.bugzy.ui.common.HomeActivityCallbacks;
import com.bluestacks.bugzy.utils.AppExecutors;
import com.bluestacks.bugzy.R;
import com.bluestacks.bugzy.models.Response;
import com.bluestacks.bugzy.models.resp.Case;
import com.bluestacks.bugzy.models.resp.ListCasesData;
import com.bluestacks.bugzy.models.resp.ListCasesRequest;
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

public class MyCasesFragment extends Fragment implements Injectable {
    private static final String PARAM_FILTER = "filter";
    private static final String PARAM_FILTER_TEXT = "filter_text";
    @Inject
    PrefsHelper mPrefs;

    @Inject
    FogbugzApiService mApiClient;

    @Inject
    Gson mGson;

    @Inject
    AppExecutors mAppExecutors;

    @BindView(R.id.recyclerView)
    protected RecyclerView mRecyclerView;

    @BindView(R.id.viewError)
    protected ErrorView mErrorView;

    /**
     * - will refer to mAppExecutor.mainThread()
     */
    private Executor mMainThreadExecutor;
    private LinearLayoutManager mLinearLayoutManager;
    private List<Case> myCases;
    private String mFilter;
    private String mFilterText;
    private static MyCasesFragment mFragment;
    private HomeActivityCallbacks mHomeActivityCallbacks;
    private RecyclerAdapter mAdapter;

    public static MyCasesFragment getInstance(String filter, String filterText) {
        mFragment = new MyCasesFragment();
        Bundle args = new Bundle();
        args.putString(PARAM_FILTER, filter);
        args.putString(PARAM_FILTER_TEXT, filterText);
        mFragment.setArguments(args);
        return mFragment;
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
        mMainThreadExecutor = mAppExecutors.mainThread();
        if (mHomeActivityCallbacks != null) {
            mHomeActivityCallbacks.onFragmentsActivityCreated(this, mFilterText, getTag());
        }
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        if(myCases == null) {
            mAppExecutors.networkIO().execute(new Runnable() {
                @Override
                public void run() {
                    fetchCases();
                }
            });
        }
        else {
            showCases(myCases);
        }
    }

    @WorkerThread
    protected void fetchCases() {
        if(TextUtils.isEmpty(mPrefs.getString(PrefsHelper.Key.ACCESS_TOKEN))) {
            return;
        }
        String[] cols =new String[]{
                "sTitle","ixPriority","sStatus","sProject","sFixFor","sArea","sPersonAssignedTo","sPersonOpenedBy","events"
        };

        ListCasesRequest request = new ListCasesRequest(cols, mFilter);
        Call<Response<ListCasesData>> cases = mApiClient.listCases(request);

        try {
            mMainThreadExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    showLoading();
                }
            });
            retrofit2.Response<Response<ListCasesData>> req = cases.execute();
            final Response<ListCasesData> response;

            if(req.isSuccessful()) {
                response = req.body();
            } else {
                String stringbody = req.errorBody().string();
                response = mGson.fromJson(stringbody, Response.class);
                Log.d("Call Failed " , req.errorBody().toString());
            }
            mMainThreadExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    onCasesResponse(response);
                }
            });
        } catch(ConnectivityInterceptor.NoConnectivityException e){
            Log.d("Cases","NoConnectivity");
            mMainThreadExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    onCasesResponse(null);
                }
            });
        } catch (IOException e) {
            Log.d("Cases","Call Failed");
            mMainThreadExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    onCasesResponse(null);
                }
            });
        }
    }

    @UiThread
    private void onCasesResponse(Response<ListCasesData> response) {
        hideLoading();
        if (response == null) {
            showError("Could not fetch cases");
            return;
        }
        if (response.getErrors().size() > 0) {
            showError(response.getErrors().get(0).getMessage());
            return;
        }
        // All good
        myCases = response.getData().getCases();
        Log.d("Cases List " , myCases.toString());
        showCases(myCases);
    }

    @UiThread
    protected void showCases(List<Case> cases) {
        showContent();
        mAdapter = new RecyclerAdapter(cases);
        mRecyclerView.setAdapter(mAdapter);
    }

    @UiThread
    private void hideLoading() {
        mErrorView.hide();
    }

    @UiThread
    protected void showLoading() {
        mRecyclerView.setVisibility(View.GONE);
        mErrorView.showProgress("Fetching " + mFilterText + "..." );
    }

    @UiThread
    protected void showContent() {
        mRecyclerView.setVisibility(View.VISIBLE);
        mErrorView.hide();
    }

    @UiThread
    private void showError(String message) {
        mRecyclerView.setVisibility(View.GONE);
        mErrorView.showError(message);
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
            return new BugHolder(inflatedView, mHomeActivityCallbacks);
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
        @Nullable
        private HomeActivityCallbacks mHomeActivityCallbacks;

        //4
        public BugHolder (View v, HomeActivityCallbacks a) {
            super(v);
            mItemDate = (TextView) v.findViewById(R.id.item_id);
            mItemDescription = (TextView) v.findViewById(R.id.item_description);
            mPriority = (LinearLayout) v.findViewById(R.id.priority);
            mHomeActivityCallbacks = a;
            v.setOnClickListener(this);
        }

        //5
        @Override
        public void onClick(View v) {
            if (mHomeActivityCallbacks != null) {
                mHomeActivityCallbacks.onCaseSelected(mBug);
            }
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
