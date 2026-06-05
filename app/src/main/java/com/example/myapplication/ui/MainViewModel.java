package com.example.myapplication.ui;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.MediatorLiveData;

import com.example.myapplication.data.repository.ApiRepository;
import com.example.myapplication.data.repository.JugadorRepository;
import com.example.myapplication.data.repository.PartidoRepository;
import com.example.myapplication.models.Jugador;
import com.example.myapplication.models.Mensaje;
import com.example.myapplication.models.Partido;
import com.example.myapplication.models.Voto;

import java.util.List;

import okhttp3.RequestBody;

public class MainViewModel extends AndroidViewModel {

    private final JugadorRepository jugadorRepository;
    private final PartidoRepository partidoRepository;
    private final ApiRepository apiRepository;
    
    private final MediatorLiveData<List<Jugador>> ranking = new MediatorLiveData<>();
    private final MediatorLiveData<Partido> ultimoPartido = new MediatorLiveData<>();
    
    private final MutableLiveData<List<Jugador>> confirmadosList = new MutableLiveData<>();
    private final MutableLiveData<List<Mensaje>> chatMensajes = new MutableLiveData<>();
    private final MutableLiveData<List<Jugador>> amigosList = new MutableLiveData<>();
    private final MutableLiveData<List<Partido>> misPartidosList = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    private final android.os.Handler chatHandler = new android.os.Handler(android.os.Looper.getMainLooper());
    private Runnable chatRunnable;
    private int activeChatMatchId = -1;
    private String activeChatToken = "";

    public MainViewModel(@NonNull Application application) {
        super(application);
        jugadorRepository = new JugadorRepository(application);
        partidoRepository = new PartidoRepository(application);
        apiRepository = new ApiRepository();
        
        ranking.addSource(jugadorRepository.getRanking(), ranking::setValue);
        ultimoPartido.addSource(partidoRepository.getUltimoPartido(), ultimoPartido::setValue);
    }

    public LiveData<Boolean> sumarseAPartido(int partidoId, String userName, String token) {
        return apiRepository.agregarConfirmado(partidoId, userName, token);
    }

    public void fetchConfirmados(int partidoId) {
        isLoading.setValue(true);
        apiRepository.getConfirmados(partidoId).observeForever(confirmados -> {
            confirmadosList.setValue(confirmados);
            isLoading.setValue(false);
        });
    }

    public LiveData<Boolean> sendMessage(Mensaje mensaje, String token) {
        isLoading.setValue(true);
        MutableLiveData<Boolean> result = new MutableLiveData<>();
        apiRepository.enviarMensaje(mensaje, token).observeForever(success -> {
            result.setValue(success);
            isLoading.setValue(false);
            if (success) {
                fetchMensajes(mensaje.getPartidoId(), token);
            }
        });
        return result;
    }

    public void fetchMensajes(int partidoId, String token) {
        apiRepository.getMensajes(partidoId, token).observeForever(mensajes -> {
            if (mensajes != null) {
                chatMensajes.setValue(mensajes);
            }
        });
    }

    public void startRealtimeChat(int partidoId, String token) {
        stopRealtimeChat();
        activeChatMatchId = partidoId;
        activeChatToken = token;
        
        chatRunnable = new Runnable() {
            @Override
            public void run() {
                if (activeChatMatchId != -1) {
                    fetchMensajes(activeChatMatchId, activeChatToken);
                    chatHandler.postDelayed(this, 3000); // Poll every 3 seconds
                }
            }
        };
        chatHandler.post(chatRunnable);
    }

    public void stopRealtimeChat() {
        chatHandler.removeCallbacks(chatRunnable);
        activeChatMatchId = -1;
    }

    public LiveData<Boolean> closeMatch(int partidoId, RequestBody body) {
        isLoading.setValue(true);
        MutableLiveData<Boolean> result = new MutableLiveData<>();
        apiRepository.cerrarPartido(partidoId, body).observeForever(success -> {
            result.setValue(success);
            isLoading.setValue(false);
        });
        return result;
    }

    public LiveData<Boolean> voteMVP(Voto voto) {
        isLoading.setValue(true);
        MutableLiveData<Boolean> result = new MutableLiveData<>();
        apiRepository.emitirVoto(voto).observeForever(success -> {
            result.setValue(success);
            isLoading.setValue(false);
        });
        return result;
    }

    public void fetchMisPartidos(String userName) {
        isLoading.setValue(true);
        apiRepository.getPartidosUsuario(userName).observeForever(partidos -> {
            misPartidosList.setValue(partidos);
            isLoading.setValue(false);
        });
    }

    public LiveData<Jugador> fetchUserProfile(String email) {
        isLoading.setValue(true);
        MutableLiveData<Jugador> result = new MutableLiveData<>();
        apiRepository.getJugadorByEmail(email).observeForever(jugador -> {
            result.setValue(jugador);
            isLoading.setValue(false);
        });
        return result;
    }

    public LiveData<Boolean> updateUserProfile(String userId, Jugador jugador, String token) {
        isLoading.setValue(true);
        MutableLiveData<Boolean> result = new MutableLiveData<>();
        apiRepository.updatePerfil(userId, jugador, token).observeForever(success -> {
            result.setValue(success);
            isLoading.setValue(false);
        });
        return result;
    }

    public LiveData<Boolean> getIsLoading() { return isLoading; }
    public void setIsLoading(boolean loading) { isLoading.setValue(loading); }

    public LiveData<List<Jugador>> getRankingList() { return ranking; }
    
    public void refreshRanking() {
        isLoading.setValue(true);
        jugadorRepository.refreshRanking();
        // Repository should update LiveData which we observe in constructor
        // We might need a callback from repository to set isLoading to false
        // For now, let's assume it updates.
        isLoading.setValue(false);
    }

    public LiveData<Partido> getUltimoPartido() { return ultimoPartido; }
    
    public void refreshUltimoPartido() {
        isLoading.setValue(true);
        partidoRepository.refreshUltimoPartido();
        isLoading.setValue(false);
    }

    public LiveData<List<Jugador>> getConfirmadosList() { return confirmadosList; }
    public void setConfirmadosList(List<Jugador> list) { confirmadosList.setValue(list); }

    public LiveData<List<Mensaje>> getChatMensajes() { return chatMensajes; }
    public void setChatMensajes(List<Mensaje> list) { chatMensajes.setValue(list); }

    public LiveData<List<Jugador>> getAmigosList() { return amigosList; }
    public void setAmigosList(List<Jugador> list) { amigosList.setValue(list); }

    public LiveData<List<Partido>> getMisPartidosList() { return misPartidosList; }
    public void setMisPartidosList(List<Partido> list) { misPartidosList.setValue(list); }
}
