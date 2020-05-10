package com.mickael.go4lunch.data.dao;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mickael.go4lunch.data.model.Lunch;

import java.util.Date;
import java.util.Map;

public class LunchFirestoreDAO {
    private static final String COLLECTION_NAME = "lunches";

    // COLLECTION REFERENCE
    public static CollectionReference getLunchesCollection() {
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    // CREATE
    public static Task<Void> createLunch(String lunchID, String userId, String lunchPlacesId, Date lunchDate) {
        Lunch lunchToCreate = new Lunch(lunchID, userId, lunchPlacesId, lunchDate);
        return LunchFirestoreDAO.getLunchesCollection().document().set(lunchToCreate);
    }

    // READ
    public static Task<DocumentSnapshot> getLunch(String lunchId) {
        return LunchFirestoreDAO.getLunchesCollection().document(lunchId).get();
    }

    // UPDATE
    public static Task<Void> updateLunch(String lunchId, Map<String, Object> lunchFields) {
        return LunchFirestoreDAO.getLunchesCollection().document(lunchId).update(lunchFields);
    }

    // DELETE
    public static Task<Void> deleteLunch(String lunchID) {
        return LunchFirestoreDAO.getLunchesCollection().document(lunchID).delete();
    }
}
