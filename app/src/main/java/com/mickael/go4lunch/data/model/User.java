package com.mickael.go4lunch.data.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class User {
    private String userId;
    private String username;
    private String urlPicture;
    private String laucnhPlaces;
}
