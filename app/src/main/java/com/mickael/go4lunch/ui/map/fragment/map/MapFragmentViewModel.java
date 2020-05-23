package com.mickael.go4lunch.ui.map.fragment.map;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.maps.model.LatLng;
import com.mickael.go4lunch.data.model.Restaurant;
import com.mickael.go4lunch.data.repository.AppRepository;

import java.util.List;

import javax.inject.Inject;

import lombok.Getter;

public class MapFragmentViewModel extends ViewModel {

    private AppRepository appRepository;

    @Getter
    private MutableLiveData<List<Restaurant>> restaurants;

    @Inject
    public MapFragmentViewModel(AppRepository pAppRepository) {
        this.appRepository = pAppRepository;
        this.restaurants = this.appRepository.getRestaurants();
    }

    public void initRestaurantPlaces(LatLng location, String radius) {
        this.appRepository.getNearbyPlaces(location, radius);
    }
}
