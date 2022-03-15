package io.roach.spring.batch.domain;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "customers")
public class Customer extends AbstractEntity<UUID> {
    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private Customer instance = new Customer();

        private Builder() {
        }

        public Builder withUserName(String userName) {
            instance.userName = userName;
            return this;
        }

        public Builder withFirstName(String firstName) {
            instance.firstName = firstName;
            return this;
        }

        public Builder withLastName(String lastName) {
            instance.lastName = lastName;
            return this;
        }

        public Builder withEmail(String email) {
            instance.email = email;
            return this;
        }

        public Builder withAddress(Address address) {
            instance.address = address;
            return this;
        }

        public Customer build() {
            return instance;
        }
    }

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    private UUID id;

    @Column(name = "user_name", length = 15, nullable = false, unique = true)
    private String userName;

    @Column(name = "first_name", length = 45)
    private String firstName;

    @Column(name = "last_name", length = 45)
    private String lastName;

    @Column(length = 128)
    private String email;

    @Embedded
    private Address address;

    @Override
    public UUID getId() {
        return id;
    }

    public String getUserName() {
        return userName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public Address getAddress() {
        return address;
    }
}
