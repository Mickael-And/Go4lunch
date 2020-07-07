package com.mickael.go4lunch.ui.restaurantdetails;

import android.util.Log;

import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.mickael.go4lunch.BuildConfig;
import com.mickael.go4lunch.data.dao.UserFirestoreDAO;
import com.mickael.go4lunch.data.model.Restaurant;
import com.mickael.go4lunch.data.model.User;
import com.mickael.go4lunch.data.repository.AppRepository;
import com.mickael.go4lunch.ui.map.fragment.restaurant.RestaurantFragmentViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import lombok.Getter;
import retrofit2.HttpException;

/**
 * {@link ViewModel} of {@link RestaurantDetailsActivity}.
 */
public class RestaurantDetailsViewModel extends ViewModel {

    private static final String TAG = RestaurantDetailsViewModel.class.getSimpleName();

    public static final String KEY_MAP_RESTAURANT_ID = "restaurantId";
    public static final String KEY_MAP_RESTAURANT_NAME = "restaurantName";

    private AppRepository appRepository;

    @Getter
    private MutableLiveData<Restaurant> selectedRestaurant;

    /**
     * Current user.
     */
    @Getter
    private MutableLiveData<User> currentUser;

    /**
     * Users list.
     */
    @Getter
    private MediatorLiveData<List<User>> users;

    private Disposable disposable;

    @Inject
    public RestaurantDetailsViewModel(AppRepository appRepository) {
        this.appRepository = appRepository;
        this.selectedRestaurant = new MutableLiveData<>();
        this.currentUser = new MutableLiveData<>();
        this.users = new MediatorLiveData<>();
    }

    /**
     * Initializes the view from a given restaurant id.
     *
     * @param restaurantId restaurant id
     */
    void initView(String restaurantId) {
        this.disposable = this.appRepository.getRestaurantDetails(restaurantId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(restaurantDetailsApiResponse -> {
                    this.selectedRestaurant.setValue(restaurantDetailsApiResponse.getRestaurant());
                    this.getUser();
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

    /**
     * Get the current user.
     */
    private void getUser() {
        if (this.isCurrentUserLOgged()) {
            UserFirestoreDAO.getUser(FirebaseAuth.getInstance().getCurrentUser().getUid()).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        this.currentUser.setValue(documentSnapshot.toObject(User.class));
                        this.getUsersByRestaurant();
                    } else {
                        Log.d(TAG, "Get user response is null or doesn't exist");
                    }
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            });
        }
    }

    /**
     * Get the list of users by restaurant.
     */
    private void getUsersByRestaurant() {
        Restaurant restaurant = this.getSelectedRestaurant().getValue();
        User currentUser = this.getCurrentUser().getValue();

        this.users.addSource(this.appRepository.getUsers(), users1 -> {
            if (restaurant != null && currentUser != null) {
                List<User> userAtNoon = new ArrayList<>();
                for (User user : users1) {
                    if (user.getLunchRestaurant() != null && user.getLunchRestaurant().get(KEY_MAP_RESTAURANT_ID).equals(restaurant.getPlaceId())
                            && !user.getUserId().equals(currentUser.getUserId())) {
                        userAtNoon.add(user);
                    }
                }
                this.users.setValue(userAtNoon);
            }
        });
    }

    /**
     * Indicates whether the current user is connected to Firebase.
     *
     * @return true if connected
     */
    Boolean isCurrentUserLOgged() {
        return (FirebaseAuth.getInstance().getCurrentUser() != null);
    }

    /**
     * Allows you to choose the restaurant for lunch.
     */
    void chooseRestaurant() {
        User currentUser = this.currentUser.getValue();
        Restaurant selectedRestaurant = this.selectedRestaurant.getValue();
        if (currentUser != null && selectedRestaurant != null) {
            if (currentUser.getLunchRestaurant() != null && currentUser.getLunchRestaurant().get(KEY_MAP_RESTAURANT_ID).equals(selectedRestaurant.getPlaceId())) {
                currentUser.setLunchRestaurant(null);
                this.currentUser.setValue(currentUser);
            } else {
                Map<String, String> userLunchRestaurant = new HashMap<>();
                userLunchRestaurant.put(KEY_MAP_RESTAURANT_ID, selectedRestaurant.getPlaceId());
                userLunchRestaurant.put(KEY_MAP_RESTAURANT_NAME, selectedRestaurant.getName());
                currentUser.setLunchRestaurant(userLunchRestaurant);
                this.currentUser.setValue(currentUser);
            }
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (this.disposable != null) {
            this.disposable.dispose();
        }
    }

    /**
     * Apply the modifications made by the user.
     */
    void saveUserModification() {
        User currentUser = this.currentUser.getValue();
        if (currentUser != null) {
            Map<String, Object> userFieldsToUpdate = new HashMap<>();
            userFieldsToUpdate.put("lunchRestaurant", currentUser.getLunchRestaurant());
            userFieldsToUpdate.put("likes", currentUser.getLikes());
            UserFirestoreDAO.updateUser(currentUser.getUserId(), userFieldsToUpdate).addOnFailureListener(e -> Log.e(TAG, "Can't update user in firestore database", e));
        }
    }

    /**
     * Lets like a restaurant.
     *
     * @return true if the restaurant is liked
     * @throws Exception if impossible to apply a choice
     */
    boolean likeRestaurant() throws Exception {
        boolean isLike;
        User currentUser = this.currentUser.getValue();
        Restaurant selectedRestaurant = this.selectedRestaurant.getValue();

        if (currentUser != null && selectedRestaurant != null) {
            String selectedRestaurantId = selectedRestaurant.getPlaceId();
            if (currentUser.getLikes().contains(selectedRestaurantId)) {
                currentUser.getLikes().remove(selectedRestaurantId);
                isLike = false;
            } else {
                currentUser.getLikes().add(selectedRestaurantId);
                isLike = true;
            }
            this.currentUser.setValue(currentUser);
        } else {
            throw new Exception();
        }
        return isLike;
    }

    /**
     * Transforms the restaurant ratio to 3 stars.
     *
     * @param rating the ratio in 5 stars
     * @return the ratio in 3 stars
     */
    public float updateRestaurantRating(String rating) {
        float restaurantRating = Float.parseFloat(rating);
        return (3 * restaurantRating) / 5;
    }

    /**
     * Buil the URL to add the restaurant photo.
     *
     * @param restaurant restaurant where we want the photo
     * @return build url
     */
    public String getRestaurantPhotoUrl(Restaurant restaurant) {
        return String.format(Locale.getDefault(), "https://maps.googleapis.com/maps/api/place/photo?" +
                "maxheight=1600" +
                "&photoreference=%s" +
                "&key=%s", restaurant.getPhotos().get(0).getPhotoReference(), BuildConfig.GOOGLE_WEB_API_KEY);
    }
}