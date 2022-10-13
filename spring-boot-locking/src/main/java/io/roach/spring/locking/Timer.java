package io.roach.spring.locking;

import java.lang.reflect.UndeclaredThrowableException;
import java.time.Duration;
import java.util.Locale;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Timer {
    private static final Logger logger = LoggerFactory.getLogger(Timer.class);

    private Timer() {
    }

    public static void timeExecution(String label, Runnable task) {
        long start = System.nanoTime();
        try {
            logger.info("Processing {}", label);
            task.run();
        } finally {
            long millis = Duration.ofNanos(System.nanoTime() - start).toMillis();
            logger.info("{} completed in {}", label, millisecondsToDisplayString(millis));
        }
    }

    public static <V> V timeExecution(String label, Callable<V> task) {
        long start = System.nanoTime();
        try {
            logger.info("Processing {}", label);
            return task.call();
        } catch (Exception e) {
            throw new UndeclaredThrowableException(e);
        } finally {
            long millis = Duration.ofNanos(System.nanoTime() - start).toMillis();
            logger.info("{} completed in {}", label, millisecondsToDisplayString(millis));
        }
    }

    public static String millisecondsToDisplayString(long timeMillis) {
        double seconds = (timeMillis / 1000.0) % 60;
        int minutes = (int) ((timeMillis / 60000) % 60);
        int hours = (int) ((timeMillis / 3600000));

        StringBuilder sb = new StringBuilder();
        if (hours > 0) {
            sb.append(String.format("%dh", hours));
        }
        if (hours > 0 || minutes > 0) {
            sb.append(String.format("%dm", minutes));
        }
        if (hours == 0 && seconds > 0) {
            sb.append(String.format(Locale.US, "%.3fs", seconds));
        }
        return sb.toString();
    }

}
