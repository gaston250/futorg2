package com.example.myapplication.data.repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myapplication.AuthManager;
import com.example.myapplication.models.Jugador;
import com.example.myapplication.models.Mensaje;
import com.example.myapplication.models.Partido;
import com.example.myapplication.models.Voto;
import com.example.myapplication.network.RetrofitClient;
import com.example.myapplication.network.SupabaseApi;

import org.json.JSONObject;

import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ApiRepository {
    private final SupabaseApi api;

    public ApiRepository() {
        this.api = RetrofitClient.createService(SupabaseApi.class);
    }

    public LiveData<List<Jugador>> getRanking() {
        MutableLiveData<List<Jugador>> data = new MutableLiveData<>();
        api.getRanking("*", "goles.desc").enqueue(new Callback<List<Jugador>>() {
            @Override
            public void onResponse(@NonNull Call<List<Jugador>> call, @NonNull Response<List<Jugador>> response) {
                if (response.isSuccessful()) {
                    data.setValue(response.body());
                }
            }
            @Override
            public void onFailure(@NonNull Call<List<Jugador>> call, @NonNull Throwable t) {
                data.setValue(null);
            }
        });
        return data;
    }

    public LiveData<Boolean> agregarConfirmado(int partidoId, String userName, String token) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();
        try {
            JSONObject json = new JSONObject();
            json.put("partido_id", partidoId);
            json.put("jugador_nombre", userName);
            json.put("pagado", false);
            
            RequestBody body = RequestBody.create(MediaType.parse("application/json"), json.toString());
            api.agregarConfirmado(body).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                    result.setValue(response.isSuccessful());
                }
                @Override
                public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                    result.setValue(false);
                }
            });
        } catch (Exception e) {
            result.setValue(false);
        }
        return result;
    }

    public LiveData<List<Jugador>> getConfirmados(int partidoId) {
        MutableLiveData<List<Jugador>> data = new MutableLiveData<>();
        api.getConfirmados("*", "eq." + partidoId).enqueue(new Callback<List<Jugador>>() {
            @Override
            public void onResponse(@NonNull Call<List<Jugador>> call, @NonNull Response<List<Jugador>> response) {
                if (response.isSuccessful()) {
                    data.setValue(response.body());
                }
            }
            @Override
            public void onFailure(@NonNull Call<List<Jugador>> call, @NonNull Throwable t) {
                data.setValue(null);
            }
        });
        return data;
    }

    public LiveData<Boolean> enviarMensaje(Mensaje mensaje, String token) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();
        api.enviarMensaje(mensaje).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                result.setValue(response.isSuccessful());
            }
            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                result.setValue(false);
            }
        });
        return result;
    }

    public LiveData<Boolean> cerrarPartido(int partidoId, RequestBody body) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();
        api.cerrarPartido("eq." + partidoId, body).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                result.setValue(response.isSuccessful());
            }
            @Override public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) { result.setValue(false); }
        });
        return result;
    }

    public LiveData<Boolean> emitirVoto(Voto voto) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();
        api.emitirVoto(voto).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                result.setValue(response.isSuccessful());
            }
            @Override public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) { result.setValue(false); }
        });
        return result;
    }

    public LiveData<Jugador> getJugadorByEmail(String email) {
        MutableLiveData<Jugador> data = new MutableLiveData<>();
        api.getJugadorByEmail("*", "eq." + email).enqueue(new Callback<List<Jugador>>() {
            @Override
            public void onResponse(@NonNull Call<List<Jugador>> call, @NonNull Response<List<Jugador>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    data.setValue(response.body().get(0));
                }
            }
            @Override public void onFailure(@NonNull Call<List<Jugador>> call, @NonNull Throwable t) { data.setValue(null); }
        });
        return data;
    }

    public LiveData<Boolean> updatePerfil(String email, Jugador jugador, String token) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();
        api.updatePerfil("eq." + email, jugador).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                result.setValue(response.isSuccessful());
            }
            @Override public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) { result.setValue(false); }
        });
        return result;
    }

    public LiveData<List<Mensaje>> getMensajes(int partidoId, String token) {
        MutableLiveData<List<Mensaje>> data = new MutableLiveData<>();
        api.getMensajes("eq." + partidoId, "*", "created_at.asc").enqueue(new Callback<List<Mensaje>>() {
            @Override
            public void onResponse(@NonNull Call<List<Mensaje>> call, @NonNull Response<List<Mensaje>> response) {
                if (response.isSuccessful()) {
                    data.setValue(response.body());
                }
            }
            @Override public void onFailure(@NonNull Call<List<Mensaje>> call, @NonNull Throwable t) { data.setValue(null); }
        });
        return data;
    }

    public LiveData<List<Partido>> getPartidosUsuario(String userName) {
        MutableLiveData<List<Partido>> data = new MutableLiveData<>();
        // Implementation depends on how the join is handled in SupabaseApi, 
        // but this matches the call signature in ViewModel.
        return data;
    }
}
