package com.bluestacks.bugzy.net;

import com.bluestacks.bugzy.models.Response;
import com.bluestacks.bugzy.models.resp.ListCasesResponse;
import com.bluestacks.bugzy.models.resp.ListPeopleResponse;
import com.bluestacks.bugzy.models.resp.LoginData;
import com.bluestacks.bugzy.models.resp.LoginRequest;
import com.bluestacks.bugzy.models.resp.MeResponse;
import com.bluestacks.bugzy.models.resp.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface FogbugzApiService {

    @POST("/f/api/0/jsonapi?cmd=logon")
    Call<Response<LoginData>> loginWithEmail(@Body LoginRequest loginRequest);

    @GET("api.asp?cmd=logon")
    User loginWithToken(@Field("token") String password);

    @GET("api.asp?cmd=logoff")
    User logout(@Field("token") String password);

    @GET("api.asp?cmd=logon")
    User login(@Query("email") String email, @Query("password") String password);

    @GET("api.asp?cmd=listCases")
    Call<ListCasesResponse> listCases(@Query("token") String token);

    @GET("api.asp?cmd=listCases")
    Call<ListCasesResponse> listCases(@Query("token") String token,@Query("cols") String cols);

    @GET("api.asp?cmd=listPeople")
    Call<ListPeopleResponse> listPeople(@Query("token") String token);

    @GET("api.asp?cmd=viewPerson")
    Call<MeResponse> getMyDetails(@Query("token") String token);
}
