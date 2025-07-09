package com.example.studypal;

import android.app.Application;
import android.util.Log;

import com.amplifyframework.core.Amplify;
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin;
import com.amplifyframework.api.aws.AWSApiPlugin;
import com.amplifyframework.datastore.AWSDataStorePlugin;
import com.amplifyframework.hub.HubChannel;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.amplifyframework.AmplifyException;
import com.amplifyframework.api.aws.AWSApiPlugin;
import com.amplifyframework.auth.AuthException;
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.AWSDataStorePlugin;
import com.amplifyframework.hub.HubChannel;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
public class MyAmplifyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        try {
            // Add all plugins before configure()
            Amplify.addPlugin(new AWSApiPlugin());               // Enables AppSync (required for sync)
            Amplify.addPlugin(new AWSDataStorePlugin());         // Enables DataStore
            Amplify.addPlugin(new AWSCognitoAuthPlugin());       // Enables Cognito Auth

            Amplify.configure(getApplicationContext());          // Configure once
            Log.i("MyAmplifyApp", "Amplify initialized successfully");

            // Optional: Subscribe to Hub to debug sync events
            Amplify.Hub.subscribe(HubChannel.DATASTORE, hubEvent -> {
                Log.i("AmplifyHub", "Event: " + hubEvent.getName() + " → " + hubEvent.getData());
            });

        } catch (AmplifyException e) {
            Log.e("MyAmplifyApp", "Could not initialize Amplify", e);
        }
    }
}
