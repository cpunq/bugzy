package com.bluestacks.bugzy.ui.login;


import com.bluestacks.bugzy.R;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class CredentialsFragment extends Fragment {
    public static CredentialsFragment newInstance() {
        Bundle args = new Bundle();
        CredentialsFragment fragment = new CredentialsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_credentials, null);
    }
}
