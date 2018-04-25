package in.bugzy.data.remote;

import com.google.gson.JsonElement;

import in.bugzy.data.remote.model.CaseEditRequest;
import in.bugzy.data.remote.model.ClearBitCompanyInfo;
import in.bugzy.data.remote.model.EditCaseData;
import in.bugzy.data.remote.model.ListAreasData;
import in.bugzy.data.remote.model.ListCategoriesData;
import in.bugzy.data.remote.model.ListMilestonesData;
import in.bugzy.data.remote.model.ListPrioritiesData;
import in.bugzy.data.remote.model.ListProjectsData;
import in.bugzy.data.remote.model.ListStatusesData;
import in.bugzy.data.remote.model.ListTagsData;
import in.bugzy.data.remote.model.Request;
import in.bugzy.data.remote.model.Response;
import in.bugzy.data.remote.model.FiltersRequest;
import in.bugzy.data.remote.model.ListCasesData;
import in.bugzy.data.remote.model.ListCasesRequest;
import in.bugzy.data.remote.model.ListPeopleData;
import in.bugzy.data.remote.model.ListPeopleRequest;
import in.bugzy.data.remote.model.LoginData;
import in.bugzy.data.remote.model.LoginRequest;
import in.bugzy.data.remote.model.MyDetailsData;
import in.bugzy.data.remote.model.MyDetailsRequest;
import in.bugzy.data.remote.model.SearchCasesRequest;

import android.arch.lifecycle.LiveData;

import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;
import retrofit2.http.Url;

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

    @POST("/api/searchCases")
    Call<Response<ListCasesData>> searchCasesCall(@Body SearchCasesRequest request);

    @POST("/api/listAreas")
    LiveData<ApiResponse<Response<ListAreasData>>> getAreas(@Body Request request);

    @POST("/api/listProjects")
    LiveData<ApiResponse<Response<ListProjectsData>>> getProjects(@Body Request request);

    @POST("/api/listFixFors")
    LiveData<ApiResponse<Response<ListMilestonesData>>> getMilestones(@Body Request request);

    @POST("/api/listPriorities")
    LiveData<ApiResponse<Response<ListPrioritiesData>>> getPriorities(@Body Request request);

    @POST("/api/listStatuses")
    LiveData<ApiResponse<Response<ListStatusesData>>> getStatuses(@Body Request request);

    @POST("/api/listTags")
    LiveData<ApiResponse<Response<ListTagsData>>> getTags(@Body Request request);

    @POST("/api/listCategories")
    LiveData<ApiResponse<Response<ListCategoriesData>>> getCategories(@Body Request request);

    @Multipart
    @POST("/api/edit")
    Call<Response<EditCaseData>> editCase(@Part("request") CaseEditRequest request, @Part List<MultipartBody.Part> attachments);

    @Multipart
    @POST("/api/new")
    Call<Response<EditCaseData>> newCase(@Part("request") CaseEditRequest request, @Part List<MultipartBody.Part> attachments);

    @Multipart
    @POST("/api/resolve")
    Call<Response<EditCaseData>> resolveCase(@Part("request") CaseEditRequest request, @Part List<MultipartBody.Part> attachments);

    @Multipart
    @POST("/api/close")
    Call<Response<EditCaseData>> closeCase(@Part("request") CaseEditRequest request, @Part List<MultipartBody.Part> attachments);

    @Multipart
    @POST("/api/reopen")
    Call<Response<EditCaseData>> reopenCase(@Part("request") CaseEditRequest request, @Part List<MultipartBody.Part> attachments);

    @Multipart
    @POST("/api/reactivate")
    Call<Response<EditCaseData>> reactivateCase(@Part("request") CaseEditRequest request, @Part List<MultipartBody.Part> attachments);


    @GET
    Call<List<ClearBitCompanyInfo>> getCompanyLogo(@Url String url, @Query("query") String query);
}
