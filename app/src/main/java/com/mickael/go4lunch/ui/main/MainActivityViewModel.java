package com.mickael.go4lunch.ui.main;

import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.mickael.go4lunch.data.dao.UserFirestoreDAO;

import java.util.ArrayList;

import javax.inject.Inject;

/**
 * {@link ViewModel} of {@link MainActivity}.
 */
public class MainActivityViewModel extends ViewModel {

    private static final String TAG = MainActivityViewModel.class.getSimpleName();

    @Inject
    public MainActivityViewModel() {
    }

    /**
     * Retrieves the user who is using the application.
     *
     * @return the connected Firebase user
     */
    @Nullable
    private FirebaseUser getCurrentUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    /**
     * Checks if the application user is connected to Firebase.
     *
     * @return true if connected
     */
    Boolean isCurrentUserLOgged() {
        return (this.getCurrentUser() != null);
    }

    /**
     * Creation of a user in database if it does not exist.
     */
    void createUserIfNeeded() {
        if (this.isCurrentUserLOgged()) {
            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            UserFirestoreDAO.getUser(firebaseUser.getUid()).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot.exists()) {
                        Log.i(TAG, "User already exist in database");
                    } else {
                        String userId = firebaseUser.getUid();
                        String username = firebaseUser.getDisplayName();
                        String urlPicture = firebaseUser.getPhotoUrl() != null ? FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl().toString() : null;
                        UserFirestoreDAO.createUser(userId, username, urlPicture, null, new ArrayList<>()).addOnFailureListener(e -> Log.e(TAG, "Can't create user in firestore database", e));
                    }
                } else {
                    Log.e(TAG, "Can't check if connected user exist in firestore database");
                }
            });
        }
    }
}
