package com.mickael.go4lunch.ui.map.activity;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import javax.inject.Inject;

public class MapActivityViewModel extends ViewModel {

    @Inject
    public MapActivityViewModel() {
    }

    @Nullable
    FirebaseUser getCurrentUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    Boolean isCurrentUserLOgged() {
        return (this.getCurrentUser() != null);
    }
}
