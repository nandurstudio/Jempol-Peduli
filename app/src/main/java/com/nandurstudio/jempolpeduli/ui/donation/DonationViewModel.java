package com.nandurstudio.jempolpeduli.ui.donation;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class DonationViewModel extends ViewModel {

    private final MutableLiveData<QuerySnapshot> donationsLiveData;
    private final CollectionReference donationRef;

    public DonationViewModel() {
        donationsLiveData = new MutableLiveData<>();
        donationRef = FirebaseFirestore.getInstance().collection("donations");
        loadDonations();
    }

    private void loadDonations() {
        donationRef.whereEqualTo("isActive", true).get()
                .addOnSuccessListener(donationsLiveData::setValue)
                .addOnFailureListener(e -> donationsLiveData.setValue(null));
    }

    public LiveData<QuerySnapshot> getDonations() {
        return donationsLiveData;
    }
}
