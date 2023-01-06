package io.roach.spring.catalog.scheduler;

import org.springframework.hateoas.RepresentationModel;

public class SchedulerModel extends RepresentationModel<SchedulerModel> {
    private String status;

    private int productsCreated;

    private int productsUpdated;

    private int productsDeleted;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getProductsCreated() {
        return productsCreated;
    }

    public void setProductsCreated(int productsCreated) {
        this.productsCreated = productsCreated;
    }

    public int getProductsUpdated() {
        return productsUpdated;
    }

    public void setProductsUpdated(int productsUpdated) {
        this.productsUpdated = productsUpdated;
    }

    public int getProductsDeleted() {
        return productsDeleted;
    }

    public void setProductsDeleted(int productsDeleted) {
        this.productsDeleted = productsDeleted;
    }
}
