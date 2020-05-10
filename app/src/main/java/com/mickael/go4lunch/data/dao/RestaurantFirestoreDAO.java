package com.mickael.go4lunch.data.dao;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mickael.go4lunch.data.model.Restaurant;
import com.mickael.go4lunch.data.model.placesapi.common.Geometry;
import com.mickael.go4lunch.data.model.placesapi.common.OpeningHours;
import com.mickael.go4lunch.data.model.placesapi.common.Photo;
import com.mickael.go4lunch.data.model.placesapi.common.PlusCode;

import java.util.Map;

public class RestaurantFirestoreDAO {
    private static final String COLLECTION_NAME = "restaurants";

    // COLLECTION REFERENCE
    public static CollectionReference getRestaurantsCollection() {
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    // CREATE
    public static Task<Void> createRestaurant(String[] types, String businessStatus, String icon, String rating, Photo[] photos, String reference, String userRatingsTotal, String priceLevel,
                                              String scope, String name, OpeningHours openingHours, Geometry geometry, String vicinity, String id, PlusCode plusCode, String placeId) {
        Restaurant restaurantToCreate = new Restaurant(types, businessStatus, icon, rating, photos, reference, userRatingsTotal, priceLevel, scope, name, openingHours, geometry, vicinity, id, plusCode, placeId);
        return RestaurantFirestoreDAO.getRestaurantsCollection().document().set(restaurantToCreate);
    }

    // READ
    public static Task<DocumentSnapshot> getRestaurant(String restaurantId) {
        return RestaurantFirestoreDAO.getRestaurantsCollection().document(restaurantId).get();
    }

    // UPDATE
    public static Task<Void> updateRestaurant(String restaurantId, Map<String, Object> restaurantFields) {
        return RestaurantFirestoreDAO.getRestaurantsCollection().document(restaurantId).update(restaurantFields);
    }

    // DELETE
    public static Task<Void> deleteRestaurant(String restaurantID) {
        return RestaurantFirestoreDAO.getRestaurantsCollection().document(restaurantID).delete();
    }
}
