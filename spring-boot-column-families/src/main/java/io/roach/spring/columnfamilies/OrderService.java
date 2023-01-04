package io.roach.spring.columnfamilies;

import java.math.BigDecimal;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.ObjectRetrievalFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

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
        entityManager.merge(order);
        return order;
    }

    public <T extends AbstractOrder> void updateOrderStatus(Class<T> orderType,
                                                         Long orderId,
                                                         long commitDelay) {
        Assert.isTrue(TransactionSynchronizationManager.isActualTransactionActive(), "Expected transaction!");

        TypedQuery<Object[]> q = entityManager.createNamedQuery(namedQuery(orderType, "findByIdForUpdateStatus"),
                Object[].class);
        q.setParameter(1, orderId);

        Object[] result = q.getSingleResult();

        OrderStatus status = (OrderStatus) result[1];
        status = status.next();

        if (commitDelay > 0) {
            logger.info("Reading order [{}] for updating status to [{}] - waiting {} sec before commit",
                    StringUtils.arrayToDelimitedString(result, ","),
                    status,
                    commitDelay);
            thinkTime(commitDelay);
            logger.info("Proceeding with commit");
        }

        entityManager
                .createQuery("update " + orderType.getSimpleName()
                        + " o set o.orderStatus = :status"
                        + " where o.id = :id")
                .setParameter("status", status)
                .setParameter("id", orderId)
                .executeUpdate();

        entityManager.flush();
    }

    private String namedQuery(Class<?> orderType, String name) {
        return orderType.getName().replace("io.roach.spring.columnfamilies.", "") + "." + name;
    }

    public <T extends AbstractOrder> void updateOrderPrice(Class<T> orderType,
                                                        Long orderId,
                                                        BigDecimal price,
                                                        long commitDelay) {
        Assert.isTrue(TransactionSynchronizationManager.isActualTransactionActive(), "Expected transaction!");

        TypedQuery<Object[]> q = entityManager.createNamedQuery(namedQuery(orderType, "findByIdForUpdatePrice"),
                Object[].class);
        q.setParameter(1, orderId);

        Object[] result = q.getSingleResult();

        if (commitDelay > 0) {
            logger.info("Reading order [{}] for incrementing price to [{}] - waiting {} sec before commit",
                    StringUtils.arrayToDelimitedString(result, ","),
                    price,
                    commitDelay);
            thinkTime(commitDelay);
            logger.info("Proceeding with commit");
        }

        entityManager
                .createQuery("update " + orderType.getSimpleName()
                        + " o set o.totalPrice = o.totalPrice + :price"
                        + " where o.id = :id")
                .setParameter("price", price)
                .setParameter("id", orderId)
                .executeUpdate();

        entityManager.flush();
    }

    private void thinkTime(long commitDelay) {
        if (commitDelay > 0) {
            try {
                Thread.sleep(commitDelay * 1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
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
