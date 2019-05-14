package me.homework.server.workers;

import me.homework.server.helpers.Logger;
import me.homework.server.helpers.PerformanceStats;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by Mihail on 10/24/2015.
 * Modified by Javier on 14/05/2019.
 */
public class ExecutorMonitor implements Runnable {

    /** Determines how often the monitor messages are printed. */
    private final int MONITOR_SLEEP_INTERVAL = 10000;

    /** The thread pool executor to monitor. */
    private ThreadPoolExecutor executor;

    public ExecutorMonitor(ThreadPoolExecutor pool) {
        this.executor = pool;
    }

    @Override
    public void run() {
        String s = "[%d/%d] Active: %d, Completed: %d, Task: %d, queueSize: %d";
        try {
            do {
                Logger.info(String.format(s,
                        executor.getPoolSize(),
                        executor.getCorePoolSize(),
                        executor.getActiveCount(),
                        executor.getCompletedTaskCount(),
                        executor.getTaskCount(),
                        executor.getQueue().size()));
                Logger.info("Average request handling time: " 
                    + PerformanceStats.getAverageHandlingTime());
                Thread.sleep(MONITOR_SLEEP_INTERVAL);
            } while (true);
        } catch (InterruptedException e) {
            Logger.error(e.getMessage());
        }
    }
}
