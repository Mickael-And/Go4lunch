package com.mickael.go4lunch.data.repository;

import android.location.Location;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.mickael.go4lunch.BuildConfig;
import com.mickael.go4lunch.data.api.RestaurantApiService;
import com.mickael.go4lunch.data.dao.UserFirestoreDAO;
import com.mickael.go4lunch.data.model.Restaurant;
import com.mickael.go4lunch.data.model.User;
import com.mickael.go4lunch.data.model.placesapi.response.RestaurantDetailsApiResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import lombok.Getter;
import retrofit2.HttpException;

import static com.mickael.go4lunch.ui.restaurantdetails.RestaurantDetailsViewModel.KEY_MAP_RESTAURANT_ID;

/**
 * Application repository.
 */
@Singleton
public class AppRepository {

    /**
     * Interface allowing Google API calls to obtain restaurants and information.
     */
    private RestaurantApiService restaurantApiService;

    /**
     * List of users in the form of {@link androidx.lifecycle.LiveData}.
     */
    @Getter
    private MutableLiveData<List<User>> users;

    /**
     * List of nearby restaurants as {@link androidx.lifecycle.LiveData}.
     */
    @Getter
    private MutableLiveData<List<Restaurant>> restaurants;

    @Inject
    public AppRepository(RestaurantApiService restaurantApiService) {
        this.restaurantApiService = restaurantApiService;
        this.restaurants = new MutableLiveData<>();
        this.users = new MutableLiveData<>();
    }

    /**
     * Get nearby restaurants.
     *
     * @param location starting position for research
     * @param radius   search radius
     */
    public void getNearbyPlaces(LatLng location, String radius) {
        Disposable disposable = this.restaurantApiService.nearbySearchRestaurantsRequest(String.format(Locale.getDefault(), "%s,%s", location.latitude, location.longitude),
                radius, BuildConfig.GOOGLE_WEB_API_KEY)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(nearbySearchRestaurantsApiResponse -> {
                    this.restaurants.setValue(this.getRestaurantsDistance(nearbySearchRestaurantsApiResponse.getRestaurants(), location));
                    if (this.users.getValue() != null) {
                        this.updateRestaurantList(this.users.getValue());
                    }
                }, throwable -> {
                    // cast to retrofit.HttpException to get the response code
                    if (throwable instanceof HttpException) {
                        HttpException response = (HttpException) throwable;
                        int code = response.code();
                        Log.i(AppRepository.class.getSimpleName(), String.format(Locale.getDefault(), "Nearby search request: Api call didn't work %d", code));
                    } else {
                        Log.i(AppRepository.class.getSimpleName(), "Nearby search request: Api call didn't work ");
                    }
                });
    }

    /**
     * Update nearby restaurants list.
     *
     * @param users application users
     */
    private void updateRestaurantList(List<User> users) {
        List<Restaurant> restaurants = this.restaurants.getValue();
        if (users != null && restaurants != null) {
            for (Restaurant restaurant : restaurants) {
                int attendanceNumber = 0;
                for (User user : users) {
                    if (user.getLunchRestaurant() != null && user.getLunchRestaurant().get(KEY_MAP_RESTAURANT_ID).equals(restaurant.getPlaceId())) {
                        attendanceNumber++;
                    }
                }
                restaurant.setAttendance(attendanceNumber);
            }
            this.restaurants.setValue(restaurants);
        } else {
            Log.d(this.getClass().getSimpleName(), "Users list to update restaurants list is null");
        }
    }

    /**
     * Get the details of a restaurant from its id.
     *
     * @param placeId id of restaurant requested
     * @return API response
     */
    public Single<RestaurantDetailsApiResponse> getRestaurantDetails(String placeId) {
        return this.restaurantApiService.restaurantDetailsRequest(placeId, BuildConfig.GOOGLE_WEB_API_KEY);
    }

    /**
     * Get restaurants distance to device.
     *
     * @param restaurants nearby restaurants list
     * @param location    device position
     * @return the list of nearby restaurants with the distance from the phone
     */
    private List<Restaurant> getRestaurantsDistance(List<Restaurant> restaurants, LatLng location) {
        for (Restaurant restaurant : restaurants) {
            Location deviceLocation = new Location("Device location");
            deviceLocation.setLatitude(location.latitude);
            deviceLocation.setLongitude(location.longitude);

            Location restaurantLocation = new Location("Restaurant location");
            restaurantLocation.setLatitude(restaurant.getGeometry().getLocation().getLat());
            restaurantLocation.setLongitude(restaurant.getGeometry().getLocation().getLng());

            float distance = deviceLocation.distanceTo(restaurantLocation);
            restaurant.setDistanceToDeviceLocation(String.format(Locale.getDefault(), "%dm", Math.round(distance)));
        }
        return restaurants;
    }

    /**
     * Initializes listening to the list of users of the application in the Firestore database.
     */
    public void initUsers() {
        UserFirestoreDAO.getUsersCollection().addSnapshotListener((queryDocumentSnapshots, e) -> {
            if (e != null) {
                Log.w(this.getClass().getSimpleName(), "Listen failed.", e);
                return;
            }
            if (queryDocumentSnapshots != null) {
                List<User> queryUsers = new ArrayList<>();
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    queryUsers.add(documentSnapshot.toObject(User.class));
                }
                this.users.setValue(queryUsers);
                this.updateRestaurantList(queryUsers);
            }
        });
    }
}
