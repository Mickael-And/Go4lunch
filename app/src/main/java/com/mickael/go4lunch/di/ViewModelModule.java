package com.mickael.go4lunch.di;

import androidx.lifecycle.ViewModel;

import com.mickael.go4lunch.data.repository.RestaurantRepository;
import com.mickael.go4lunch.ui.map.MapActivityViewModel;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Map;

import javax.inject.Provider;
import javax.inject.Singleton;

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

    @Singleton
    @Provides
        //TODO: Virer ce provide car inject dns constructeur de la factory et deja singleton sur la classe
    ViewModelFactory provideViewModelFactory(Map<Class<? extends ViewModel>, Provider<ViewModel>> providerMap) {
        return new ViewModelFactory(providerMap);
    }

    @Provides
    @IntoMap
    @ViewModelKey(MapActivityViewModel.class)
    ViewModel provideMapActivityViewModel(RestaurantRepository pRestaurantRepository) {
        return new MapActivityViewModel(pRestaurantRepository);
    }

}
