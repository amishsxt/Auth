package com.example.amishauthapp.Views.SplashScreen;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.appcompat.app.AppCompatActivity;

import com.example.amishauthapp.R;
import com.example.amishauthapp.Views.Auth.SignInActivity;
import com.example.amishauthapp.Views.LandingScreen.HomeActivity;
import com.example.amishauthapp.databinding.ActivitySplashBinding;

public class SplashActivity extends AppCompatActivity {

    private ActivitySplashBinding xml;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //root
        xml =ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(xml.getRoot());

        // Fade-in animation
        Animation fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        fadeInAnimation.setDuration(800);

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
            // The user is not logged in
            Intent intent = new Intent(SplashActivity.this, SignInActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }

    }
}