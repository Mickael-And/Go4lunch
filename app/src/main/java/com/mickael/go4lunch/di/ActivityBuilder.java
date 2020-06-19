package com.mickael.go4lunch.di;

import com.mickael.go4lunch.ui.main.MainActivity;
import com.mickael.go4lunch.ui.map.activity.MapActivity;
import com.mickael.go4lunch.ui.map.fragment.map.MapFragment;
import com.mickael.go4lunch.ui.map.fragment.restaurant.RestaurantFragment;
import com.mickael.go4lunch.ui.map.fragment.workmate.WorkmateFragment;
import com.mickael.go4lunch.ui.restaurantdetails.RestaurantDetailsActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * Class used to provide the Dagger dependency tree with activities / fragments that will need injections.
 */
@Module
public abstract class ActivityBuilder {
    @ContributesAndroidInjector
    abstract MainActivity bindMainActivity();

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
