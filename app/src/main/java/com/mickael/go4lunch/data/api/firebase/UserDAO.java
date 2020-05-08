package com.mickael.go4lunch.data.api.firebase;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mickael.go4lunch.data.model.User;

public class UserDAO {
    private static final String COLLECTION_NAME = "users";

    // COLLECTION REFERENCE
    public static CollectionReference getUsersCollection() {
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    // CREATE
    public static Task<Void> createUser(String userID, String username, String urlPicture, String launchPlaces) {
        User userToCreate = new User(userID, username, urlPicture, launchPlaces);
        return UserDAO.getUsersCollection().document(userID).set(userToCreate);
    }

    // READ
    public static Task<DocumentSnapshot> getUser(String userId) {
        return UserDAO.getUsersCollection().document(userId).get();
    }

    // UPDATE
    public static Task<Void> updateUsername(String userId, String username) {
        return UserDAO.getUsersCollection().document(userId).update("username", username);
    }

    public static Task<Void> updateEmail(String userId, String email) {
        return UserDAO.getUsersCollection().document(userId).update("emaol", email);
    }

    // DELETE
    public static Task<Void> deleteUser(String userID) {
        return UserDAO.getUsersCollection().document(userID).delete();
    }
}
