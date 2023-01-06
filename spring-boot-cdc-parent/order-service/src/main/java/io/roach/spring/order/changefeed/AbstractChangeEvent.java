package io.roach.spring.order.changefeed;

import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class AbstractChangeEvent<T extends Payload<ID>, ID> {
    static class Metadata {
        @JsonProperty("resolved")
        private String resolved;
    }

    @JsonProperty("payload")
    private List<Envelope<T, ID>> envelope;

    @JsonProperty("__crdb__")
    private Metadata metadata;

    @JsonProperty("length")
    private int length;

    public List<Envelope<T, ID>> getEnvelope() {
        return envelope;
    }

    public int getLength() {
        return length;
    }

    public Optional<LogicalTimestamp> getResolvedTimestamp() {
        return metadata != null ? Optional.ofNullable(LogicalTimestamp.parse(metadata.resolved)) : Optional.empty();
    }
}
