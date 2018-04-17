package com.bluestacks.bugzy.ui.about;

import com.google.gson.Gson;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bluestacks.bugzy.R;
import com.bluestacks.bugzy.data.model.Status;
import com.bluestacks.bugzy.ui.common.Injectable;

import javax.inject.Inject;

/**
 * A placeholder fragment containing a simple view.
 */
public class AboutActivityFragment extends Fragment implements Injectable {
    private AboutFragmentViewModel mViewModel;

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
        return inflater.inflate(R.layout.fragment_about, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this, mViewModelFactory).get(AboutFragmentViewModel.class);

        mViewModel.getContributorsLiveData().observe(this, contributorsState -> {
            if (contributorsState.status == Status.SUCCESS) {
                Log.d("", mGson.toJson(contributorsState.data));
            }
        });
        mViewModel.loadData();
    }
}
