package com.mickael.go4lunch.data.model.placesapi;

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
public class NearbySearchPlacesApiResponse {
    private String[] html_attributions;

    private Results[] results;

    private String status;
}
