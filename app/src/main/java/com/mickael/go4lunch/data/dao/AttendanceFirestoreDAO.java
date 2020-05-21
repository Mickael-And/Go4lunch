package com.mickael.go4lunch.data.dao;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mickael.go4lunch.data.model.Attendance;

import java.util.Map;

public class AttendanceFirestoreDAO {
    private static final String COLLECTION_NAME = "attendances";

    // COLLECTION REFERENCE
    public static CollectionReference getAttendancesCollection() {
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    // CREATE
    public static Task<Void> createAttendance(String restaurantId, int restaurantAttendance) {
        Attendance attendanceToCreate = new Attendance(restaurantId, restaurantAttendance);
        return AttendanceFirestoreDAO.getAttendancesCollection().document(restaurantId).set(attendanceToCreate);
    }

    // READ
    public static Task<DocumentSnapshot> getAttendance(String restaurantId) {
        return AttendanceFirestoreDAO.getAttendancesCollection().document(restaurantId).get();
    }

    // UPDATE
    public static Task<Void> updateAttendance(String restaurantId, Map<String, Object> attendanceFields) {
        return AttendanceFirestoreDAO.getAttendancesCollection().document(restaurantId).update(attendanceFields);
    }

    // DELETE
    public static Task<Void> deleteAttendance(String restaurantID) {
        return AttendanceFirestoreDAO.getAttendancesCollection().document(restaurantID).delete();
    }
}
