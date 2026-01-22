package edu.miu.ea.kafkaevents.producer;

import edu.miu.ea.kafkaevents.model.User;
import edu.miu.ea.kafkaevents.model.UserRegisteredEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.UUID;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class ProducerController {

    private final UserEventProducer producer;

    @PostMapping("/register")
    public String register(@RequestBody User user) {
        System.out.println("Saving User to the DB " + user);
        UserRegisteredEvent event = UserRegisteredEvent.builder()
                .id(UUID.randomUUID().toString())
                .username(user.getUsername())
                .email(user.getEmail())
                .occurredAt(Instant.now().toString())
                .build();
        // adding to the kafka
        // it can later be used to send verification email
        try {
            producer.publish(event);
        }
        catch (Exception e) {
            System.out.println("Exception Occurred with a message: " + e.getMessage());
        }
        return "User registration event published, check your email for confirmation !";
    }

    @GetMapping
    public String welcome() {
        return "Welcome to Spring Kafka : User Event Service !";
    }
}

