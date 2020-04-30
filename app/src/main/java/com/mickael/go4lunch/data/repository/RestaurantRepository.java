package com.mickael.go4lunch.data.repository;

import com.mickael.go4lunch.data.api.RestaurantApiService;
import com.mickael.go4lunch.data.model.placesapi.NearbySearchPlacesApiResponse;

import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Single;

@Singleton
public class RestaurantRepository {

    private RestaurantApiService restaurantApiService;

    @Inject
    public RestaurantRepository(RestaurantApiService restaurantApiService) {
        this.restaurantApiService = restaurantApiService;
    }

    public Single<NearbySearchPlacesApiResponse> makeANearbySearchRequest(String latitude, String longitude, String radius, String type) {
        String location = String.format(Locale.getDefault(), "%s,%s", latitude, longitude);
        return this.restaurantApiService.nearbySearchRequest(location, radius, type);
    }

}
