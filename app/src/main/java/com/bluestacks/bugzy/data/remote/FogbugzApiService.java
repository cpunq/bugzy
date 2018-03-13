package com.bluestacks.bugzy.data.remote;

import com.google.gson.JsonElement;

import com.bluestacks.bugzy.data.remote.model.ListAreasData;
import com.bluestacks.bugzy.data.remote.model.ListMilestonesData;
import com.bluestacks.bugzy.data.remote.model.ListProjectsData;
import com.bluestacks.bugzy.data.remote.model.Request;
import com.bluestacks.bugzy.data.remote.model.Response;
import com.bluestacks.bugzy.data.remote.model.FiltersRequest;
import com.bluestacks.bugzy.data.remote.model.ListCasesData;
import com.bluestacks.bugzy.data.remote.model.ListCasesRequest;
import com.bluestacks.bugzy.data.remote.model.ListPeopleData;
import com.bluestacks.bugzy.data.remote.model.ListPeopleRequest;
import com.bluestacks.bugzy.data.remote.model.LoginData;
import com.bluestacks.bugzy.data.remote.model.LoginRequest;
import com.bluestacks.bugzy.data.remote.model.MyDetailsData;
import com.bluestacks.bugzy.data.remote.model.MyDetailsRequest;
import com.bluestacks.bugzy.data.remote.model.SearchCasesRequest;

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
    LiveData<ApiResponse<Response<ListCasesData>>> listCases(@Body ListCasesRequest request);

    @POST("/api/listPeople")
    LiveData<ApiResponse<Response<ListPeopleData>>> listPeople(@Body ListPeopleRequest request);

    @POST("/api/viewPerson")
    LiveData<ApiResponse<Response<MyDetailsData>>> getMyDetails(@Body MyDetailsRequest request);

    @POST("/api/listFilters")
    LiveData<ApiResponse<Response<JsonElement>>> getFilters(@Body FiltersRequest request);

    @POST("/api/searchCases")
    LiveData<ApiResponse<Response<ListCasesData>>> searchCases(@Body SearchCasesRequest request);

    @POST("/api/listAreas")
    LiveData<ApiResponse<Response<ListAreasData>>> getAreas(@Body Request request);

    @POST("/api/listProjects")
    LiveData<ApiResponse<Response<ListProjectsData>>> getProjects(@Body Request request);

    @POST("/api/listFixFors")
    LiveData<ApiResponse<Response<ListMilestonesData>>> getMilestones(@Body Request request);
}
