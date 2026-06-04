package com.example.myapplication.data.repository;

import android.content.Context;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import com.example.myapplication.data.local.AppDatabase;
import com.example.myapplication.data.local.dao.JugadorDao;
import com.example.myapplication.data.local.entities.JugadorEntity;
import com.example.myapplication.models.Jugador;
import com.example.myapplication.network.RetrofitClient;
import com.example.myapplication.network.SupabaseApi;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Single source of truth for Player data.
 * Coordinates between local Room database and remote Supabase API.
 */
public class JugadorRepository {
    private final JugadorDao jugadorDao;
    private final SupabaseApi supabaseApi;
    private final ExecutorService executorService;

    public JugadorRepository(Context context) {
        AppDatabase db = AppDatabase.getDatabase(context);
        jugadorDao = db.jugadorDao();
        supabaseApi = RetrofitClient.createService(SupabaseApi.class);
        executorService = Executors.newFixedThreadPool(2);
    }

    public LiveData<List<Jugador>> getRanking() {
        // Return LiveData from Room, transformed to Model
        return Transformations.map(jugadorDao.getAllRanking(), entities -> {
            List<Jugador> models = new ArrayList<>();
            for (JugadorEntity entity : entities) {
                models.add(entity.toModel());
            }
            return models;
        });
    }

    public void refreshRanking() {
        supabaseApi.getRanking("*", "goles.desc").enqueue(new Callback<List<Jugador>>() {
            @Override
            public void onResponse(Call<List<Jugador>> call, Response<List<Jugador>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    executorService.execute(() -> {
                        List<JugadorEntity> entities = new ArrayList<>();
                        for (Jugador model : response.body()) {
                            JugadorEntity entity = JugadorEntity.fromModel(model);
                            if (entity != null) entities.add(entity);
                        }
                        jugadorDao.deleteAll();
                        jugadorDao.insertAll(entities);
                    });
                }
            }

            @Override
            public void onFailure(Call<List<Jugador>> call, Throwable t) {
                // Handle failure (maybe log it)
            }
        });
    }
}
