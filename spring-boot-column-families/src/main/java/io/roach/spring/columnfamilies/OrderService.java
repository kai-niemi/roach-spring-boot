package io.roach.spring.columnfamilies;

import java.math.BigDecimal;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.ObjectRetrievalFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.Assert;

@Service
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class OrderService {
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @PersistenceContext
    private EntityManager entityManager;

    public <T extends AbstractOrder> List<T> findAllOrders(Class<T> orderType) {
        Assert.isTrue(TransactionSynchronizationManager.isActualTransactionActive(), "Expected transaction!");

        CriteriaQuery<T> cq = entityManager.getCriteriaBuilder()
                .createQuery(orderType);
        cq.select(cq.from(orderType));

        return entityManager.createQuery(cq).getResultList();
    }

    public <T extends AbstractOrder> T getOrderById(Class<T> orderType, Long orderId) {
        T order = entityManager.find(orderType, orderId);
        if (order == null) {
            throw new ObjectRetrievalFailureException(orderType, orderId);
        }
        return order;
    }

    public <T extends AbstractOrder> T placeOrder(T order) {
        entityManager.persist(order);
        return order;
    }

    public <T extends AbstractOrder> T updateOrderDetails(Class<T> orderType,
                                                          Long orderId,
                                                          ShipmentStatus status,
                                                          BigDecimal increment,
                                                          long commitDelay) {
        Assert.isTrue(TransactionSynchronizationManager.isActualTransactionActive(), "Expected transaction!");

        T order = entityManager.find(orderType, orderId);
        if (order == null) {
            throw new ObjectRetrievalFailureException(Order1.class, orderId);
        }

        if (commitDelay > 0) {
            logger.info("Reading order id [{}] with status [{}] and total [{}] - waiting {} sec before commit",
                    order.getId(),
                    order.getStatus(),
                    order.getTotalPrice(),
                    commitDelay);
            try {
                Thread.sleep(commitDelay * 1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            logger.info("Proceeding with write");
        }

        if (!order.getStatus().equals(status)) {
            logger.info("Set status to {}", status);
            order.setStatus(status);
        }

        if (BigDecimal.ZERO.compareTo(increment) != 0) {
            logger.info("Increment total with {}", increment);
            order.incrementTotalPrice(increment);
        }

        entityManager.merge(order);
        entityManager.flush();

        return order;
    }

    public <T extends AbstractOrder> void deleteOne(Class<T> orderType, Long orderId) {
        T order = entityManager.find(orderType, orderId);
        entityManager.remove(order);
    }

    public <T extends AbstractOrder> void deleteAll(Class<T> orderType) {
        Assert.isTrue(TransactionSynchronizationManager.isActualTransactionActive(), "Expected transaction!");
        Query removeAll1 = entityManager.createQuery("delete from " + orderType.getName(), orderType);
        removeAll1.executeUpdate();
    }
}
