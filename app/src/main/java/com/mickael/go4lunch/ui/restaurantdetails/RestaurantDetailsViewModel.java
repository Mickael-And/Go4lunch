package com.mickael.go4lunch.ui.restaurantdetails;

import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.mickael.go4lunch.data.dao.RestaurantFirestoreDAO;
import com.mickael.go4lunch.data.dao.UserFirestoreDAO;
import com.mickael.go4lunch.data.model.Restaurant;
import com.mickael.go4lunch.data.model.User;
import com.mickael.go4lunch.data.repository.RestaurantRepository;
import com.mickael.go4lunch.ui.map.fragment.restaurant.RestaurantFragmentViewModel;

import java.util.Date;
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

    private RestaurantRepository restaurantRepository;

    @Getter
    private MutableLiveData<Restaurant> liveRestaurant;

    @Getter
    private MutableLiveData<Boolean> liveIsSelected;

    private Disposable disposable;

    @Nullable
    private User currentUser;

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
                        this.chechIfRestaurantIsSelected();
                    } else {
                        Log.d(TAG, "Get user response is null or doesn't exist");
                        this.currentUser = null;
                    }
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                    this.currentUser = null;
                }
                System.out.println("RestaurantDetailsViewModel.findCurrentUser in task");
                System.out.println(currentUser);
            });
        }
    }

    private void chechIfRestaurantIsSelected() {
        if (isCurrentUserLOgged()) {
            this.liveIsSelected.setValue(this.userLunchIsThisRestaurant());
        } else {
            this.liveIsSelected.setValue(false);
            Log.e(TAG, "User isn't logged");
        }
    }

    private Date getReferenceDate() {
        Date referenceDate = new Date();
        referenceDate.setHours(14);
        referenceDate.setMinutes(0);
        referenceDate.setSeconds(0);

        Date dateNow = new Date();

        if (dateNow.after(referenceDate)) {
            return referenceDate;
        } else {
            referenceDate.setDate(referenceDate.getDate() - 1);
            return referenceDate;
        }
    }

    private boolean userLunchIsThisRestaurant() {
        boolean lunchRestaurant;
        if (this.currentUser != null) {
            if (this.liveRestaurant.getValue() != null && this.liveRestaurant.getValue().getPlaceId() != null && this.liveRestaurant.getValue().getPlaceId().equals(this.currentUser.getLunchplaceId())) {
                lunchRestaurant = this.currentUser.getLunchDate() != null && this.currentUser.getLunchDate().after(this.getReferenceDate());
            } else {
                lunchRestaurant = false;
            }
        } else {
            Log.i(TAG, "No user found");
            lunchRestaurant = false;
        }
        return lunchRestaurant;
    }

    Boolean isCurrentUserLOgged() {
        return (this.getCurrentUser() != null);
    }

    @Nullable
    private FirebaseUser getCurrentUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    void chooseRestaurant() {
        if (isCurrentUserLOgged()) {
            if (this.liveIsSelected.getValue() != null && this.liveIsSelected.getValue()) {
                if (this.currentUser != null) {
                    this.currentUser.setLunchDate(null);
                    this.currentUser.setLunchplaceId(null);
                    this.liveIsSelected.setValue(false);
                } else {
                    Log.d(TAG, "No user found");
                }
            } else {
                if (this.currentUser != null && this.liveRestaurant.getValue() != null) {
                    this.currentUser.setLunchDate(new Date());
                    this.currentUser.setLunchplaceId(this.liveRestaurant.getValue().getPlaceId());
                    this.liveIsSelected.setValue(true);
                } else {
                    Log.d(TAG, "No user found or restaurant is null");
                }
            }
        } else {
            Log.e(TAG, "User isn't logged");
        }
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
            if (this.liveIsSelected.getValue()) {
                RestaurantFirestoreDAO.createRestaurant(this.liveRestaurant.getValue().getVicinity(), this.liveRestaurant.getValue().getInternationalPhoneNumber(), this.liveRestaurant.getValue().getGeometry()
                        , this.liveRestaurant.getValue().getPhotos(), this.liveRestaurant.getValue().getName(), this.liveRestaurant.getValue().getPlaceId(), this.liveRestaurant.getValue().getRating()
                        , this.liveRestaurant.getValue().getWebsite(), this.liveRestaurant.getValue().getOpeningHours())
                        .addOnFailureListener(e -> Log.e(TAG, "Can't create restaurant in firestore database", e));
            }

            Map<String, Object> userFieldsToUpdate = new HashMap<>();
            userFieldsToUpdate.put("lunchplaceId", this.currentUser.getLunchplaceId());
            userFieldsToUpdate.put("lunchDate", this.currentUser.getLunchDate());

            UserFirestoreDAO.updateUser(this.currentUser.getUserId(), userFieldsToUpdate).addOnFailureListener(e -> Log.e(TAG, "Can't update user in firestore database", e));
        } else {
            Log.d(TAG, "Can't save user lunch because there is a null");
        }
    }
}
