package com.mickael.go4lunch.data.model;

import androidx.annotation.Nullable;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Respresents a user.
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class User {
    /**
     * User id.
     */
    private String userId;

    /**
     * User name.
     */
    private String username;

    /**
     * Url user profile picture.
     */
    private String urlPicture;

    /**
     * User lunch.
     */
    @Nullable
    private Map<String, String> lunchRestaurant;

    /**
     * List of restaurants liked by the user.
     */
    private List<String> likes;
}
