package io.roach.spring.identity.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

@Entity
@Table(name = "account_numid")
public class CustomIDAccountEntity extends AccountEntity<Long> {
    @Id
    @Column(updatable = false, nullable = false)
    @GeneratedValue(generator = "custom-generator")
    @GenericGenerator(name = "custom-generator",
            parameters = @Parameter(name = "batchSize", value = "64"),
            strategy = "io.roach.spring.identity.config.hibernate.CustomIDGenerator")
    private Long id;

    @Override
    public Long getId() {
        return id;
    }
}
