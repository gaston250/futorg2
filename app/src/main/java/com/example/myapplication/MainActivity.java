package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.example.myapplication.databinding.ActivityMainBinding;
import com.example.myapplication.models.Jugador;
import com.example.myapplication.models.Partido;
import com.example.myapplication.network.RetrofitClient;
import com.example.myapplication.network.SupabaseApi;
import com.example.myapplication.ui.MainViewModel;
import com.example.myapplication.BuildConfig;
import com.example.myapplication.R;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private static final String SUPABASE_KEY = BuildConfig.SUPABASE_KEY;
    
    private SupabaseApi supabaseApi;
    private MainViewModel viewModel;
    private NavController navController;
    
    private String userName = "Jugador";
    private String userId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        AuthManager authManager = AuthManager.getInstance(this);
        if (!authManager.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        userId = authManager.getUserId();
        userName = authManager.getUserName();

        viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        RetrofitClient.init(authManager);
        setupNavigation();
        setupBottomNavigation();
        setupRetrofit();
        
        loadInitialData();
    }

    private void setupNavigation() {
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();
        }
    }

    private void setupBottomNavigation() {
        if (navController != null) {
            NavigationUI.setupWithNavController(binding.bottomNavigation, navController);

            binding.fabCreateMatch.setOnClickListener(v -> 
                navController.navigate(R.id.nav_create_partido)
            );

            navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
                if (destination.getId() == R.id.nav_home) {
                    binding.fabCreateMatch.setVisibility(View.VISIBLE);
                } else {
                    binding.fabCreateMatch.setVisibility(View.GONE);
                }
            });
        }
    }

    private void setupRetrofit() {
        try {
            supabaseApi = com.example.myapplication.network.RetrofitClient.createService(SupabaseApi.class);
        } catch (Exception e) {
            Log.e("MainActivity", "Retrofit Setup Error", e);
        }
    }

    private void loadInitialData() {
        loadRanking();
        loadUltimoPartido();
    }

    private void loadRanking() {
        viewModel.refreshRanking();
    }

    private void loadUltimoPartido() {
        viewModel.refreshUltimoPartido();
    }
}
