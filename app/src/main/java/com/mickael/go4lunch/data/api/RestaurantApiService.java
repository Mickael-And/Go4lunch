package com.mickael.go4lunch.data.api;

import com.mickael.go4lunch.data.model.placesapi.NearbySearchPlacesApiResponse;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RestaurantApiService {

    @GET("api/place/nearbysearch/json?key=AIzaSyDuiYTUSAt7OeV6tIoXzTVil6XW5j-NCwc")
    Single<NearbySearchPlacesApiResponse> nearbySearchRequest(@Query("location") String location, @Query("radius") String radius, @Query("type") String type);
}
