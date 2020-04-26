package com.mickael.go4lunch.ui.map;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
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

    GoogleMap googleMap;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    private boolean locationPermissionGranted;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.map_fragment, container, false);
        ButterKnife.bind(this, view);
        this.mapView.onCreate(savedInstanceState);
        this.mapView.getMapAsync(this);
        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        this.enableMyLocation();
    }
    
    private void enableMyLocation() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            this.locationPermissionGranted = true;
        } else {
            if (this.googleMap != null) {
                this.googleMap.setMyLocationEnabled(true);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        this.locationPermissionGranted = false;
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (PermissionUtils.isPermissionGranted(permissions, grantResults, Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Enable the my location layer if the permission has been granted.
                this.locationPermissionGranted = true;
                enableMyLocation();
            } else {
                // Permission was denied. Display an error message
                // [START_EXCLUDE]
                // Display the missing permission error dialog when the fragments resume.
                Toast.makeText(getContext(), "Missing permission", Toast.LENGTH_SHORT).show();
                // [END_EXCLUDE]
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
}
