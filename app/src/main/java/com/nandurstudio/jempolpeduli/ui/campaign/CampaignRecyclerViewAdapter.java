package com.nandurstudio.jempolpeduli.ui.campaign;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.nandurstudio.jempolpeduli.R;
import com.nandurstudio.jempolpeduli.databinding.FragmentCampaignBinding;
import com.nandurstudio.jempolpeduli.placeholder.PlaceholderContent.PlaceholderItem;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link PlaceholderItem}.
 * TODO: Replace the implementation with code for your data type.
 */
public class CampaignRecyclerViewAdapter extends RecyclerView.Adapter<CampaignRecyclerViewAdapter.ViewHolder> {

    private final List<PlaceholderItem> mValues;

    public CampaignRecyclerViewAdapter(List<PlaceholderItem> items) {
        mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new ViewHolder(FragmentCampaignBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mIdView.setText(mValues.get(position).id);
        holder.mContentView.setText(mValues.get(position).content);

        // Set gambar menggunakan Glide (atau library lain)
        Glide.with(holder.mImageView.getContext())
                .load(mValues.get(position).imageUrl)
                .placeholder(R.drawable.side_nav_bar)
                .into(holder.mImageView);

        // Tambahkan fungsi klik
        holder.mImageView.setOnClickListener(v -> {
            // Contoh aksi: tampilkan toast atau pindah halaman
            Toast.makeText(v.getContext(), "Clicked: " + holder.mItem.content, Toast.LENGTH_SHORT).show();

            // Atau navigasi ke detail:
            // Intent intent = new Intent(v.getContext(), DetailActivity.class);
            // intent.putExtra("item_id", holder.mItem.id);
            // v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView mIdView;
        public final TextView mContentView;
        public final ImageView mImageView;
        public PlaceholderItem mItem;

        public ViewHolder(@NonNull FragmentCampaignBinding binding) {
            super(binding.getRoot());
            mIdView = binding.itemNumber;
            mContentView = binding.content;
            mImageView = binding.itemImage; // Tambahkan ini
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}