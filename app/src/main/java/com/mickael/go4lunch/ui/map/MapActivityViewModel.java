package com.mickael.go4lunch.ui.map;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mickael.go4lunch.data.repository.RestaurantRepository;

public class MapActivityViewModel extends ViewModel {

    private RestaurantRepository restaurantRepository;

    public MapActivityViewModel(RestaurantRepository pRestaurantRepository) {
        this.restaurantRepository = pRestaurantRepository;
    }

    @Nullable
    FirebaseUser getCurrentUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    Boolean isCurrentUserLOgged(){
        return (this.getCurrentUser() != null);
    }
}
