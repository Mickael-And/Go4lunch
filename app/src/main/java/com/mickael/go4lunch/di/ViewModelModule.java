package com.mickael.go4lunch.di;

import androidx.lifecycle.ViewModel;

import com.mickael.go4lunch.ui.map.activity.MapActivityViewModel;
import com.mickael.go4lunch.ui.map.fragment.map.MapFragmentViewModel;
import com.mickael.go4lunch.ui.map.fragment.restaurant.RestaurantFragmentViewModel;
import com.mickael.go4lunch.ui.map.fragment.workmate.WorkmateFragmentViewModel;
import com.mickael.go4lunch.ui.restaurantdetails.RestaurantDetailsViewModel;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import dagger.Binds;
import dagger.MapKey;
import dagger.Module;
import dagger.multibindings.IntoMap;

@Module
public abstract class ViewModelModule {

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @MapKey
    @interface ViewModelKey {
        Class<? extends ViewModel> value();
    }

    @Binds
    @IntoMap
    @ViewModelKey(MapActivityViewModel.class)
    abstract ViewModel provideMapActivityViewModel(MapActivityViewModel mapActivityViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(MapFragmentViewModel.class)
    abstract ViewModel provideMapFragmentViewModel(MapFragmentViewModel mapFragmentViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(RestaurantFragmentViewModel.class)
    abstract ViewModel provideRestaurantFragmentViewModel(RestaurantFragmentViewModel restaurantFragmentViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(WorkmateFragmentViewModel.class)
    abstract ViewModel provideWorkmateFragmentViewModel(WorkmateFragmentViewModel workmateFragmentViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(RestaurantDetailsViewModel.class)
    abstract ViewModel provideRestaurantDetailsViewModel(RestaurantDetailsViewModel restaurantDetailsViewModel);
}
