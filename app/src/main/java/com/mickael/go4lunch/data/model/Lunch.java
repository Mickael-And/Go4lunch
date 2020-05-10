package com.mickael.go4lunch.data.model;

import java.util.Date;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Lunch {
    @NonNull
    private String lunchId;
    @NonNull
    private String userId;
    @NonNull
    private String lunchPlacesId;
    @NonNull
    private Date lunchDate;
}
