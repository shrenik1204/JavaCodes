package helper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by akash.mercer on 24-Oct-17.
 */

public class ExecutorServiceHelper {

    private static ExecutorServiceHelper executorServiceHelper;

    private static ExecutorService executorService;

    private static final int THREAD_POOL_COUNT = 5;

    private ExecutorServiceHelper() {

    }

    public static synchronized ExecutorServiceHelper getInstance() {
        if (executorServiceHelper == null) {
            executorServiceHelper = new ExecutorServiceHelper();
        }

        return executorServiceHelper;
    }

    public ExecutorService getExecutorService() {
        if (executorService == null || executorService.isShutdown()) {
            executorService = Executors.newFixedThreadPool(THREAD_POOL_COUNT);
        }

        return executorService;
    }

    public boolean shutdown() throws InterruptedException {
        executorService.shutdown();

        return executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.MINUTES);
    }

    public boolean shutdownNow() throws InterruptedException {
        executorService.shutdownNow();

        return executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.MINUTES);
    }
}
