package com.mickael.go4lunch.activity.map;

import android.os.Build;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.mickael.go4lunch.R;
import com.mickael.go4lunch.activity.map.dummy.DummyContent;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MapActivity extends AppCompatActivity implements WorkmateFragment.OnListFragmentInteractionListener, RestaurantFragment.OnListFragmentInteractionListener {

    @BindView(R.id.map_toolbar)
    Toolbar toolbar;

    @BindView(R.id.bottom_navigation_bar)
    BottomNavigationView bottomNavigationBar;

    final Fragment mapFragment = MapFragment.newInstance();
    final Fragment restaurantListFragment = RestaurantFragment.newInstance(1); // TODO: Changer nombres de colonnes ?
    final Fragment workmateFragment = WorkmateFragment.newInstance(1); // TODO: Changer nombre de colonnes ?
    final FragmentManager fragmentManager = getSupportFragmentManager();
    Fragment active = mapFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.map_activity);
        ButterKnife.bind(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryVariant));
        }
        setSupportActionBar(this.toolbar);

        this.configureBottomNavigation();

        this.fragmentManager.beginTransaction().add(R.id.map_activity_container, workmateFragment, "3").hide(workmateFragment).commit();
        this.fragmentManager.beginTransaction().add(R.id.map_activity_container, restaurantListFragment, "2").hide(restaurantListFragment).commit();
        this.fragmentManager.beginTransaction().add(R.id.map_activity_container, mapFragment, "1").commit();
    }

    /**
     * Configuration of the {@link com.google.android.material.bottomnavigation.BottomNavigationView}.
     */
    private void configureBottomNavigation() {
        this.bottomNavigationBar.setOnNavigationItemSelectedListener(item -> this.updateFragment(item.getItemId()));
    }

    /**
     * Update {@link androidx.fragment.app.Fragment} to display.
     *
     * @param itemId item id
     * @return true
     */
    private boolean updateFragment(int itemId) {
        switch (itemId) {
            case R.id.map_item:
                this.fragmentManager.beginTransaction().hide(this.active).show(this.mapFragment).commit();
                this.active = this.mapFragment;
                return true;

            case R.id.list_item:
                this.fragmentManager.beginTransaction().hide(this.active).show(this.restaurantListFragment).commit();
                this.active = this.restaurantListFragment;
                return true;
            case R.id.workmates_item:
                this.fragmentManager.beginTransaction().hide(this.active).show(this.workmateFragment).commit();
                this.active = this.workmateFragment;
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onListFragmentInteraction(DummyContent.DummyItem item) {
        Toast.makeText(this, item.content, Toast.LENGTH_SHORT).show();
    }
}
