package in.bugzy.ui.login;


import in.bugzy.R;
import in.bugzy.data.model.Status;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OrganisationFrgment extends Fragment{
    private LoginViewModel mLoginViewModel;

    @Inject
    ViewModelProvider.Factory mViewModelFactory;

    @BindView(R.id.et_org_name)
    EditText mOrgNameView;

    @BindView(R.id.iv_org_logo)
    ImageView mOrgImageView;

    @BindView(R.id.progress_bar_logo)
    ProgressBar mLogoProgressBar;

    public static OrganisationFrgment newInstance() {
        Bundle args = new Bundle();
        OrganisationFrgment fragment = new OrganisationFrgment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_enter_organisation, null);
        ButterKnife.bind(this, v);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mLoginViewModel = ViewModelProviders.of(getActivity(), mViewModelFactory).get(LoginViewModel.class);
        mLogoProgressBar.setVisibility(View.GONE);

        mOrgNameView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mOrgImageView.setVisibility(View.GONE);
                mLoginViewModel.organisationNameChanged(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mLoginViewModel.getOrganisationLogoResource().observe(this, resourceState -> {
            if (resourceState.status == Status.LOADING) {
                mLogoProgressBar.setVisibility(View.VISIBLE);
            }
            if (resourceState.status == Status.ERROR) {
                mLogoProgressBar.setVisibility(View.GONE);
            }
            if (TextUtils.isEmpty(resourceState.data)) {
                return;
            }
            mOrgImageView.setVisibility(View.VISIBLE);
            Glide.with(getContext())
                    .load(resourceState.data)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            mLogoProgressBar.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            mLogoProgressBar.setVisibility(View.GONE);
                            return false;
                        }
                    })
//                    .thumbnail(Glide.with(getContext()).load(R.drawable.loading_ring))
                    .into(mOrgImageView);
        });
    }
}
