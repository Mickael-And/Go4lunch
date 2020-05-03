package com.mickael.go4lunch.data.model.placesapi;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

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
public class Photo {

    @SerializedName("photo_reference")
    @Expose
    private String photoReference;

    private String width;

    @SerializedName("html_attributions")
    @Expose
    private String[] html_attributions;

    private String height;
}
