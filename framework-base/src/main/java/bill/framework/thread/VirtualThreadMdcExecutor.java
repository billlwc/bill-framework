package bill.framework.thread;

import org.slf4j.MDC;

import java.util.Map;
import java.util.concurrent.*;

public class VirtualThreadMdcExecutor {
    private static final ThreadFactory factory = Thread.ofVirtual().name("virtual-mdc-", 0).factory();

    /**
     *
     * @return Executor
     */
    public static ExecutorService newVirtualThreadPerTaskExecutor() {
        ThreadFactory factory = Thread.ofVirtual().name("virtual-mdc-", 0).factory();
        return Executors.newThreadPerTaskExecutor(runnable -> {
            Map<String, String> contextMap = MDC.getCopyOfContextMap();
            return factory.newThread(() -> {
                try {
                    if (contextMap != null) {
                        MDC.setContextMap(contextMap);
                    }
                    runnable.run();
                } finally {
                    MDC.clear();
                }
            });
        });
    }


    /**
     * Runnable 版本
     */
    public static void start(Runnable task) {
        Map<String, String> contextMap = MDC.getCopyOfContextMap();
        Thread thread= factory.newThread(() -> {
            Map<String, String> originalContext = MDC.getCopyOfContextMap();
            try {
                if (contextMap != null) MDC.setContextMap(contextMap);
                task.run();
            } finally {
                if (originalContext != null) MDC.setContextMap(originalContext);
                else MDC.clear();
            }
        });
        thread.start();
    }

    /** Callable 版本，返回 Future */
    public static <V> Future<V> submit(Callable<V> task) {
        ExecutorService executor = Executors.newThreadPerTaskExecutor(factory);
        Map<String, String> contextMap = MDC.getCopyOfContextMap();
        return executor.submit(() -> {
            Map<String, String> originalContext = MDC.getCopyOfContextMap();
            try {
                if (contextMap != null) MDC.setContextMap(contextMap);
                return task.call();
            } finally {
                if (originalContext != null) MDC.setContextMap(originalContext);
                else MDC.clear();
            }
        });
    }
}
