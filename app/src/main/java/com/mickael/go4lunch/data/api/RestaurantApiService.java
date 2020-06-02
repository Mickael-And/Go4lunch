package com.mickael.go4lunch.data.api;

import com.mickael.go4lunch.data.model.placesapi.response.NearbySearchRestaurantsApiResponse;
import com.mickael.go4lunch.data.model.placesapi.response.RestaurantDetailsApiResponse;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RestaurantApiService {

    @GET("nearbysearch/json?type=restaurant")
    Single<NearbySearchRestaurantsApiResponse> nearbySearchRestaurantsRequest(@Query("location") String location, @Query("radius") String radius, @Query("key") String webApiKey);


    @GET("details/json?fields=vicinity,international_phone_number,geometry,photos,name,place_id,rating,website")
    Single<RestaurantDetailsApiResponse> restaurantDetailsRequest(@Query("place_id") String placeId, @Query("key") String webApiKey);
}
