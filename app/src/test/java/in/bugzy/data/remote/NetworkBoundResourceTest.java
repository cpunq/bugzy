package in.bugzy.data.remote;


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


import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

import in.bugzy.data.model.Resource;
import in.bugzy.util.ApiUtil;
import in.bugzy.util.InstantAppExecutors;
import in.bugzy.utils.AppExecutors;
import retrofit2.Response;

@RunWith(Parameterized.class)
public class NetworkBoundResourceTest {
    private final boolean useRealExecutors;

    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();

    private NetworkBoundResource<Foo, Foo> networkBoundResource;

    private Function<Foo, Void> saveCallResult;
    private Function<Foo, Boolean> shouldFetch;
    private Function<Void, LiveData<ApiResponse<Foo>>> createCall;

    private MutableLiveData<Foo> dbData = new MutableLiveData<>();
    private AtomicBoolean fetchedOnce = new AtomicBoolean(false);

    @Parameterized.Parameters
    public static List<Boolean> param() {
        return Arrays.asList(true, false);
    }

    public NetworkBoundResourceTest(boolean realExecutors) {
        useRealExecutors = false;
    }

    @Before
    public void init() {
        AppExecutors appExecutors =  new InstantAppExecutors();
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
        AtomicReference<Foo> saved = new AtomicReference<>();
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
//        drain();
        verify(observer).onChanged(Resource.loading(null));
        reset(observer);
        dbData.setValue(null);
//        drain();
        assertThat(saved.get(), is(networkResult));
        verify(observer).onChanged(Resource.success(fetchedDbValue));


    }

    static class Foo {
        int value;

        Foo(int value) {
            this.value = value;
        }
    }
}
