package com.mickael.go4lunch.ui.map.fragment.restaurant;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.textview.MaterialTextView;
import com.mickael.go4lunch.R;
import com.mickael.go4lunch.data.model.placesapi.Restaurant;
import com.mickael.go4lunch.di.ViewModelFactory;
import com.mickael.go4lunch.utils.PermissionUtils;

import java.util.ArrayList;

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

    @BindView(R.id.list_restaurants)
    RecyclerView recyclerView;

    @BindView(R.id.tv_restaurants_error_message)
    MaterialTextView tvErrorMessage;

    @Inject
    ViewModelFactory viewModelFactory;

    private RestaurantFragmentViewModel viewModel;

    private RestaurantListAdapter restaurantsListAdapter;

    private static final String TAG = RestaurantFragment.class.getSimpleName();

    private FusedLocationProviderClient fusedLocationProviderClient;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    private static final int DEFAULT_RADIUS_FOR_RESTAURANT_REQUEST = 2000;

    private static final String RESTAURANT_TYPE_PLACES = "restaurant";

    private PlacesClient placesClient;

    // TODO: Constructeur vide ?
    public static RestaurantFragment newInstance() {
        return new RestaurantFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.viewModel = new ViewModelProvider(this, viewModelFactory).get(RestaurantFragmentViewModel.class);
        this.fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());
        Places.initialize(getContext(), getString(R.string.google_maps_key));
        this.placesClient = Places.createClient(getContext());
        this.viewModel.getRestaurants().observe(this, restaurants -> {
            if (restaurants != null && !restaurants.isEmpty()) {
                this.recyclerView.setVisibility(View.VISIBLE);
                this.tvErrorMessage.setVisibility(View.INVISIBLE);
                this.restaurantsListAdapter.updateList(restaurants);
            } else {
                this.recyclerView.setVisibility(View.INVISIBLE);
                this.tvErrorMessage.setVisibility(View.VISIBLE);
            }

        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_restaurant_list, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.restaurantsListAdapter = new RestaurantListAdapter(new ArrayList<>(), restaurant -> System.out.println(restaurant.getName()));
        this.recyclerView.setAdapter(this.restaurantsListAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            this.findRestaurants();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (PermissionUtils.isPermissionGranted(permissions, grantResults, Manifest.permission.ACCESS_FINE_LOCATION)) {
                this.findRestaurants();
            } else {
                Toast.makeText(getContext(), "Missing permissions to found restaurants", Toast.LENGTH_SHORT).show();
                this.recyclerView.setVisibility(View.INVISIBLE);
                this.tvErrorMessage.setVisibility(View.VISIBLE);
            }
        }
    }

    private void findRestaurants() {
        Task<Location> locationResult = this.fusedLocationProviderClient.getLastLocation();
        locationResult.addOnCompleteListener(getActivity(), task -> {
            if (task.isSuccessful()) {
                if (task.getResult() != null) {
                    LatLng deviceLocation = new LatLng(task.getResult().getLatitude(), task.getResult().getLongitude());
                    this.viewModel.makeANearbySearchRequest(deviceLocation, String.valueOf(DEFAULT_RADIUS_FOR_RESTAURANT_REQUEST), RESTAURANT_TYPE_PLACES);
                } else {
                    Log.d(TAG, "Current location is null");
                }
            } else {
                Log.d(TAG, "Exception: %s", task.getException());
            }
        });
    }

    public interface OnItemClickListener {
        void onClick(Restaurant restaurant);
    }
}