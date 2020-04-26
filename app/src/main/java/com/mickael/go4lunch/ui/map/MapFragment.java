package com.mickael.go4lunch.ui.map;

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
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Task;
import com.mickael.go4lunch.R;
import com.mickael.go4lunch.utils.PermissionUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    public static MapFragment newInstance() {
        return new MapFragment();
    }

    @BindView(R.id.map)
    MapView mapView;

    private static final String TAG = MapFragment.class.getSimpleName();

    private GoogleMap googleMap;

    private FusedLocationProviderClient fusedLocationProviderClient;

    private final LatLng defaultLocation = new LatLng(43.890087, -0.503689);

    private static final int DEFAULT_ZOOM = 15;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    private boolean locationPermissionGranted;

    private Location lastKnownLocation;

    private static final String KEY_CAMERA_POSITION = "camera_position";

    private static final String KEY_LOCATION = "location";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.map_fragment, container, false);
        ButterKnife.bind(this, view);
        if (savedInstanceState != null) {
            this.lastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
        }
        this.fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());
        this.mapView.onCreate(savedInstanceState);
        this.mapView.getMapAsync(this);

        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        this.enableMyLocation();
        this.getLocationDevice();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        this.locationPermissionGranted = false;
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (PermissionUtils.isPermissionGranted(permissions, grantResults, Manifest.permission.ACCESS_FINE_LOCATION)) {
                this.locationPermissionGranted = true;
                this.enableMyLocation();
                this.getLocationDevice();
            } else {
                Toast.makeText(getContext(), "Missing permission", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onResume() {
        this.mapView.onResume();
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        this.mapView.onLowMemory();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        if (this.googleMap != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, this.googleMap.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, this.lastKnownLocation);
            super.onSaveInstanceState(outState);
        }
    }

    private void enableMyLocation() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            this.locationPermissionGranted = true;
            if (this.googleMap != null) {
                this.googleMap.setMyLocationEnabled(true);
            }
        }
    }

    private void getLocationDevice() {
        if (this.locationPermissionGranted) {
            Task<Location> locationResult = this.fusedLocationProviderClient.getLastLocation();
            locationResult.addOnCompleteListener(getActivity(), task -> {
                if (task.isSuccessful()) {
                    this.lastKnownLocation = task.getResult();
                    if (this.lastKnownLocation != null) {
                        this.googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(this.lastKnownLocation.getLatitude()
                                , this.lastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                    }
                } else {
                    Log.d(TAG, "Current location is null. Using defaults.");
                    Log.d(TAG, "Exception: %s", task.getException());
                    this.googleMap.moveCamera(CameraUpdateFactory
                            .newLatLngZoom(defaultLocation, DEFAULT_ZOOM));
                }
            });
        } else {
            this.googleMap.moveCamera(CameraUpdateFactory
                    .newLatLngZoom(defaultLocation, DEFAULT_ZOOM));
        }
    }
}
