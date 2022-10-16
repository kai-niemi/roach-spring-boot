package io.roach.spring.idempotency.domain.transaction;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.TypeDef;

import io.roach.spring.idempotency.domain.common.AbstractJsonDataType;
import io.roach.spring.idempotency.domain.poe.PoeTag;

@Entity
@Table(name = "poe_tag")
@TypeDef(name = "jsonb", typeClass = TransactionCollectionTag.JsonType.class, defaultForType = TransactionEntity.class)
public class TransactionCollectionTag extends PoeTag<List<TransactionEntity>> {
    public static class JsonType extends AbstractJsonDataType<TransactionEntity> {
        @Override
        public Class<TransactionEntity> returnedClass() {
            return TransactionEntity.class;
        }

        @Override
        public boolean isCollectionType() {
            return true;
        }
    }
}
