package in.bugzy.ui.about;

import com.google.gson.Gson;

import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.net.Uri;
import androidx.annotation.Nullable;
import com.google.android.material.snackbar.Snackbar;
import androidx.fragment.app.Fragment;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import in.bugzy.R;
import in.bugzy.data.model.GitUser;
import in.bugzy.data.model.Status;
import in.bugzy.ui.common.ErrorView;
import in.bugzy.ui.common.Injectable;
import in.bugzy.utils.OnItemClickListener;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AboutActivityFragment extends Fragment implements Injectable {
    private AboutFragmentViewModel mViewModel;
    private List<GitUser> mContributors;
    private ContributorsAdapter mAdapter;

    @BindView(R.id.progressbar_contributors)
    ProgressBar mContributorsProgressbar;

    @BindView(R.id.errorview_contributors)
    ErrorView mContributorsErrorView;

    @BindView(R.id.recyclerview_contributors)
    RecyclerView mContributorsRecyclerView;

    @Inject
    ViewModelProvider.Factory mViewModelFactory;

    @Inject
    Gson mGson;

    public static AboutActivityFragment newInstance() {
        Bundle args = new Bundle();
        AboutActivityFragment fragment = new AboutActivityFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_about, container, false);
        ButterKnife.bind(this, v);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setupViews();
        mViewModel = ViewModelProviders.of(this, mViewModelFactory).get(AboutFragmentViewModel.class);

        mViewModel.getContributorsLiveData().observe(this, contributorsState -> {
            if (contributorsState.data != null) {
                showContributors(contributorsState.data);
            }
            if (contributorsState.status == Status.LOADING) {
                showLoadingContributors();
            } else if (contributorsState.status == Status.ERROR) {
                showContributorsError(contributorsState.message);
            } else if (contributorsState.status == Status.SUCCESS) {
                success();
            }

        });
        mViewModel.loadData();
    }

    public void success() {
        mContributorsErrorView.hide();
        mContributorsProgressbar.setVisibility(View.GONE);
    }

    public void setupViews() {
        mAdapter = new ContributorsAdapter(this, getActivity().getTheme());
        mContributorsRecyclerView.setAdapter(mAdapter);
        mContributorsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter.setItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                GitUser user =  mContributors.get(position);
                String url = user.getHtmlUrl();
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });
    }

    @OnClick(R.id.button_rate_us)
    public void rateUsClicked() {
        String url = "https://play.google.com/store/apps/details?id=in.bugzy";
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    @OnClick(R.id.button_github)
    public void githubClicked() {
        String url = "https://github.com/cpunq/bugzy";
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    @OnClick(R.id.button_report_issue)
    public void reportIssueClicked() {
        String url = "https://github.com/cpunq/bugzy/issues";
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    @OnClick(R.id.button_libraries)
    public void externalLibrariesClicked() {
        AboutActivityViewModel viewModel = ViewModelProviders.of(getActivity(), mViewModelFactory).get(AboutActivityViewModel.class);
        viewModel.navigateToLibraries();
    }


    public void showContributors(List<GitUser> contributors) {
        mContributors = contributors;
        showContent();
        mAdapter.setData(mContributors);
        mAdapter.notifyDataSetChanged();
    }

    protected void showContent() {
        mContributorsErrorView.hide();
    }

    public void showContributorsError(String errorMessage) {
        mContributorsProgressbar.setVisibility(View.GONE);
        if (mContributors != null) {
            Snackbar.make(getView(), errorMessage, Snackbar.LENGTH_INDEFINITE)
                    .setAction("RETRY", view -> mViewModel.loadData())
                    .show();
            return;
        }
        mContributorsErrorView.showMessage(errorMessage);
        mContributorsErrorView.setOnClickListener(view -> mViewModel.loadData());
    }

    public void showLoadingContributors() {
        if (mContributors != null) {
            // Data already present
            mContributorsProgressbar.setVisibility(View.VISIBLE);
            return;
        }
        mContributorsErrorView.showProgress("Loading contributors");
    }
}
