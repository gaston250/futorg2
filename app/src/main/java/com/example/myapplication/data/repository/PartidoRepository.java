package com.example.myapplication.data.repository;

import android.content.Context;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import com.example.myapplication.data.local.AppDatabase;
import com.example.myapplication.data.local.dao.PartidoDao;
import com.example.myapplication.data.local.entities.PartidoEntity;
import com.example.myapplication.models.Partido;
import com.example.myapplication.network.RetrofitClient;
import com.example.myapplication.network.SupabaseApi;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PartidoRepository {
    private final PartidoDao partidoDao;
    private final SupabaseApi supabaseApi;
    private final ExecutorService executorService;

    public PartidoRepository(Context context) {
        AppDatabase db = AppDatabase.getDatabase(context);
        partidoDao = db.partidoDao();
        supabaseApi = RetrofitClient.createService(SupabaseApi.class);
        executorService = Executors.newFixedThreadPool(2);
    }

    public LiveData<Partido> getUltimoPartido() {
        return Transformations.map(partidoDao.getUltimoPartido(), entity -> {
            if (entity == null) return null;
            return entity.toModel();
        });
    }

    public void refreshUltimoPartido() {
        supabaseApi.getUltimoPartido("*", "id.desc", 1).enqueue(new Callback<List<Partido>>() {
            @Override
            public void onResponse(Call<List<Partido>> call, Response<List<Partido>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    executorService.execute(() -> {
                        PartidoEntity entity = PartidoEntity.fromModel(response.body().get(0));
                        if (entity != null) {
                            partidoDao.deleteAll();
                            partidoDao.insert(entity);
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<List<Partido>> call, Throwable t) {
            }
        });
    }
}
