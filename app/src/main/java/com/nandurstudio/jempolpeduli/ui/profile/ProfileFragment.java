package com.nandurstudio.jempolpeduli.ui.profile;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.SetOptions;
import com.nandurstudio.jempolpeduli.R;
import com.nandurstudio.jempolpeduli.data.model.LoggedInUser;
import com.nandurstudio.jempolpeduli.databinding.FragmentProfileBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentReference;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";
    private FragmentProfileBinding binding;
    private ProfileViewModel profileViewModel;

    // Firebase Firestore instance
    private FirebaseFirestore db;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        profileViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        db = FirebaseFirestore.getInstance();

        // Load user data
        loadUserData();

        // Get Firebase user data
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            String userId = firebaseUser.getUid();
            String name = firebaseUser.getDisplayName();
            String email = firebaseUser.getEmail();
            String photoUrl = firebaseUser.getPhotoUrl() != null ? firebaseUser.getPhotoUrl().toString().replace("s96-c", "s492-c") : null;

            // Populate the LoggedInUser model
            LoggedInUser user = new LoggedInUser(userId, name);
            Map<String, Object> userData = new HashMap<>();
            userData.put("userId", user.getUserId());
            userData.put("displayName", user.getDisplayName());
            userData.put("email", email);
            userData.put("photoUrl", photoUrl);

            // Save to Firestore
            saveUserData(userId, userData);
        } else {
            Toast.makeText(getContext(), "No user logged in", Toast.LENGTH_SHORT).show();
        }

        if (firebaseUser != null) {
            Log.d("FirebaseUser", "UID: " + firebaseUser.getUid());
            Log.d("FirebaseUser", "DisplayName: " + firebaseUser.getDisplayName());
            Log.d("FirebaseUser", "Email: " + firebaseUser.getEmail());
            Log.d("FirebaseUser", "PhoneNumber: " + firebaseUser.getPhoneNumber());
            Log.d("FirebaseUser", "PhotoUrl: " + (firebaseUser.getPhotoUrl() != null ? firebaseUser.getPhotoUrl().toString() : "null"));
            Log.d("FirebaseUser", "ProviderId: " + firebaseUser.getProviderId());
            Log.d("FirebaseUser", "IsEmailVerified: " + firebaseUser.isEmailVerified());
        } else {
            Log.d("FirebaseUser", "No user is logged in.");
        }

        // Bind data to UI
        final ImageView profileImageView = binding.profilePicture;
        profileViewModel.getPhotoUrl().observe(getViewLifecycleOwner(), photoUrl -> {
            if (photoUrl != null) {
                Glide.with(this)
                        .load(photoUrl)
                        .circleCrop()
                        .into(profileImageView);
            } else {
                profileImageView.setImageResource(R.drawable.ic_launcher_foreground);
            }
        });

        // Set click listener for enlarging image
        binding.profilePicture.setOnClickListener(v -> {
            String photoUrl = profileViewModel.getPhotoUrl().getValue();
            if (photoUrl != null) {
                FullscreenImageDialogFragment.newInstance(photoUrl)
                        .show(getParentFragmentManager(), "fullscreen_image");
            }
        });

        // Bind name and email to UI
        profileViewModel.getName().observe(getViewLifecycleOwner(), name -> binding.profileName.setText(name));

        profileViewModel.getEmail().observe(getViewLifecycleOwner(), email -> {

            Log.d(TAG, "onCreateView: "+email);
            binding.profileEmail.setText(email);
        });

        // Save Button to update profile data
        binding.saveButton.setOnClickListener(v -> saveProfileDataToFirestore(firebaseUser));

        // Bind user input fields (address, phone, nickname) for editing
        EditText profileAddress = binding.profileAddress;
        EditText profilePhone = binding.profilePhone;
        EditText profileNickname = binding.profileNickname;

        // Update data in Firestore when editing fields
        binding.saveButton.setOnClickListener(v -> {
            String address = profileAddress.getText().toString();
            String phone = profilePhone.getText().toString();
            String nickname = profileNickname.getText().toString();

            assert firebaseUser != null;
            updateUserProfile(firebaseUser.getUid(), address, phone, nickname);
        });

        profileViewModel.getName().observe(getViewLifecycleOwner(), name -> binding.profileName.setText(name));
        profileViewModel.getEmail().observe(getViewLifecycleOwner(), email -> binding.profileEmail.setText(email));

        return root;
    }

    private void loadUserData() {
        String userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        db.collection("users").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String name = documentSnapshot.getString("displayName");
                        String email = documentSnapshot.getString("email");
                        String photoUrl = documentSnapshot.getString("photoUrl");

                        // Update UI
                        profileViewModel.setUserData(name, email, photoUrl);

                        // Load donation count
                        loadDonationCount(userId); // Panggil fungsi untuk mendapatkan jumlah donasi
                    } else {
                        Log.d(TAG, "No such document");
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "Failed to load user data", e));
    }

    private void saveUserData(String userId, Map<String, Object> userData) {
        db.collection("users")
                .document(userId)
                .set(userData, SetOptions.merge()) // Create or update document
                .addOnSuccessListener(aVoid -> Log.d(TAG, "User profile created/updated successfully"))
                .addOnFailureListener(e -> Log.w(TAG, "Error creating/updating user profile", e));
    }

    private void saveProfileDataToFirestore(FirebaseUser user) {
        if (user != null) {
            String userId = user.getUid();
            String name = profileViewModel.getName().getValue();
            String email = profileViewModel.getEmail().getValue();
            String photoUrl = user.getPhotoUrl() != null ? user.getPhotoUrl().toString() : null;

            // Prepare data to be saved in Firestore
            Map<String, Object> userData = new HashMap<>();
            userData.put("name", name);
            userData.put("email", email);
            userData.put("photoUrl", photoUrl);

            // Save user data to Firestore
            DocumentReference userRef = db.collection("users").document(userId);
            userRef.set(userData, SetOptions.merge())  // Use merge to only update specific fields
                    .addOnSuccessListener(aVoid -> {
                        // Success callback
                        Log.d("ProfileFragment", "User profile updated successfully");
                    })
                    .addOnFailureListener(e -> {
                        // Failure callback
                        Log.w("ProfileFragment", "Error updating user profile", e);
                    });
        }
    }

    private void updateUserProfile(String userId, String address, String phone, String nickname) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("users").document(userId);

        // Membuat Map untuk update data
        Map<String, Object> updates = new HashMap<>();
        updates.put("address", address);
        updates.put("phoneNumber", phone);
        updates.put("nickname", nickname);

        // Update data di Firestore
        userRef.update(updates)
                .addOnSuccessListener(aVoid -> {
                    // Tanggapan sukses
                    Log.d("Firestore", "User profile updated successfully");
                })
                .addOnFailureListener(e -> {
                    // Tangani kegagalan
                    Log.e("Firestore", "Error updating user profile", e);
                });
    }

    @SuppressLint("SetTextI18n")
    private void loadDonationCount(String userId) {
        // Set placeholder sebelum memulai pengambilan data
        if (binding != null) {
            binding.profileDonations.setText("Loading donations...");
        }

        db.collection("donations")
                .whereEqualTo("userId", userId) // Sesuaikan dengan field yang sesuai di koleksi "donations"
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (binding != null) {
                        // Pastikan binding tidak null sebelum memodifikasi UI
                        int donationCount = querySnapshot != null ? querySnapshot.size() : 0;
                        binding.profileDonations.setText("Donations: " + donationCount);
                    }
                })
                .addOnFailureListener(e -> {
                    if (binding != null) {
                        // Tampilkan pesan error jika terjadi kegagalan
                        binding.profileDonations.setText("Failed to load donations");
                    }
                    Log.e(TAG, "Error getting donation count", e);
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}