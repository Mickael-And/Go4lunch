package com.mickael.go4lunch.ui.map.fragment;

import androidx.lifecycle.ViewModel;

import com.mickael.go4lunch.data.model.placesapi.NearbySearchPlacesApiResponse;
import com.mickael.go4lunch.data.repository.RestaurantRepository;

import javax.inject.Inject;

import io.reactivex.Single;

public class MapFragmentViewModel extends ViewModel {


    private RestaurantRepository restaurantRepository;

    @Inject
    public MapFragmentViewModel(RestaurantRepository pRestaurantRepository) {
        this.restaurantRepository = pRestaurantRepository;

    }

    public Single<NearbySearchPlacesApiResponse> makeANearbySearchRequest(String latitude, String longitude, String radius, String type) {
        return this.restaurantRepository.makeANearbySearchRequest(latitude, longitude, radius, type);

    }


}
