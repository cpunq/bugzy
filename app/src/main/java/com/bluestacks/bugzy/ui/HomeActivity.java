package com.bluestacks.bugzy.ui;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

import android.content.Context;
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
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bluestacks.bugzy.models.Response;
import com.bluestacks.bugzy.models.resp.Filter;
import com.bluestacks.bugzy.models.resp.FiltersData;
import com.bluestacks.bugzy.models.resp.FiltersRequest;
import com.bluestacks.bugzy.ui.common.ErrorView;
import com.bluestacks.bugzy.utils.AppExecutors;
import com.bluestacks.bugzy.BaseActivity;
import com.bluestacks.bugzy.R;
import com.bluestacks.bugzy.models.resp.MyDetailsData;
import com.bluestacks.bugzy.models.resp.MyDetailsRequest;
import com.bluestacks.bugzy.models.resp.Person;
import com.bluestacks.bugzy.net.ConnectivityInterceptor;
import com.bluestacks.bugzy.net.FogbugzApiService;
import com.bluestacks.bugzy.utils.PrefsHelper;
import com.guardanis.imageloader.ImageRequest;

import org.json.JSONArray;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;

public class HomeActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener, MyCasesFragment.CasesFragmentActivityContract {
    public static final String TAG = HomeActivity.class.getName();
    private TextView mUserName;
    private TextView mUserEmail;
    private FragmentManager mFragmentManager;
    private Fragment mCurrentFragment;
    private Context context;
    private List<Filter> mFilters;
    private int mHomeNavItemId = -1;
    private HashMap<String, Integer> mNavItemTagMap = new HashMap<>();

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

    @BindView(R.id.progress_bar)
    protected ProgressBar mProgressBar;

    @Inject
    FogbugzApiService mApiClient;

    @Inject
    AppExecutors mAppExecutors;

