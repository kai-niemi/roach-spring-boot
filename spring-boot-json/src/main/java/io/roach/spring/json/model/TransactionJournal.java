package io.roach.spring.json.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import org.hibernate.annotations.TypeDef;

@Entity
@DiscriminatorValue("TRANSACTION")
@TypeDef(name = "jsonb", typeClass = TransactionJournal.JsonType.class, defaultForType = Transaction.class)
public class TransactionJournal extends Journal<Transaction> {
    public static class JsonType extends AbstractJsonDataType<Transaction> {
        @Override
        public Class<Transaction> returnedClass() {
            return Transaction.class;
        }
    }
}
