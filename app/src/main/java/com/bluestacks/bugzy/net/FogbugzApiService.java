package com.bluestacks.bugzy.net;

import com.bluestacks.bugzy.models.Response;
import com.bluestacks.bugzy.models.resp.ListCasesData;
import com.bluestacks.bugzy.models.resp.ListCasesRequest;
import com.bluestacks.bugzy.models.resp.ListPeopleResponse;
import com.bluestacks.bugzy.models.resp.LoginData;
import com.bluestacks.bugzy.models.resp.LoginRequest;
import com.bluestacks.bugzy.models.resp.MeResponse;
import com.bluestacks.bugzy.models.resp.MyDetailsData;
import com.bluestacks.bugzy.models.resp.MyDetailsRequest;
import com.bluestacks.bugzy.models.resp.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface FogbugzApiService {

    @POST("/f/api/0/jsonapi")
    Call<Response<LoginData>> loginWithEmail(@Body LoginRequest loginRequest);

    @GET("api.asp?cmd=logon")
    User loginWithToken(@Field("token") String password);

    @GET("api.asp?cmd=logoff")
    User logout(@Field("token") String password);

    @GET("api.asp?cmd=logon")
    User login(@Query("email") String email, @Query("password") String password);

    @GET("api.asp?cmd=listCases")
    Call<ListCasesData> listCases(@Query("token") String token);

    @POST("/f/api/0/jsonapi")
    Call<Response<ListCasesData>> listCases(@Body ListCasesRequest request);

    @GET("api.asp?cmd=listPeople")
    Call<ListPeopleResponse> listPeople(@Query("token") String token);

    @POST("/f/api/0/jsonapi")
    Call<Response<MyDetailsData>> getMyDetails(@Body MyDetailsRequest request);
}
