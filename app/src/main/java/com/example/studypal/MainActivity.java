package com.example.studypal;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.amplifyframework.AmplifyException;
import com.amplifyframework.auth.cognito.result.AWSCognitoAuthSignOutResult;
import com.amplifyframework.auth.options.AuthSignOutOptions;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin;
import com.amplifyframework.api.aws.AWSApiPlugin;
import com.amplifyframework.datastore.AWSDataStorePlugin;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Toolbar toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        toolbar.setOnMenuItemClickListener(item -> {
            if(item.getItemId() == R.id.menu_logout) {
                AuthSignOutOptions options = AuthSignOutOptions.builder()
                        .globalSignOut(true)
                        .build();

                Amplify.Auth.signOut(options, signOutResult -> {
                    runOnUiThread(() -> {
                        if (signOutResult instanceof AWSCognitoAuthSignOutResult.CompleteSignOut) {
                            Toast.makeText(MainActivity.this, "Sign out successful", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(MainActivity.this, LoginActivity.class));
                            finish(); // Close MainActivity
                        } else if (signOutResult instanceof AWSCognitoAuthSignOutResult.PartialSignOut) {
                            Toast.makeText(MainActivity.this, "Partial sign out", Toast.LENGTH_SHORT).show();
                        } else if (signOutResult instanceof AWSCognitoAuthSignOutResult.FailedSignOut) {
                            Toast.makeText(MainActivity.this, "Sign out failed", Toast.LENGTH_SHORT).show();
                        }
                    });
                });

            }
            if(item.getItemId()==R.id.menu_deadlines){
                startActivity(new Intent(MainActivity.this, Deadline.class));
            }
            return true;
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "reminder_channel", "Reminders",
                    NotificationManager.IMPORTANCE_HIGH
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }


        // Load default fragment
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, new home_fragment())
                    .commit();
        }

        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;

                int itemId = item.getItemId();

                if (itemId == R.id.nav_home) {
                    selectedFragment = new home_fragment();
                } else if (itemId == R.id.nav_chat) {
                    selectedFragment = new chat_fragment();
                } else if (itemId == R.id.nav_progress) {
                    selectedFragment = new progess_fragment();
                } else if (itemId == R.id.nav_profile) {
                    selectedFragment = new profile_fragment();
                }

                if (selectedFragment != null) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.container, selectedFragment)
                            .commit();
                    return true;
                }
                return false;
            });
        }
    }
