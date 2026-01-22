package edu.miu.ea.kafkaevents;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class UserEventsKafkaApplication {

    void main(String[] args) {
        SpringApplication.run(UserEventsKafkaApplication.class, args);
        System.out.println("App is up and running ...");
    }

}
