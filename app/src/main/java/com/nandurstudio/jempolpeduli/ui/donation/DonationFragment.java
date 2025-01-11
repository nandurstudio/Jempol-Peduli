package com.nandurstudio.jempolpeduli.ui.donation;

import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.nandurstudio.jempolpeduli.R;
import com.nandurstudio.jempolpeduli.data.model.Donation;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class DonationFragment extends Fragment {

    private FirebaseFirestore db;

    private EditText editTextDonationName, editTextDonationAddress, editTextDonationDescription, editTextDonationQuantity;
    private ImageView imageViewDonation;
    private Spinner spinnerDonationStatus;
    private Button buttonAddDonation;

    private Uri selectedImageUri = null;

    private final String[] statusOptions = {"available", "taken"}; // Menentukan status donasi yang valid
    private ArrayAdapter<String> statusAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_donation, container, false);

        db = FirebaseFirestore.getInstance();

        // Initialize UI Components
        editTextDonationName = root.findViewById(R.id.editTextDonationName);
        editTextDonationAddress = root.findViewById(R.id.editTextDonationAddress);
        editTextDonationDescription = root.findViewById(R.id.editTextDonationDescription);
        editTextDonationQuantity = root.findViewById(R.id.editTextDonationQuantity);
        imageViewDonation = root.findViewById(R.id.imageViewDonation);
        spinnerDonationStatus = root.findViewById(R.id.spinnerDonationStatus);
        buttonAddDonation = root.findViewById(R.id.buttonAddDonation);

        // Setup Spinner
        statusAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, statusOptions);
        spinnerDonationStatus.setAdapter(statusAdapter);

        // Button Listeners
        root.findViewById(R.id.buttonSelectImage).setOnClickListener(v ->
                setImageFromAssets(imageViewDonation, "donation_default.jpg"));
        buttonAddDonation.setOnClickListener(v -> saveDonationData());

        return root;
    }

    // Set gambar dari folder assets ke ImageView
    private void setImageFromAssets(ImageView imageView, String fileName) {
        AssetManager assetManager = requireContext().getAssets();
        try (InputStream inputStream = assetManager.open(fileName)) {
            Drawable drawable = Drawable.createFromStream(inputStream, null);
            imageView.setImageDrawable(drawable);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(requireContext(), "Gagal memuat gambar dari assets", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveDonationData() {
        String name = editTextDonationName.getText().toString();
        String address = editTextDonationAddress.getText().toString();
        String description = editTextDonationDescription.getText().toString();
        String quantityStr = editTextDonationQuantity.getText().toString();
        String status = spinnerDonationStatus.getSelectedItem().toString();

        if (name.isEmpty() || address.isEmpty() || description.isEmpty() || quantityStr.isEmpty()) {
            Toast.makeText(requireContext(), "Semua field harus diisi!", Toast.LENGTH_SHORT).show();
            return;
        }

        int quantity;
        try {
            quantity = Integer.parseInt(quantityStr);
        } catch (NumberFormatException e) {
            Toast.makeText(requireContext(), "Kuantitas harus berupa angka!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Gunakan nama gambar default jika tidak ada gambar dipilih
        String imageFileName = "donation_default.jpg";

        saveDonationToFirestore(name, address, description, quantity, imageFileName, status);
    }

    //TODO: Buatkan Save dan Delete
    private void saveDonationToFirestore(String name, String address, String description, int quantity, @Nullable String imageFileName, String status) {
        String userId = FirebaseAuth.getInstance().getUid();

        Donation donation = new Donation();
        donation.setUserId(userId);
        donation.setName(name);
        donation.setAddress(address);
        donation.setDescription(description);
        donation.setQuantity(quantity);
        donation.setImageUrl(imageFileName); // Simpan nama file gambar prebuilt
        donation.setStatus(status);
        donation.setActive(status.equals("available"));
        donation.setLikes(new ArrayList<>());
        donation.setDislikes(new ArrayList<>());

        db.collection("donations")
                .add(donation)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(requireContext(), "Donasi berhasil ditambahkan!", Toast.LENGTH_SHORT).show();
                    resetForm();
                })
                .addOnFailureListener(e -> Toast.makeText(requireContext(), "Gagal menyimpan donasi: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void resetForm() {
        editTextDonationName.setText("");
        editTextDonationAddress.setText("");
        editTextDonationDescription.setText("");
        editTextDonationQuantity.setText("");
        spinnerDonationStatus.setSelection(0);
        imageViewDonation.setImageResource(android.R.color.transparent);
        selectedImageUri = null;
    }
}
