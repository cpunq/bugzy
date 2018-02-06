package com.bluestacks.bugzy.ui;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.UiThread;
import android.support.annotation.WorkerThread;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.SubMenu;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bluestacks.bugzy.models.Response;
import com.bluestacks.bugzy.models.resp.Filter;
import com.bluestacks.bugzy.models.resp.FiltersData;
import com.bluestacks.bugzy.models.resp.FiltersRequest;
import com.bluestacks.bugzy.utils.AppExecutors;
import com.bluestacks.bugzy.BaseActivity;
import com.bluestacks.bugzy.BugzyApp;
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
        implements NavigationView.OnNavigationItemSelectedListener {
    private TextView mUserName;
    private TextView mUserEmail;
    private FragmentManager mFragmentManager;
    private Fragment mCurrentFragment;
    private Context context;
    private List<Filter> mFilters;

    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;

    @BindView(R.id.nav_view)
    protected NavigationView navigationView;

    @BindView(R.id.edit_button)
    protected ImageView mEdit;

    @BindView(R.id.assign_button)
    protected ImageView mAssign;

    @BindView(R.id.close_button)
    protected ImageView mClose;

    @BindView(R.id.fab)
    protected FloatingActionButton fab;

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


        mEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog d = new Dialog(context);
                d.requestWindowFeature(Window.FEATURE_NO_TITLE);
                d.setCancelable(false);
                d.setContentView(R.layout.edit_dialog);
                RecyclerView sd = (RecyclerView) d.findViewById(R.id.recyclerView);
                sd.setAdapter(new RecyclerAdapter(((BugzyApp) getApplication()).persons));
                d.show();
            }
        });
        hideActionIcons();
        setSupportActionBar(toolbar);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

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
        }
        mAppExecutors.networkIO().execute(new Runnable() {
            @Override
            public void run() {
                fetchFilters();
            }
        });
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
            }
        } catch(ConnectivityInterceptor.NoConnectivityException e) {
            Log.d("Connectivitity Error ", "Error");
        } catch (IOException e) {
            Log.d("Cases","Call Failed");
        }
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

    @UiThread
    private void showFilters(List<Filter> filters) {

        if (mFilters != null) {
            // Filters already present, clear the list and add new
            for (Filter filter : mFilters) {
                int id = filter.getFilter().hashCode();
                if (mFiltersMap.containsKey(id)) {
                    navigationView.getMenu().removeItem(id);
                }
            }
            // Removed
        }

        mFiltersMap.clear();
        mFilters = filters;

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
            onNavigationItemSelected(myCasesItem.setChecked(true));
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

        if (id == R.id.nav_people) {
            mCurrentFragment = PeopleFragment.getInstance();
        } else {
            // Check if its a filter
            if (mFiltersMap.containsKey(item.getItemId())) {
                //its from a filter
                mCurrentFragment = MyCasesFragment.getInstance(mFiltersMap.get(item.getItemId()).getFilter());
            }
        }

        // Insert the fragment by replacing any existing fragment
        mFragmentManager.beginTransaction()
                .replace(R.id.container_frame, mCurrentFragment)
                .setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.exit_to_right, R.anim.exit_to_left)
                .commit();

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

    public void setFragment(Fragment fragment) {
        mCurrentFragment = fragment;
        mFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.exit_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                .addToBackStack(mCurrentFragment.getTag())
                .replace(R.id.container_frame, mCurrentFragment)
                .commit();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

    public void setTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    public void showActionIcons() {
        mEdit.animate().scaleX(1).scaleY(1).setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(300);
        mAssign.animate().scaleX(1).scaleY(1).setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(300);
        mClose.animate().scaleX(1).scaleY(1).setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(300);
        mEdit.setVisibility(View.VISIBLE);
        mAssign.setVisibility(View.VISIBLE);
        mClose.setVisibility(View.VISIBLE);
    }

    public void hideActionIcons() {
        mEdit.animate().scaleX(0).scaleY(0).setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(300);
        mAssign.animate().scaleX(0).scaleY(0).setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(300);
        mClose.animate().scaleX(0).scaleY(0).setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(300);
        mEdit.setVisibility(View.GONE);
        mAssign.setVisibility(View.GONE);
        mClose.setVisibility(View.GONE);
    }

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
