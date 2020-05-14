package com.mickael.go4lunch.ui.map.fragment.restaurant;

import android.graphics.Color;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.mickael.go4lunch.R;
import com.mickael.go4lunch.data.model.Restaurant;
import com.mickael.go4lunch.data.model.User;
import com.mickael.go4lunch.ui.map.fragment.restaurant.RestaurantFragment.OnItemClickListener;

import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Restaurant} and makes a call to the
 * specified {@link OnItemClickListener}.
 */
public class RestaurantListAdapter extends RecyclerView.Adapter<RestaurantListAdapter.RestaurantViewHolder> {

    private List<Restaurant> restaurants;
    private final OnItemClickListener clickListener;
    private LatLng deviceLocation;
    private RestaurantFragmentViewModel viewModel;

    public RestaurantListAdapter(RestaurantFragmentViewModel viewModel, List<Restaurant> restaurants, OnItemClickListener listener) {
        this.viewModel = viewModel;
        this.restaurants = restaurants;
        this.clickListener = listener;
    }

    @NonNull
    @Override
    public RestaurantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RestaurantViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_restaurant, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final RestaurantViewHolder holder, int position) {
        Restaurant restaurant = this.restaurants.get(position);

        holder.restaurantName.setText(restaurant.getName());
        holder.distanceRestaurantToDevice.setText(this.getDistance(restaurant));
        holder.restaurantAdress.setText(restaurant.getVicinity());
        this.updateNumberOfWorkmates(holder, restaurant);
        holder.restaurantRating.setRating(this.updateRestaurantRating(restaurant.getRating()));
        if (restaurant.getPhotos() != null) {
            this.updateItemPhoto(holder, restaurant);
        }
        this.updateItemHours(holder, restaurant);
        holder.itemView.setOnClickListener(view -> clickListener.onClick(restaurant));
    }

    private void updateNumberOfWorkmates(RestaurantViewHolder holder, Restaurant restaurant) {
        this.viewModel.getNumberOfWorkmatesByPlaceAtNoon(restaurant.getPlaceId()).addOnCompleteListener(task -> {
            int cptWorkmatesAtNoon = 0;
            Date dateReference = this.viewModel.getReferenceDate();
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    User user = document.toObject(User.class);
                    if (user.getLunchDate().after(dateReference)) {
                        cptWorkmatesAtNoon++;
                    }
                }
                holder.workmatesNumber.setText(String.format(Locale.getDefault(), "(%d)", cptWorkmatesAtNoon));
            }
        });
    }

    private void updateItemHours(RestaurantViewHolder holder, Restaurant restaurant) {
        if (restaurant.getOpeningHours() != null) {
            if (restaurant.getOpeningHours().isOpenNow()) {
                holder.openingHours.setText(holder.itemView.getContext().getString(R.string.restaurant_open));
                holder.openingHours.setTextColor(Color.BLACK);
            } else {
                holder.openingHours.setText(holder.itemView.getContext().getString(R.string.restaurant_closed));
                holder.openingHours.setTextColor(Color.RED);
            }
        } else {
            holder.openingHours.setTextColor(Color.BLACK);
            holder.openingHours.setText(holder.itemView.getContext().getString(R.string.unknown_opening_hours));
        }
    }

    private void updateItemPhoto(RestaurantViewHolder holder, Restaurant restaurant) {
        String url = String.format(Locale.getDefault(), "https://maps.googleapis.com/maps/api/place/photo?" +
                "maxheight=1600" +
                "&photoreference=%s" +
                "&key=AIzaSyDuiYTUSAt7OeV6tIoXzTVil6XW5j-NCwc", restaurant.getPhotos().get(0).getPhotoReference());
        Glide.with(holder.itemView)
                .load(url)
                .transform(new CenterCrop(), new RoundedCorners(25))
                .into(holder.restaurantImg);
    }

    private float updateRestaurantRating(String rating) {
        float restaurantRating = Float.parseFloat(rating);
        return (3 * restaurantRating) / 5;
    }

    private String getDistance(Restaurant restaurant) {
        Location restaurantLocation = new Location("Restaurant location");
        restaurantLocation.setLatitude(restaurant.getGeometry().getLocation().getLat());
        restaurantLocation.setLongitude(restaurant.getGeometry().getLocation().getLng());

        Location device = new Location("Device location");
        device.setLatitude(this.deviceLocation.latitude);
        device.setLongitude(this.deviceLocation.longitude);

        float distance = device.distanceTo(restaurantLocation);

        return String.format(Locale.getDefault(), "%dm", Math.round(distance));
    }

    @Override
    public int getItemCount() {
        return restaurants.size();
    }

    public void updateList(List<Restaurant> pRestaurants, LatLng deviceLocation) {
        this.deviceLocation = deviceLocation;
        this.restaurants = pRestaurants;
        notifyDataSetChanged();
    }

    public static class RestaurantViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_restaurant_name)
        MaterialTextView restaurantName;
        @BindView(R.id.tv_distance_to_device)
        MaterialTextView distanceRestaurantToDevice;
        @BindView(R.id.tv_restaurant_adress)
        MaterialTextView restaurantAdress;
        @BindView(R.id.tv_workmates_number)
        MaterialTextView workmatesNumber;
        @BindView(R.id.tv_opening_hours)
        MaterialTextView openingHours;
        @BindView(R.id.restaurant_rating)
        RatingBar restaurantRating;
        @BindView(R.id.img_restaurant)
        ImageView restaurantImg;

        public RestaurantViewHolder(@NonNull View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