    @Inject
    Gson mGson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        context = this;
        onViewsReady();
    }

    protected void onViewsReady() {
        mFragmentManager = getSupportFragmentManager();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        hideActionIcons();
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
        //Lock the drawer
        showFiltersIfAvailable();
    }

    @Override
    protected void onResume() {
        super.onResume();
        showUserInfoIfAvailable();
    }

    private void showFiltersIfAvailable() {
        List<Filter> filters = getFilters();
        if (getFilters() != null) {
            // Available, show these filters
            showFilters(filters);
            // And continue fetching from net
        } else {
            //show working
            showWorking();
        }
        mAppExecutors.networkIO().execute(new Runnable() {
            @Override
            public void run() {
                fetchFilters();
            }
        });
    }

    @UiThread
    private void showWorking() {
        mProgressBar.setVisibility(View.VISIBLE);
        mContentContainer.setVisibility(View.GONE);
        mErrorView.setVisibility(View.GONE);
    }

    @UiThread
    private void showError(String message) {
        if (mFilters == null) {
            mContentContainer.setVisibility(View.GONE);
            mErrorView.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.GONE);
            mErrorView.setErrorText(message);
            return;
        }

        // If filters are already present, just show a toast, or snackbar
        Snackbar.make(mContentContainer, message, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onAttachFragment(android.app.Fragment fragment) {
        super.onAttachFragment(fragment);
        Log.d(TAG, "onAttachFragment" + fragment.getClass().getName());
    }

    @UiThread
    private void showContent() {
        mContentContainer.setVisibility(View.VISIBLE);
        mErrorView.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.GONE);
    }

    private List<Filter> getFilters() {
        String filterString = mPrefs.getString(PrefsHelper.Key.FILTERS_LIST);
        if (TextUtils.isEmpty(filterString)) {
            return null;
        }
        Type typeOfObjectsList = new TypeToken<ArrayList<Filter>>() {}.getType();
        List<Filter> filters = mGson.fromJson(filterString, typeOfObjectsList);
        return filters;
    }

    @WorkerThread
    private void fetchFilters() {
//        FiltersData data = new FiltersData();
//        List<Filter> filters = new ArrayList<>();
//        for (int i = 0 ; i < 5 ; i++) {
//            Filter f = new Filter();
//            f.setFilter((100 + i) + "");
//            f.setText("My Cases + " + i);
//            f.setType("Shared");
//            filters.add(f);
//        }
//        data.setFilters(filters);
//        Response<FiltersData> response = new Response<>(data);
//        onFiltersResponse(response);

        Call<com.bluestacks.bugzy.models.Response<JsonElement>> req = mApiClient.getFilters(new FiltersRequest());
        try {
            retrofit2.Response<Response<JsonElement>> resp = req.execute();

            //TODO: handle all of this gracefully
            if(resp.isSuccessful()) {
                JsonElement body = resp.body().getData();
                Log.d("HomeActivity", body.toString());
                JsonArray filtersjson = body.getAsJsonObject().getAsJsonArray("filters");
                final List<Filter> filters = new ArrayList<>();
                for (int i = 0 ; i < filtersjson.size() ; i++) {
                    JsonElement d = filtersjson.get(i);
                    try {
                        Filter f = mGson.fromJson(d, Filter.class);
                        // Set it on disk
                        filters.add(f);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Log.d("HomeActivity", d.toString());
                }

                mPrefs.setString(PrefsHelper.Key.FILTERS_LIST, mGson.toJson(filters));

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showFilters(filters);
                    }
                });
            } else {
                showErrorMainThread("Some error, please try again");
            }
        } catch(ConnectivityInterceptor.NoConnectivityException e) {
            showErrorMainThread("Connectivity error, please try again");
        } catch (IOException e) {
            showErrorMainThread("Some error, please try again");
        }
    }

    @WorkerThread
    private void showErrorMainThread(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showError(message);
            }
        });
    }

    @UiThread
    private void onFiltersResponse(Response<FiltersData> response) {
        if (response == null) {
            // Show Error
            return;
        }
//        showFilters(response.getData().getFilters());
    }

    HashMap<Integer, Filter> mFiltersMap = new HashMap<>();

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
        int i = 0;
        MenuItem myCasesItem = null;
        for (Filter filter : mFilters) {
            // Only showing shared/saved filters
            if (!filter.getType().equals("shared") && !filter.getType().equals("saved")) {
                // Skip this, as a filter with same name already exists
                // TODO: think about this skipping, as there can be filters with different
                // TODO: types but same names
                continue;
            }
            MenuItem mi;
            int id = filter.getFilter().hashCode();

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
        if (myCasesItem != null) {
            mHomeNavItemId = myCasesItem.getItemId();
            onNavigationItemSelected(myCasesItem.setChecked(true));
        } else {
            //think of having some other menuItem as the home
        }
    }

    @UiThread
    private void showUserInfoIfAvailable() {
        if (TextUtils.isEmpty(mPrefs.getString(PrefsHelper.Key.PERSON_ID))) {
            // No userinfo, fetch from network
            mAppExecutors.networkIO().execute(new Runnable() {
                @Override
                public void run() {
                    fetchUserInfo();
                }
            });
            return;
        }
        showUserInfo();
    }

    @WorkerThread
    protected void fetchUserInfo() {
        if(!isLoggedIn()) {
          redirectLogin();
          return;
        }
        Call<Response<MyDetailsData>> req = mApiClient.getMyDetails(new MyDetailsRequest());

        try {
            retrofit2.Response<Response<MyDetailsData>> resp = req.execute();
            if(resp.isSuccessful()) {
                Person me = resp.body().getData().getPerson();
                mPrefs.setString(PrefsHelper.Key.USER_NAME, me.getFullname());
                mPrefs.setString(PrefsHelper.Key.USER_EMAIL, me.getEmail());
                mPrefs.setString(PrefsHelper.Key.PERSON_ID, me.getPersonid()+"");
                mAppExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        showUserInfo();
                    }
                });
            } else {
                Log.d("Call Failed ", resp.errorBody().toString());
            }
        }
        catch(ConnectivityInterceptor.NoConnectivityException e){
            Log.d("Connectivitity Error ", "Error");
        }
        catch (IOException e) {
            Log.d("Cases","Call Failed");
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
        } else {
            // Check if its a filter
            if (mFiltersMap.containsKey(item.getItemId())) {
                //its from a filter
                Filter f = mFiltersMap.get(item.getItemId());
                tag = "filter_" + f.getFilter();
                fragment = MyCasesFragment.getInstance(f.getFilter(), f.getText());
            }
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
    protected void showUserInfo() {
        String img_path = "https://bluestacks.fogbugz.com/default.asp?ixPerson="+mPrefs.getString(PrefsHelper.Key.PERSON_ID)+"&pg=pgAvatar&pxSize=140";
        ImageRequest.create(navigationView.getHeaderView(0).findViewById(R.id.pro))
                .setTargetUrl(img_path)
                .setFadeTransition(150)
                .execute();
        mUserName.setText(mPrefs.getString(PrefsHelper.Key.USER_NAME));
        mUserEmail.setText(mPrefs.getString(PrefsHelper.Key.USER_EMAIL));
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
    public void onContentFragmentsActivityCreated(Fragment fragment, String title, String tag) {
        this.setTitle(title);
        if (mNavItemTagMap.containsKey(tag)) {
            navigationView.getMenu().findItem(mNavItemTagMap.get(tag)).setChecked(true);
        }
    }

    @Override
    public void showActionIcons() {
    }

    @Override
    public void hideActionIcons() {
    }

    @Override
    public void showFab() {
            fab.animate().scaleX(1).scaleY(1).setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(300);
            fab.setVisibility(View.VISIBLE);
    }

    public void hideFab() {
            fab.setVisibility(View.GONE);
            fab.animate().scaleX(0).scaleY(0).setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(300);
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
