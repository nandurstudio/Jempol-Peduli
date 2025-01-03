package com.nandurstudio.jempolpeduli;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.Menu;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import androidx.core.splashscreen.SplashScreen;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.nandurstudio.jempolpeduli.databinding.ActivityMainBinding;
import com.nandurstudio.jempolpeduli.ui.campaign.CampaignFragment;
import com.nandurstudio.jempolpeduli.ui.donation.DonationFragment;
import com.nandurstudio.jempolpeduli.ui.home.HomeFragment;
import com.nandurstudio.jempolpeduli.ui.inbox.InboxFragment;
import com.nandurstudio.jempolpeduli.ui.login.LoginActivity;
import com.nandurstudio.jempolpeduli.ui.profile.ProfileFragment;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Pasang splash screen
        SplashScreen.installSplashScreen(this);

        // Inisialisasi Firebase
        FirebaseApp.initializeApp(this);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);
        binding.appBarMain.fab.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), LoginActivity.class);
            view.getContext().startActivity(intent);
        });

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;

        View headerView = navigationView.getHeaderView(0); // Ambil header dari NavigationView

        ImageView profileImageView = headerView.findViewById(R.id.profileImageView);
        TextView profileNameTextView = headerView.findViewById(R.id.profileNameTextView);
        TextView profileEmailTextView = headerView.findViewById(R.id.profileEmailTextView);

        ViewPager2 viewPager = findViewById(R.id.viewPager);
        NavigationBarView navigationBarView = findViewById(R.id.bottom_navigation);

        ViewPagerAdapter adapter = new ViewPagerAdapter(this);
        viewPager.setAdapter(adapter);

        navigationBarView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_home) {
                viewPager.setCurrentItem(0);  // Pindah ke HomeFragment
                Toast.makeText(this, "Item 1 clicked", Toast.LENGTH_SHORT).show();
                return true;
            } else if (item.getItemId() == R.id.nav_donation) {
                viewPager.setCurrentItem(1);  // Pindah ke DonationFragment
                Toast.makeText(this, "Item 2 clicked", Toast.LENGTH_SHORT).show();
                return true;
            } else if (item.getItemId() == R.id.nav_profile) {
                viewPager.setCurrentItem(2);  // Pindah ke ProfileFragment
                Toast.makeText(this, "Item 3 clicked", Toast.LENGTH_SHORT).show();
                return true;
            } else if (item.getItemId() == R.id.nav_inbox) {
                viewPager.setCurrentItem(3);  // Pindah ke InboxFragment
                Toast.makeText(this, "Item 4 clicked", Toast.LENGTH_SHORT).show();
                return true;
            } else if (item.getItemId() == R.id.nav_campaign) {
                viewPager.setCurrentItem(4);  // Pindah ke CampaignFragment
                Toast.makeText(this, "Item 5 clicked", Toast.LENGTH_SHORT).show();
                return true;
            }
            return false;
        });

        // Sinkronkan swipe dengan bottom nav
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                // Mengubah judul app bar sesuai fragment yang dipilih
                switch (position) {
                    case 0:
                        navigationBarView.setSelectedItemId(R.id.nav_home);  // Sinkronkan dengan Home
                        Objects.requireNonNull(getSupportActionBar()).setTitle("Home");
                        break;
                    case 1:
                        navigationBarView.setSelectedItemId(R.id.nav_donation);  // Sinkronkan dengan Donation
                        Objects.requireNonNull(getSupportActionBar()).setTitle("Donation");
                        break;
                    case 2:
                        navigationBarView.setSelectedItemId(R.id.nav_profile);  // Sinkronkan dengan Profile
                        Objects.requireNonNull(getSupportActionBar()).setTitle("Profile");
                        break;
                    case 3:
                        navigationBarView.setSelectedItemId(R.id.nav_inbox);  // Sinkronkan dengan Inbox
                        Objects.requireNonNull(getSupportActionBar()).setTitle("Inbox");
                        break;
                    case 4:
                        navigationBarView.setSelectedItemId(R.id.nav_campaign);  // Sinkronkan dengan Campaign
                        Objects.requireNonNull(getSupportActionBar()).setTitle("Campaign");
                        break;
                    default:
                        navigationBarView.setSelectedItemId(R.id.nav_home);  // Default ke Home
                        Objects.requireNonNull(getSupportActionBar()).setTitle("Home");
                        break;
                }
            }
        });

        // Periksa apakah pengguna sudah login
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            // Pengguna belum login, alihkan ke LoginActivity
            Intent loginIntent = new Intent(this, LoginActivity.class);
            loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Hapus backstack
            startActivity(loginIntent);
            finish(); // Tutup MainActivity
            return;
        }

        // Setup Drawer Toggle
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, binding.appBarMain.toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        // Tambahkan di sini setelah pengguna dipastikan sudah login
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null) {
            String name = account.getDisplayName(); // Nama pengguna
            String email = account.getEmail();     // Email pengguna
            String photoUrl = (account.getPhotoUrl() != null) ? account.getPhotoUrl().toString() : null; // URL foto profil

            // Set nama dan email ke TextView
            profileNameTextView.setText(name);
            profileEmailTextView.setText(email);

            // Set foto profil ke ImageView
            if (photoUrl != null) {
                Glide.with(this)
                        .load(photoUrl)
                        .circleCrop() // Untuk membuat gambar menjadi lingkaran
                        .into(profileImageView);
            }
        }

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_donation, R.id.nav_profile, R.id.nav_inbox, R.id.nav_campaign)
                .setOpenableLayout(drawer)
                .build();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_logout) {
            // Logout dari Firebase
            FirebaseAuth.getInstance().signOut();
            GoogleSignIn.getClient(this, GoogleSignInOptions.DEFAULT_SIGN_IN).signOut();

            // Redirect ke LoginActivity
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Hapus backstack
            startActivity(intent);
            finish(); // Tutup MainActivity
            return true;
        }

        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static class ViewPagerAdapter extends FragmentStateAdapter {

        public ViewPagerAdapter(FragmentActivity activity) {
            super(activity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case 0:
                    return new HomeFragment(); // Fragment untuk Home
                case 1:
                    return new DonationFragment(); // Fragment untuk Donation
                case 2:
                    return new ProfileFragment(); // Fragment untuk Profile
                case 3:
                    return new InboxFragment(); // Fragment untuk Inbox
                case 4:
                    return new CampaignFragment(); // Fragment untuk Campaign
                default:
                    return new HomeFragment(); // Default ke HomeFragment jika tidak ada case yang cocok
            }
        }

        @Override
        public int getItemCount() {
            return 5; // Jumlah fragment yang ingin ditampilkan
        }
    }
}