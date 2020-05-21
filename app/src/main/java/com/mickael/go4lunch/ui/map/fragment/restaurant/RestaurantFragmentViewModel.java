package com.mickael.go4lunch.ui.map.fragment.restaurant;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.mickael.go4lunch.data.model.Restaurant;
import com.mickael.go4lunch.data.repository.RestaurantRepository;

import java.util.List;

import javax.inject.Inject;

import lombok.Getter;

public class RestaurantFragmentViewModel extends ViewModel {

    @Getter
    private MutableLiveData<List<Restaurant>> restaurants;

    @Inject
    public RestaurantFragmentViewModel(RestaurantRepository pRestaurantRepository) {
        this.restaurants = pRestaurantRepository.getRestaurants();
    }

}
