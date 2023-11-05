package com.example.amishauthapp.Views.Auth;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
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
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONObject;

import java.util.Arrays;

public class SignInActivity extends AppCompatActivity {

    private ActivitySignInBinding xml;
    private ProgressDialog progressDialog;
    private GoogleSignInClient mGoogleSignInClient;
    private static int RC_SIGN_IN = 40;
    private static final String NOTIFICATION_CHANNEL_ID = "channel_id";

    private AuthViewModel authViewModel;
    private CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //root
        xml = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(xml.getRoot());

        //init
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        initGoogle();
        initFacebook();

        xml.facebookSignInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Login with read permissions
                LoginManager.getInstance().logInWithReadPermissions(SignInActivity.this, Arrays.asList("public_profile"));
            }
        });


        xml.googleSignInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
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

    private void initFacebook(){
        // Initialize Facebook SDK with the App ID
        FacebookSdk.setClientToken(getString(R.string.facebook_client_token));

        callbackManager = CallbackManager.Factory.create();

        // Initialize Facebook SDK
        FacebookSdk.sdkInitialize(getApplicationContext());

        // Register the FacebookCallback
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        // Handle the successful login

                        AccessToken accessToken = AccessToken.getCurrentAccessToken();

                        if (accessToken != null) {
                            GraphRequest request = GraphRequest.newMeRequest(
                                    accessToken,
                                    new GraphRequest.GraphJSONObjectCallback() {
                                        @Override
                                        public void onCompleted(JSONObject object, GraphResponse response) {
                                            String name = object.optString("name");

                                            loginUser(true, name);

                                            showLoginSuccessfulNotification("Facebook", name, 2);
                                            navigate();
                                        }
                                    });

                            Bundle parameters = new Bundle();
                            parameters.putString("fields", "id,name,link,picture.type(large)");
                            request.setParameters(parameters);
                            request.executeAsync();
                        }
                        else{
                            Toast.makeText(SignInActivity.this, "Facebook signin falied", Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onCancel() {
                        // Handle the canceled login
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // Handle the login error
                    }
                });
    }

    private void loginUser(boolean bool, String name){
        // Get the SharedPreferences instance
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

        // Get the SharedPreferences Editor
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Set the login status
        editor.putBoolean("isLoggedIn", bool);
        editor.putString("userName", name);

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
                    String email = FirebaseAuth.getInstance().getCurrentUser().getEmail().toString();
                    loginUser(true, email);
                    hideProgressDialog();

                    showLoginSuccessfulNotification("Google", email, 1);
                    navigate();
                }

                @Override
                public void onFailure(String ex) {
                    hideProgressDialog();
                    Toast.makeText(SignInActivity.this, ex, Toast.LENGTH_SHORT).show();
                }
            });
        }

        // Handle the Facebook callback
        callbackManager.onActivityResult(requestCode, resultCode, data) ;
    }

    private void navigate(){
        Intent intent = new Intent(SignInActivity.this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void showLoginSuccessfulNotification(String signinType, String name, int notificationId) {
        // Create a notification channel (for Android 8 and above)
        createNotificationChannel();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.baseline_notifications_24)
                .setContentTitle(signinType +" Login Successful")
                .setContentText(name);

        // Create an Intent for the notification to launch the home activity
        Intent resultIntent = new Intent(this, HomeActivity.class);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Set the content intent for the notification
        builder.setContentIntent(resultPendingIntent);

        // Get an instance of the NotificationManager
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Notify using a unique ID to distinguish different notifications
        notificationManager.notify(notificationId, builder.build());
    }

    private void createNotificationChannel() {
        // Create the notification channel for Android 8 and above
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    NOTIFICATION_CHANNEL_ID,
                    "Login Notifications",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription("Login notifications");
            channel.enableLights(true);
            channel.setLightColor(Color.BLUE);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}