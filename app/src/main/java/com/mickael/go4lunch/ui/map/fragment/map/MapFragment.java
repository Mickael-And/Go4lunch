package com.mickael.go4lunch.ui.map.fragment.map;

import android.Manifest;
import android.content.Intent;
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

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.mickael.go4lunch.BuildConfig;
import com.mickael.go4lunch.R;
import com.mickael.go4lunch.data.model.Restaurant;
import com.mickael.go4lunch.di.ViewModelFactory;
import com.mickael.go4lunch.ui.restaurantdetails.RestaurantDetailsActivity;
import com.mickael.go4lunch.utils.PermissionUtils;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.support.DaggerFragment;

import static com.mickael.go4lunch.ui.map.fragment.restaurant.RestaurantFragment.EXTRAS_RESTAURANT_ID;

public class MapFragment extends DaggerFragment implements OnMapReadyCallback {
    @BindView(R.id.map)
    MapView mapView;

    @Inject
    ViewModelFactory viewModelFactory;

    private MapFragmentViewModel viewModel;

    private static final String TAG = MapFragment.class.getSimpleName();

    private GoogleMap googleMap;

    private FusedLocationProviderClient fusedLocationProviderClient;

    private final LatLng defaultLocation = new LatLng(43.890087, -0.503689);

    private static final int DEFAULT_ZOOM = 15;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    private static final int DEFAULT_RADIUS_FOR_RESTAURANT_REQUEST = 2000;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.viewModel = new ViewModelProvider(this, viewModelFactory).get(MapFragmentViewModel.class);
        this.fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());
        Places.initialize(getContext(), BuildConfig.GOOGLE_MAPS_API_KEY);
        Places.createClient(getContext());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        ButterKnife.bind(this, view);
        this.mapView.onCreate(savedInstanceState); // Null if in onCreate method because ButterKnife isn't initialized
        return view;
    }

    @Override
    public void onStop() {
        super.onStop();
        this.mapView.onStop();
    }

    @Override
    public void onStart() {
        super.onStart();
        this.mapView.onStart();
        this.mapView.getMapAsync(this);
    }


    @Override
    public void onResume() {
        super.onResume();
        this.mapView.onResume();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        this.mapView.onDestroy();
    }


    @Override
    public void onLowMemory() {
        super.onLowMemory();
        this.mapView.onLowMemory();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        this.viewModel.getRestaurants().observe(this, this::displayRestaurants);
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            this.enableMyLocation();
            this.updateMapOnLocationDevice();
        }

        this.googleMap.setOnMarkerClickListener(marker -> {
            Intent intent = new Intent(getActivity(), RestaurantDetailsActivity.class);
            intent.putExtra(EXTRAS_RESTAURANT_ID, marker.getTitle());
            startActivity(intent);
            return true;
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (PermissionUtils.isPermissionGranted(permissions, grantResults, Manifest.permission.ACCESS_FINE_LOCATION)) {
                this.enableMyLocation();
                this.updateMapOnLocationDevice();
            } else {
                Toast.makeText(getContext(), "Missing permissions to use map", Toast.LENGTH_SHORT).show();
                this.moveMap(this.defaultLocation, DEFAULT_ZOOM);
            }
        }
    }

    private void enableMyLocation() {
        if (this.googleMap != null) {
            this.googleMap.setMyLocationEnabled(true);
        }
    }

    private void updateMapOnLocationDevice() {
        Task<Location> locationResult = this.fusedLocationProviderClient.getLastLocation();
        locationResult.addOnCompleteListener(getActivity(), task -> {
            if (task.isSuccessful()) {
                if (task.getResult() != null) {
                    LatLng deviceLocation = new LatLng(task.getResult().getLatitude(), task.getResult().getLongitude());
                    this.moveMap(deviceLocation, DEFAULT_ZOOM);
                    this.viewModel.initRestaurantPlaces(deviceLocation, String.valueOf(DEFAULT_RADIUS_FOR_RESTAURANT_REQUEST));
                } else {
                    Log.d(TAG, "Current location is null. Using defaults.");
                    this.moveMap(this.defaultLocation, DEFAULT_ZOOM);
                }
            } else {
                Log.d(TAG, "Exception: %s", task.getException());
                this.moveMap(this.defaultLocation, DEFAULT_ZOOM);
            }
        });
    }

    private void displayRestaurants(List<Restaurant> restaurants) {
        this.googleMap.clear();

        for (Restaurant restaurant : restaurants) {
            if (restaurant.getAttendance() > 0) {
                this.googleMap.addMarker(new MarkerOptions()
                        .position(new LatLng(restaurant.getGeometry().getLocation().getLat(), restaurant.getGeometry().getLocation().getLng()))
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN))
                        .title(restaurant.getPlaceId()));
            } else {
                this.googleMap.addMarker(new MarkerOptions()
                        .position(new LatLng(restaurant.getGeometry().getLocation().getLat(), restaurant.getGeometry().getLocation().getLng()))
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
                        .title(restaurant.getPlaceId()));
            }
        }
    }

    private void moveMap(LatLng location, float zoomValue) {
        this.googleMap.moveCamera(CameraUpdateFactory
                .newLatLngZoom(location, zoomValue));
    }
}
