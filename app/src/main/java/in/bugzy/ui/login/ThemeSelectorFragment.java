package in.bugzy.ui.login;


import in.bugzy.R;
import in.bugzy.common.Const;

import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ThemeSelectorFragment extends Fragment {
    private LoginViewModel mLoginViewModel;

    @Inject
    ViewModelProvider.Factory mViewModelFactory;

    @BindView(R.id.radio_group_theme)
    RadioGroup mThemeRadioGroup;

    public static ThemeSelectorFragment newInstance() {
        Bundle args = new Bundle();
        ThemeSelectorFragment fragment = new ThemeSelectorFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_theme_selector, null);
        ButterKnife.bind(this, v);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mLoginViewModel = ViewModelProviders.of(getActivity(), mViewModelFactory).get(LoginViewModel.class);

        int theme = mLoginViewModel.getTheme();
        if (theme == Const.DARK_THEME) {
            mThemeRadioGroup.check(R.id.btn_darktheme);
        } else {
            mThemeRadioGroup.check(R.id.btn_lighttheme);
        }

        mThemeRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == R.id.btn_lighttheme) {
                    mLoginViewModel.themeChanged(Const.LIGHT_THEME);
                }
                if (i == R.id.btn_darktheme) {
                    mLoginViewModel.themeChanged(Const.DARK_THEME);
                }
            }
        });
    }
}
