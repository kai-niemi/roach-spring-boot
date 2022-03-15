package io.roach.spring.batch.domain;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;

public class ShipmentStatusEnumType extends org.hibernate.type.EnumType<ShipmentStatus> {
    @Override
    public void nullSafeSet(PreparedStatement st, Object value, int index, SharedSessionContractImplementor session)
            throws HibernateException, SQLException {
        st.setObject(index, value != null ? ((Enum<?>) value).name() : null, Types.OTHER);
    }
}
