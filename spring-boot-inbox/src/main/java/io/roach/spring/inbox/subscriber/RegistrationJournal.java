package io.roach.spring.inbox.subscriber;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import org.hibernate.annotations.TypeDef;
import org.springframework.hateoas.server.core.Relation;

import io.roach.spring.inbox.event.RegistrationEvent;

@Entity
@DiscriminatorValue("CUSTOMER_REG")
@TypeDef(name = "jsonb", typeClass = RegistrationJournal.JsonType.class,
        defaultForType = RegistrationEvent.class)
@Relation(value = "registration-event",
        collectionRelation = "registration-events")
public class RegistrationJournal extends AbstractJournal<RegistrationEvent> {
    public static class JsonType extends AbstractJsonDataType<RegistrationEvent> {
        @Override
        public Class<RegistrationEvent> returnedClass() {
            return RegistrationEvent.class;
        }
    }
}
