package com.mickael.go4lunch.di;

import com.mickael.go4lunch.ui.map.MapActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class ActivityBuilder {

    @ContributesAndroidInjector//TODO: Parenthèses à enlever ou modules à fournir ?
    abstract MapActivity bindMapActivity();
}
