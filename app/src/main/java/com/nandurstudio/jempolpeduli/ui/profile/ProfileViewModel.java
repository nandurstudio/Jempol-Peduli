package com.nandurstudio.jempolpeduli.ui.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ProfileViewModel extends ViewModel {

    private final MutableLiveData<String> name = new MutableLiveData<>();
    private final MutableLiveData<String> email = new MutableLiveData<>();
    private final MutableLiveData<String> photoUrl = new MutableLiveData<>();

    public LiveData<String> getName() {
        return name;
    }

    public LiveData<String> getEmail() {
        return email;
    }

    public LiveData<String> getPhotoUrl() {
        return photoUrl;
    }

    public void setUserData(String name, String email, String photoUrl) {
        this.name.setValue(name);
        this.email.setValue(email);
        this.photoUrl.setValue(photoUrl);
    }
}
