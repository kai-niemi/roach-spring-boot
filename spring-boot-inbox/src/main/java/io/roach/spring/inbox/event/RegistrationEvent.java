package io.roach.spring.inbox.event;

import java.util.Date;
import java.util.UUID;

public class RegistrationEvent {
    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private final RegistrationEvent instance = new RegistrationEvent();

        public Builder withGeneratedId() {
            withId(UUID.randomUUID());
            return this;
        }

        public Builder withId(UUID id) {
            this.instance.id = id;
            return this;
        }

        public Builder withName(String name) {
            this.instance.name = name;
            return this;
        }

        public Builder withEmail(String email) {
            this.instance.email = email;
            return this;
        }

        public Builder withJurisdiction(String jurisdiction) {
            this.instance.jurisdiction = jurisdiction;
            return this;
        }

        public RegistrationEvent build() {
            instance.createdAt = new Date();
            return instance;
        }
    }

    private UUID id;

    private String name;

    private String email;

    private String jurisdiction;

    private Date createdAt;

    protected RegistrationEvent() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getJurisdiction() {
        return jurisdiction;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RegistrationEvent)) {
            return false;
        }

        RegistrationEvent that = (RegistrationEvent) o;

        if (!id.equals(that.id)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "RegistrationEvent{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", jurisdiction='" + jurisdiction + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
