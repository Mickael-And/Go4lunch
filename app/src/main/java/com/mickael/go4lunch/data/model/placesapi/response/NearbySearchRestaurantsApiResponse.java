package com.mickael.go4lunch.data.model.placesapi.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.mickael.go4lunch.data.model.Restaurant;

import java.util.List;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

/**
 * Object containing the response of the API request looking for nearby restaurants.
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NearbySearchRestaurantsApiResponse {

    /**
     * Lis of nearby restaurants.
     */
    @SerializedName("results")
    @Expose
    private List<Restaurant> restaurants;
}
