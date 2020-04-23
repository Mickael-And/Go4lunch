package com.mickael.go4lunch.ui.map;

import androidx.lifecycle.ViewModel;

import com.mickael.go4lunch.data.repository.RestaurantRepository;

import javax.inject.Inject;

public class MapActivityViewModel extends ViewModel {

    private RestaurantRepository restaurantRepository;

    public MapActivityViewModel(RestaurantRepository pRestaurantRepository) {
        this.restaurantRepository = pRestaurantRepository;
    }
}
