package in.bugzy.util;

import java.util.concurrent.Executor;

import in.bugzy.utils.AppExecutors;


public class InstantAppExecutors extends AppExecutors {
    private static Executor instant = command -> command.run();

    public InstantAppExecutors() {
        super(instant, instant, instant);
    }
}
