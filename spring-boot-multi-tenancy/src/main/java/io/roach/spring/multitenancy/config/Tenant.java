package io.roach.spring.multitenancy.config;

public enum Tenant {
    alpha(true),
    bravo(true),
    caesar(true);

    private final boolean versioned;

    Tenant(boolean versioned) {
        this.versioned = versioned;
    }

    public boolean isVersioned() {
        return versioned;
    }
}
