package io.roach.spring.inbox.subscriber;

import java.util.concurrent.ThreadLocalRandom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import io.roach.spring.inbox.event.RegistrationEvent;

@Component
public class RegistrationConsumer {
    private final ThreadLocalRandom random = ThreadLocalRandom.current();

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private RegistrationJournalRepository registrationJournalRepository;

    @JmsListener(destination = "${active-mq.topic}", containerFactory = "jmsListenerContainerFactory")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void receiveMessage(RegistrationEvent event) {
        logger.info("Received registration event: {}", event.toString());

        Assert.notNull(event.getId(), "Event ID is null");

        // Upsert to inbox table (journal) by de-duplicating on the event ID since best we get is at-least-once
        // delivery. With JDBC we could use INSERT INTO .. ON CONFLICT DO NOTHING.
        RegistrationJournal journal = registrationJournalRepository.findById(event.getId()).orElseGet(() -> {
            RegistrationJournal newRegistration = new RegistrationJournal();
            newRegistration.setTag(random.nextBoolean()
                    ? "red" : random.nextBoolean()
                    ? "green" : random.nextBoolean()
                    ? "blue" : "yellow");
            newRegistration.setStatus("created");
            return newRegistration;
        });

        if (journal.isNew()) {
            journal.setEvent(event);
            registrationJournalRepository.save(journal);
        }
    }
}
