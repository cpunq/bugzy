package com.bluestacks.bugzy.data.remote;

import com.google.gson.JsonElement;

import com.bluestacks.bugzy.models.Response;
import com.bluestacks.bugzy.models.resp.FiltersRequest;
import com.bluestacks.bugzy.models.resp.ListCasesData;
import com.bluestacks.bugzy.models.resp.ListCasesRequest;
import com.bluestacks.bugzy.models.resp.ListPeopleData;
import com.bluestacks.bugzy.models.resp.ListPeopleRequest;
import com.bluestacks.bugzy.models.resp.LoginData;
import com.bluestacks.bugzy.models.resp.LoginRequest;
import com.bluestacks.bugzy.models.resp.MyDetailsData;
import com.bluestacks.bugzy.models.resp.MyDetailsRequest;
import com.bluestacks.bugzy.models.resp.SearchCasesRequest;

import android.arch.lifecycle.LiveData;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface FogbugzApiService {

    @POST("/api/logon")
    LiveData<ApiResponse<Response<LoginData>>> loginWithEmail(@Body LoginRequest loginRequest);

    @POST("/api/logon")
    Call<Response<LoginData>> login(@Body LoginRequest loginRequest);

    @POST("/api/listCases")
    Call<Response<ListCasesData>> listCases(@Body ListCasesRequest request);

    @POST("/api/listPeople")
    Call<Response<ListPeopleData>> listPeople(@Body ListPeopleRequest request);

    @POST("/api/viewPerson")
    Call<Response<MyDetailsData>> getMyDetails(@Body MyDetailsRequest request);

    @POST("/api/listFilters")
    Call<Response<JsonElement>> getFilters(@Body FiltersRequest request);

    @POST("/api/searchCases")
    Call<Response<ListCasesData>> searchCases(@Body SearchCasesRequest request);
}
