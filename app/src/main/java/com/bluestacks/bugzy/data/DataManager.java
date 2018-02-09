package com.bluestacks.bugzy.data;


import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

import com.bluestacks.bugzy.common.Const;
import com.bluestacks.bugzy.data.local.DatabaseHelper;
import com.bluestacks.bugzy.data.local.PrefsHelper;
import com.bluestacks.bugzy.data.remote.ConnectivityInterceptor;
import com.bluestacks.bugzy.data.remote.FogbugzApiService;
import com.bluestacks.bugzy.models.Error;
import com.bluestacks.bugzy.models.Response;
import com.bluestacks.bugzy.models.resp.Case;
import com.bluestacks.bugzy.models.resp.Filter;
import com.bluestacks.bugzy.models.resp.FiltersData;
import com.bluestacks.bugzy.models.resp.FiltersRequest;
import com.bluestacks.bugzy.models.resp.ListCasesData;
import com.bluestacks.bugzy.models.resp.ListCasesRequest;
import com.bluestacks.bugzy.models.resp.ListPeopleData;
import com.bluestacks.bugzy.models.resp.ListPeopleRequest;
import com.bluestacks.bugzy.models.resp.Person;
import com.bluestacks.bugzy.models.resp.SearchCasesRequest;

import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;
import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.Call;

@Singleton
public class DataManager {
    private PrefsHelper mPrefs;
    private FogbugzApiService mFogbugzApi;
    private DatabaseHelper mDbHelper;
    private Gson mGson;

    @Inject
    DataManager(PrefsHelper prefsHelper, FogbugzApiService apiService,
                DatabaseHelper dbHelper, Gson gson) {
        mPrefs = prefsHelper;
        mFogbugzApi = apiService;
        mDbHelper = dbHelper;
        mGson = gson;
    }

    /**
     * Get people from db.
     * @return list of people
     */
    public List<Person> getPeople() {
        return mDbHelper.getPeople();
    }

    public @Nullable List<Case> getCases(String filter) {
        return mDbHelper.getCases(filter);
    }

    public @Nullable List<Filter> getFilters() {
        String filterString = mPrefs.getString(PrefsHelper.Key.FILTERS_LIST);
        if (TextUtils.isEmpty(filterString)) {
            return null;
        }
        Type typeOfObjectsList = new TypeToken<ArrayList<Filter>>() {}.getType();
        List<Filter> filters = mGson.fromJson(filterString, typeOfObjectsList);
        return filters;
    }

    @WorkerThread
    public Response<FiltersData> fetchFilters() {
        FiltersData da = new FiltersData();
        Response<FiltersData> response = new Response<>(da);
        List<Error> errors = new ArrayList<>();
        // Empty error list
        response.setErrors(errors);


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

        Call<Response<JsonElement>> req = mFogbugzApi.getFilters(new FiltersRequest());
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

                da.setFilters(filters);
            } else {
                // Error from server
                String stringbody = resp.errorBody().string();
                Response<JsonElement> jsonDataResponse;
                jsonDataResponse = mGson.fromJson(stringbody, Response.class);

                // Transfer the errors to our response object
                response.getErrors().addAll(jsonDataResponse.getErrors());
            }
            return response;
        } catch(ConnectivityInterceptor.NoConnectivityException e) {
            response.setErrors(getErrorListForNoNetwork());
        } catch (IOException e) {
            response.setErrors(getErrorListForNetworkError());
        }
        return response;
    }

    @WorkerThread
    public Response<ListCasesData> fetchCases(String filter) {
        ListCasesData d = new ListCasesData();
        Response<ListCasesData> response = new Response<>(d);

        String[] cols =new String[]{
                "sTitle","ixPriority","sStatus","sProject","sPersonAssignedTo","sPersonOpenedBy"
        };
        ListCasesRequest request = new ListCasesRequest(cols, filter);
        Call<Response<ListCasesData>> cases = mFogbugzApi.listCases(request);
        try {
            retrofit2.Response<Response<ListCasesData>> req = cases.execute();
            if(req.isSuccessful()) {
                response = req.body();
                // Update cases in db
                mDbHelper.setCases(response.getData().getCases(), filter);
            } else {
                String stringbody = req.errorBody().string();
                response = mGson.fromJson(stringbody, Response.class);
            }
            return response;
        } catch(ConnectivityInterceptor.NoConnectivityException e){
            response.setErrors(getErrorListForNoNetwork());
        } catch (IOException e) {
            response.setErrors(getErrorListForNetworkError());
        }
        return response;
    }

    @WorkerThread
    public Response<ListPeopleData> fetchPeople() {
        ListPeopleData da = new ListPeopleData();
        Response<ListPeopleData> response = new Response<>(da);

        Call<com.bluestacks.bugzy.models.Response<ListPeopleData>> call = mFogbugzApi.listPeople(new ListPeopleRequest());

        try {
            retrofit2.Response<Response<ListPeopleData>> resp = call.execute();
            final com.bluestacks.bugzy.models.Response<ListPeopleData> body;
            if(resp.isSuccessful()) {
                response = resp.body();
                // Update people in db
                mDbHelper.setPeople(response.getData().getPersons());
            } else {
                Log.d("Call Failed ", resp.errorBody().toString());
                String stringbody = resp.errorBody().string();
                response = mGson.fromJson(stringbody, com.bluestacks.bugzy.models.Response.class);
            }
            return response;
        } catch(ConnectivityInterceptor.NoConnectivityException e){
            response.setErrors(getErrorListForNoNetwork());
        } catch (IOException e) {
            response.setErrors(getErrorListForNetworkError());
        }
        return response;
    }

    public Response<ListCasesData> fetchCaseDetails(int bugId) {
        ListCasesData d = new ListCasesData();
        Response<ListCasesData> response = new Response<>(d);

        String[] cols =new String[]{
                "sTitle","ixPriority","sStatus","sProject","sFixFor","sArea","sPersonAssignedTo","sPersonOpenedBy","events"
        };
        Call<Response<ListCasesData>> cases = mFogbugzApi.searchCases(new SearchCasesRequest(cols, bugId+""));
        try {
            retrofit2.Response<Response<ListCasesData>> req = cases.execute();
            if(req.isSuccessful()) {
                response = req.body();
            } else {
                String stringbody = req.errorBody().string();
                response = mGson.fromJson(stringbody, Response.class);
            }
            return response;
        } catch(ConnectivityInterceptor.NoConnectivityException e){
            response.setErrors(getErrorListForNoNetwork());
        } catch (IOException e) {
            response.setErrors(getErrorListForNetworkError());
        }
        return response;
    }

    private List<Error> getErrorListForNoNetwork() {
        List<Error> errors = new ArrayList<>();
        Error error = new Error();
        error.setCode(Const.NO_NETWORK);
        error.setMessage("Please check your connection");
        errors.add(error);
        return errors;
    }

    private List<Error> getErrorListForNetworkError() {
        List<Error> errors = new ArrayList<>();
        Error error = new Error();
        error.setCode(Const.NO_NETWORK);
        error.setMessage("Oops! We can't reach Fogbugz");
        errors.add(error);
        return errors;
    }

}
