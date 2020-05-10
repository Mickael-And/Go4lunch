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

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Restaurant {

    String vicinity;

    @SerializedName("international_phone_number")
    @Expose
    String internationalPhoneNumber;

    Geometry geometry;

    List<Photo> photos;

    String name;

    @SerializedName("place_id")
    @Expose
    String placeId;

    String rating;

    String website;

    @SerializedName("opening_hours")
    @Expose
    OpeningHours openingHours;


}
