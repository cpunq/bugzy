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

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface FogbugzApiService {

    @POST("/f/api/0/jsonapi")
    Call<Response<LoginData>> loginWithEmail(@Body LoginRequest loginRequest);

    @POST("/f/api/0/jsonapi")
    Call<Response<ListCasesData>> listCases(@Body ListCasesRequest request);

    @POST("/f/api/0/jsonapi")
    Call<Response<ListPeopleData>> listPeople(@Body ListPeopleRequest request);

    @POST("/f/api/0/jsonapi")
    Call<Response<MyDetailsData>> getMyDetails(@Body MyDetailsRequest request);

    @POST("/f/api/0/jsonapi")
    Call<Response<JsonElement>> getFilters(@Body FiltersRequest request);
}
