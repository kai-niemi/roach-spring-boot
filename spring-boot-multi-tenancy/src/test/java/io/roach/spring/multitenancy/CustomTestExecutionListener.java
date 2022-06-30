package io.roach.spring.multitenancy;

import java.lang.reflect.Method;

import org.junit.jupiter.api.Assertions;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import io.roach.spring.multitenancy.aspect.TenantScope;
import io.roach.spring.multitenancy.config.TenantContext;

public class CustomTestExecutionListener extends AbstractTestExecutionListener implements Ordered {
    @Override
    public int getOrder() {
        return 4000 + 1;
    }

    @Override
    public void beforeTestMethod(TestContext testContext) {
        Method testMethod = testContext.getTestMethod();
        TenantScope tc = AnnotationUtils.getAnnotation(testMethod, TenantScope.class);
        if (tc != null) {
            Assertions.assertFalse(TransactionSynchronizationManager.isActualTransactionActive(),
                    "Transaction not expected here");
            TenantContext.setTenantId(tc.value());
        }
    }

    @Override
    public void afterTestMethod(TestContext testContext) throws Exception {
        TenantContext.clear();
    }
}
