package com.mickael.go4lunch.ui.map.activity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textview.MaterialTextView;
import com.mickael.go4lunch.R;
import com.mickael.go4lunch.di.ViewModelFactory;
import com.mickael.go4lunch.notifications.AlarmReceiver;
import com.mickael.go4lunch.ui.SettingsDialogFragment;
import com.mickael.go4lunch.ui.main.MainActivity;
import com.mickael.go4lunch.ui.restaurantdetails.RestaurantDetailsActivity;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.support.DaggerAppCompatActivity;

import static com.mickael.go4lunch.ui.SettingsDialogFragment.NOTIFICATIONS_KEY;
import static com.mickael.go4lunch.ui.SettingsDialogFragment.NOTIFICATIONS_REQUEST_CODE;
import static com.mickael.go4lunch.ui.SettingsDialogFragment.SHARED_PREF_FILE;
import static com.mickael.go4lunch.ui.map.fragment.restaurant.RestaurantFragment.EXTRAS_RESTAURANT_ID;
import static com.mickael.go4lunch.ui.restaurantdetails.RestaurantDetailsViewModel.KEY_MAP_RESTAURANT_ID;

/**
 * Main activity containing the different fragments.
 */
public class MapActivity extends DaggerAppCompatActivity {

    private static final int AUTOCOMPLETE_REQUEST_CODE = 1;

    @BindView(R.id.map_toolbar)
    Toolbar toolbar;

    @BindView(R.id.bottom_navigation_bar)
    BottomNavigationView bottomNavigationBar;

    @BindView(R.id.map_activity_drawer_layout)
    DrawerLayout drawerLayout;

    @BindView(R.id.navigation_view)
    NavigationView navigationView;

    @Inject
    ViewModelFactory viewModelFactory;

    private MapActivityViewModel viewModel;

    /**
     * ViewHolder containing user information in the navigation menu.
     */
    private HeaderNavigationViewHolder headerNavigationViewHolder;

