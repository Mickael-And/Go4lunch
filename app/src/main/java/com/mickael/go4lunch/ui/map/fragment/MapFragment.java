package com.mickael.go4lunch.ui.map.fragment;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
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
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.mickael.go4lunch.R;
import com.mickael.go4lunch.data.model.placesapi.Results;
import com.mickael.go4lunch.di.ViewModelFactory;
import com.mickael.go4lunch.utils.PermissionUtils;

import java.util.Locale;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.support.DaggerFragment;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.HttpException;

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

    private boolean locationPermissionGranted;

    private Location lastKnownLocation;

    private static final String KEY_CAMERA_POSITION = "camera_position";

    private static final String KEY_LOCATION = "location";

    private PlacesClient placesClient;

    Disposable disposable;

    public static MapFragment newInstance() {
        return new MapFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.map_fragment, container, false);
        ButterKnife.bind(this, view);
        this.viewModel = new ViewModelProvider(this, viewModelFactory).get(MapFragmentViewModel.class);

        if (savedInstanceState != null) {
            this.lastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
        }
        this.fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());
        this.mapView.onCreate(savedInstanceState);
        this.mapView.getMapAsync(this);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Places.initialize(getContext(), getString(R.string.google_maps_key));
        this.placesClient = Places.createClient(getContext());
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
        if (this.disposable != null) {
            this.disposable.dispose();
        }
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


    private void displayRestaurants() {
        // TODO: Change magic number
        this.disposable = this.viewModel.makeANearbySearchRequest(String.valueOf(this.lastKnownLocation.getLatitude()), String.valueOf(this.lastKnownLocation.getLongitude()), String.valueOf(3000), "restaurant")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(nearbySearchPlacesApiResponse -> {
                    for (Results result : nearbySearchPlacesApiResponse.getResults()) {
                        this.googleMap.addMarker(new MarkerOptions()
                                .position(new LatLng(result.getGeometry().getLocation().getLat(), result.getGeometry().getLocation().getLng()))
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
//                                .icon(this.bitmapDescriptorFromVector(getContext(), R.drawable.ic_restaurant_black_24dp)) // TODO: Essayer de récupérer l'icon de la requete rest
                                .title(result.getName()));
                    }
                }, throwable -> {
                    // cast to retrofit.HttpException to get the response code
                    if (throwable instanceof HttpException) {
                        HttpException response = (HttpException) throwable;
                        int code = response.code();
                        Log.i(MapFragmentViewModel.class.getSimpleName(), String.format(Locale.getDefault(), "fetchRestaurants: Api call didn't work %d", code));
                    } else {
                        Log.i(MapFragmentViewModel.class.getSimpleName(), "fetchRestaurants: Api call didn't work ");
                    }
                });
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
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
                        this.displayRestaurants();
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
