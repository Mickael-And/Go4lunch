package com.mickael.go4lunch.data.repository;

import com.google.android.gms.maps.model.LatLng;
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

    public Single<NearbySearchPlacesApiResponse> getNearbyPlaces(LatLng location, String radius, String type) {
        return this.restaurantApiService.nearbySearchRequest(String.format(Locale.getDefault(), "%s,%s", location.latitude, location.longitude), radius, type);
    }

}
