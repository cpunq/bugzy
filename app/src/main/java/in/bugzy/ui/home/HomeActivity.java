package in.bugzy.ui.home;

import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import android.content.Intent;
import androidx.annotation.UiThread;
import androidx.fragment.app.FragmentManager;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AlertDialog;
import android.view.Menu;
import android.view.View;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import in.bugzy.BugzyApp;
import in.bugzy.common.Const;
import in.bugzy.data.model.Status;
import in.bugzy.data.model.Case;
import in.bugzy.data.model.Filter;
import in.bugzy.ui.about.AboutActivity;
import in.bugzy.ui.casedetails.CaseDetailsActivity;
import in.bugzy.ui.common.BugzyAlertDialog;
import in.bugzy.ui.common.HomeActivityCallbacks;
import in.bugzy.ui.editcase.CaseEditActivity;
import in.bugzy.ui.login.LoginActivity;
import in.bugzy.ui.common.ErrorView;
import in.bugzy.ui.BaseActivity;
import in.bugzy.R;
import in.bugzy.data.model.Person;
import in.bugzy.ui.search.SearchActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener, HomeActivityCallbacks {
    public static final String TAG = HomeActivity.class.getName();
    private TextView mUserName;
    private TextView mUserEmail;
    private FragmentManager mFragmentManager;
    private Fragment mCurrentFragment;
    private List<Filter> mFilters;
    private int mHomeNavItemId = -1;
    private int mAppliedTheme;
    private HashMap<String, Integer> mNavItemTagMap = new HashMap<>();
    private HashMap<Integer, Filter> mFiltersMap = new HashMap<>();
    private HomeViewModel mHomeViewModel;
    private AlertDialog mLogoutDialog;

    @Inject
    ViewModelProvider.Factory mViewModelFactory;

    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;

    @BindView(R.id.nav_view)
    protected NavigationView navigationView;

    @BindView(R.id.fab)
    protected FloatingActionButton fab;

    @BindView(R.id.error_view)
    protected ErrorView mErrorView;

    @BindView(R.id.container_frame)
    protected FrameLayout mContentContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setAppliedTheme();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        mHomeViewModel = ViewModelProviders.of(this, mViewModelFactory).get(HomeViewModel.class);

        onViewsReady();
        subscribeToViewModel();
    }

    private void setAppliedTheme() {
        mAppliedTheme = ((BugzyApp)getApplication()).getAppliedTheme();
        if(((BugzyApp)getApplication()).getAppliedTheme() == Const.DARK_THEME)  {
            setTheme(R.style.AppTheme_Dark);
        } else {
            // Light Theme
            setTheme(R.style.AppTheme);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    protected void onViewsReady() {
        mFragmentManager = getSupportFragmentManager();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.setDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        mUserName = navigationView.getHeaderView(0).findViewById(R.id.user_name);
        mUserEmail = navigationView.getHeaderView(0).findViewById(R.id.user_email);

        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        setupFab();
    }

    protected void setupFab() {
        fab.setOnClickListener(viewRef -> {
            Intent i = new Intent(this, CaseEditActivity.class);
            i.putExtra(CaseEditActivity.PARAM_MODE, CaseEditActivity.MODE_NEW);
            startActivity(i);
        });
    }

    private void subscribeToViewModel() {
        mHomeViewModel.getIsLoggedIn().observe(this, loggedIn -> {
            if (!loggedIn) {
                redirectLogin();
            }
        });
        mHomeViewModel.getFiltersState().observe(this, filtersDataResource -> {
            if (filtersDataResource.data != null) {
                showFilters(filtersDataResource.data.getFilters());
            }
            if (filtersDataResource.status == Status.LOADING) {
                showWorking();
                return;
            }
            if (filtersDataResource.status == Status.ERROR) {
                showError(filtersDataResource.message);
                return;
            }
            if (filtersDataResource.status == Status.SUCCESS) {
                // TODO: hide working
            }
        });
        mHomeViewModel.getMyDetailsState().observe(this, personResource -> {
            if (personResource.data != null) {
                showUserInfo(personResource.data);
            }
        });
    }

    @UiThread
    private void showWorking() {
        if (mFilters == null) {
            mContentContainer.setVisibility(View.GONE);
            mErrorView.showProgress("Fetching filters..");
        } else {
            // TODO: show some small working, or refreshing
        }
    }

    @UiThread
    private void showError(String message) {
        if (mFilters == null) {
            mContentContainer.setVisibility(View.GONE);
            mErrorView.showError(message);
            return;
        }
        // If filters are already present, just show a toast, or snackbar
        Snackbar.make(mContentContainer, message, Snackbar.LENGTH_LONG).show();
    }

    @UiThread
    private void showContent() {
        mContentContainer.setVisibility(View.VISIBLE);
        mErrorView.hide();
    }

    @Override
    public void onCaseSelected(Case cas) {
        Intent i = new Intent(this, CaseDetailsActivity.class);
        Bundle arg = new Bundle();
        arg.putString("bug_id", String.valueOf(cas.getIxBug()));
        arg.putSerializable("bug", cas);
        i.putExtras(arg);
        this.startActivity(i);
    }

    private void removeFiltersFromNavigationView() {
        for (Filter filter : mFilters) {
            int id = filter.getFilter().hashCode();
            if (mFiltersMap.containsKey(id)) {
                navigationView.getMenu().removeItem(id);
            }
        }
    }

    /**
     * - Clears the existing navigation data and prepares new
     *   as per the given filters
     * @param filterList
     * @return a default menuItemId
     */
    private int prepareNavHelperData(List<Filter> filterList) {
        if (filterList == null)  {
            return -1;
        }
        // Clear previous data if any
        mFiltersMap.clear();
        // Clear the navItemTagMap as well, because the navigation items have been changed
        mNavItemTagMap.clear();

        // Add the new filters to navigation view
        int firstMenuItemId = -1;
        MenuItem myCasesItem = null;
        for (Filter filter : mFilters) {
            // Only showing shared/saved filters
            if (!filter.getType().equals("shared") && !filter.getType().equals("saved")) {
                // Skip any other type of filter
                continue;
            }
            MenuItem mi;
            int id = filter.getFilter().hashCode();
            if (firstMenuItemId == -1) {
                firstMenuItemId = id;
            }
            String tag = "filter_" + filter.getFilter();
            mNavItemTagMap.put(tag, id);

            if (filter.getText().toLowerCase().contains("my cases")) {
                // Ensuring that my cases item appears on the top
                mi = navigationView.getMenu().add(R.id.group_filters, id , 0, filter.getText());
                myCasesItem = mi;
            } else {
                mi = navigationView.getMenu().add(R.id.group_filters, id, 1, filter.getText());
            }
            mi.setCheckable(true);
            mFiltersMap.put(id, filter);
        }
        // Add the people tag
        mNavItemTagMap.put("people", R.id.nav_people);

        if (myCasesItem != null){
            return myCasesItem.getItemId();
        } else if (firstMenuItemId != -1) {
            return firstMenuItemId;
        } else {
            return R.id.nav_people;
        }
    }

    @UiThread
    private void showFilters(List<Filter> filters) {
        showContent();
        if (mFilters != null) {
            // Filters already present, clear the list and add new
            removeFiltersFromNavigationView();
            // Removed
        }
        mFilters = filters;
        int defaultItemId = prepareNavHelperData(filters);
        mHomeNavItemId = defaultItemId;

        // Unlock the drawer
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);

        if (mCurrentFragment != null) {
            /**
             * This can happen in case of an orientation change or when user navigates
             * back to this activity. The currently active fragment will call
             * {@link #onFragmentsActivityCreated(Fragment, String, String)} before the
             * filters get loaded.
             * And hence, we won't go forward to select the default fragment
             */
            String tag = mCurrentFragment.getTag();
            if (mNavItemTagMap.containsKey(tag)) {
                navigationView.getMenu().findItem(mNavItemTagMap.get(tag)).setChecked(true);
            }
            // If there already is a selected Fragment, don't select a new one
            return;
        }

        // Set default fragment
        MenuItem mi = navigationView.getMenu().findItem(defaultItemId);
        mHomeNavItemId = mi.getItemId();
        onNavigationItemSelected(mi.setChecked(true));
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        int count = getFragmentManager().getBackStackEntryCount();

        if (count == 0) {
            super.onBackPressed();
            //additional code
        } else {
            getFragmentManager().popBackStack();
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            }
        }
    }

    /**
     * A small factory method
     * @return
     */
    private BugzyAlertDialog getDialogInstance() {
        if (mAppliedTheme == Const.DARK_THEME) {
            return new BugzyAlertDialog(this, R.style.CaseEditTheme_AlertDialog_Dark);
        } else {
            return new BugzyAlertDialog(this, R.style.CaseEditTheme_AlertDialog);
        }
    }

    private AlertDialog getLogoutDialog() {
        BugzyAlertDialog dialog = getDialogInstance();
        dialog.setTitle("Are you sure?");
        dialog.setMessage("This will remove all your cached data.");
        dialog.setPositiveButtonText("Logout");
        dialog.setNegativeButtonText("Cancel");
        dialog.setOnPositiveButtonClickListener(view -> {
            mHomeViewModel.logout();
        });
        dialog.setOnNegativeButtonClickListener(view -> {
            if (mLogoutDialog != null && mLogoutDialog.isShowing()) {
                mLogoutDialog.dismiss();
            }
        });
        dialog.setCancelable(false);
        return dialog;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mLogoutDialog != null && mLogoutDialog.isShowing()) {
            mLogoutDialog.dismiss();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            Intent intent = new Intent(this, SearchActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Fragment fragment = null;
        String tag = null;
        boolean addToBackstack = true;

        if (id == R.id.nav_people) {
            if (mCurrentFragment != null && "people".equals(mCurrentFragment.getTag())) {
                // Do not open the same fragment again
                return true;
            }
            fragment = PeopleFragment.getInstance();
            tag = "people";
        } else if (mFiltersMap.containsKey(item.getItemId())) {
            Filter f = mFiltersMap.get(item.getItemId());
            tag = "filter_" + f.getFilter();
            if (mCurrentFragment != null && tag.equals(mCurrentFragment.getTag())) {
                // Do not open the same fragment again
                return true;
            }
            fragment = MyCasesFragment.getInstance(f.getFilter(), f.getText());
        } else if (id == R.id.nav_logout) {
            mLogoutDialog = getLogoutDialog();
            mLogoutDialog.show();
            return true;
        } else if (id == R.id.nav_about) {
            startActivity(new Intent(this, AboutActivity.class));
            return true;
        } else {
            // Else do nothing as of now
            return true;
        }

        if (id == mHomeNavItemId) {
            // If HOME item then skip backstack
            addToBackstack = false;
        }
        setContentFragment(fragment, addToBackstack, tag);
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @UiThread
    protected void showUserInfo(Person p) {
        String img_path = "https://bluestacks.fogbugz.com/default.asp?ixPerson="+p.getPersonid()+"&pg=pgAvatar&pxSize=140";
        ImageView iv = navigationView.getHeaderView(0).findViewById(R.id.pro);
        Glide.with(this)
                .load(img_path)
                .apply(RequestOptions.circleCropTransform())
                .into(iv);
        mUserName.setText(p.getFullname());
        mUserEmail.setText(p.getEmail());
    }

    public void redirectLogin() {
        Intent mLogin = new Intent(this,LoginActivity.class);
        startActivity(mLogin);
        finish();
    }

    @Override
    public void setContentFragment(Fragment fragment, boolean addToBackStack, String tag) {
        mCurrentFragment = fragment;
        FragmentTransaction ft = mFragmentManager.beginTransaction()
                .replace(R.id.container_frame, mCurrentFragment, tag);

        //TODO: use customAnimation only when we are going one level deeper in the navigation
//                .setCustomAnimations(R.anim.exit_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)

        if (mNavItemTagMap.containsKey(tag) && mHomeNavItemId == mNavItemTagMap.get(tag)) {
            clearBackStack();
        }

        if (addToBackStack) {
            ft.addToBackStack(null);
        }
        ft.commitAllowingStateLoss();
    }

    public void clearBackStack() {
        FragmentManager fm = getSupportFragmentManager();
        for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
            fm.popBackStack();
        }
    }

    @Override
    public void setTitle(String title) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }
    }

    @Override
    public void onFragmentsActivityCreated(Fragment fragment, String title, String tag) {
        this.setTitle(title);
        mCurrentFragment = fragment;
        if (mNavItemTagMap.containsKey(tag)) {
            navigationView.getMenu().findItem(mNavItemTagMap.get(tag)).setChecked(true);
        }
    }
}
