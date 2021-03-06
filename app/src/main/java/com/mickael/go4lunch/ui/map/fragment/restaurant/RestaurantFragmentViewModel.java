package com.mickael.go4lunch.ui.map.fragment.restaurant;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.mickael.go4lunch.data.model.Restaurant;
import com.mickael.go4lunch.data.repository.AppRepository;

import java.util.List;

import javax.inject.Inject;

import lombok.Getter;

/**
 * {@link ViewModel} of {@link RestaurantFragment}.
 */
public class RestaurantFragmentViewModel extends ViewModel {

    /**
     * List of nearby restaurants
     */
    @Getter
    private MutableLiveData<List<Restaurant>> restaurants;

    @Inject
    public RestaurantFragmentViewModel(AppRepository pAppRepository) {
        this.restaurants = pAppRepository.getRestaurants();
    }

}
