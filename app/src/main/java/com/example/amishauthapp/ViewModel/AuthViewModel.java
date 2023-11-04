package com.example.amishauthapp.ViewModel;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.amishauthapp.Model.AuthRepo;
import com.example.amishauthapp.Model.Callbacks.OnCompleteCallback;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.Task;

public class AuthViewModel extends AndroidViewModel {

    private AuthRepo authRepo;

    public AuthViewModel(@NonNull Application application) {
        super(application);

        authRepo = new AuthRepo(application);
    }

    public void handleSignInResult(Task<GoogleSignInAccount> task, OnCompleteCallback listener) {
        authRepo.handleSignInResult(task, listener);
    }

    public void signOut(GoogleSignInClient mGoogleSignInClient, OnCompleteCallback listener){
        authRepo.signOut(mGoogleSignInClient, listener);
    }
}
