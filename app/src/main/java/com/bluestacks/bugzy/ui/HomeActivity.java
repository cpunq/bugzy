package com.bluestacks.bugzy.ui;

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
import android.widget.Toast;

import com.bluestacks.bugzy.AppExecutors;
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

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Response;

public class HomeActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private Person me;
    private TextView mUserName;
    private TextView mUserEmail;
    private FragmentManager mFragmentManager;
    private Fragment mCurrentFragment;
    private Context context;
    private String mAccessToken;

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
    PrefsHelper mPrefs;

    @Inject
    FogbugzApiService mApiClient;

    @Inject
    AppExecutors mAppExecutors;

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
                sd.setAdapter(new RecyclerAdapter(((BugzyApp)getApplication()).persons));
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

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        onNavigationItemSelected(navigationView.getMenu().getItem(0).setChecked(true));
        mUserName = (TextView) navigationView.getHeaderView(0).findViewById(R.id.user_name);
        mUserEmail = (TextView) navigationView.getHeaderView(0).findViewById(R.id.user_email);
        mAppExecutors.networkIO().execute(new Runnable() {
            @Override
            public void run() {
                getDetails();
            }
        });
    }


    @WorkerThread
    protected void getDetails() {
        String token = mPrefs.getString(PrefsHelper.Key.ACCESS_TOKEN, "");
        if(TextUtils.isEmpty(token)) {
          redirectLogin();
        }
        else{
            mAccessToken = token;
            Call<com.bluestacks.bugzy.models.Response<MyDetailsData>> response = mApiClient.getMyDetails(new MyDetailsRequest());

            try {
                Response<com.bluestacks.bugzy.models.Response<MyDetailsData>> resp = response.execute();

                if(resp.isSuccessful()) {
                    me = resp.body().getData().getPerson();
                    mPrefs.setString(PrefsHelper.Key.USER_NAME, me.getFullname());
                    mPrefs.setString(PrefsHelper.Key.USER_EMAIL, me.getEmail());
                    mPrefs.setString(PrefsHelper.Key.PERSON_ID, me.getPersonid()+"");
                    mAppExecutors.mainThread().execute(new Runnable() {
                        @Override
                        public void run() {
                            updateUserInfo();
                        }
                    });

                }
                else {
                    Log.d("Call Failed ", resp.errorBody().toString());
                }

            }
            catch(ConnectivityInterceptor.NoConnectivityException e){
                mAppExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        showConnectivityError();
                    }
                });
            }
            catch (IOException e) {
                Log.d("Cases","Call Failed");
            }
        }


    }

    @UiThread
    protected void showConnectivityError() {
        Toast.makeText(this,"No internet",Toast.LENGTH_LONG).show();
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

        if (id == R.id.nav_camera) {
            mCurrentFragment = MyCasesFragment.getInstance();

            // Handle the camera action
        } else if (id == R.id.nav_gallery) {
            mCurrentFragment = PeopleFragment.getInstance();
        } else if (id == R.id.nav_slideshow) {
            mCurrentFragment = CaseDetailsFragment.getInstance();
            Bundle arg = new Bundle();
            arg.putString("bug_id","9605");
            mCurrentFragment.setArguments(arg);
        }

        // Insert the fragment by replacing any existing fragment

        mFragmentManager.beginTransaction()
                .replace(R.id.container_frame, mCurrentFragment)
                .setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.exit_to_right, R.anim.exit_to_left)
                .commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @UiThread
    protected void updateUserInfo() {
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
