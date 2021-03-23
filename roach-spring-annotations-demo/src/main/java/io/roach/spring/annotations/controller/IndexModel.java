package io.roach.spring.annotations.controller;

import org.springframework.hateoas.RepresentationModel;

public class IndexModel extends RepresentationModel<IndexModel> {
    private String message;

    public IndexModel() {
    }

    public IndexModel(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

}
