package com.mickael.go4lunch.data.model.placesapi.common;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

/**
 * Place coordinates.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Location {
    /**
     * Latitude.
     */
    double lat;

    /**
     * Longitude.
     */
    double lng;
}
