package com.nandurstudio.jempolpeduli.ui.campaign;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nandurstudio.jempolpeduli.R;
import com.nandurstudio.jempolpeduli.placeholder.PlaceholderContent;

/**
 * A fragment representing a campaign of Items.
 */
public class CampaignFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public CampaignFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static CampaignFragment newInstance(int columnCount) {
        CampaignFragment fragment = new CampaignFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_campaign_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            CustomRecyclerView recyclerView = view.findViewById(R.id.campaign);


            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }

            // Tambahkan separator
            recyclerView.addItemDecoration(new CustomDividerItemDecoration(context, 32)); // 32dp padding

            recyclerView.setAdapter(new CampaignRecyclerViewAdapter(PlaceholderContent.ITEMS));
        }
        return view;
    }
}