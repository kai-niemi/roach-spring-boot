# CockroachDB Spring Boot :: Annotations

Contains various meta-annotations for marking transactional methods as retryable, using 
time travel queries, follower reads and other transaction hints. Combined with AOP aspects,
these can be used to weave in different behavior in Spring Boot applications with a very
small footprint, including:

- Adopting the ECB pattern for transactional robustness and clarity of demarcations.
- Transaction retries with exponential backoff.
- Time travel queries, meaning reading from the past to reduce contention or increase efficiency.  
- Follower reads, reading from any follower replica (time-travel).  
- Change transaction priorities to reduce contention, etc.

## Relevant Resources:

- [Session Variables](https://www.cockroachlabs.com/docs/stable/show-vars.html#supported-variables)

## Relevant Articles:

- [Spring Annotations for CockroachDB](https://blog.cloudneutral.se/spring-annotations-for-cockroachdb)
