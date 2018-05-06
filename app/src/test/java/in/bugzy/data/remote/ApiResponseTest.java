package in.bugzy.data.remote;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import in.bugzy.common.Const;
import in.bugzy.data.remote.model.Error;
import okhttp3.MediaType;
import okhttp3.ResponseBody;
import retrofit2.Response;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.CoreMatchers.*;

@RunWith(JUnit4.class)
public class ApiResponseTest {
    Gson mGson = new GsonBuilder().create();

    @Test
    public void exception() {
        Exception e = new Exception("foo");
        ApiResponse<String> apiResponse = new ApiResponse<>(e, mGson);
        assertThat(apiResponse.body, nullValue());
        assertThat(apiResponse.httpCode, is(500));
        assertThat(apiResponse.errorMessage, is("foo"));
    }

    @Test
    public void success() {
        ApiResponse<String> apiResponse = new ApiResponse<>(Response.success("foo"), mGson);
        assertThat(apiResponse.body, is("foo"));
        assertThat(apiResponse.httpCode, is(200));
        assertThat(apiResponse.errorMessage, nullValue());
    }

    @Test
    public void networkError() {
        ApiResponse<String> apiResponse = new ApiResponse<String>(
                Response.error(404, ResponseBody.create(MediaType.parse("application/text"),  "sdg")),
                mGson
        );
        assertThat(apiResponse.body, nullValue());
        assertThat(apiResponse.httpCode, is(404));
        assertThat(apiResponse.fbCode, is(Const.NETWORK_ERROR));
    }

    @Test
    public void fogBugzError() {
        Error k = new Error("foo", null, "11");
        String content = "{\"errors\"=["+ mGson.toJson(k) +"]}";
        ApiResponse<String> apiResponse = new ApiResponse<String>(
                Response.error(500, ResponseBody.create(MediaType.parse("application/json"),
                        content)),
                mGson
        );
        assertThat(apiResponse.body, notNullValue());
        assertThat(apiResponse.httpCode, is(500));
        assertThat(apiResponse.errorMessage, is("foo"));
    }
}
