package io.roach.spring.multitenancy.config;

/**
 * Thread local registry for tenant IDs
 */
public abstract class TenantRegistry {
    private TenantRegistry() {
    }

    // Thread local variable containing each thread's ID
    private static final ThreadLocal<TenantName> threadLocal = new ThreadLocal<>();

    public static void setTenantId(TenantName tenantId) {
        threadLocal.set(tenantId);
    }

    public static TenantName getTenantId() {
        return threadLocal.get();
    }

    public static void clear() {
        threadLocal.remove();
    }
}
