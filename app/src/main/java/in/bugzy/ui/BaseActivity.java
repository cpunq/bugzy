package in.bugzy.ui;


import in.bugzy.data.local.PrefsHelper;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import javax.inject.Inject;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;

public class BaseActivity extends AppCompatActivity implements HasSupportFragmentInjector {
    @Inject
    protected PrefsHelper mPrefs;

    @Inject
    DispatchingAndroidInjector<Fragment> fragmentInjector;

    @Override
    public AndroidInjector<Fragment> supportFragmentInjector() {
        return fragmentInjector;
    }

    private String getAccessToken() {
        return mPrefs.getString(PrefsHelper.Key.ACCESS_TOKEN, "");
    }

    protected boolean isLoggedIn() {
        return !TextUtils.isEmpty(getAccessToken());
    }
}
