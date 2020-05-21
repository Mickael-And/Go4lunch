package com.mickael.go4lunch.ui.restaurantdetails;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RatingBar;

import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textview.MaterialTextView;
import com.mickael.go4lunch.R;
import com.mickael.go4lunch.data.model.Restaurant;
import com.mickael.go4lunch.di.ViewModelFactory;
import com.mickael.go4lunch.ui.main.MainActivity;
import com.mickael.go4lunch.ui.map.fragment.restaurant.RestaurantFragment;

import java.util.Locale;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dagger.android.support.DaggerAppCompatActivity;

public class RestaurantDetailsActivity extends DaggerAppCompatActivity {
    private static final int CALL_PHONE_PERMISSION_REQUEST_CODE = 2;

    @BindView(R.id.restaurant_details_coordinatorlayout)
    CoordinatorLayout coordinatorLayout;
    @BindView(R.id.appbar_picture)
    ImageView appBarPicture;
    @BindView(R.id.tv_restaurant_details_name)
    MaterialTextView restaurantName;
    @BindView(R.id.restaurant_details_rating)
    RatingBar restaurantRating;
    @BindView(R.id.tv_restaurant_details_adress)
    MaterialTextView restaurantAddress;
    @BindView(R.id.fab_choose_restaurant)
    FloatingActionButton fabRestaurantChoosen;

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
            this.viewModel.getSelectedRestaurant(extras.getString(RestaurantFragment.EXTRAS_RESTAURANT_ID));
        }
        this.viewModel.getLiveRestaurant().observe(this, this::updateComponentsValues);
        this.viewModel.getLiveIsSelected().observe(this, this::updateFab);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!this.viewModel.isCurrentUserLOgged()) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }


    private void updateComponentsValues(Restaurant restaurant) {
        if (restaurant != null) {
            this.updateHeaderPhoto(restaurant);
            this.restaurantName.setText(restaurant.getName());
            this.restaurantRating.setRating(this.updateRestaurantRating(restaurant.getRating()));
            this.restaurantAddress.setText(restaurant.getVicinity());
        }
    }

    private void updateFab(boolean isSelected) {
        if (isSelected) {
            this.fabRestaurantChoosen.setImageResource(R.drawable.ic_check_circle_black_24dp);
        } else {
            this.fabRestaurantChoosen.setImageResource(R.drawable.ic_radio_button_unchecked_black_24dp);
        }
    }

    private float updateRestaurantRating(String rating) {
        float restaurantRating = Float.parseFloat(rating);
        return (3 * restaurantRating) / 5;
    }

    private void updateHeaderPhoto(Restaurant restaurant) {
        if (restaurant.getPhotos() != null) {
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


    @OnClick(R.id.fab_choose_restaurant)
    public void chooseRestaurant() {
        this.viewModel.chooseRestaurant();
    }

    @OnClick(R.id.tv_restaurant_details_phone)
    public void callRestaurant() {
        String phoneNumber = this.viewModel.getLiveRestaurant().getValue().getInternationalPhoneNumber();
        if (phoneNumber != null) {
            Intent callIntent = new Intent(Intent.ACTION_DIAL);
            callIntent.setData(Uri.parse(String.format(Locale.getDefault(), "tel:%s", phoneNumber)));
            startActivity(callIntent);
        } else {
            Snackbar.make(this.coordinatorLayout, "No number for this restaurant", Snackbar.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.tv_restaurant_details_like)
    public void like() {
        System.out.println("Like bobby!");
    }

    @OnClick(R.id.tv_restaurant_details_website)
    public void visitWebsite() {
        String website = this.viewModel.getLiveRestaurant().getValue().getWebsite();
        if (website != null) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(website));
            startActivity(intent);
        } else {
            Snackbar.make(this.coordinatorLayout, "No website for this restaurant", Snackbar.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.viewModel.saveLunch();
    }
}
