package com.mickael.go4lunch.data.model.placesapi;

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
public class Results {
    private String[] types;

    private String business_status;

    private String icon;

    private String rating;

    private Photos[] photos;

    private String reference;

    private String user_ratings_total;

    private String price_level;

    private String scope;

    private String name;

    private Opening_hours opening_hours;

    private Geometry geometry;

    private String vicinity;

    private String id;

    private Plus_code plus_code;

    private String place_id;
}
