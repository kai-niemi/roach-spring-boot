package io.roach.spring.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotation for arbitrary transaction hints.
 *
 * @author Kai Niemi
 */
@Target({})
@Retention(RUNTIME)
public @interface TransactionHint {
    /**
     * Name of the hint.
     */
    String name();

    /**
     * Value of the hint.
     */
    String value() default "";

    int intValue() default -1;
}
