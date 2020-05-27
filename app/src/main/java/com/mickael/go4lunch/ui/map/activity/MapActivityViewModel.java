package com.mickael.go4lunch.ui.map.activity;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mickael.go4lunch.data.repository.AppRepository;

import javax.inject.Inject;

public class MapActivityViewModel extends ViewModel {

    private AppRepository appRepository;

    @Inject
    public MapActivityViewModel(AppRepository appRepository) {
        this.appRepository = appRepository;
    }

    public void initUsers() {
        this.appRepository.initUsers();
    }

    @Nullable
    FirebaseUser getCurrentUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    Boolean isCurrentUserLOgged() {
        return (this.getCurrentUser() != null);
    }
}
