package com.mickael.go4lunch.ui.map.fragment.map;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QuerySnapshot;
import com.mickael.go4lunch.data.dao.UserFirestoreDAO;
import com.mickael.go4lunch.data.model.Restaurant;
import com.mickael.go4lunch.data.repository.RestaurantRepository;

import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import lombok.Getter;
import retrofit2.HttpException;

public class MapFragmentViewModel extends ViewModel {


    private RestaurantRepository restaurantRepository;

    private Disposable disposable;

    @Getter
    private MutableLiveData<List<Restaurant>> restaurants;

    @Inject
    public MapFragmentViewModel(RestaurantRepository pRestaurantRepository) {
        this.restaurantRepository = pRestaurantRepository;
        this.restaurants = new MutableLiveData<>();
    }

    public void makeANearbySearchRequest(LatLng location, String radius) {
        this.disposable = this.restaurantRepository.getNearbyPlaces(location, radius)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(nearbySearchPlacesApiResponse -> restaurants.setValue(nearbySearchPlacesApiResponse.getRestaurants()),
                        throwable -> {
                            // cast to retrofit.HttpException to get the response code
                            if (throwable instanceof HttpException) {
                                HttpException response = (HttpException) throwable;
                                int code = response.code();
                                Log.i(MapFragmentViewModel.class.getSimpleName(), String.format(Locale.getDefault(), "Nearby search request: Api call didn't work %d", code));
                            } else {
                                Log.i(MapFragmentViewModel.class.getSimpleName(), "Nearby search request: Api call didn't work ");
                            }
                        });
    }

    public Task<QuerySnapshot> getNumberOfWorkmateAtPlace(String placeId) {
        return UserFirestoreDAO.getUsersCollection().whereEqualTo("lunchplaceId", placeId).get();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (this.disposable != null) {
            this.disposable.dispose();
        }
    }
}
