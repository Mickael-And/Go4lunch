package com.mickael.go4lunch.data.model;

import androidx.annotation.Nullable;

import java.util.List;
import java.util.Map;

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
    @Nullable
    private Map<String, String> lunchRestaurant;
    private List<String> likes;
}
