package com.nandurstudio.jempolpeduli.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;
import com.nandurstudio.jempolpeduli.R;

public class FullscreenImageDialogFragment extends DialogFragment {

    private static final String ARG_IMAGE_URL = "image_url";

    public static FullscreenImageDialogFragment newInstance(String imageUrl) {
        FullscreenImageDialogFragment fragment = new FullscreenImageDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_IMAGE_URL, imageUrl);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_fullscreen_image, container, false);

        Toolbar toolbar = view.findViewById(R.id.fullscreen_toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(v -> dismiss());

        PhotoView photoView = view.findViewById(R.id.fullscreen_image);

        if (getArguments() != null) {
            String imageUrl = getArguments().getString(ARG_IMAGE_URL);
            Glide.with(this)
                    .load(imageUrl)
                    .into(photoView);
        }

        return view;
    }

    @Override
    public int getTheme() {
        return R.style.FullScreenDialog;
    }
}
