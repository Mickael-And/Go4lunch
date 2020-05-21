package com.mickael.go4lunch.ui.map.fragment.map;

import android.location.Location;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QuerySnapshot;
import com.mickael.go4lunch.data.dao.UserFirestoreDAO;
import com.mickael.go4lunch.data.model.Restaurant;
import com.mickael.go4lunch.data.repository.RestaurantRepository;

import java.util.Date;
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

    @Getter
    private MutableLiveData<List<Restaurant>> restaurants;

    @Inject
    public MapFragmentViewModel(RestaurantRepository pRestaurantRepository) {
        this.restaurantRepository = pRestaurantRepository;
        this.restaurants = this.restaurantRepository.getRestaurants();
    }

    public void initRestaurantPlaces(LatLng location, String radius) {
        this.restaurantRepository.getNearbyPlaces(location, radius);
    }
}
