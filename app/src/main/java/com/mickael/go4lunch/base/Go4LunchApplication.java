package com.mickael.go4lunch.base;

import com.mickael.go4lunch.di.DaggerApplicationGraph;

import dagger.android.AndroidInjector;
import dagger.android.support.DaggerApplication;

public class Go4LunchApplication extends DaggerApplication {
    @Override
    protected AndroidInjector<? extends DaggerApplication> applicationInjector() {
        return DaggerApplicationGraph.factory().create(this);
    }
}
