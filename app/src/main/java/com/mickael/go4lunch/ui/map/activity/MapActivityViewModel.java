package com.mickael.go4lunch.ui.map.activity;

import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mickael.go4lunch.data.dao.UserFirestoreDAO;
import com.mickael.go4lunch.data.model.User;
import com.mickael.go4lunch.data.repository.AppRepository;

import javax.inject.Inject;

import lombok.Getter;

/**
 * {@link ViewModel} of {@link MapActivity}.
 */
public class MapActivityViewModel extends ViewModel {

    /**
     * Application repository.
     */
    private AppRepository appRepository;

    /**
     * Connected user.
     */
    @Getter
    private User currentUser;

    @Inject
    public MapActivityViewModel(AppRepository appRepository) {
        this.appRepository = appRepository;
    }

    /**
     * Initializes the list used in the application containing all Go4Lunch users.
     */
    void initUsers() {
        this.appRepository.initUsers();
        this.getUser(getFirebaseUser().getUid());
    }

    /**
     * Retrieves in firesotre database the information of the connected user.
     *
     * @param uid id de l'utilisateur
     */
    private void getUser(String uid) {
        UserFirestoreDAO.getUsersCollection().document(uid).addSnapshotListener((documentSnapshot, e) -> {
            if (e != null) {
                Log.w(this.getClass().getSimpleName(), "Listen failed.", e);
                return;
            }

            if (documentSnapshot != null && documentSnapshot.exists()) {
                this.currentUser = documentSnapshot.toObject(User.class);
            } else {
                Log.d(this.getClass().getSimpleName(), "Current data: null");
            }
        });
    }

    /**
     * Get the connected Firebase user.
     *
     * @return connected user
     */
    @Nullable
    FirebaseUser getFirebaseUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    /**
     * Checks if the application user is connected to Firebase.
     *
     * @return true if connected
     */
    Boolean isCurrentUserLOgged() {
        return (this.getFirebaseUser() != null);
    }
}
