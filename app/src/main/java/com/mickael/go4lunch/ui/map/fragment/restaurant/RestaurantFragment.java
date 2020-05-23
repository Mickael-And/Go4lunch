package com.mickael.go4lunch.ui.map.fragment.restaurant;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;
import com.mickael.go4lunch.R;
import com.mickael.go4lunch.data.model.Restaurant;
import com.mickael.go4lunch.di.ViewModelFactory;
import com.mickael.go4lunch.ui.restaurantdetails.RestaurantDetailsActivity;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.support.DaggerFragment;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnItemClickListener}
 * interface.
 */
public class RestaurantFragment extends DaggerFragment {

    public static final String EXTRAS_RESTAURANT_ID = "restaurant.id";

    @BindView(R.id.list_restaurants)
    RecyclerView recyclerView;

    @BindView(R.id.tv_restaurants_error_message)
    MaterialTextView tvErrorMessage;

    @Inject
    ViewModelFactory viewModelFactory;

    private RestaurantListAdapter restaurantsListAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RestaurantFragmentViewModel viewModel = new ViewModelProvider(this, this.viewModelFactory).get(RestaurantFragmentViewModel.class);
        viewModel.getRestaurants().observe(this, this::manageRestaurantList);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_restaurant_list, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.restaurantsListAdapter = new RestaurantListAdapter(new ArrayList<>(), restaurant -> {
            Intent intent = new Intent(getActivity(), RestaurantDetailsActivity.class);
            intent.putExtra(EXTRAS_RESTAURANT_ID, restaurant.getPlaceId());
            startActivity(intent);
        });
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this.recyclerView.getContext(), LinearLayoutManager.VERTICAL);
        this.recyclerView.addItemDecoration(dividerItemDecoration);
        this.recyclerView.setAdapter(this.restaurantsListAdapter);
    }

    private void manageRestaurantList(List<Restaurant> restaurants) {
        if (restaurants != null && !restaurants.isEmpty()) {
            this.recyclerView.setVisibility(View.VISIBLE);
            this.tvErrorMessage.setVisibility(View.INVISIBLE);
            this.restaurantsListAdapter.updateList(restaurants);
        } else {
            this.recyclerView.setVisibility(View.INVISIBLE);
            this.tvErrorMessage.setVisibility(View.VISIBLE);
        }
    }

    public interface OnItemClickListener {
        void onClick(Restaurant restaurant);
    }
}
