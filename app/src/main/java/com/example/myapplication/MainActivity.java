package com.example.myapplication;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.myapplication.databinding.ActivityMainBinding;
import com.example.myapplication.models.Jugador;
import com.example.myapplication.network.SupabaseApi;
import com.onesignal.OneSignal;
import com.onesignal.debug.LogLevel;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    // IMPORTANTE: Cambia estas constantes por tus valores reales de OneSignal y Supabase
    private static final String ONESIGNAL_APP_ID = "YOUR_ONESIGNAL_APP_ID";
    private static final String SUPABASE_URL = "https://placeholder.supabase.co/";
    private static final String SUPABASE_KEY = "YOUR_SUPABASE_KEY";
    
    private SupabaseApi supabaseApi;
    private RankingAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Inicializar OneSignal solo si hay un ID válido
        if (!ONESIGNAL_APP_ID.equals("YOUR_ONESIGNAL_APP_ID")) {
            OneSignal.getDebug().setLogLevel(LogLevel.VERBOSE);
            OneSignal.initWithContext(this, ONESIGNAL_APP_ID);
        }

        setupRetrofit();
        setupRecyclerView();
        setupNavigation();
        loadRanking();
    }

    private void setupRetrofit() {
        try {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(SUPABASE_URL.startsWith("http") ? SUPABASE_URL : "https://placeholder.supabase.co/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            supabaseApi = retrofit.create(SupabaseApi.class);
        } catch (Exception e) {
            Log.e("MainActivity", "Error en setupRetrofit: " + e.getMessage());
        }
    }

    private void setupRecyclerView() {
        adapter = new RankingAdapter(new ArrayList<>());
        binding.rvRanking.setLayoutManager(new LinearLayoutManager(this));
        binding.rvRanking.setAdapter(adapter);
    }

    private void setupNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            
            // Ocultar todas las secciones
            binding.sectionHome.setVisibility(View.GONE);
            binding.sectionRanking.setVisibility(View.GONE);
            binding.sectionCreate.setVisibility(View.GONE);
            binding.sectionProfile.setVisibility(View.GONE);

            // Mostrar la seleccionada
            if (id == R.id.nav_home) {
                binding.sectionHome.setVisibility(View.VISIBLE);
                return true;
            } else if (id == R.id.nav_ranking) {
                binding.sectionRanking.setVisibility(View.VISIBLE);
                return true;
            } else if (id == R.id.nav_create) {
                binding.sectionCreate.setVisibility(View.VISIBLE);
                return true;
            } else if (id == R.id.nav_profile) {
                binding.sectionProfile.setVisibility(View.VISIBLE);
                return true;
            }
            return false;
        });
    }

    private void loadRanking() {
        if (supabaseApi == null || SUPABASE_URL.contains("placeholder")) {
            showMockData();
            return;
        }

        supabaseApi.getRanking(SUPABASE_KEY, "Bearer " + SUPABASE_KEY, "*", "goles.desc")
                .enqueue(new Callback<List<Jugador>>() {
                    @Override
                    public void onResponse(Call<List<Jugador>> call, Response<List<Jugador>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            adapter.updateData(response.body());
                        } else {
                            showMockData();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Jugador>> call, Throwable t) {
                        Log.e("MainActivity", "Error Supabase: " + t.getMessage());
                        showMockData();
                    }
                });
    }

    private void showMockData() {
        List<Jugador> mockList = new ArrayList<>();
        String[] nombres = {"Bastian", "Matias", "Julian", "Enzo", "Fran"};
        String[] posiciones = {"Delantero", "Mediocampista", "Defensor", "Arquero", "Delantero"};
        
        for (int i = 0; i < nombres.length; i++) {
            Jugador j = new Jugador();
            j.setNombre(nombres[i]);
            j.setPosicion(posiciones[i]);
            j.setGoles(25 - (i * 3));
            j.setPartidosJugados(12);
            j.setPartidosGanados(8 - i);
            mockList.add(j);
        }
        adapter.updateData(mockList);
    }
}
