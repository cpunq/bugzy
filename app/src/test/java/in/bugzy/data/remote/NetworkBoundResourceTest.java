package in.bugzy.data.remote;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mockito;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;


import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

import in.bugzy.data.model.Resource;
import in.bugzy.data.remote.model.Error;
import in.bugzy.util.ApiUtil;
import in.bugzy.util.CountingAppExecutors;
import in.bugzy.util.InstantAppExecutors;
import in.bugzy.utils.AppExecutors;
import okhttp3.MediaType;
import okhttp3.ResponseBody;
import retrofit2.Response;

@RunWith(Parameterized.class)
public class NetworkBoundResourceTest {
    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();
    private Gson mGson = new GsonBuilder().create();

    private NetworkBoundResource<Foo, Foo> networkBoundResource;
    private CountingAppExecutors mCountingAppExecutors;

    private Function<Foo, Void> saveCallResult;
    private Function<Foo, Boolean> shouldFetch;
    private Function<Void, LiveData<ApiResponse<Foo>>> createCall;

    private MutableLiveData<Foo> dbData = new MutableLiveData<>();
    private AtomicBoolean fetchedOnce = new AtomicBoolean(false);
    private final boolean useRealExecutors;

    @Parameterized.Parameters
    public static List<Boolean> param() {
        return Arrays.asList(true, false);
    }

    public NetworkBoundResourceTest(boolean realExecutors) {
        useRealExecutors = realExecutors;
        if (useRealExecutors){
            mCountingAppExecutors = new CountingAppExecutors();
        }
    }

