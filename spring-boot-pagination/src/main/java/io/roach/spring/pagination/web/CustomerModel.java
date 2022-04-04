package io.roach.spring.pagination.web;

import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@Relation(value = LinkRels.CUSTOMER_REL,
        collectionRelation = LinkRels.CUSTOMERS_REL)
@JsonPropertyOrder({"links", "embedded"})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomerModel extends RepresentationModel<CustomerModel> {
    private String userName;

    private String fullName;

    public String getUserName() {
        return userName;
    }

    public CustomerModel setUserName(String userName) {
        this.userName = userName;
        return this;
    }

    public String getFullName() {
        return fullName;
    }

    public CustomerModel setFullName(String fullName) {
        this.fullName = fullName;
        return this;
    }
}
