package io.roach.spring.multitenancy.config;

/**
 * Thread local registry for tenant IDs
 */
public abstract class TenantContext {
    private TenantContext() {
    }

    // Thread local variable containing each thread's ID
    private static final ThreadLocal<Tenant> threadLocal = new ThreadLocal<>();

    public static void setTenantId(Tenant tenantId) {
        threadLocal.set(tenantId);
    }

    public static Tenant getTenantId() {
        return threadLocal.get();
    }

    public static void clear() {
        threadLocal.remove();
    }
}
