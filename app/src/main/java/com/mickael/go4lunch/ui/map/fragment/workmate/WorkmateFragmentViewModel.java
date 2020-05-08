package com.mickael.go4lunch.ui.map.fragment.workmate;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.mickael.go4lunch.data.api.firebase.UserDAO;
import com.mickael.go4lunch.data.model.User;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import lombok.Getter;

public class WorkmateFragmentViewModel extends ViewModel {

    @Getter
    private MutableLiveData<List<User>> users;

    @Inject
    public WorkmateFragmentViewModel() {
        this.users = new MutableLiveData<>();
        UserDAO.getUsersCollection().addSnapshotListener((queryDocumentSnapshots, e) -> {

            if (e != null) {
                Log.w(this.getClass().getSimpleName(), "Listen failed.", e);
                return;
            }
            List<User> queryUsers = new ArrayList<>();
            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                queryUsers.add(documentSnapshot.toObject(User.class));
            }
            this.users.setValue(queryUsers);
        });
    }
}
