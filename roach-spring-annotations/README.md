# CockroachDB Spring Boot :: Annotations

Contains various annotations for marking transactional methods as retryable, using 
time travel queries, follower reads and other transaction hints. 

These annotations combined with AOP aspects can be used to weave in different
behavior in Spring Boot applications, including:

- Transaction retries with exponential backoff
- Time travel queries (reading from the past)  
- Follower reads (reading from any follower)  
- Adopting the ECB pattern for transactional robustness and clarity
- Transaction priorities, etc.

See:

- [Supported Variables](https://www.cockroachlabs.com/docs/stable/show-vars.html#supported-variables)