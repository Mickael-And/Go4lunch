package com.mickael.go4lunch.ui.restaurantdetails;

import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.mickael.go4lunch.data.dao.AttendanceFirestoreDAO;
import com.mickael.go4lunch.data.dao.UserFirestoreDAO;
import com.mickael.go4lunch.data.model.Attendance;
import com.mickael.go4lunch.data.model.Restaurant;
import com.mickael.go4lunch.data.model.User;
import com.mickael.go4lunch.data.repository.RestaurantRepository;
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

    private RestaurantRepository restaurantRepository;

    @Getter
    private MutableLiveData<Restaurant> liveRestaurant;

    @Getter
    private MutableLiveData<Boolean> liveIsSelected;

    private Disposable disposable;

    @Nullable
    private User currentUser;

    private boolean restaurantStartState;

    @Inject
    public RestaurantDetailsViewModel(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
        this.liveRestaurant = new MutableLiveData<>();
        this.liveIsSelected = new MutableLiveData<>();
    }

    void getSelectedRestaurant(String placeId) {
        this.disposable = this.restaurantRepository.getRestaurantDetails(placeId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(restaurantDetailsApiResponse -> {
                    this.liveRestaurant.setValue(restaurantDetailsApiResponse.getRestaurant());
                    this.findCurrentUser();
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

    private void findCurrentUser() {
        if (this.isCurrentUserLOgged()) {
            UserFirestoreDAO.getUser(this.getCurrentUser().getUid()).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        this.currentUser = documentSnapshot.toObject(User.class);
                        this.userLunchIsThisRestaurant();
                    } else {
                        Log.d(TAG, "Get user response is null or doesn't exist");
                        this.currentUser = null;
                    }
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                    this.currentUser = null;
                }
            });
        }
    }

    private void userLunchIsThisRestaurant() {
        boolean lunchRestaurant;
        if (isCurrentUserLOgged()) {
            if (this.currentUser != null) {
                Map<String, String> userLunchRestaurant = this.currentUser.getLunchRestaurant();
                if (userLunchRestaurant != null) {
                    String restaurantId = userLunchRestaurant.get(KEY_MAP_RESTAURANT_ID);
                    lunchRestaurant = restaurantId != null && this.liveRestaurant.getValue().getPlaceId().equals(restaurantId);
                } else {
                    lunchRestaurant = false;
                }
            } else {
                Log.i(TAG, "No user found");
                lunchRestaurant = false;
            }
        } else {
            lunchRestaurant = false;
            Log.e(TAG, "User isn't logged");
        }
        this.restaurantStartState = lunchRestaurant;
        this.liveIsSelected.setValue(this.restaurantStartState);
    }

    Boolean isCurrentUserLOgged() {
        return (this.getCurrentUser() != null);
    }

    @Nullable
    private FirebaseUser getCurrentUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    void chooseRestaurant() {
        this.liveIsSelected.setValue(!this.liveIsSelected.getValue());
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (this.disposable != null) {
            this.disposable.dispose();
        }
    }


    public void saveLunch() {
        if (this.liveRestaurant.getValue() != null && this.currentUser != null && this.liveIsSelected.getValue() != null) {
            if (this.liveIsSelected.getValue() != this.restaurantStartState) {
                if (this.liveIsSelected.getValue()) {

                    if (this.currentUser.getLunchRestaurant() != null) {
                        Map<String, String> lunchRestaurant = this.currentUser.getLunchRestaurant();

                        this.restaurantRepository.updateRepositoryAttendance(false, lunchRestaurant.get(KEY_MAP_RESTAURANT_ID));
                        this.updateFirestoreAttendance(false, lunchRestaurant.get(KEY_MAP_RESTAURANT_ID));
                    }

                    this.restaurantRepository.updateRepositoryAttendance(true, this.getLiveRestaurant().getValue().getPlaceId());
                    this.updateFirestoreAttendance(true, this.getLiveRestaurant().getValue().getPlaceId());

                    Map<String, String> userLunchRestaurant = new HashMap<>();
                    userLunchRestaurant.put(KEY_MAP_RESTAURANT_ID, this.liveRestaurant.getValue().getPlaceId());
                    userLunchRestaurant.put(KEY_MAP_RESTAURANT_NAME, this.liveRestaurant.getValue().getName());

                    this.updateUser(userLunchRestaurant);

                } else {
                    this.restaurantRepository.updateRepositoryAttendance(false, this.getLiveRestaurant().getValue().getPlaceId());
                    this.updateFirestoreAttendance(false, this.getLiveRestaurant().getValue().getPlaceId());

                    this.updateUser(null);
                }
            }
        } else {
            Log.d(TAG, "Can't save user lunch because there is a null");
        }
    }

    /**
     * @param stateUpdate true increase
     */
    private void updateFirestoreAttendance(boolean stateUpdate, String restaurantId) {
        AttendanceFirestoreDAO.getAttendance(restaurantId)
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        int restaurantAttendance = documentSnapshot.toObject(Attendance.class).getRestaurantAttendance();
                        Map<String, Object> fields = new HashMap<>();
                        if (stateUpdate) {
                            fields.put("restaurantAttendance", restaurantAttendance + 1);
                        } else {
                            fields.put("restaurantAttendance", restaurantAttendance - 1);
                        }
                        AttendanceFirestoreDAO.updateAttendance(restaurantId, fields)
                                .addOnFailureListener(e -> Log.d(this.getClass().getSimpleName(), "Can't update attendance restaurant in firestore database", e));
                    } else {
                        if (stateUpdate) {
                            AttendanceFirestoreDAO.createAttendance(restaurantId, 1)
                                    .addOnFailureListener(e -> Log.d(this.getClass().getSimpleName(), "Can't create attendance restaurant in firestore database", e));
                        }
                    }
                });
    }

    private void updateUser(Map<String, String> userLunchRestaurant) {
        Map<String, Object> userFieldsToUpdate = new HashMap<>();
        userFieldsToUpdate.put("lunchRestaurant", userLunchRestaurant);
        UserFirestoreDAO.updateUser(this.currentUser.getUserId(), userFieldsToUpdate).addOnFailureListener(e -> Log.e(TAG, "Can't update user in firestore database", e));
    }
}
