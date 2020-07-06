package com.mickael.go4lunch.data.model;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

/**
 * Represents a user message.
 */
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString(of = {"message", "dateCreated", "userSender"})
public class Message {

    /**
     * Text of the message.
     */
    String message;

    /**
     * Creation date.
     */
    @ServerTimestamp
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
