package io.roach.spring.catalog.scheduler;

import org.springframework.hateoas.RepresentationModel;

public class SchedulingModel extends RepresentationModel<SchedulingModel> {
    private boolean insertsEnabled;

    private boolean updatesEnabled;

    private boolean deletesEnabled;

    private int productsCreated;

    private int productsUpdated;

    private int productsDeleted;

    public boolean isInsertsEnabled() {
        return insertsEnabled;
    }

    public void setInsertsEnabled(boolean insertsEnabled) {
        this.insertsEnabled = insertsEnabled;
    }

    public boolean isUpdatesEnabled() {
        return updatesEnabled;
    }

    public void setUpdatesEnabled(boolean updatesEnabled) {
        this.updatesEnabled = updatesEnabled;
    }

    public boolean isDeletesEnabled() {
        return deletesEnabled;
    }

    public void setDeletesEnabled(boolean deletesEnabled) {
        this.deletesEnabled = deletesEnabled;
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
