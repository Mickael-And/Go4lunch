package com.mickael.go4lunch.di;

import android.app.Application;

import com.mickael.go4lunch.base.Go4LunchApplication;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;

@Module
public abstract class AppModule {
    @Singleton
    @Binds
    abstract Application provideApplication(Go4LunchApplication go4LunchApplication);
}
