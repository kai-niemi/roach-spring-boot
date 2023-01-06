package io.roach.spring.order.changefeed;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.concurrent.TimeUnit;

import org.springframework.util.StringUtils;

/**
 * Utility for parsing CockroachDB hybrid-logical clock (HLC) timestamps. These
 * are composed of a physical component always close to local wall time and
 * a logical component used to distinguish between events with the same physical
 * component.
 */
public class LogicalTimestamp implements Comparable<LogicalTimestamp> {
    /**
     * Parse a HLC timestamp tuple to a LogicalTimestamp.
     * <p>
     * The HLC string is a tuple (NNN.NNN) with the integer part in wall clock time in nanoseconds.
     * The fractional part is the logical counter, a 10-digit integer which is ignored.
     *
     * @param hlc a HLC tuple
     * @return an instant if the HLC is non-empty
     * @throws IllegalArgumentException if the tuple is malformed (NNN.NNN)
     */
    public static LogicalTimestamp parse(String hlc) {
        if (hlc == null) {
            return null;
        }
        if (!StringUtils.hasLength(hlc)) {
            throw new IllegalArgumentException("Empty HLC");
        }
        String[] tuple = split(hlc);
        long physical = TimeUnit.NANOSECONDS.toNanos(Long.parseLong(tuple[0]));
        int logical = Integer.parseInt(tuple[1]);
        return new LogicalTimestamp(physical, logical);
    }

    private static String[] split(String tuple) {
        String[] parts = tuple.split("\\.");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Bad syntax: " + tuple);
        }
        return parts;
    }

    private final long physicalWallClockTimeNanos;

    private final int logicalCounter;

    public LogicalTimestamp(long nanoTime, int logicalCounter) {
        this.physicalWallClockTimeNanos = nanoTime;
        this.logicalCounter = logicalCounter;
    }

    public long getPhysicalWallClockTimeNanos() {
        return physicalWallClockTimeNanos;
    }

    public int getLogicalCounter() {
        return logicalCounter;
    }

    /**
     * @return the physical wall clock time as an Instant in millis
     */
    public Instant toInstant() {
        long millis = TimeUnit.NANOSECONDS.toMillis(physicalWallClockTimeNanos);
        return Instant.ofEpochMilli(millis);
    }

    /**
     * @return the physical wall clock time as a LocalDateTime in system default time zone
     */
    public LocalDateTime toLocalDateTime() {
        return toLocalDateTime(ZoneId.systemDefault());
    }

    public LocalDateTime getLocalDateTime() {
        return toLocalDateTime();
    }

    /**
     * @param zoneId time zone to use
     * @return the physical wall clock time as a LocalDateTime in given time zone
     */
    public LocalDateTime toLocalDateTime(ZoneId zoneId) {
        return LocalDateTime.ofInstant(toInstant(), zoneId);
    }

    @Override
    public int compareTo(LogicalTimestamp o) {
        return Long.compare(this.physicalWallClockTimeNanos + this.logicalCounter,
                o.physicalWallClockTimeNanos + o.logicalCounter);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        LogicalTimestamp that = (LogicalTimestamp) o;

        if (physicalWallClockTimeNanos != that.physicalWallClockTimeNanos) {
            return false;
        }
        return logicalCounter == that.logicalCounter;
    }

    @Override
    public int hashCode() {
        int result = (int) (physicalWallClockTimeNanos ^ (physicalWallClockTimeNanos >>> 32));
        result = 31 * result + logicalCounter;
        return result;
    }

    @Override
    public String toString() {
        return "LogicalTimestamp{" +
                "physicalWallClockTimeNanos=" + physicalWallClockTimeNanos +
                ", logicalCounter=" + logicalCounter +
                ", localDateTime=" + toLocalDateTime() +
                '}';
    }
}
