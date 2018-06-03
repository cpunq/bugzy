package in.bugzy.util;


import android.support.annotation.NonNull;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import in.bugzy.utils.AppExecutors;


public class CountingAppExecutors {
    private final Object LOCK = new Object();
    private int taskCount = 0;
    protected AppExecutors mAppExecutors;

    public CountingAppExecutors() {
        Runnable increment = () -> {
            synchronized (LOCK) {
                taskCount--;
                if (taskCount == 0) {
                    LOCK.notifyAll();
                }
            }
        };
        Runnable decrement = () -> {
            synchronized (LOCK) {
                taskCount++;
            }
        };

        mAppExecutors = new AppExecutors(
                new CountingExecutor(increment, decrement),
                new CountingExecutor(increment, decrement),
                new CountingExecutor(increment, decrement)
        );
    }

    public AppExecutors getAppExecutors() {
        return mAppExecutors;
    }

    public void drainTasks(int time, TimeUnit timeUnit)
            throws InterruptedException, TimeoutException {
        long end = System.currentTimeMillis() + timeUnit.toMillis(time);
        while (true) {
            synchronized (LOCK) {
                if (taskCount == 0) {
                    return;
                }
                long now = System.currentTimeMillis();
                long remaining = end - now;
                if (remaining > 0) {
                    LOCK.wait(remaining);
                } else {
                    throw new TimeoutException("could not drain tasks");
                }
            }
        }
    }

    public static class CountingExecutor implements Executor {
        private final Executor delegate = Executors.newSingleThreadExecutor();
        private Runnable increment;
        private Runnable decrement;


        CountingExecutor(Runnable increment, Runnable decrement) {
            this.increment = increment;
            this.decrement = decrement;
        }

        @Override
        public void execute(@NonNull Runnable command) {
            increment.run();
            delegate.execute(() -> {
                try {
                    command.run();
                } finally {
                    decrement.run();
                }
            });
        }
    }
}
