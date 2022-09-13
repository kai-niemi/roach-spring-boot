package io.roach.spring.outbox.aspect;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.Assert;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SequenceWriter;
import com.fasterxml.jackson.databind.SerializationFeature;

import io.roach.spring.outbox.domain.TransactionEntity;

@Aspect
@Component
@Order(Ordered.LOWEST_PRECEDENCE - 1) // Make sure it's ordered after TX advisor (by a higher value)
public class OutboxAspect {
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private final ObjectMapper mapper = new ObjectMapper()
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
            .enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)
            .setSerializationInclusion(JsonInclude.Include.NON_NULL);

    @Autowired
    private DataSource dataSource;

    private JdbcTemplate jdbcTemplate;

    @PostConstruct
    public void init() {
        logger.info("Bootstrapping outbox aspect");
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @AfterReturning(pointcut = "execution(* io.roach.spring.outbox.domain.DefaultTransactionService.createTransaction(..))", returning = "transactionEntity")
    public void doAfterCreateTransactions(TransactionEntity transactionEntity) {
        if (!TransactionSynchronizationManager.isActualTransactionActive()) {
            throw new IllegalStateException("No transaction context");
        }

        Assert.isTrue(!transactionEntity.isNew(), "Expected persistent entity but got transient");

        try {
            String payload;
            if (logger.isTraceEnabled()) {
                payload = mapper.writer()
                        .withDefaultPrettyPrinter()
                        .writeValueAsString(transactionEntity);
                logger.trace("Writing payload to outbox: {}", payload);
            } else {
                payload = mapper.writer()
                        .writeValueAsString(transactionEntity);
            }
            jdbcTemplate.update(
                    "INSERT INTO t_outbox (aggregate_type,aggregate_id,event_type,payload) VALUES (?,?,?,?)",
                    ps -> {
                        ps.setString(1, "transaction");
                        ps.setString(2, transactionEntity.getId().toString());
                        ps.setString(3, "TransactionCreatedEvent");
                        ps.setObject(4, payload);
                    });
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Error serializing outbox JSON payload", e);
        }
    }

    private final ExecutorService executorService = Executors.newCachedThreadPool();

    @AfterReturning(pointcut = "execution(* io.roach.spring.outbox.domain.DefaultTransactionService.createTransactionCollection(..))", returning = "transactionEntities")
    public void doAfterCreateTransactions(List<TransactionEntity> transactionEntities) throws IOException {
        if (!TransactionSynchronizationManager.isActualTransactionActive()) {
            throw new IllegalStateException("No transaction context");
        }

        try (PipedInputStream in = new PipedInputStream();
             PipedOutputStream pipedOut = new PipedOutputStream(in);
             BufferedOutputStream out = new BufferedOutputStream(pipedOut);
             SequenceWriter sequenceWriter = mapper.writer().writeValues(out)) {

            sequenceWriter.init(true);

            Future<?> insertFuture = executorService.submit(() -> {
                jdbcTemplate.update(
                        "INSERT INTO t_outbox (aggregate_type,aggregate_id,event_type,payload) VALUES (?,?,?,?)",
                        ps -> {
                            ps.setString(1, "transaction_batch");
                            ps.setString(2, null);
                            ps.setString(3, "TransactionCreatedEvent");
                            ps.setCharacterStream(4, new InputStreamReader(in));
                        });
            });

            sequenceWriter.write(transactionEntities);
            sequenceWriter.close();

            try {
                insertFuture.get();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException("Thread interrupt", e);
            } catch (ExecutionException e) {
                throw new IllegalStateException("Error serializing outbox JSON payload", e.getCause());
            }
        }
    }
}

