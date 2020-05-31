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

public class MapActivityViewModel extends ViewModel {

    private AppRepository appRepository;

    @Getter
    private User currentUser;

    @Inject
    public MapActivityViewModel(AppRepository appRepository) {
        this.appRepository = appRepository;
    }

    public void initUsers() {
        this.appRepository.initUsers();
        this.getUser(getFirebaseUser().getUid());
    }

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

    @Nullable
    FirebaseUser getFirebaseUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    Boolean isCurrentUserLOgged() {
        return (this.getFirebaseUser() != null);
    }
}
