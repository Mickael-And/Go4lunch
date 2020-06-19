package com.mickael.go4lunch.ui.restaurantdetails;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RatingBar;

import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textview.MaterialTextView;
import com.mickael.go4lunch.BuildConfig;
import com.mickael.go4lunch.R;
import com.mickael.go4lunch.data.model.Restaurant;
import com.mickael.go4lunch.data.model.User;
import com.mickael.go4lunch.di.ViewModelFactory;
import com.mickael.go4lunch.ui.main.MainActivity;
import com.mickael.go4lunch.ui.map.fragment.restaurant.RestaurantFragment;

import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dagger.android.support.DaggerAppCompatActivity;

import static com.mickael.go4lunch.ui.restaurantdetails.RestaurantDetailsViewModel.KEY_MAP_RESTAURANT_ID;

/**
 * Activity displaying details of a restaurant.
 */
public class RestaurantDetailsActivity extends DaggerAppCompatActivity {
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
    @BindView(R.id.list_workmates_by_restaurant)
    RecyclerView workmatesList;

    @BindView(R.id.tv_restaurant_details_like)
    MaterialTextView tvLike;

    @Inject
    ViewModelFactory viewModelFactory;

    private RestaurantDetailsViewModel viewModel;

    private WorkmatesDetailsAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_restaurant_details);
        ButterKnife.bind(this);
        this.viewModel = new ViewModelProvider(this, viewModelFactory).get(RestaurantDetailsViewModel.class);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            this.viewModel.initView(extras.getString(RestaurantFragment.EXTRAS_RESTAURANT_ID));
        }
        this.viewModel.getSelectedRestaurant().observe(this, this::updateRestaurantComponents);
        this.viewModel.getCurrentUser().observe(this, this::updateUserComponents);
        this.initWorkmatesList();
        this.viewModel.getUsers().observe(this, this::updateUserList);
    }

    /**
     * Initializes the list of colleagues.
     */
    private void initWorkmatesList() {
        this.adapter = new WorkmatesDetailsAdapter();
        this.workmatesList.setAdapter(this.adapter);
    }

    /**
     * Updates the list of colleagues to display.
     *
     * @param users users to display
     */
    private void updateUserList(List<User> users) {
        this.adapter.updateList(users);
    }

    /**
     * Updates the components of the view linked to the user.
     *
     * @param user user
     */
    private void updateUserComponents(User user) {
        if (user.getLunchRestaurant() != null && user.getLunchRestaurant().get(KEY_MAP_RESTAURANT_ID).equals(this.viewModel.getSelectedRestaurant().getValue().getPlaceId())) {
            this.fabRestaurantChoosen.setImageResource(R.drawable.ic_check_circle_black_24dp);
        } else {
            this.fabRestaurantChoosen.setImageResource(R.drawable.ic_radio_button_unchecked_black_24dp);
        }

        if (user.getLikes().size() > 0) {
            if (user.getLikes().contains(this.viewModel.getSelectedRestaurant().getValue().getPlaceId())) {
                this.tvLike.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_star_orange_24dp, 0, 0);
            } else {
                this.tvLike.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_star_border_orange_24dp, 0, 0);
            }
        } else {
            this.tvLike.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_star_border_orange_24dp, 0, 0);
        }
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

    /**
     * Updates components related to the restaurant.
     *
     * @param restaurant restaurant
     */
    private void updateRestaurantComponents(Restaurant restaurant) {
        if (restaurant != null) {
            this.updateHeaderPhoto(restaurant);
            this.restaurantName.setText(restaurant.getName());
            this.restaurantRating.setRating(this.updateRestaurantRating(restaurant.getRating()));
            this.restaurantAddress.setText(restaurant.getVicinity());
        }
    }

    /**
     * Transforms the restaurant ratio to 3 stars.
     *
     * @param rating the ratio in 5 stars
     * @return the ratio in 3 stars
     */
    private float updateRestaurantRating(String rating) {
        float restaurantRating = Float.parseFloat(rating);
        return (3 * restaurantRating) / 5;
    }

    /**
     * Updates the photo of the restaurant.
     *
     * @param restaurant restaurant
     */
    private void updateHeaderPhoto(Restaurant restaurant) {
        if (restaurant.getPhotos() != null) {
            String url = String.format(Locale.getDefault(), "https://maps.googleapis.com/maps/api/place/photo?" +
                    "maxheight=1600" +
                    "&photoreference=%s" +
                    "&key=%s", restaurant.getPhotos().get(0).getPhotoReference(), BuildConfig.GOOGLE_WEB_API_KEY);
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
        String phoneNumber = this.viewModel.getSelectedRestaurant().getValue().getInternationalPhoneNumber();
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
        try {
            if (this.viewModel.likeRestaurant()) {
                Snackbar.make(this.coordinatorLayout, "You like this restaurant", Snackbar.LENGTH_SHORT).show();
            } else {
                Snackbar.make(this.coordinatorLayout, "You don't like this restaurant anymore", Snackbar.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Snackbar.make(this.coordinatorLayout, "Error to update", Snackbar.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.tv_restaurant_details_website)
    public void visitWebsite() {
        String website = this.viewModel.getSelectedRestaurant().getValue().getWebsite();
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
        this.viewModel.saveUserModification();
    }
}
