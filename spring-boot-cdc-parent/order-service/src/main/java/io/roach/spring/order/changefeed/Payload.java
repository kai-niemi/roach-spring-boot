package io.roach.spring.order.changefeed;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Payload<T extends Event<ID>, ID> {
    public static class Metadata {
        @JsonProperty("topic")
        private String topic;

        @JsonProperty("updated")
        private String updated;

        @JsonProperty("key")
        private List<String> key = new ArrayList<>();

        public String getTopic() {
            return topic;
        }

        public String getUpdated() {
            return updated;
        }

        public List<String> getKey() {
            return key;
        }
    }

    @JsonProperty("__crdb__")
    private Metadata metadata;

    @JsonProperty("event_table")
    private String table;

    @JsonProperty("event_timestamp")
    private String timestamp;

    @JsonProperty("event_type")
    private String type;

    @JsonProperty("event_before")
    private T before;

    @JsonProperty("event_after")
    private T after;

    public Metadata getMetadata() {
        return metadata;
    }

    public T getBefore() {
        return before;
    }

    public T getAfter() {
        return after;
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
                "table='" + table + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", type='" + type + '\'' +
                ", beforePayload=" + before +
                ", afterPayload=" + after +
                '}';
    }
}

