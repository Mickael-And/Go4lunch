package com.mickael.go4lunch.ui.map.fragment.restaurant;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.maps.model.LatLng;
import com.mickael.go4lunch.data.model.placesapi.Restaurant;
import com.mickael.go4lunch.data.repository.RestaurantRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import lombok.Getter;
import retrofit2.HttpException;

public class RestaurantFragmentViewModel extends ViewModel {


    private RestaurantRepository restaurantRepository;

    private Disposable disposable;

    @Getter
    private MutableLiveData<List<Restaurant>> restaurants;

    @Inject
    public RestaurantFragmentViewModel(RestaurantRepository pRestaurantRepository) {
        this.restaurantRepository = pRestaurantRepository;
        this.restaurants = new MutableLiveData<>();
    }

    public void makeANearbySearchRequest(LatLng location, String radius, String type) {
        this.disposable = this.restaurantRepository.getNearbyPlaces(location, radius, type)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(nearbySearchPlacesApiResponse -> restaurants.setValue(Arrays.asList(nearbySearchPlacesApiResponse.getRestaurants())),
                        throwable -> {
                            // cast to retrofit.HttpException to get the response code
                            if (throwable instanceof HttpException) {
                                HttpException response = (HttpException) throwable;
                                int code = response.code();
                                Log.i(RestaurantFragmentViewModel.class.getSimpleName(), String.format(Locale.getDefault(), "Nearby search request: Api call didn't work %d", code));
                            } else {
                                Log.i(RestaurantFragmentViewModel.class.getSimpleName(), "Nearby search request: Api call didn't work ");
                            }
                        });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (this.disposable != null) {
            this.disposable.dispose();
        }
    }
}
