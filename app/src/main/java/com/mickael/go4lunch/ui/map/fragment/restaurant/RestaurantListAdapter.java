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
import com.mickael.go4lunch.R;
import com.mickael.go4lunch.data.model.Restaurant;
import com.mickael.go4lunch.ui.map.fragment.restaurant.RestaurantFragment.OnItemClickListener;

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

    public RestaurantListAdapter(List<Restaurant> restaurants, OnItemClickListener listener) {
        this.restaurants = restaurants;
        this.clickListener = listener;
    }

    @NonNull
    @Override
    public RestaurantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RestaurantViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_restaurant, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final RestaurantViewHolder holder, int position) {
        Restaurant restaurant = this.restaurants.get(position);

        holder.restaurantName.setText(restaurant.getName());
        holder.distanceRestaurantToDevice.setText(this.getDistance(restaurant));
        holder.restaurantAdress.setText(restaurant.getVicinity());
        holder.restaurantRating.setRating((int) Double.parseDouble(restaurant.getRating()));
        if (restaurant.getPhotos() != null) {
            this.updateItemPhoto(holder, restaurant);
        }
        this.updateItemHours(holder, restaurant);
        holder.itemView.setOnClickListener(view -> clickListener.onClick(restaurant));
    }

    private void updateItemHours(RestaurantViewHolder holder, Restaurant restaurant) { // TODO: remplacer les string en ressources
        if (restaurant.getOpeningHours() != null) {
            if (restaurant.getOpeningHours().isOpenNow()) {
                holder.openingHours.setText("Open");
                holder.openingHours.setTextColor(Color.BLACK);
            } else {
                holder.openingHours.setText("Closed");
                holder.openingHours.setTextColor(Color.RED);
            }
        } else {
            holder.openingHours.setTextColor(Color.BLACK);
            holder.openingHours.setText("Not specified");
        }
    }

    private void updateItemPhoto(RestaurantViewHolder holder, Restaurant restaurant) {
        String url = String.format(Locale.getDefault(), "https://maps.googleapis.com/maps/api/place/photo?" +
                "maxheight=400" +
                "&photoreference=%s" +
                "&key=AIzaSyDuiYTUSAt7OeV6tIoXzTVil6XW5j-NCwc", restaurant.getPhotos()[0].getPhotoReference());
        Glide.with(holder.itemView)
                .load(url)
                .transform(new CenterCrop(), new RoundedCorners(25))
                .into(holder.restaurantImg);
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
        @BindView(R.id.img_workmates_number)
        ImageView workmatesNumber;
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
