package com.example.studypal;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.amplifyframework.auth.AuthUserAttribute;
import com.amplifyframework.core.Amplify;

public class ProfileViewModel extends ViewModel {
    private MutableLiveData<String> name = new MutableLiveData<>();
    private MutableLiveData<String> email = new MutableLiveData<>();
    private boolean fetched = false;

    public LiveData<String> getName() {
        return name;
    }

    public LiveData<String> getEmail() {
        return email;
    }

    public void fetchUserAttributes() {
        if (fetched) return;

        Amplify.Auth.fetchUserAttributes(
                attributes -> {
                    String fetchedName = "";
                    String fetchedEmail = "";

                    for (AuthUserAttribute attribute : attributes) {
                        if ("email".equals(attribute.getKey().getKeyString())) {
                            fetchedEmail = attribute.getValue();
                        } else if ("name".equals(attribute.getKey().getKeyString())) {
                            fetchedName = attribute.getValue();
                        }
                    }

                    name.postValue(fetchedName);
                    email.postValue(fetchedEmail);
                    fetched = true;
                },
                error -> Log.e("Auth", "Failed to fetch user attributes", error)
        );
    }
}
