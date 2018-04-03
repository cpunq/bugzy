package com.bluestacks.bugzy.ui.login;


import com.bluestacks.bugzy.R;
import com.bluestacks.bugzy.data.model.Status;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CredentialsFragment extends Fragment {
    private LoginViewModel mLoginViewModel;

    @Inject
    ViewModelProvider.Factory mViewModelFactory;

    @BindView(R.id.edittext_user_email)
    EditText mEmailView;

    @BindView(R.id.edittext_user_password)
    EditText mPasswordView;

    @BindView(R.id.tv_url_message)
    TextView mUrlMessageView;

    public static CredentialsFragment newInstance() {
        Bundle args = new Bundle();
        CredentialsFragment fragment = new CredentialsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_credentials, null);
        ButterKnife.bind(this, v);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mLoginViewModel = ViewModelProviders.of(getActivity(), mViewModelFactory).get(LoginViewModel.class);

        mEmailView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mLoginViewModel.emailChanged(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mPasswordView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mLoginViewModel.passwordChanged(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        mLoginViewModel.getLoginState().observe(this, responseResource -> {
            if (responseResource.status == Status.LOADING) {
                setInteractionEnabled(false);
                return;
            }
            setInteractionEnabled(true);
        });

        mLoginViewModel.getUrlMessage().observe(this, message -> {
            mUrlMessageView.setText(message);
        });
    }

    private void setInteractionEnabled(boolean set) {
        mPasswordView.setEnabled(set);
        mEmailView.setEnabled(set);
    }
}
