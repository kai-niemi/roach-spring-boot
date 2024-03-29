package io.roach.spring.multitenancy.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.Assert;

import io.roach.spring.annotations.aspect.AdvisorOrder;
import io.roach.spring.multitenancy.config.TenantContext;

@Aspect
@Order(AdvisorOrder.HIGHEST)
public class TenantContextAspect {
    @Pointcut("@annotation(tenantContext)")
    public void anyMultiTenantOperation(TenantScope tenantContext) {
    }

    @Around(value = "anyMultiTenantOperation(tenantContext)",
            argNames = "pjp,tenantContext")
    public Object aroundAnyMultiTenantOperation(ProceedingJoinPoint pjp, TenantScope tenantContext)
            throws Throwable {
        Assert.isTrue(!TransactionSynchronizationManager.isActualTransactionActive(), "Transaction not supported");
        TenantContext.setTenantId(tenantContext.value());
        try {
            return pjp.proceed();
        } finally {
            TenantContext.clear();
        }
    }
}
