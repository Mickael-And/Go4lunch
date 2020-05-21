package com.mickael.go4lunch.data.repository;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.mickael.go4lunch.data.dao.UserFirestoreDAO;
import com.mickael.go4lunch.data.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import lombok.Getter;

@Singleton
public class UserRepository {

    @Getter
    private MutableLiveData<List<User>> users;

    @Inject
    public UserRepository() {
        this.users = new MutableLiveData<>();
        this.initUsers();
    }

    private void initUsers() {
        UserFirestoreDAO.getUsersCollection().addSnapshotListener((queryDocumentSnapshots, e) -> {
            if (e != null) {
                Log.w(this.getClass().getSimpleName(), "Listen failed.", e);
                return;
            }
            if (queryDocumentSnapshots != null) {
                List<User> queryUsers = new ArrayList<>();
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    queryUsers.add(documentSnapshot.toObject(User.class));
                }
                this.users.setValue(queryUsers);
            }
        });
    }
}
