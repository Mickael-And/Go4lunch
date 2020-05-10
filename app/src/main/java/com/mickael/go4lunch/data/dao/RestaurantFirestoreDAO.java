package com.mickael.go4lunch.data.dao;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mickael.go4lunch.data.model.Restaurant;
import com.mickael.go4lunch.data.model.placesapi.common.Geometry;
import com.mickael.go4lunch.data.model.placesapi.common.OpeningHours;
import com.mickael.go4lunch.data.model.placesapi.common.Photo;

import java.util.List;
import java.util.Map;

public class RestaurantFirestoreDAO {
    private static final String COLLECTION_NAME = "restaurants";

    // COLLECTION REFERENCE
    public static CollectionReference getRestaurantsCollection() {
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    // CREATE
    public static Task<Void> createRestaurant(String vicinity, String internationalPhoneNumber, Geometry geometry, List<Photo> photos,
                                              String name, String placeId, String rating, String website, OpeningHours openingHours) {
        Restaurant restaurantToCreate = new Restaurant(vicinity, internationalPhoneNumber, geometry, photos, name, placeId, rating, website, openingHours);
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
