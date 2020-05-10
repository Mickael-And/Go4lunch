package com.mickael.go4lunch.data.model.placesapi.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.mickael.go4lunch.data.model.Restaurant;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RestaurantDetailsApiResponse {
    @SerializedName("result")
    @Expose
    private Restaurant restaurant;
}