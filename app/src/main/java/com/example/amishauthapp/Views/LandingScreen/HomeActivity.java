package com.example.amishauthapp.Views.LandingScreen;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.amishauthapp.Model.Callbacks.OnCompleteCallback;
import com.example.amishauthapp.ViewModel.AuthViewModel;
import com.example.amishauthapp.Views.Auth.SignInActivity;
import com.example.amishauthapp.databinding.ActivityHomeBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

public class HomeActivity extends AppCompatActivity {

    private ActivityHomeBinding xml;
    private AlertDialog.Builder builder;

    private AuthViewModel authViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //root
        xml = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(xml.getRoot());

        //init
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        builder = new AlertDialog.Builder(this);
        checkUserName();

        xml.logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logoutDialogBox();
            }
        });
    }

    private void logoutUser(boolean bool){
        // Get the SharedPreferences instance
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

        // Get the SharedPreferences Editor
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Set the login status
        editor.putBoolean("isLoggedIn", bool);
        editor.putString("userName", "No UserName");

        // Apply the changes
        editor.apply();
    }

    private void checkUserName(){
        // Get the SharedPreferences instance
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

        // Retrieve the User Name
        String name = sharedPreferences.getString("userName", "No UserName");
        xml.userNameText.setText(name);
    }

    private void logoutDialogBox(){
        //logOut logic
        builder.setTitle("Log Out")
                .setMessage("Do you want to log out?")
                .setCancelable(true)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        // Initialize the Google SignIn client.
                        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(HomeActivity.this,
                                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                        .requestEmail()
                                        .build());

                        authViewModel.signOut(mGoogleSignInClient, new OnCompleteCallback() {
                            @Override
                            public void onSuccess() {
                                logoutUser(false);

                                Intent intent = new Intent(HomeActivity.this, SignInActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }

                            @Override
                            public void onFailure(String ex) {
                                Toast.makeText(HomeActivity.this, ex.toString(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                })
                .show();
    }
}