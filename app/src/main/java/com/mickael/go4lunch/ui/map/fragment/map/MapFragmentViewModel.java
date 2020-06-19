package com.mickael.go4lunch.ui.map.fragment.map;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.maps.model.LatLng;
import com.mickael.go4lunch.data.model.Restaurant;
import com.mickael.go4lunch.data.repository.AppRepository;

import java.util.List;

import javax.inject.Inject;

import lombok.Getter;

/**
 * {@link ViewModel} of {@link MapFragment}.
 */
public class MapFragmentViewModel extends ViewModel {

    /**
     * Application repository.
     */
    private AppRepository appRepository;

    /**
     * List of nearby restaurants.
     */
    @Getter
    private MutableLiveData<List<Restaurant>> restaurants;

    @Inject
    public MapFragmentViewModel(AppRepository pAppRepository) {
        this.appRepository = pAppRepository;
        this.restaurants = this.appRepository.getRestaurants();
    }

    /**
     * Ask to find nearby restaurants.
     *
     * @param location coordinates from which we start the search
     * @param radius   radius where to find restaurants
     */
    void initRestaurantPlaces(LatLng location, String radius) {
        this.appRepository.getNearbyPlaces(location, radius);
    }
}
