package com.mickael.go4lunch.di;

import com.mickael.go4lunch.base.Go4LunchApplication;

import javax.inject.Singleton;

import dagger.Component;
import dagger.android.AndroidInjector;
import dagger.android.support.AndroidSupportInjectionModule;

@Component(modules = {
        AppModule.class,
        ActivityBuilder.class,
        AndroidSupportInjectionModule.class,
        ApiModule.class,
        ViewModelModule.class})
@Singleton
public interface ApplicationGraph extends AndroidInjector<Go4LunchApplication> {

    @Component.Factory
    interface Factory extends AndroidInjector.Factory<Go4LunchApplication> {
    }
}