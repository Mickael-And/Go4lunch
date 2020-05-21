package com.mickael.go4lunch.data.repository;

import android.location.Location;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.DocumentSnapshot;
import com.mickael.go4lunch.data.api.RestaurantApiService;
import com.mickael.go4lunch.data.dao.AttendanceFirestoreDAO;
import com.mickael.go4lunch.data.model.Attendance;
import com.mickael.go4lunch.data.model.Restaurant;
import com.mickael.go4lunch.data.model.placesapi.response.RestaurantDetailsApiResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import lombok.Getter;
import retrofit2.HttpException;

@Singleton
public class RestaurantRepository {

    private RestaurantApiService restaurantApiService;

    private List<Restaurant> temporaryList = new ArrayList<>();

    @Getter
    private MutableLiveData<List<Restaurant>> restaurants;

    private Disposable disposable;

    @Inject
    public RestaurantRepository(RestaurantApiService restaurantApiService) {
        this.restaurantApiService = restaurantApiService;
        this.restaurants = new MutableLiveData<>();
    }

    public void getNearbyPlaces(LatLng location, String radius) {
        this.disposable = this.restaurantApiService.nearbySearchRestaurantsRequest(String.format(Locale.getDefault(), "%s,%s", location.latitude, location.longitude), radius)
                .toObservable()
                .flatMap(nearbySearchRestaurantsApiResponse -> Observable.create(emitter -> {
                    for (Restaurant restaurant : nearbySearchRestaurantsApiResponse.getRestaurants()) {
                        emitter.onNext(restaurant);
                    }
                    emitter.onComplete();
                }))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposable1 -> temporaryList.clear())
                .subscribe(o -> {
                    Restaurant restaurant = (Restaurant) o;
                    Location deviceLocation = new Location("Device location");
                    deviceLocation.setLatitude(location.latitude);
                    deviceLocation.setLongitude(location.longitude);

                    Location restaurantLocation = new Location("Restaurant location");
                    restaurantLocation.setLatitude(restaurant.getGeometry().getLocation().getLat());
                    restaurantLocation.setLongitude(restaurant.getGeometry().getLocation().getLng());

                    restaurant.setDistanceToDeviceLocation(this.getDistance(restaurantLocation, deviceLocation));

                    AttendanceFirestoreDAO.getAttendance(restaurant.getPlaceId()).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot snapshot = task.getResult();
                            if (snapshot != null && snapshot.exists()) {
                                restaurant.setAttendance(snapshot.toObject(Attendance.class).getRestaurantAttendance());
                                temporaryList.add(restaurant);
                            } else {
                                restaurant.setAttendance(0);
                                temporaryList.add(restaurant);
                            }
                        } else {
                            restaurant.setAttendance(0);
                            temporaryList.add(restaurant);
                        }
                        this.restaurants.setValue(temporaryList);
                    });
                }, throwable -> {
                    // cast to retrofit.HttpException to get the response code
                    if (throwable instanceof HttpException) {
                        HttpException response = (HttpException) throwable;
                        int code = response.code();
                        Log.i(RestaurantRepository.class.getSimpleName(), String.format(Locale.getDefault(), "Nearby search request: Api call didn't work %d", code));
                    } else {
                        Log.i(RestaurantRepository.class.getSimpleName(), "Nearby search request: Api call didn't work ");
                    }
                });
    }

    public Single<RestaurantDetailsApiResponse> getRestaurantDetails(String placeId) {
        return this.restaurantApiService.restaurantDetailsRequest(placeId);
    }

    private String getDistance(Location restaurantLocation, Location deviceLocation) {
        Location device = new Location("Device location");
        device.setLatitude(deviceLocation.getLatitude());
        device.setLongitude(deviceLocation.getLongitude());

        float distance = device.distanceTo(restaurantLocation);

        return String.format(Locale.getDefault(), "%dm", Math.round(distance));
    }

    public void updateRepositoryAttendance(boolean updateState, String restaurantId) {
        List<Restaurant> restaurants = this.getRestaurants().getValue();

        for (Restaurant restaurantToUpdate : restaurants) {
            if (restaurantToUpdate.getPlaceId().equals(restaurantId)) {
                if (updateState) {
                    restaurantToUpdate.setAttendance(restaurantToUpdate.getAttendance() + 1);
                } else {
                    restaurantToUpdate.setAttendance(restaurantToUpdate.getAttendance() - 1);
                }
            }
        }
        this.restaurants.setValue(restaurants);
    }
}
