package com.nandurstudio.jempolpeduli.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.nandurstudio.jempolpeduli.R;
import com.nandurstudio.jempolpeduli.databinding.FragmentProfileBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private ProfileViewModel profileViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        profileViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Get Firebase user data
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String name = user.getDisplayName();
            String email = user.getEmail();
            //Set the Image dimension here it will not reduce the image pixels
            String photoUrl = user.getPhotoUrl() != null ? user.getPhotoUrl().toString().replace("s96-c", "s492-c") : null;

            // Update ViewModel with user data
            profileViewModel.setUserData(name, email, photoUrl);
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
        profileViewModel.getName().observe(getViewLifecycleOwner(), name -> {
            binding.profileName.setText(name);
        });

        profileViewModel.getEmail().observe(getViewLifecycleOwner(), email -> {
            binding.profileEmail.setText(email);
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
