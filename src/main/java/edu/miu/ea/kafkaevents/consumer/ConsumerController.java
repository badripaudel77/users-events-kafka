package edu.miu.ea.kafkaevents.consumer;

import edu.miu.ea.kafkaevents.model.UserRegisteredEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/consumers")
public class ConsumerController {
    // Manual Polling from the given topic
    // Let us say we want to send email to all the stuck as a JOB
    @Value("${app.events.topic:user.emailevents}")
    private String TOPIC_NAME;

    private final KafkaTemplate<String, UserRegisteredEvent> kafkaTemplate;

    public ConsumerController(KafkaTemplate<String, UserRegisteredEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @GetMapping("/send")
    public String sendEmailToAll() {
        kafkaTemplate.receive(TOPIC_NAME, 1, 100);
        return "All Messages Consumed : All remaining emails are sent";
    }
}
