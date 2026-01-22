package edu.miu.ea.kafkaevents.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaConfig {

    @Value("${app.events.topic}")
    private String TOPIC_NAME;

    @Bean
    public NewTopic userEventsTopic() {
        System.out.println("Creating TOPIC : " + TOPIC_NAME);
        return new NewTopic(TOPIC_NAME, 1, (short) 1); // 1 partition, 1 replica
    }

}
