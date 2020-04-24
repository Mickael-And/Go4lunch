package com.mickael.go4lunch.di;

import androidx.lifecycle.ViewModel;

import com.mickael.go4lunch.data.repository.RestaurantRepository;
import com.mickael.go4lunch.ui.map.MapActivityViewModel;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import dagger.MapKey;
import dagger.Module;
import dagger.Provides;
import dagger.multibindings.IntoMap;

@Module
public class ViewModelModule {

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @MapKey
    @interface ViewModelKey {
        Class<? extends ViewModel> value();
    }

    @Provides
    @IntoMap
    @ViewModelKey(MapActivityViewModel.class)
    ViewModel provideMapActivityViewModel(RestaurantRepository pRestaurantRepository) {
        return new MapActivityViewModel(pRestaurantRepository);
    }

}