    /**
     * Device coordinates.
     */
    private LatLng locationDevice;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_map);
        ButterKnife.bind(this);
        this.viewModel = new ViewModelProvider(this, viewModelFactory).get(MapActivityViewModel.class);

        this.configureStatusBar();
        this.configureToolbar();
        this.configureDrawerLayout();
        this.configureNavigationView();
        this.configureBottomNavigation();
        this.updateUserInformation();
        this.viewModel.initUsers();
        this.getLocationDevice();
        this.initNotifications();
    }

    /**
     * Creation of the alarm allowing the sending of a notification at 12 noon each day.
     */
    private void initNotifications() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF_FILE, MODE_PRIVATE);

        Intent alarmIntent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, NOTIFICATIONS_REQUEST_CODE, alarmIntent, 0);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (sharedPreferences.getBoolean(NOTIFICATIONS_KEY, true)) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, 12);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
        } else {
            alarmManager.cancel(pendingIntent);
        }
    }

    /**
     * Allows the retrieval of device coordinates.
     */
    private void getLocationDevice() {
        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
        locationResult.addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                if (task.getResult() != null) {
                    this.locationDevice = new LatLng(task.getResult().getLatitude(), task.getResult().getLongitude());
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Return to application launch activity if user is not logged in
        if (!this.viewModel.isCurrentUserLOgged()) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_item, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search_item:
                //Launch of Google search bar
                List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME);
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
                        .setTypeFilter(TypeFilter.ESTABLISHMENT)
                        .setLocationRestriction(RectangularBounds.newInstance(new LatLng(this.locationDevice.latitude - 0.01, this.locationDevice.longitude - 0.01), new LatLng(this.locationDevice.latitude + 0.01, this.locationDevice.longitude + 0.01)))
                        .build(this);
                startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                Intent intent = new Intent(this, RestaurantDetailsActivity.class);
                intent.putExtra(EXTRAS_RESTAURANT_ID, place.getId());
                startActivity(intent);
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.i(this.getClass().getSimpleName(), status.getStatusMessage());
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Updates the information displayed in the navigation menu for the user.
     */
    private void updateUserInformation() {
        if (this.viewModel.isCurrentUserLOgged()) {
            if (this.viewModel.getFirebaseUser().getPhotoUrl() != null) {
                Glide.with(this)
                        .load(this.viewModel.getFirebaseUser().getPhotoUrl())
                        .apply(RequestOptions.circleCropTransform())
                        .into(this.headerNavigationViewHolder.userProfilImage);
            }
            String mail = TextUtils.isEmpty(this.viewModel.getFirebaseUser().getEmail()) ? getString(R.string.no_email_found) : this.viewModel.getFirebaseUser().getEmail();
            String username = TextUtils.isEmpty(this.viewModel.getFirebaseUser().getDisplayName()) ? getString(R.string.no_username_found) : this.viewModel.getFirebaseUser().getDisplayName();
            this.headerNavigationViewHolder.tvUserMail.setText(mail);
            this.headerNavigationViewHolder.tvUserName.setText(username);
        }
    }

    /**
     * Configuration of the statusBar.
     */
    private void configureStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryVariant));
        }
    }

    /**
     * Configuration of {@link Toolbar}.
     */
    private void configureToolbar() {
        this.setSupportActionBar(this.toolbar);
    }

    /**
     * Configuration of {@link DrawerLayout}.
     */
    private void configureDrawerLayout() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, this.drawerLayout, this.toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        this.drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        this.headerNavigationViewHolder = new HeaderNavigationViewHolder(this.navigationView.getHeaderView(0));
    }

    /**
     * Configuration of {@link NavigationView}.
     */
    private void configureNavigationView() {
        this.navigationView.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.navigation_user_lunch_item:
                    if (this.viewModel.getCurrentUser().getLunchRestaurant() != null) {
                        String userLunchPlaceId = this.viewModel.getCurrentUser().getLunchRestaurant().get(KEY_MAP_RESTAURANT_ID);
                        Intent intent = new Intent(this, RestaurantDetailsActivity.class);
                        intent.putExtra(EXTRAS_RESTAURANT_ID, userLunchPlaceId);
                        startActivity(intent);
                    } else {
                        Toast.makeText(this, "You have not yet chosen a restaurant", Toast.LENGTH_SHORT).show();
                    }

                    break;
                case R.id.navigation_user_settings_item:
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    Fragment previous = getSupportFragmentManager().findFragmentByTag("dialog");
                    if (previous != null) {
                        fragmentTransaction.remove(previous);
                    }
                    fragmentTransaction.addToBackStack(null);

                    DialogFragment newFragment = SettingsDialogFragment.newInstance();
                    newFragment.show(fragmentTransaction, "dialog");

                    break;
                case R.id.navigation_user_logout_item:
                    this.signOutUser();
                    break;
                default:
                    break;
            }
            this.drawerLayout.closeDrawer(GravityCompat.START);

            return true;
        });
    }

    /**
     * Disconnects the user from firebase and returns to the launch activity({@link MainActivity}).
     */
    private void signOutUser() {
        AuthUI.getInstance().signOut(this).addOnSuccessListener(this, aVoid -> {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }

    /**
     * Configuration of the {@link BottomNavigationView}.
     */
    private void configureBottomNavigation() {
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(R.id.navigation_map, R.id.navigation_restaurants, R.id.navigation_workmates).build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(this.bottomNavigationBar, navController);
    }

    @Override
    public void onBackPressed() {
        if (this.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            this.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * ViewHolder containing the information of the user connected to Firebase.
     */
    static class HeaderNavigationViewHolder {
        @BindView(R.id.img_user_profil)
        ImageView userProfilImage;

        @BindView(R.id.tv_user_name)
        MaterialTextView tvUserName;

        @BindView(R.id.tv_user_mail)
        MaterialTextView tvUserMail;

        HeaderNavigationViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

}
