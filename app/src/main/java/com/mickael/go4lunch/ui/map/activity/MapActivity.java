package com.mickael.go4lunch.ui.map.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.auth.AuthUI;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textview.MaterialTextView;
import com.mickael.go4lunch.R;
import com.mickael.go4lunch.di.ViewModelFactory;
import com.mickael.go4lunch.ui.main.MainActivity;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.support.DaggerAppCompatActivity;

public class MapActivity extends DaggerAppCompatActivity {

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
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(R.id.navigation_map, R.id.navigation_restaurants, R.id.navigation_workmates).build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController,appBarConfiguration);
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
