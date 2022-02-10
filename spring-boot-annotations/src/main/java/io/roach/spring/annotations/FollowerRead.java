package io.roach.spring.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Indicates that the annotated class or method can read from any replica.
 * A follower reads in CockroachDB represents a computed time interval
 * sufficiently in the past for reads to be served by closest follower replica.
 *
 * https://www.cockroachlabs.com/docs/stable/follower-reads.html
 *
 * @author Kai Niemi
 */
@Inherited
@Documented
@Target(ElementType.METHOD)
@Retention(RUNTIME)
public @interface FollowerRead {
    /**
     * @return use time bounded or exact staleness (default)
     */
    String staleness() default "(exact)";
}
