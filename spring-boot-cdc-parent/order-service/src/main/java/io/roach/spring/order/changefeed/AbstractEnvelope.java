package io.roach.spring.order.changefeed;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class AbstractEnvelope<T extends Event<ID>, ID> {
    public static class Metadata {
        @JsonProperty("resolved")
        private String resolved;

        public String getResolved() {
            return resolved;
        }

        public Optional<LogicalTimestamp> getResolvedTimestamp() {
            return resolved != null
                    ? Optional.ofNullable(LogicalTimestamp.parse(resolved))
                    : Optional.empty();
        }
    }

    @JsonProperty("__crdb__")
    private Metadata metadata;

    @JsonProperty("payload")
    private List<Payload<T, ID>> payloads = new ArrayList<>();

    @JsonProperty("length")
    private int length;

    public Metadata getMetadata() {
        return metadata;
    }

    public List<Payload<T, ID>> getPayloads() {
        return payloads;
    }

    public int getLength() {
        return length;
    }
}
