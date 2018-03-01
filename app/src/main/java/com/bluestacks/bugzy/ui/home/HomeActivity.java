package com.bluestacks.bugzy.ui.home;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.annotation.UiThread;
import android.support.annotation.WorkerThread;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bluestacks.bugzy.data.DataManager;
import com.bluestacks.bugzy.models.Response;
import com.bluestacks.bugzy.models.Status;
import com.bluestacks.bugzy.models.resp.Case;
import com.bluestacks.bugzy.models.resp.Filter;
import com.bluestacks.bugzy.models.resp.FiltersData;
import com.bluestacks.bugzy.ui.casedetails.CaseDetailsActivity;
import com.bluestacks.bugzy.ui.common.HomeActivityCallbacks;
import com.bluestacks.bugzy.ui.login.LoginActivity;
import com.bluestacks.bugzy.ui.common.ErrorView;
import com.bluestacks.bugzy.ui.login.LoginViewModel;
import com.bluestacks.bugzy.utils.AppExecutors;
import com.bluestacks.bugzy.ui.BaseActivity;
import com.bluestacks.bugzy.R;
import com.bluestacks.bugzy.models.resp.MyDetailsData;
import com.bluestacks.bugzy.models.resp.MyDetailsRequest;
import com.bluestacks.bugzy.models.resp.Person;
import com.bluestacks.bugzy.data.remote.ConnectivityInterceptor;
import com.bluestacks.bugzy.data.remote.FogbugzApiService;
import com.bluestacks.bugzy.data.local.PrefsHelper;
import com.guardanis.imageloader.ImageRequest;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;

public class HomeActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener, HomeActivityCallbacks {
    public static final String TAG = HomeActivity.class.getName();
    private TextView mUserName;
    private TextView mUserEmail;
    private FragmentManager mFragmentManager;
    private Fragment mCurrentFragment;
    private List<Filter> mFilters;
    private int mHomeNavItemId = -1;
    private HashMap<String, Integer> mNavItemTagMap = new HashMap<>();
    private HashMap<Integer, Filter> mFiltersMap = new HashMap<>();
    private HomeViewModel mHomeViewModel;

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

    @Inject
    FogbugzApiService mApiClient;

    @Inject
    AppExecutors mAppExecutors;

    @Inject
    DataManager mDataManager;

    @Inject
    Gson mGson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        mHomeViewModel = ViewModelProviders.of(this, mViewModelFactory).get(HomeViewModel.class);

        onViewsReady();
        subscribeToViewModel();
    }

    protected void onViewsReady() {
        mFragmentManager = getSupportFragmentManager();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.setDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        mUserName = (TextView) navigationView.getHeaderView(0).findViewById(R.id.user_name);
        mUserEmail = (TextView) navigationView.getHeaderView(0).findViewById(R.id.user_email);

        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
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

    @UiThread
    private void showFilters(List<Filter> filters) {
        showContent();
        if (mFilters != null) {
            // Filters already present, clear the list and add new
            removeFiltersFromNavigationView();
            // Removed
        }

        mFiltersMap.clear();
        // Clear the navItemTagMap as well, because the navigation items have been changed
        mNavItemTagMap.clear();
        mFilters = filters;
        mHomeNavItemId = -1;

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
        // Unlock the drawer
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);

        // Set default fragment
        if (myCasesItem != null) {
            mHomeNavItemId = myCasesItem.getItemId();
            onNavigationItemSelected(myCasesItem.setChecked(true));
        }
        //think of having some other menuItem as the home
        else if (firstMenuItemId != -1) {
            // See if firstMenuItem is available
            MenuItem mi = navigationView.getMenu().findItem(firstMenuItemId);
            mHomeNavItemId = mi.getItemId();
            onNavigationItemSelected(mi.setChecked(true));
        } else {
            // Use people tab as default tab
            MenuItem mi = navigationView.getMenu().findItem(R.id.nav_people);
            mHomeNavItemId = mi.getItemId();
            onNavigationItemSelected(mi.setChecked(true));
        }
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
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
            fragment = PeopleFragment.getInstance();
            tag = "people";
        } else if (mFiltersMap.containsKey(item.getItemId())) {
            // Check if its a filter
            //its from a filter
            Filter f = mFiltersMap.get(item.getItemId());
            tag = "filter_" + f.getFilter();
            fragment = MyCasesFragment.getInstance(f.getFilter(), f.getText());
        } else {
            // Else do nothing as of now
            return true;
        }

        mNavItemTagMap.put(tag, id);
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
        ImageRequest.create(navigationView.getHeaderView(0).findViewById(R.id.pro))
                .setTargetUrl(img_path)
                .setFadeTransition(150)
                .execute();
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
        ft.commit();
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
        if (mNavItemTagMap.containsKey(tag)) {
            navigationView.getMenu().findItem(mNavItemTagMap.get(tag)).setChecked(true);
        }
    }

    public class RecyclerAdapter extends RecyclerView.Adapter<PeopleFragment.BugHolder> {

        private List<Person> mPersons;
        public RecyclerAdapter(List<Person> persons) {
            mPersons = persons ;
        }
        @Override
        public PeopleFragment.BugHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View inflatedView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.person_item, parent, false);
            return new PeopleFragment.BugHolder(inflatedView);
        }

        @Override
        public void onBindViewHolder(PeopleFragment.BugHolder holder, int position) {
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
            mItemDate = (TextView) v.findViewById(R.id.person);
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
        }
    }
}