    @Before
    public void init() {
        AppExecutors appExecutors = useRealExecutors
                ? mCountingAppExecutors.getAppExecutors()
                : new InstantAppExecutors();

        networkBoundResource = new NetworkBoundResource<Foo, Foo>(appExecutors) {
            @Override
            protected void saveCallResult(@NonNull Foo item) {
                saveCallResult.apply(item);
            }

            @Override
            protected boolean shouldFetch(@Nullable Foo data) {
                return shouldFetch.apply(data) && fetchedOnce.compareAndSet(false, true);
            }

            @NonNull
            @Override
            protected LiveData<Foo> loadFromDb() {
                return dbData;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<Foo>> createCall() {
                return createCall.apply(null);
            }
        };
    }

    @Test
    public void basicFromNetwork() {
        AtomicReference<Foo> saved = new AtomicReference<Foo>();
        shouldFetch = Objects::isNull;
        Foo fetchedDbValue = new Foo(1);
        saveCallResult = foo -> {
            saved.set(foo);
            dbData.setValue(fetchedDbValue);
            return null;
        };
        final Foo networkResult = new Foo(1);
        createCall = (aVoid) -> ApiUtil.createCall(Response.success(networkResult));
        Observer<Resource<Foo>> observer = Mockito.mock(Observer.class);
        networkBoundResource.asLiveData().observeForever(observer);

        drain();
        verify(observer).onChanged(Resource.loading(null));
        reset(observer);
        dbData.setValue(null);
        drain();
        assertThat(saved.get(), is(networkResult));
        verify(observer).onChanged(Resource.success(fetchedDbValue));

    }

    @Test
    public void failureFromNetwork() {
        AtomicBoolean saved = new AtomicBoolean();
        shouldFetch = Objects::isNull;
        Foo fetchedDbValue = new Foo(1);
        saveCallResult = foo -> {
            saved.set(true);
            dbData.setValue(fetchedDbValue);
            return null;
        };
        Error k = new Error("foo", null, "11");
        String content = "{\"errors\"=["+ mGson.toJson(k) +"]}";
        ResponseBody body = ResponseBody.create(MediaType.parse("application/json"),content);
        createCall = (aVoid) -> ApiUtil.createCall(Response.error(500, body));


        Observer<Resource<Foo>> observer = Mockito.mock(Observer.class);
        networkBoundResource.asLiveData().observeForever(observer);

        // Drain always before verifying
        drain();
        verify(observer).onChanged(Resource.loading(null));
        reset(observer);
        dbData.setValue(null);
        drain();
        assertThat(saved.get(), is(false));
        verify(observer).onChanged(Resource.error("foo", null));
    }

    @Test
    public void dbSuccessWithoutNetwork() {
        AtomicBoolean saved = new AtomicBoolean();
        shouldFetch = Objects::isNull;
        saveCallResult = foo -> {
            saved.set(true);
            return null;
        };

        Observer<Resource<Foo>> observer = Mockito.mock(Observer.class);
        networkBoundResource.asLiveData().observeForever(observer);

        drain();
        verify(observer).onChanged(Resource.loading(null));
        reset(observer);

        Foo dbFoo = new Foo(1);
        dbData.setValue(dbFoo);

        drain();
        verify(observer).onChanged(Resource.success(dbFoo));
        assertThat(saved.get(), is(false));

        Foo dbFoo2 = new Foo(2);
        dbData.setValue(dbFoo2);

        drain();
        verify(observer).onChanged(Resource.success(dbFoo2));

        verifyNoMoreInteractions(observer);
    }

    @Test
    public void dbSuccessWithFetch() {
        Foo dbFoo = new Foo(1);
        AtomicBoolean saved = new AtomicBoolean();
        shouldFetch = (foo) -> foo == dbFoo;
        saveCallResult = foo -> {
            saved.set(true);
            return null;
        };

        Observer<Resource<Foo>> observer = Mockito.mock(Observer.class);
        networkBoundResource.asLiveData().observeForever(observer);
        MutableLiveData<ApiResponse<Foo>> apiResponseLiveData = new MutableLiveData();

        createCall = (aVoid) -> apiResponseLiveData;

        drain();
        verify(observer).onChanged(Resource.loading(null));
        reset(observer);

        dbData.setValue(dbFoo);
        drain();
        verify(observer).onChanged(Resource.loading(dbFoo));

        ResponseBody body = ResponseBody.create(MediaType.parse("text/html"), "error");
        apiResponseLiveData.setValue(new ApiResponse<>(Response.error(400, body), mGson));
        drain();
        verify(observer).onChanged(Resource.error("Oops! We can't reach Fogbugz", dbFoo));

        Foo dbFoo2 = new Foo(2);
        dbData.setValue(dbFoo2);
        drain();
        verify(observer).onChanged(Resource.error("Oops! We can't reach Fogbugz", dbFoo2));
        verifyNoMoreInteractions(observer);
    }

    @Test
    public void dbSuccessWithRefetch() {
        Foo dbFoo = new Foo(1);
        Foo dbFoo2 = new Foo(2);
        AtomicReference<Foo> saved = new AtomicReference<>();
        shouldFetch = (foo) -> foo == dbFoo;
        saveCallResult = foo -> {
            saved.set(foo);
            dbData.setValue(dbFoo2);
            return null;
        };

        MutableLiveData<ApiResponse<Foo>> apiResponseLiveData = new MutableLiveData();
        createCall = (aVoid) -> apiResponseLiveData;


        Observer<Resource<Foo>> observer = Mockito.mock(Observer.class);
        networkBoundResource.asLiveData().observeForever(observer);

        drain();
        verify(observer).onChanged(Resource.loading(null));
        reset(observer);

        dbData.setValue(dbFoo);
        drain();
        verify(observer).onChanged(Resource.loading(dbFoo));

        final Foo networkResult = new Foo(1);
        apiResponseLiveData.setValue(new ApiResponse<>(Response.success(networkResult), mGson));

        drain();
        assertThat(saved.get(), is(networkResult));
        verify(observer).onChanged(Resource.success(dbFoo2));
        verifyNoMoreInteractions(observer);
    }

    public void drain() {
        if (!useRealExecutors) {
            return;
        }
        try {
            mCountingAppExecutors.drainTasks(1, TimeUnit.SECONDS);
        } catch (Throwable t) {
            throw new AssertionError(t);
        }
    }

    static class Foo {
        int value;

        Foo(int value) {
            this.value = value;
        }
    }
}
