package com.mickael.go4lunch.data.dao;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mickael.go4lunch.data.model.User;

import java.util.List;
import java.util.Map;

/**
 * DAO allowing user management in Firestore database.
 */
public class UserFirestoreDAO {

    /**
     * Name of the user collection..
     */
    private static final String COLLECTION_NAME = "users";

    /**
     * User collection reference.
     *
     * @return the reference of the collection
     */
    public static CollectionReference getUsersCollection() {
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    // CREATE
    public static Task<Void> createUser(String userID, String username, String urlPicture, Map<String, String> lunchRestaurant, List<String> likes) {
        User userToCreate = new User(userID, username, urlPicture, lunchRestaurant, likes);
        return UserFirestoreDAO.getUsersCollection().document(userID).set(userToCreate);
    }

    // READ
    public static Task<DocumentSnapshot> getUser(String userId) {
        return UserFirestoreDAO.getUsersCollection().document(userId).get();
    }

    // UPDATE
    public static Task<Void> updateUser(String userId, Map<String, Object> userFields) {
        return UserFirestoreDAO.getUsersCollection().document(userId).update(userFields);
    }

    // DELETE
    public static Task<Void> deleteUser(String userID) {
        return UserFirestoreDAO.getUsersCollection().document(userID).delete();
    }
}