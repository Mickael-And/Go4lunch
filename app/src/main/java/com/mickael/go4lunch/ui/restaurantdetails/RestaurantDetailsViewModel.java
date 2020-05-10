package com.mickael.go4lunch.ui.restaurantdetails;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.mickael.go4lunch.data.model.Restaurant;
import com.mickael.go4lunch.data.repository.RestaurantRepository;
import com.mickael.go4lunch.ui.map.fragment.restaurant.RestaurantFragmentViewModel;

import java.util.Locale;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import lombok.Getter;
import retrofit2.HttpException;

public class RestaurantDetailsViewModel extends ViewModel {

    private RestaurantRepository restaurantRepository;

    @Getter
    private MutableLiveData<Restaurant> liveRestaurant;

    private Disposable disposable;

    @Inject
    public RestaurantDetailsViewModel(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
        this.liveRestaurant = new MutableLiveData<>();
    }

    public void getSelectedRestaurant(String placeId) {
        System.out.println(placeId);
        this.disposable = this.restaurantRepository.getRestaurantDetails(placeId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(restaurantDetailsApiResponse -> {
                    this.liveRestaurant.setValue(restaurantDetailsApiResponse.getRestaurant());
                }, throwable -> {
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
