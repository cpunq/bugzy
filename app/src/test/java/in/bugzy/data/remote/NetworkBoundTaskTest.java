package in.bugzy.data.remote;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import in.bugzy.data.remote.model.Error;

import android.arch.lifecycle.Observer;
import android.support.annotation.NonNull;

import java.util.function.Function;

import in.bugzy.data.model.Resource;
import in.bugzy.util.InstantAppExecutors;
import in.bugzy.utils.AppExecutors;
import okhttp3.MediaType;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.mock.Calls;

import static org.mockito.Mockito.verify;

@RunWith(JUnit4.class)
public class NetworkBoundTaskTest {
    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();

    private NetworkBoundTask<Foo> mNetworkBoundTask;
    private Function<Void, Call<Foo>> createCall;
    private Gson mGson = new GsonBuilder().create();
    private AppExecutors appExecutors;

    public NetworkBoundTaskTest() {
    }

    @Before
    public void init() {
        appExecutors = new InstantAppExecutors();

        mNetworkBoundTask = new NetworkBoundTask<Foo>(appExecutors, mGson) {
            @Override
            public void saveCallResult(@NonNull Foo result) {
            }

            @NonNull
            @Override
            protected Call<Foo> createCall() {
                return createCall.apply(null);
            }
        };
    }

    @Test
    public void successFromNetwork() {
        final Foo networkResult = new Foo(1);
        createCall = (aVoid) -> Calls.response(Response.success(networkResult));

        Observer<Resource<Foo>> observer = Mockito.mock(Observer.class);
        mNetworkBoundTask.asLiveData().observeForever(observer);

        // Execute the task
        appExecutors.networkIO().execute(mNetworkBoundTask);
        verify(observer).onChanged(Resource.loading(null));
        verify(observer).onChanged(Resource.success(networkResult));
    }


    @Test
    public void failureFromNetwork() {
        ResponseBody body = ResponseBody.create(MediaType.parse("text/html"), "error");
        createCall = (aVoid) -> Calls.response(Response.error(400, body));

        Observer<Resource<Foo>> observer = Mockito.mock(Observer.class);
        mNetworkBoundTask.asLiveData().observeForever(observer);

        // Execute the task
        appExecutors.networkIO().execute(mNetworkBoundTask);
        verify(observer).onChanged(Resource.loading(null));
        verify(observer).onChanged(Resource.error("Oops! We can't reach Fogbugz", null));
    }

    @Test
    public void failureFromFogbugz() {
        Error k = new Error("fook", null, "11");
        String content = "{\"errors\"=["+ mGson.toJson(k) +"]}";
        ResponseBody body = ResponseBody.create(MediaType.parse("application/json"),content);

        createCall = (aVoid) -> Calls.response(Response.error(500, body));
        Observer<Resource<Foo>> observer = Mockito.mock(Observer.class);
        mNetworkBoundTask.asLiveData().observeForever(observer);

        // Execute the task
        appExecutors.networkIO().execute(mNetworkBoundTask);
        verify(observer).onChanged(Resource.loading(null));
        verify(observer).onChanged(Resource.error("fook", null));
    }

    static class Foo {
        int value;

        Foo(int value) {
            this.value = value;
        }
    }
}
