package com.example.studypal;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.amplifyframework.auth.AuthException;

import com.amplifyframework.auth.AuthUserAttribute;
import com.amplifyframework.auth.AuthUserAttributeKey;
import com.amplifyframework.auth.options.AuthSignUpOptions;
import com.amplifyframework.core.Amplify;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class register_activity extends AppCompatActivity {

    private static final int RC_GOOGLE_SIGN_IN = 1000;
    private static final String TAG = "RegisterActivity";

    private EditText etFullName, etEmail, etPassword, etConfirmPassword;
    private Button btnRegister, btnGoogleRegister;
    private TextView tvLogin;

    private GoogleSignInClient googleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);

        btnRegister = findViewById(R.id.btnRegister);
        btnGoogleRegister = findViewById(R.id.btnGoogleRegister);
        tvLogin = findViewById(R.id.tvLogin);

        // Configure Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("552553940909-183ih06gqd8ajaktodreulqpc850k4hm.apps.googleusercontent.com")// from google-services.json
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);

        btnRegister.setOnClickListener(v -> registerUser());
        btnGoogleRegister.setOnClickListener(v -> startGoogleSignIn());
        tvLogin.setOnClickListener(v -> {
            Intent intent = new Intent(register_activity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void registerUser() {
        String fullName = etFullName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString();
        String confirmPassword = etConfirmPassword.getText().toString();

        // Input validation
        if (TextUtils.isEmpty(fullName)) {
            etFullName.setError("Full name required");
            etFullName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Email required");
            etEmail.requestFocus();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Enter a valid email");
            etEmail.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Password required");
            etPassword.requestFocus();
            return;
        }

        if (password.length() < 6) {
            etPassword.setError("Password must be at least 6 characters");
            etPassword.requestFocus();
            return;
        }

        if (!password.equals(confirmPassword)) {
            etConfirmPassword.setError("Passwords do not match");
            etConfirmPassword.requestFocus();
            return;
        }

        // Prepare user attributes
        List<AuthUserAttribute> attributes = new ArrayList<>();
        attributes.add(new AuthUserAttribute(AuthUserAttributeKey.email(), email));
        attributes.add(new AuthUserAttribute(AuthUserAttributeKey.name(), fullName));

        AuthSignUpOptions options = AuthSignUpOptions.builder()
                .userAttributes(attributes)
                .build();

        // Register with Amplify Auth

        Amplify.Auth.signUp(
                email,
                password,
                options,
                result -> {
                    runOnUiThread(() -> showConfirmationDialog(email));
                },
                error -> runOnUiThread(() -> {
                    final String errorMessage = (error != null && error.getMessage() != null) ? error.getMessage() : "";

                    if (errorMessage.contains("UsernameExistsException")) {
                        Toast.makeText(register_activity.this, "User already exists. Please log in instead.", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(register_activity.this, "Signup failed: " + errorMessage, Toast.LENGTH_LONG).show();
                    }
                })

        );
    }
    private void showConfirmationDialog(String email) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Confirmation Code");

        final EditText input = new EditText(this);
        input.setHint("Confirmation Code");
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(input);

        builder.setPositiveButton("Continue", (dialog, which) -> {
            String code = input.getText().toString();
            confirmUser(email, code);
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void startGoogleSignIn() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_GOOGLE_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_GOOGLE_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                // Got Google account, now authenticate with AWS Cognito using token
                awsCognitoWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                Log.e(TAG, "Google sign-in failed", e);
                Toast.makeText(this, "Google sign-in failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }
    private void confirmUser(String email, String confirmationCode) {
        Amplify.Auth.confirmSignUp(
                email,
                confirmationCode,
                result -> {
                    if (result.isSignUpComplete()) {
                        runOnUiThread(() -> {
                            Toast.makeText(this, "Account verified successfully!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        });
                    } else {
                        Log.i("Auth", "Confirmation not complete");
                    }
                },
                error -> {
                    runOnUiThread(() -> Toast.makeText(this, "Failed to confirm: " + error.getMessage(), Toast.LENGTH_LONG).show());
                    Log.e("Auth", "Confirm sign up failed", error);
                }
        );
    }

    private void awsCognitoWithGoogle(String idToken) {
        Amplify.Auth.signInWithWebUI(
                this,
                result -> {
                    runOnUiThread(() -> Toast.makeText(register_activity.this,
                            "Google sign-in successful!",
                            Toast.LENGTH_SHORT).show());
                    // Navigate to app home screen here
                },
                error -> {
                    runOnUiThread(() -> Toast.makeText(register_activity.this,
                            "Google sign-in failed: " + error.getMessage(),
                            Toast.LENGTH_LONG).show());
                    Log.e(TAG, "AWS sign in with Google failed", error);
                }
        );
    }
}
