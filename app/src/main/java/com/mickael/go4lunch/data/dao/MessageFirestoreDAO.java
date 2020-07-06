package com.mickael.go4lunch.data.dao;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.mickael.go4lunch.data.model.Message;

/**
 * DAO allowing message management in Firestore database.
 */
public class MessageFirestoreDAO {

    /**
     * Name of the message collection.
     */
    private static final String COLLECTION_NAME = "messages";

    /**
     * Message collection reference.
     *
     * @return the reference of the collection
     */
    public static CollectionReference getMessagessCollection() {
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    /**
     * Get all messages.
     *
     * @return query
     */
    public static Query getAllMessages() {
        return MessageFirestoreDAO.getMessagessCollection()
                .orderBy("dateCreated")
                .limit(50);
    }

    /**
     * Create a message.
     *
     * @param message message to create
     * @return task
     */
    public static Task<Void> createMessage(Message message) {
        return MessageFirestoreDAO.getMessagessCollection().document()
                .set(message);
    }
}