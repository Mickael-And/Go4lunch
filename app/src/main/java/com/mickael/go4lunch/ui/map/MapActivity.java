package com.mickael.go4lunch.ui.map;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.auth.AuthUI;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.mickael.go4lunch.R;
import com.mickael.go4lunch.di.ViewModelFactory;
import com.mickael.go4lunch.ui.main.MainActivity;
import com.mickael.go4lunch.ui.map.dummy.DummyContent;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.support.DaggerAppCompatActivity;

public class MapActivity extends DaggerAppCompatActivity implements WorkmateFragment.OnListFragmentInteractionListener, RestaurantFragment.OnListFragmentInteractionListener {

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

    private HeaderNavigationViewHolder headerNavigationViewHolder;

    // TODO: Changer de méthode de gestion des fragments dans le navigation bottom car ne supporte pas le landscape
    final Fragment mapFragment = MapFragment.newInstance();
    final Fragment restaurantListFragment = RestaurantFragment.newInstance(1); // TODO: Changer nombre de colonnes ?
    final Fragment workmateFragment = WorkmateFragment.newInstance(1); // TODO: Changer nombre de colonnes ?
    final FragmentManager fragmentManager = getSupportFragmentManager();
    Fragment active = mapFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.map_activity);
        ButterKnife.bind(this);

        this.configureStatusBar();
        this.configureToolbar();
        this.configureDrawerLayout();
        this.configureNavigationView();
        this.configureBottomNavigation();

        this.fragmentManager.beginTransaction().add(R.id.map_activity_container, workmateFragment, "3").hide(workmateFragment).commit();
        this.fragmentManager.beginTransaction().add(R.id.map_activity_container, restaurantListFragment, "2").hide(restaurantListFragment).commit();
        this.fragmentManager.beginTransaction().add(R.id.map_activity_container, mapFragment, "1").commit();

        this.viewModel = new ViewModelProvider(this, viewModelFactory).get(MapActivityViewModel.class);
        this.updateUserInformation();
    }

    private void updateUserInformation() {
        if (this.viewModel.isCurrentUserLOgged()) {
            if (this.viewModel.getCurrentUser().getPhotoUrl() != null) {
                Glide.with(this)
                        .load(this.viewModel.getCurrentUser().getPhotoUrl())
                        .apply(RequestOptions.circleCropTransform())
                        .into(this.headerNavigationViewHolder.userProfilImage);
            }
            String mail = TextUtils.isEmpty(this.viewModel.getCurrentUser().getEmail()) ? getString(R.string.no_email_found) : this.viewModel.getCurrentUser().getEmail();
            String username = TextUtils.isEmpty(this.viewModel.getCurrentUser().getDisplayName()) ? getString(R.string.no_username_found) : this.viewModel.getCurrentUser().getDisplayName();
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
                    break;
                case R.id.navigation_user_settings_item:
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
    public void onBackPressed() {
        if (this.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            this.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onListFragmentInteraction(DummyContent.DummyItem item) {
        Toast.makeText(this, item.content, Toast.LENGTH_SHORT).show();
    }


    static class HeaderNavigationViewHolder {
        @BindView(R.id.img_user_profil)
        ImageView userProfilImage;

        @BindView(R.id.tv_user_name)
        TextView tvUserName;

        @BindView(R.id.tv_user_mail)
        TextView tvUserMail;

        HeaderNavigationViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

}
