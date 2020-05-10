package com.mickael.go4lunch.data.repository;

import com.google.android.gms.maps.model.LatLng;
import com.mickael.go4lunch.data.api.RestaurantApiService;
import com.mickael.go4lunch.data.model.placesapi.response.NearbySearchRestaurantsApiResponse;
import com.mickael.go4lunch.data.model.placesapi.response.RestaurantDetailsApiResponse;

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

    public Single<NearbySearchRestaurantsApiResponse> getNearbyPlaces(LatLng location, String radius) {
        return this.restaurantApiService.nearbySearchRestaurantsRequest(String.format(Locale.getDefault(), "%s,%s", location.latitude, location.longitude), radius);
    }

    public Single<RestaurantDetailsApiResponse> getRestaurantDetails(String placeId) {
        return this.restaurantApiService.restaurantDetailsRequest(placeId);
    }
}
