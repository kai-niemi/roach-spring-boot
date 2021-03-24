package io.roach.spring.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Indicates that the annotated class or method can read (non-authoritative) from a
 * given timestamp in the past.
 * <p>
 * https://www.cockroachlabs.com/docs/stable/as-of-system-time
 *
 * @author Kai Niemi
 */
@Inherited
@Documented
@Target(ElementType.METHOD)
@Retention(RUNTIME)
public @interface TimeTravel {
    /**
     * See https://www.cockroachlabs.com/docs/stable/interval.html
     *
     * @return interval expression (ignored if FOLLOWER_READ mode is used)
     */
    String interval() default "-30s";
}
