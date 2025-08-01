package com.example.studypal;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.amplifyframework.auth.AuthCategoryBehavior;
import com.amplifyframework.core.Amplify;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.amplifyframework.auth.AuthProvider;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.hub.HubChannel;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.atomic.AtomicReference;

public class LoginActivity extends AppCompatActivity {

    private static final int RC_GOOGLE_SIGN_IN = 1001;
    private static final String TAG = "LoginActivity";

    private EditText etEmail, etPassword;
    private Button btnLogin, btnGoogleLogin;
    private TextView tvRegister;
    private TextView tvForgotPassword;


    private static final boolean isAmplifyConfigured = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w("FCM", "Fetching FCM registration token failed", task.getException());
                        return;
                    }

                    String fcmToken = task.getResult();
                    Log.d("FCM", "Token: " + fcmToken);
                    sendTokenToServer(fcmToken);
                });

        Amplify.Auth.getCurrentUser(
                authUser -> {
                    // User is signed in
                    runOnUiThread(() -> {
                        startActivity(new Intent(this, MainActivity.class));
                        finish();
                    });
                },
                error -> {
                    // User is not signed in
                }
        );
        Amplify.Auth.fetchAuthSession(
                result -> {
                    if (result.isSignedIn()) {
                        Amplify.DataStore.clear(
                                () -> {
                                    Log.i("DataStore", "Cleared on login");
                                    Amplify.DataStore.start(
                                            () -> Log.i("DataStore", "Sync restarted on login"),
                                            error -> Log.e("DataStore", "Start failed", error)
                                    );
                                },
                                error -> Log.e("DataStore", "Clear failed on login", error)
                        );
                    }
                },
                error -> Log.e("Auth", "Failed to fetch session", error)
        );

        Amplify.Hub.subscribe(HubChannel.DATASTORE, hubEvent -> {
            switch (hubEvent.getName()) {
                case "outboxMutationProcessed":
                    Log.i("Sync", "Synced to cloud: " + hubEvent.getData());
                    break;
                case "networkStatus":
                    Log.i("Network", "Network change: " + hubEvent.getData());
                    break;
                case "ready":
                    Log.i("Sync", "DataStore is ready and synced");
                    break;
                default:
                    Log.d("HubEvent", "Event: " + hubEvent.getName());
            }
        });

        Amplify.DataStore.start(
                () -> Log.i("Amplify", "DataStore started successfully"),
                error -> Log.e("Amplify", "DataStore failed to start", error)
        );

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnGoogleLogin = findViewById(R.id.btnGoogleLogin);
        tvRegister = findViewById(R.id.tvRegister);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        btnLogin.setOnClickListener(v -> loginUser());
        btnGoogleLogin.setOnClickListener(v -> startGoogleSignIn());

        tvRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, register_activity.class);
            startActivity(intent);
            finish();
        });

        tvForgotPassword.setOnClickListener(v -> {
            showForgotPasswordDialog();
            });


        // Google Sign-In setup
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("552553940909-183ih06gqd8ajaktodreulqpc850k4hm.apps.googleusercontent.com")
                .requestEmail()
                .build();
        GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(this, gso);
    }


    private void showForgotPasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Reset Password");

        final EditText input = new EditText(this);
        input.setHint("Enter your registered email");
        input.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        builder.setView(input);

        builder.setPositiveButton("Send Code", (dialog, which) -> {
            String email = input.getText().toString().trim();
            if (!email.isEmpty()) {
                initiateForgotPassword(email);
            } else {
                Toast.makeText(this, "Please enter email", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void initiateForgotPassword(String email) {
        Amplify.Auth.resetPassword(
                email,
                result -> {
                    if (!result.isPasswordReset()) {
                        runOnUiThread(() -> {
                            Toast.makeText(this, "Verification code sent to your email.", Toast.LENGTH_LONG).show();
                            showConfirmResetDialog(email);  // Pass email to next step
                        });
                    }
                },
                error -> runOnUiThread(() ->
                        Toast.makeText(this, "Failed: " + error.getMessage(), Toast.LENGTH_LONG).show()
                )
        );
    }

    private void showConfirmResetDialog(String email) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter OTP and New Password");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText inputCode = new EditText(this);
        inputCode.setHint("Enter OTP");
        inputCode.setInputType(InputType.TYPE_CLASS_NUMBER);
        layout.addView(inputCode);

        final EditText inputPassword = new EditText(this);
        inputPassword.setHint("New Password");
        inputPassword.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        layout.addView(inputPassword);

        builder.setView(layout);

        builder.setPositiveButton("Reset Password", (dialog, which) -> {
            String code = inputCode.getText().toString().trim();
            String newPassword = inputPassword.getText().toString().trim();
            confirmResetPassword(email, newPassword, code);  // Pass email (username)
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void confirmResetPassword(String email, String newPassword, String code) {
        Amplify.Auth.confirmResetPassword(
                email,
                newPassword,
                code,
                () -> runOnUiThread(() -> {
                    Toast.makeText(this, "Password reset successful!", Toast.LENGTH_LONG).show();
                }),
                error -> runOnUiThread(() -> {
                    Toast.makeText(this, "Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                })
        );
    }


    private void loginUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show();
            return;
        }

        Amplify.Auth.signIn(
                email,
                password,
                result -> {
                    if (result.isSignedIn()) {
                        runOnUiThread(() -> {
                            Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(this, MainActivity.class));
                            finish();
                        });
                    } else {
                        runOnUiThread(() -> Toast.makeText(this, "Sign-in not complete", Toast.LENGTH_SHORT).show());
                    }
                },
                error -> runOnUiThread(() ->
                        Toast.makeText(this, "Login failed: " + error.getMessage(), Toast.LENGTH_LONG).show())
        );
    }

    private void sendTokenToServer(String token) {
        String url = "https://your-api-id.execute-api.region.amazonaws.com/prod/register-token";

        AtomicReference<String> userId = new AtomicReference<>("");
        Amplify.Auth.getCurrentUser(
                user-> userId.set(user.getUserId()),
                error -> Log.e("AUTH", "Failed to fetch user attributes", error));
        JSONObject json = new JSONObject();
        try {
            json.put("userId", userId);  // if using Cognito
            json.put("token", token);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, json,
                response -> Log.d("FCM", "Token sent to server successfully"),
                error -> Log.e("FCM", "Failed to send token", error)
        );

        queue.add(request);
    }

    private void startGoogleSignIn() {
        Amplify.Auth.signInWithSocialWebUI(
                AuthProvider.google(),
                this,
                result -> {
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Google sign-in successful!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(this, MainActivity.class));
                        finish();
                    });
                },
                error -> {
                    Log.e(TAG, "Google login failed", error);
                    runOnUiThread(() ->
                            Toast.makeText(this, "Google login failed: " + error.getMessage(), Toast.LENGTH_LONG).show());
                }
        );
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_GOOGLE_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                awsCognitoWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                Log.e(TAG, "Google sign-in failed", e);
                Toast.makeText(this, "Google sign-in failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    private void awsCognitoWithGoogle(String idToken) {
        Amplify.Auth.signInWithWebUI(
                this,
                result -> {
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Google sign-in successful!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(this, MainActivity.class));
                        finish();
                    });
                },
                error -> {
                    Log.e(TAG, "Google login failed", error);
                    runOnUiThread(() -> Toast.makeText(this, "Google login failed: " + error.getMessage(), Toast.LENGTH_LONG).show());
                }
        );
    }
}
