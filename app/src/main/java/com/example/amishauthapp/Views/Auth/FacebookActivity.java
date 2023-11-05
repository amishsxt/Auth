package com.example.amishauthapp.Views.Auth;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.amishauthapp.databinding.ActivityFacebookBinding;

public class FacebookActivity extends AppCompatActivity {

    private ActivityFacebookBinding xml;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //root
        xml = ActivityFacebookBinding.inflate(getLayoutInflater());
        setContentView(xml.getRoot());
    }
}