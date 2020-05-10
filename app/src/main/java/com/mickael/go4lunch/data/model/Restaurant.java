package com.mickael.go4lunch.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.mickael.go4lunch.data.model.placesapi.common.Geometry;
import com.mickael.go4lunch.data.model.placesapi.common.OpeningHours;
import com.mickael.go4lunch.data.model.placesapi.common.Photo;
import com.mickael.go4lunch.data.model.placesapi.common.PlusCode;

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
    private String[] types;

    @SerializedName("business_status")
    @Expose
    private String businessStatus;

    private String icon;

    private String rating;

    @SerializedName("photos")
    @Expose
    private Photo[] photos;

    private String reference;

    @SerializedName("user_ratings_total")
    @Expose
    private String userRatingsTotal;

    @SerializedName("price_level")
    @Expose
    private String priceLevel;

    private String scope;

    private String name;

    @SerializedName("opening_hours")
    @Expose
    private OpeningHours openingHours;

    private Geometry geometry;

    private String vicinity;

    private String id;

    @SerializedName("plus_code")
    @Expose
    private PlusCode plusCode;

    @SerializedName("place_id")
    @Expose
    private String placeId;
}
