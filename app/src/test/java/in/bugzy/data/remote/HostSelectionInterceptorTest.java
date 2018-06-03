package in.bugzy.data.remote;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.IOException;
import java.util.function.Function;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.is;

@RunWith(JUnit4.class)
public class HostSelectionInterceptorTest {
    private HostSelectionInterceptor mHostSelectionInterceptor;
    private Function<Interceptor.Chain, Response> testRequest;
    private OkHttpClient mOkHttpClient;

    @Before
    public void init() throws Exception {
        mHostSelectionInterceptor =  new HostSelectionInterceptor();
        Interceptor testInterceptor = chain -> testRequest.apply(chain);
        mOkHttpClient = new OkHttpClient().newBuilder()
                .addInterceptor(mHostSelectionInterceptor)
                .addInterceptor(testInterceptor)
                .build();
    }

    @Test
    public void dynamicHostChange() throws Exception {
        String myHost = "myhost.com";
        String myHost2 = "hahaha.com";

        mHostSelectionInterceptor.setHost(myHost);
        testRequest = (chain) -> {
            try {
                assertThat(chain.request().url().host(), is(myHost));
                return chain.proceed(chain.request());
            } catch (IOException e) {
                return null;
            }
        };
        mOkHttpClient.newCall(new Request.Builder().url("http://hello.com/api").build()).execute();

        // Change host
        mHostSelectionInterceptor.setHost(myHost2);
        testRequest = (chain) -> {
            try {
                assertThat(chain.request().url().host(), is(myHost2));
                return chain.proceed(chain.request());
            } catch (IOException e) {
                return null;
            }
        };
        mOkHttpClient.newCall(new Request.Builder().url("http://hello.com/api").build()).execute();
    }
}
