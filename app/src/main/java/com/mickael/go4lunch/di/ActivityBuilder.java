package com.mickael.go4lunch.di;

import com.mickael.go4lunch.ui.map.activity.MapActivity;
import com.mickael.go4lunch.ui.map.fragment.map.MapFragment;
import com.mickael.go4lunch.ui.map.fragment.restaurant.RestaurantFragment;
import com.mickael.go4lunch.ui.map.fragment.workmate.WorkmateFragment;
import com.mickael.go4lunch.ui.restaurantdetails.RestaurantDetailsActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class ActivityBuilder {
    @ContributesAndroidInjector
    abstract MapActivity bindMapActivity();

    @ContributesAndroidInjector
    abstract MapFragment bindMapFragment();

    @ContributesAndroidInjector
    abstract RestaurantFragment bindRestaurantFragment();

    @ContributesAndroidInjector
    abstract WorkmateFragment bindWorkmateFragment();

    @ContributesAndroidInjector
    abstract RestaurantDetailsActivity bindRestaurantDetailsActivity();
}
