package com.example.studypal;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.amplifyframework.auth.AuthUserAttribute;
import com.amplifyframework.auth.cognito.result.AWSCognitoAuthSignOutResult;
import com.amplifyframework.auth.options.AuthSignOutOptions;
import com.amplifyframework.core.Amplify;

public class profile_fragment extends Fragment {
    private ProfileViewModel viewModel;

    public profile_fragment() {
        // Required empty public constructor
    }

    public static profile_fragment newInstance(String param1, String param2) {
        profile_fragment fragment = new profile_fragment();
        Bundle args = new Bundle();
        args.putString("param1", param1);
        args.putString("param2", param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView nameTextView = view.findViewById(R.id.tvUserName);
        TextView emailTextView = view.findViewById(R.id.tvUserEmail);
        Button editProfileButton = view.findViewById(R.id.btnEditProfile);
        Button logoutButton = view.findViewById(R.id.btnLogout);

        viewModel = new ViewModelProvider(this).get(ProfileViewModel.class);

        viewModel.getName().observe(getViewLifecycleOwner(), nameTextView::setText);
        viewModel.getEmail().observe(getViewLifecycleOwner(), emailTextView::setText);

        viewModel.fetchUserAttributes(); // Only fetches once

        editProfileButton.setOnClickListener(v -> {
            // Handle edit profile button click
        });

        logoutButton.setOnClickListener(v -> {
            AuthSignOutOptions options = AuthSignOutOptions.builder()
                    .globalSignOut(true)
                    .build();

            Amplify.Auth.signOut(options, signOutResult -> {
                requireActivity().runOnUiThread(() -> {
                    if (signOutResult instanceof AWSCognitoAuthSignOutResult.CompleteSignOut) {
                        Toast.makeText(getActivity(), "Sign out successful", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getActivity(), LoginActivity.class));
                        requireActivity().finish();
                    } else if (signOutResult instanceof AWSCognitoAuthSignOutResult.PartialSignOut) {
                        Toast.makeText(getActivity(), "Partial sign out", Toast.LENGTH_SHORT).show();
                    } else if (signOutResult instanceof AWSCognitoAuthSignOutResult.FailedSignOut) {
                        Toast.makeText(getActivity(), "Sign out failed", Toast.LENGTH_SHORT).show();
                    }
                });
            });
        });
    }
}
