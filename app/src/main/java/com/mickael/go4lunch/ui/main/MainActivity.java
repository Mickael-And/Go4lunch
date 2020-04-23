package com.mickael.go4lunch.ui.main;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.material.snackbar.Snackbar;
import com.mickael.go4lunch.R;
import com.mickael.go4lunch.ui.map.MapActivity;

import java.util.Collections;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.main_coordinator_Layout)
    CoordinatorLayout mainCoordinatorLayout;

    /**
     * Identifier for Sign-In Activity.
     */
    private static final int CODE_SIGN_IN_ACTIVITY = 123;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        this.handleResponseAfterSignIn(requestCode, resultCode, data);
    }

    /**
     * Launch Google Sign-In Activity.
     */
    private void startGoogleSignInActivity() {
        startActivityForResult(AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setTheme(R.style.AppTheme)
                .setAvailableProviders(
                        Collections.singletonList(new AuthUI.IdpConfig.GoogleBuilder().build()))
                .setIsSmartLockEnabled(false, true)
                .build(), CODE_SIGN_IN_ACTIVITY);
    }

    /**
     * Launch Facebook Sign-In Activity.
     */
    private void startFacebookSignInActivity() {
        startActivityForResult(AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setTheme(R.style.AppTheme)
                .setAvailableProviders(
                        Collections.singletonList(new AuthUI.IdpConfig.FacebookBuilder().build()))
                .setIsSmartLockEnabled(false, true)
                .build(), CODE_SIGN_IN_ACTIVITY);
    }

    /**
     * Processes the response received from user authentication.
     *
     * @param requestCode The integer request code originally supplied to startActivityForResult(), allowing you to identify who this result came from
     * @param resultCode  The integer result code returned by the child activity through its setResult()
     * @param data        An Intent, which can return result data to the caller (various data can be attached to Intent "extras")
     */
    private void handleResponseAfterSignIn(int requestCode, int resultCode, Intent data) {
        if (requestCode == CODE_SIGN_IN_ACTIVITY) {
            if (resultCode == RESULT_OK) {
                Intent intent = new Intent(this, MapActivity.class);
                startActivity(intent);
                finish();
            } else {
                IdpResponse response = IdpResponse.fromResultIntent(data);
                if (response == null) {
                    this.showSnackBar(this.mainCoordinatorLayout, getString(R.string.error_authentification_canceled));
                } else if (response.getError() != null && response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                    this.showSnackBar(this.mainCoordinatorLayout, getString(R.string.error_no_internet));
                } else if (response.getError() != null && response.getError().getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                    this.showSnackBar(this.mainCoordinatorLayout, getString(R.string.error_unknown_error));
                }
            }
        }
    }

    /**
     * Show a {@link Snackbar}.
     *
     * @param coordinatorLayout Coordinator where the snackBar will be
     * @param message           Message of the SnackBar
     */
    private void showSnackBar(CoordinatorLayout coordinatorLayout, String message) {
        Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_SHORT).show();
    }

    @OnClick(R.id.btn_gle_sign_in)
    public void onClickGoogleLoginButton() {
        this.startGoogleSignInActivity();
    }

    @OnClick(R.id.btn_fb_sign_in)
    public void onClickFacebookLoginButton() {
        this.startFacebookSignInActivity();
    }
}
