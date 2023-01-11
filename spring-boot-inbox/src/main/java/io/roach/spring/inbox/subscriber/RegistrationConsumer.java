package io.roach.spring.inbox.subscriber;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import io.roach.spring.inbox.event.RegistrationEvent;

@Component
public class RegistrationConsumer {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private RegistrationJournalRepository registrationJournalRepository;

    @JmsListener(destination = "${active-mq.topic}", containerFactory = "jmsListenerContainerFactory")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void receiveMessage(RegistrationEvent event) {
        logger.info("Received registration event: {}", event.toString());

        // Insert to inbox table (journal)
        RegistrationJournal journal = new RegistrationJournal();
        journal.setTag("tag");
        journal.setEvent(event);

        registrationJournalRepository.save(journal);
    }
}
