package com.example.amishauthapp.Model;

import android.app.Activity;
import android.app.Application;

import androidx.annotation.NonNull;

import com.example.amishauthapp.Model.Callbacks.OnCompleteCallback;
import com.example.amishauthapp.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class AuthRepo {

    private Application application;
    private FirebaseAuth firebaseAuth;

    public AuthRepo(Application application) {
        this.application = application;

        firebaseAuth = FirebaseAuth.getInstance();
    }

    public void handleSignInResult(Task<GoogleSignInAccount> task, OnCompleteCallback listener) {

        try {
            // Google Sign-In was successful, authenticate with Firebase.
            GoogleSignInAccount account = task.getResult(ApiException.class);
            // Log in using Firebase Authentication.
            firebaseAuth.signInWithCredential(GoogleAuthProvider.getCredential(account.getIdToken(), null))
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success
                                FirebaseUser user = firebaseAuth.getCurrentUser();
                                listener.onSuccess();
                            } else {
                                // Sign in failed
                                listener.onFailure(task.getException().getMessage());
                            }
                        }
                    });
        } catch (ApiException e) {
            // Google Sign-In failed
            listener.onFailure(task.getException().getMessage());
        }
    }

    public void signOut(GoogleSignInClient mGoogleSignInClient, OnCompleteCallback listener){

        mGoogleSignInClient.signOut()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // Handle the sign-out result
                        if (task.isSuccessful()) {
                            // The user is successfully signed out.
                            // Sign out the user.
                            firebaseAuth.signOut();
                            listener.onSuccess();
                        } else {
                            // Sign-out failed, handle the error.
                            listener.onFailure(task.getException().getMessage());
                        }
                    }
                });

    }
}
