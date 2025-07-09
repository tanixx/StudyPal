package com.example.studypal;

import android.util.Log;
import com.amplifyframework.AmplifyException;
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.pushnotifications.pinpoint.AWSPinpointPushNotificationsPlugin;
import android.app.Application;
public class NotificationBackend extends Application {

        @Override
        public void onCreate() {
            super.onCreate();
            try {
                Amplify.addPlugin(new AWSCognitoAuthPlugin());
                Amplify.addPlugin(new AWSPinpointPushNotificationsPlugin());
                Amplify.configure(getApplicationContext());
                Log.i("Amplify", "Initialized Amplify");
            } catch (AmplifyException e) {
                Log.e("Amplify", "Could not initialize Amplify", e);
            }
        }
    }

