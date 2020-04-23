package com.mickael.go4lunch.data.api;

import io.reactivex.Observable;
import retrofit2.http.GET;

public interface RestaurantApiService {

    @GET("")
    Observable<RestaurantApiResponse> fetchRestaurants();

}
