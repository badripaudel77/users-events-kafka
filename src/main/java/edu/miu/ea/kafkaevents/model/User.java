package edu.miu.ea.kafkaevents.model;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class User {
    private String id;
    private String username;
    private String email;
}


