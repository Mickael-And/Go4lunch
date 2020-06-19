package com.mickael.go4lunch.data.api;

import com.mickael.go4lunch.data.model.placesapi.response.NearbySearchRestaurantsApiResponse;
import com.mickael.go4lunch.data.model.placesapi.response.RestaurantDetailsApiResponse;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Interface allowing Google API calls to obtain restaurants and information.
 */
public interface RestaurantApiService {

    /**
     * Request for obtaining restaurants within a given radius as well as a position.
     *
     * @param location  starting position
     * @param radius    department in which we want to recover restaurants
     * @param webApiKey Google API key
     * @return API response as an Observable
     */
    @GET("nearbysearch/json?type=restaurant")
    Single<NearbySearchRestaurantsApiResponse> nearbySearchRestaurantsRequest(@Query("location") String location, @Query("radius") String radius, @Query("key") String webApiKey);

    /**
     * Request to retrieve the details of a restaurant.
     *
     * @param placeId   place id
     * @param webApiKey Google API key
     * @return API response as an Observable
     */
    @GET("details/json?fields=vicinity,international_phone_number,geometry,photos,name,place_id,rating,website")
    Single<RestaurantDetailsApiResponse> restaurantDetailsRequest(@Query("place_id") String placeId, @Query("key") String webApiKey);
}
