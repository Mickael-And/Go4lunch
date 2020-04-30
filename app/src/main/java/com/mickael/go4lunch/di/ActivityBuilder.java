package com.mickael.go4lunch.di;

import com.mickael.go4lunch.ui.map.activity.MapActivity;
import com.mickael.go4lunch.ui.map.fragment.MapFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class ActivityBuilder {
    @ContributesAndroidInjector
    abstract MapActivity bindMapActivity();

    @ContributesAndroidInjector
    abstract MapFragment bindMapFragment();
}
