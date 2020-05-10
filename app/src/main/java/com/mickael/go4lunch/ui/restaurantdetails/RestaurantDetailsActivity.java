package com.mickael.go4lunch.ui.restaurantdetails;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RatingBar;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.google.android.material.textview.MaterialTextView;
import com.mickael.go4lunch.R;
import com.mickael.go4lunch.data.model.Restaurant;
import com.mickael.go4lunch.di.ViewModelFactory;
import com.mickael.go4lunch.ui.map.fragment.restaurant.RestaurantFragment;

import java.util.Locale;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.support.DaggerAppCompatActivity;

public class RestaurantDetailsActivity extends DaggerAppCompatActivity {
    @BindView(R.id.appbar_picture)
    ImageView appBarPicture;
    @BindView(R.id.tv_restaurant_details_name)
    MaterialTextView restaurantName;
    @BindView(R.id.restaurant_details_rating)
    RatingBar restaurantRating;
    @BindView(R.id.tv_restaurant_details_adress)
    MaterialTextView restaurantAddress;

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
            System.out.println(extras.getString((RestaurantFragment.EXTRAS_RESTAURANT_ID)));
            this.viewModel.getSelectedRestaurant(extras.getString(RestaurantFragment.EXTRAS_RESTAURANT_ID));
        }
        this.viewModel.getLiveRestaurant().observe(this, this::updateComponentsValues);
    }


    private void updateComponentsValues(Restaurant restaurant) {
        if (restaurant != null) {
            this.updateHeaderPhoto(restaurant);
            this.restaurantName.setText(restaurant.getName());
            this.restaurantRating.setRating(this.updateRestaurantRating(restaurant.getRating()));
            this.restaurantAddress.setText(restaurant.getVicinity());
        }
    }

    private float updateRestaurantRating(String rating) {
        float restaurantRating = Float.parseFloat(rating);
        return (3 * restaurantRating) / 5;
    }

    private void updateHeaderPhoto(Restaurant restaurant) {
        System.out.println("Restaurant details photos = " + restaurant.getPhotos());
        if(restaurant.getPhotos() != null){
            String url = String.format(Locale.getDefault(), "https://maps.googleapis.com/maps/api/place/photo?" +
                    "maxheight=1600" +
                    "&photoreference=%s" +
                    "&key=AIzaSyDuiYTUSAt7OeV6tIoXzTVil6XW5j-NCwc", restaurant.getPhotos().get(0).getPhotoReference());
            Glide.with(this)
                    .load(url)
                    .centerCrop()
                    .into(this.appBarPicture);
        }
    }
}
