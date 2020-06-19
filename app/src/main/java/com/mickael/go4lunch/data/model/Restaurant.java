package com.mickael.go4lunch.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.mickael.go4lunch.data.model.placesapi.common.Geometry;
import com.mickael.go4lunch.data.model.placesapi.common.OpeningHours;
import com.mickael.go4lunch.data.model.placesapi.common.Photo;

import java.util.List;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

/**
 * Represents a restaurant.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Restaurant {

    /**
     * Restaurant address.
     */
    String vicinity;

    /**
     * Restaurant phone number.
     */
    @SerializedName("international_phone_number")
    @Expose
    String internationalPhoneNumber;

    /**
     * Restaurant position.
     */
    Geometry geometry;

    /**
     * Restaurant photo.
     */
    List<Photo> photos;

    /**
     * Restaurant name.
     */
    String name;

    /**
     * Place id.
     */
    @SerializedName("place_id")
    @Expose
    String placeId;

    /**
     * Restaurant rating.
     */
    String rating;

    /**
     * Restaurant website.
     */
    String website;

    /**
     * Restaurant opening hours.
     */
    @SerializedName("opening_hours")
    @Expose
    OpeningHours openingHours;

    /**
     * Restaurant attendance by users.
     */
    int attendance;

    /**
     * Restaurant distance to device.
     */
    String distanceToDeviceLocation;
}
