package io.roach.spring.order.changefeed;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Envelope<T extends Payload<ID>, ID> {
    private List<String> key = new ArrayList<>();

    @JsonProperty("event_table")
    private String table;

    @JsonProperty("event_timestamp")
    private String timestamp;

    @JsonProperty("event_type")
    private String type;

    @JsonProperty("event_before")
    private T beforePayload;

    @JsonProperty("event_after")
    private T afterPayload;

    public T getBeforePayload() {
        return beforePayload;
    }

    public ID getBeforeId() {
        return beforePayload.getId();
    }

    public T getAfterPayload() {
        return afterPayload;
    }

    public ID getAfterId() {
        return afterPayload.getId();
    }

    public List<String> getKey() {
        return key;
    }

    public String getTable() {
        return table;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public Optional<LogicalTimestamp> getLogicalTimestamp() {
        return timestamp != null ? Optional.of(LogicalTimestamp.parse(timestamp)) : Optional.empty();
    }

    public Operation getOperation() {
        if ("create".equals(type)) {
            return Operation.insert;
        } else if ("delete".equals(type)) {
            return Operation.delete;
        } else if ("update".equals(type)) {
            return Operation.update;
        } else {
            return Operation.unknown;
        }
    }

    @Override
    public String toString() {
        return "Envelope{" +
                "key=" + key +
                ", table='" + table + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", type='" + type + '\'' +
                ", beforePayload=" + beforePayload +
                ", afterPayload=" + afterPayload +
                '}';
    }
}

