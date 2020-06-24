package com.mickael.go4lunch.data.dao;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * DAO allowing chat management in Firestore database.
 */
class ChatFirestoreDAO {

    /**
     * Name of the chat collection.
     */
    private static final String COLLECTION_NAME = "chats";

    /**
     * Chat collection reference.
     *
     * @return the reference of the collection
     */
    static CollectionReference getChatsCollection() {
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

}