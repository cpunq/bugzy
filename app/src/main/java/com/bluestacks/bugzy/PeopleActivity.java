package com.bluestacks.bugzy;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bluestacks.bugzy.models.resp.ListPeopleResponse;
import com.bluestacks.bugzy.models.resp.Person;
import com.bluestacks.bugzy.models.resp.User;
import com.bluestacks.bugzy.net.ConnectivityInterceptor;
import com.bluestacks.bugzy.net.FogbugzApiFactory;
import com.bluestacks.bugzy.net.FogbugzApiService;
import com.bluestacks.bugzy.utils.PrefsHelper;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Response;

@EActivity
public class PeopleActivity extends BaseActivity{

        @ViewById(R.id.recyclerView)
        protected RecyclerView mRecyclerView;

        private LinearLayoutManager mLinearLayoutManager;

        private Call<User> me;
        private Call<ListPeopleResponse> mCases;
        private ListPeopleResponse myCases;
        private String mAccessToken;


        @Inject PrefsHelper mPrefs;
        @Inject FogbugzApiService mApiClient;

        private RecyclerAdapter mAdapter;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
        }


        @AfterViews
        protected void onViewsReady() {
            mLinearLayoutManager = new LinearLayoutManager(this);
            mRecyclerView.setLayoutManager(mLinearLayoutManager);
            getToken();
        }


        @Background
        protected void getToken() {

            if(TextUtils.isEmpty(mPrefs.getString(PrefsHelper.Key.ACCESS_TOKEN))) {
                me =  mApiClient.loginWithEmail("manish@bluestacks.com","junQfood_2708");
                try{
                    String result = me.execute().body().getAuthToken();
                    Log.d("Token : " , result);
                    mPrefs.setString(PrefsHelper.Key.ACCESS_TOKEN,result);
                    mPrefs.setBoolean(PrefsHelper.Key.USER_LOGGED_IN, true);
                    mAccessToken = result;
                }
                catch (IOException e) {

                }
            }
            else{
                mAccessToken = mPrefs.getString(PrefsHelper.Key.ACCESS_TOKEN);

                mCases = mApiClient.listPeople(mAccessToken);

                try {
                    Response<ListPeopleResponse> resp = mCases.execute();

                    if(resp.isSuccessful()) {
                        myCases = resp.body();
                        for(Person s : myCases.getPersons()) {
                            Log.d("Bug id",String.valueOf(s.getPersonid()));
                        }
                        updateToken(myCases.getPersons());
                        Log.d("Cases List " , myCases.toString());
                    }
                    else {
                        Log.d("Call Failed ", resp.errorBody().toString());
                    }

                }
                catch(ConnectivityInterceptor.NoConnectivityException e){
                    showConnectivityError();
                }
                catch (IOException e) {
                    Log.d("Cases","Call Failed");
                }
            }


        }

        @UiThread
        protected void updateToken(List<Person> persons) {
            mAdapter = new RecyclerAdapter(persons);
            mRecyclerView.setAdapter(mAdapter);
        }

        @UiThread
        protected void showConnectivityError() {
            Toast.makeText(this,"No internet",Toast.LENGTH_LONG).show();
        }


        public class RecyclerAdapter extends RecyclerView.Adapter<BugHolder> {

            private List<Person> mPersons;
            public RecyclerAdapter(List<Person> persons) {
                mPersons = persons ;
            }
            @Override
            public BugHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View inflatedView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.bug_item_row, parent, false);
                return new BugHolder(inflatedView);
            }

            @Override
            public void onBindViewHolder(BugHolder holder, int position) {
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
                mItemDate = (TextView) v.findViewById(R.id.item_id);
                mItemDescription = (TextView) v.findViewById(R.id.item_description);
                mPriority = (LinearLayout) v.findViewById(R.id.priority);
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
                mItemDescription.setText(person.getEmail());
                mPriority.setBackgroundColor(Color.parseColor("#ddb65b"));

            }
        }


}
