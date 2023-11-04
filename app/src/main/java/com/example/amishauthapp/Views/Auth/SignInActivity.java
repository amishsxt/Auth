package com.example.amishauthapp.Views.Auth;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.amishauthapp.Model.Callbacks.OnCompleteCallback;
import com.example.amishauthapp.R;
import com.example.amishauthapp.ViewModel.AuthViewModel;
import com.example.amishauthapp.Views.LandingScreen.HomeActivity;
import com.example.amishauthapp.databinding.ActivitySignInBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.Task;

public class SignInActivity extends AppCompatActivity {

    private ActivitySignInBinding xml;
    private ProgressDialog progressDialog;
    private GoogleSignInClient mGoogleSignInClient;
    private int RC_SIGN_IN = 40;

    private AuthViewModel authViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //root
        xml = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(xml.getRoot());

        //init
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        xml.googleSignInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initGoogle();

                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });

        xml.facebookSignInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    private void initGoogle(){
        // Initialize the Google SignIn client.
        mGoogleSignInClient = GoogleSignIn.getClient(this,
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.default_web_client_id))
                        .requestEmail()
                        .build());
    }

    private void loginUser(boolean bool){
        // Get the SharedPreferences instance
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

        // Get the SharedPreferences Editor
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Set the login status
        editor.putBoolean("isLoggedIn", bool);

        // Apply the changes
        editor.apply();

    }

    private void showProgressDialog(){
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Signing...");
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    private void hideProgressDialog(){
        progressDialog.cancel();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {

            showProgressDialog();

            // The Task returned from this call is always completed, no need to attach a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            authViewModel.handleSignInResult(task, new OnCompleteCallback() {
                @Override
                public void onSuccess() {
                    loginUser(true);
                    hideProgressDialog();

                    showNotification();

                    Intent intent = new Intent(SignInActivity.this, HomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }

                @Override
                public void onFailure(String ex) {
                    hideProgressDialog();
                    Toast.makeText(SignInActivity.this, ex, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void showNotification(){
        // Get the user's email address.
        String userEmail = GoogleSignIn.getLastSignedInAccount(this).getEmail();

        //notification
        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        Log.d("off","1");
        synchronized (nm) {
            Notification welcomeNotification = new Notification.Builder(this)
                    .setSmallIcon(R.drawable.google_logo)
                    .setContentText("Welcome " + userEmail + " !")
                    .setSubText("User signed in!")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .build();

            Log.d("off","2");

            nm.notify();

            Log.d("off","3");
        }
    }

}