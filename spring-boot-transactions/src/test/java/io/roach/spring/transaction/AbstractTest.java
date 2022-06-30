package io.roach.spring.transaction;

import java.util.Locale;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

@SpringBootTest(classes = {TransactionApplication.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class})
public abstract class AbstractTest {
    private static int spinner = 0;

    public static void tick() {
        System.out.printf(Locale.US, "\r(%s)", "|/-\\".toCharArray()[spinner++ % 4]);
        System.out.flush();
    }

    protected final Logger logger = LoggerFactory.getLogger(getClass());
}
