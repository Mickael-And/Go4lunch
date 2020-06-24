package com.mickael.go4lunch.data.model;

import java.util.Date;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

/**
 * Represents a user message.
 */
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Message {

    /**
     * Text of the message.
     */
    String message;

    /**
     * Creation date.
     */
    Date dateCreated;

    /**
     * User sender.
     */
    User userSender;

    public Message(String message, User userSender) {
        this.message = message;
        this.userSender = userSender;
    }
}
