package io.roach.spring.inbox.producer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import io.roach.spring.inbox.event.RegistrationEvent;

@Component
public class RegistrationEventProducer {
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private JmsTemplate jmsTemplate;

    @Value("${active-mq.topic}")
    private String topic;

    public void sendMessage(RegistrationEvent event) {
        logger.info("Send registration event to JMS topic [{}]:\n{}", topic, event);
        jmsTemplate.convertAndSend(topic, event);
    }
}
