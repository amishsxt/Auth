package com.example.amishauthapp.Model.Callbacks;

import com.google.firebase.auth.FirebaseUser;

public interface OnCompleteCallback {

    public void onSuccess();
    public void onFailure(String ex);
}
