package com.mickael.go4lunch.ui.restaurantdetails;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.mickael.go4lunch.R;
import com.mickael.go4lunch.di.ViewModelFactory;
import com.mickael.go4lunch.ui.map.fragment.restaurant.RestaurantFragment;

import javax.inject.Inject;

import butterknife.ButterKnife;
import dagger.android.support.DaggerAppCompatActivity;

public class RestaurantDetailsActivity extends DaggerAppCompatActivity {
    @Inject
    ViewModelFactory viewModelFactory;

    private RestaurantDetailsViewModel viewModel;
    
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_restaurant_details);
        ButterKnife.bind(this);
        this.viewModel = new ViewModelProvider(this, viewModelFactory).get(RestaurantDetailsViewModel.class);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            System.out.println(extras.getString(RestaurantFragment.EXTRAS_RESTAURANT_ID));
        }
    }
}
