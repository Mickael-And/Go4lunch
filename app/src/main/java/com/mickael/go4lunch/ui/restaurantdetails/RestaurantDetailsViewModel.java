package com.mickael.go4lunch.ui.restaurantdetails;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.mickael.go4lunch.data.dao.UserFirestoreDAO;
import com.mickael.go4lunch.data.model.Restaurant;
import com.mickael.go4lunch.data.model.User;
import com.mickael.go4lunch.data.repository.AppRepository;
import com.mickael.go4lunch.ui.map.fragment.restaurant.RestaurantFragmentViewModel;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import lombok.Getter;
import retrofit2.HttpException;

public class RestaurantDetailsViewModel extends ViewModel {

    private static final String TAG = RestaurantDetailsViewModel.class.getSimpleName();

    public static final String KEY_MAP_RESTAURANT_ID = "restaurantId";
    public static final String KEY_MAP_RESTAURANT_NAME = "restaurantName";

    private AppRepository appRepository;

    @Getter
    private MutableLiveData<Restaurant> selectedRestaurant;

    @Getter
    private MutableLiveData<User> currentUser;

    private Disposable disposable;

    @Inject
    public RestaurantDetailsViewModel(AppRepository appRepository) {
        this.appRepository = appRepository;
        this.selectedRestaurant = new MutableLiveData<>();
        this.currentUser = new MutableLiveData<>();
    }

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

    private void getUser() {
        if (this.isCurrentUserLOgged()) {
            UserFirestoreDAO.getUser(FirebaseAuth.getInstance().getCurrentUser().getUid()).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        this.currentUser.setValue(documentSnapshot.toObject(User.class));
                    } else {
                        Log.d(TAG, "Get user response is null or doesn't exist");
                    }
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            });
        }
    }

    Boolean isCurrentUserLOgged() {
        return (FirebaseAuth.getInstance().getCurrentUser() != null);
    }

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

    void saveUserModification() {
        User currentUser = this.currentUser.getValue();
        if (currentUser != null) {
            Map<String, Object> userFieldsToUpdate = new HashMap<>();
            userFieldsToUpdate.put("lunchRestaurant", currentUser.getLunchRestaurant());
            userFieldsToUpdate.put("likes", currentUser.getLikes());
            UserFirestoreDAO.updateUser(currentUser.getUserId(), userFieldsToUpdate).addOnFailureListener(e -> Log.e(TAG, "Can't update user in firestore database", e));
        }
    }

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
}