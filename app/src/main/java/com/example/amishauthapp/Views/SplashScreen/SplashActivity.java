package com.example.amishauthapp.Views.SplashScreen;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.amishauthapp.R;
import com.example.amishauthapp.Views.Auth.SignInActivity;
import com.example.amishauthapp.Views.LandingScreen.HomeActivity;
import com.example.amishauthapp.databinding.ActivitySplashBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class SplashActivity extends AppCompatActivity {

    private ActivitySplashBinding xml;
    private static final String POST_NOTIFICATIONS_PERMISSION = "android.permission.POST_NOTIFICATIONS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //root
        xml =ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(xml.getRoot());

        askPermission();

        // Fade-in animation
        Animation fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        fadeInAnimation.setDuration(2000);

        fadeInAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                checkLoginStatus();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        xml.splashText.startAnimation(fadeInAnimation);
    }

    private void checkLoginStatus(){
        // Get the SharedPreferences instance
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

        // Retrieve the login status
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);

        // Check the login status
        if (isLoggedIn) {
            Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else {
            // The user is not logged in, perform appropriate actions
            Intent intent = new Intent(SplashActivity.this, SignInActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }

    }

    public void askPermission(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            if(ContextCompat.checkSelfPermission(SplashActivity.this,
                    Manifest.permission.POST_NOTIFICATIONS) !=
            PackageManager.PERMISSION_GRANTED){

                ActivityCompat.requestPermissions(SplashActivity.this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},101);
            }
        }
    }
}