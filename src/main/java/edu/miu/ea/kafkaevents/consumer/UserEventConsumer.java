package edu.miu.ea.kafkaevents.consumer;

import edu.miu.ea.kafkaevents.model.UserRegisteredEvent;
import org.springframework.kafka.annotation.BackOff;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.stereotype.Component;

@Component
public class UserEventConsumer {

    // Sending the email to the user for confirmation
    // Event comes as string, but since we have used deserializer, it would be deserialized.
    // Try three times on an interval of 2s when it throws exception
    @RetryableTopic(
            attempts = "3",
            backOff = @BackOff(delay = 2000),
            autoCreateTopics = "true"
    )
    @KafkaListener(topics = "user.emailevents", groupId = "user-event-group")
    public void consume(UserRegisteredEvent event) {
        System.out.println("Consumed : Confirmation Email sent, please verify -> " + event);
    }
}
