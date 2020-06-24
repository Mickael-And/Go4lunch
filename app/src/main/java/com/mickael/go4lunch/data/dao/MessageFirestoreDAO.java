package com.mickael.go4lunch.data.dao;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
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
    public static CollectionReference getMessagesCollection() {
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    /**
     * Get all message in a chat.
     *
     * @param chat chat choosen
     * @return query
     */
    public static Query getAllMessageForChat(String chat) {
        return ChatFirestoreDAO.getChatsCollection()
                .document(chat)
                .collection(COLLECTION_NAME)
                .orderBy("dateCreated")
                .limit(50);
    }

    /**
     * Create a message.
     *
     * @param chat    chat choosen
     * @param message message to create
     * @return task
     */
    public static Task<DocumentReference> createMessage(String chat, Message message) {
        return ChatFirestoreDAO.getChatsCollection().document(chat)
                .collection(COLLECTION_NAME)
                .add(message);
    }
}