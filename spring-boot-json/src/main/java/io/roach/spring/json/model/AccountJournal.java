package io.roach.spring.json.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import org.hibernate.annotations.TypeDef;

@Entity
@DiscriminatorValue("ACCOUNT")
@TypeDef(name = "jsonb", typeClass = AccountJournal.JsonType.class, defaultForType = Account.class)
public class AccountJournal extends Journal<Account> {
    public static class JsonType extends AbstractJsonDataType<Account> {
        @Override
        public Class<Account> returnedClass() {
            return Account.class;
        }
    }
}
