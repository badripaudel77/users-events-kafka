package edu.miu.ea.kafkaevents.producer;

import edu.miu.ea.kafkaevents.model.UserRegisteredEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserEventProducer {

    private final KafkaTemplate<String, UserRegisteredEvent> kafkaTemplate;

    @Value("${app.events.topic:user.emailevents}")
    private String TOPIC_NAME;

    public void publish(UserRegisteredEvent event) {
        kafkaTemplate.send(TOPIC_NAME, event);
        System.out.println("Produced : User registered successfully -> " + event);
    }
}

