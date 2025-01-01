package com.nandurstudio.jempolpeduli;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.Menu;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import androidx.core.splashscreen.SplashScreen;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.nandurstudio.jempolpeduli.databinding.ActivityMainBinding;
import com.nandurstudio.jempolpeduli.ui.login.LoginActivity;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Pasang splash screen
        SplashScreen.installSplashScreen(this);

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

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_home) {
                Toast.makeText(this, "Item 1 clicked", Toast.LENGTH_SHORT).show();
                return true;
            } else if (item.getItemId() == R.id.nav_donation) {
                Toast.makeText(this, "Item 2 clicked", Toast.LENGTH_SHORT).show();
                return true;
            } else if (item.getItemId() == R.id.nav_profile) {
                Toast.makeText(this, "Item 3 clicked", Toast.LENGTH_SHORT).show();
                return true;
            } else if (item.getItemId() == R.id.nav_inbox) {
                Toast.makeText(this, "Item 4 clicked", Toast.LENGTH_SHORT).show();
                return true;
            } else if (item.getItemId() == R.id.nav_campaign) {
                Toast.makeText(this, "Item 5 clicked", Toast.LENGTH_SHORT).show();
                return true;
            }
            return false;
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

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        // Hubungkan BottomNavigationView dengan NavController
        NavigationUI.setupWithNavController(bottomNavigationView, navController);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
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
}