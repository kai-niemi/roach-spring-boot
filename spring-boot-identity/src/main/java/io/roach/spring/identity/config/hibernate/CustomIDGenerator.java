package io.roach.spring.identity.config.hibernate;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.Type;

public class CustomIDGenerator implements IdentifierGenerator {
    private final Deque<Long> cachedIds = new LinkedList<>();

    private int batchSize;

    @Override
    public void configure(Type type, Properties properties,
                          ServiceRegistry serviceRegistry) throws MappingException {
        this.batchSize = Integer.parseInt(properties.getProperty("batchSize", "32"));
    }

    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object obj)
            throws HibernateException {
        if (cachedIds.isEmpty()) {
            StringBuilder sb = new StringBuilder("select ");
            IntStream.rangeClosed(1, batchSize).forEach(value -> {
                sb.append("unordered_unique_rowid() as id").append(value);
                if (value < batchSize) {
                    sb.append(",");
                }
            });
            Stream<Object[]> ids = session.createNativeQuery(sb.toString()).stream();
            ids.collect(Collectors.toList()).forEach(arr -> {
                Arrays.stream(arr).forEach(o1 -> {
                    BigInteger bi = (BigInteger) o1;
                    this.cachedIds.add(bi.longValue());
                });
            });
        }
        return cachedIds.poll();
    }
}
