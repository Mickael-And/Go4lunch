package com.mickael.go4lunch.data.api;

import com.mickael.go4lunch.data.model.Restaurant;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RestaurantApiResponse {

    private List<Restaurant> results;

    @Inject
    public RestaurantApiResponse() {
        this.results = new ArrayList<>();
    }
}
