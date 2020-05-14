package com.mickael.go4lunch.ui.map.fragment.workmate;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.mickael.go4lunch.data.dao.RestaurantFirestoreDAO;
import com.mickael.go4lunch.data.dao.UserFirestoreDAO;
import com.mickael.go4lunch.data.model.User;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import lombok.Getter;

public class WorkmateFragmentViewModel extends ViewModel {

    @Getter
    private MutableLiveData<List<User>> users;

    @Inject
    public WorkmateFragmentViewModel() {
        this.users = new MutableLiveData<>();
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

    private Date getReferenceDate() {
        Date referenceDate = new Date();
        referenceDate.setHours(14);
        referenceDate.setMinutes(0);
        referenceDate.setSeconds(0);

        Date dateNow = new Date();

        if (dateNow.after(referenceDate)) {
            return referenceDate;
        } else {
            referenceDate.setDate(referenceDate.getDate() - 1);
            return referenceDate;
        }
    }

    boolean isEatingLunchAtNoon(User user) {
        boolean isEating;
        if (user.getLunchDate() != null) {
            isEating = user.getLunchDate().after(this.getReferenceDate());
        } else {
            isEating = false;
        }
        return isEating;
    }

    Task<DocumentSnapshot> getRestaurant(String restaurantId) {
        return RestaurantFirestoreDAO.getRestaurant(restaurantId);
    }
}
