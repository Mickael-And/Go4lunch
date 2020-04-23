package com.mickael.go4lunch.data.repository;

import com.mickael.go4lunch.data.api.RestaurantApiService;
import com.mickael.go4lunch.data.model.Restaurant;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Observable;

@Singleton
public class RestaurantRepository {

    private RestaurantApiService restaurantApiService;

    @Inject
    public RestaurantRepository(RestaurantApiService restaurantApiService) {
        this.restaurantApiService = restaurantApiService;
    }


    /*
     * We are using this method to fetch the movies list
     * NetworkBoundResource is part of the Android architecture
     * components. You will notice that this is a modified version of
     * that class. That class is based on LiveData but here we are
     * using Observable from RxJava.
     *
     * There are three methods called:
     * a. fetch data from server
     * b. fetch data from local
     * c. save data from api in local
     *
     * So basically we fetch data from server, store it locally
     * and then fetch data from local and update the UI with
     * this data.
     *
     * */
// e
}
