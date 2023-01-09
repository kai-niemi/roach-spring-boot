package io.roach.spring.json;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.roach.spring.json.model.Transaction;

public class ObjectMapperTest extends AbstractIntegrationTest {
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testSerialisation() throws JsonProcessingException {
        Transaction transaction = Transaction.builder()
                .withGeneratedId()
                .withBookingDate(LocalDate.now().minusDays(2))
                .withTransferDate(LocalDate.now())
                .andItem()
                .withAmount(BigDecimal.valueOf(-50.00))
                .withNote("debit A")
                .withRunningBalance(BigDecimal.valueOf(49.50))
                .then()
                .andItem()
                .withAmount(BigDecimal.valueOf(50.00))
                .withRunningBalance(BigDecimal.valueOf(48.50))
                .withNote("credit A")
                .then()
                .build();

        logger.info(objectMapper.writeValueAsString(transaction));
    }
}
