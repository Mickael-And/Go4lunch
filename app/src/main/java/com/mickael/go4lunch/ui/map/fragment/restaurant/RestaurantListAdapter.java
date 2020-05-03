package com.mickael.go4lunch.ui.map.fragment.restaurant;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mickael.go4lunch.R;
import com.mickael.go4lunch.data.model.placesapi.Restaurant;
import com.mickael.go4lunch.ui.map.dummy.DummyContent.DummyItem;
import com.mickael.go4lunch.ui.map.fragment.restaurant.RestaurantFragment.OnItemClickListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
 * specified {@link OnItemClickListener}.
 */
public class RestaurantListAdapter extends RecyclerView.Adapter<RestaurantListAdapter.RestaurantViewHolder> {

    private List<Restaurant> restaurants;
    private final OnItemClickListener clickListener;

    public RestaurantListAdapter(List<Restaurant> restaurants, OnItemClickListener listener) {
        this.restaurants = restaurants;
        clickListener = listener;
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
        holder.itemView.setOnClickListener(view -> clickListener.onClick(restaurant));
    }

    @Override
    public int getItemCount() {
        return restaurants.size();
    }

    public void updateList(List<Restaurant> pRestaurants) {
        this.restaurants = pRestaurants;
        notifyDataSetChanged();

    }

    public static class RestaurantViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.content)
        TextView restaurantName;

        public RestaurantViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
