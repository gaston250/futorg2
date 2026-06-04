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
        apiRepository.getConfirmados(partidoId).observeForever(confirmadosList::setValue);
    }

    public LiveData<Boolean> sendMessage(Mensaje mensaje, String token) {
        return apiRepository.enviarMensaje(mensaje, token);
    }

    public void fetchMensajes(int partidoId, String token) {
        apiRepository.getMensajes(partidoId, token).observeForever(chatMensajes::setValue);
    }

    public LiveData<Boolean> closeMatch(int partidoId, RequestBody body) {
        return apiRepository.cerrarPartido(partidoId, body);
    }

    public LiveData<Boolean> voteMVP(Voto voto) {
        return apiRepository.emitirVoto(voto);
    }

    public void fetchMisPartidos(String userName) {
        apiRepository.getPartidosUsuario(userName).observeForever(misPartidosList::setValue);
    }

    public LiveData<Jugador> fetchUserProfile(String email) {
        return apiRepository.getJugadorByEmail(email);
    }

    public LiveData<Boolean> updateUserProfile(String userId, Jugador jugador, String token) {
        return apiRepository.updatePerfil(userId, jugador, token);
    }

    public LiveData<List<Jugador>> getRankingList() { return ranking; }
    
    public void refreshRanking() {
        jugadorRepository.refreshRanking();
    }

    public LiveData<Partido> getUltimoPartido() { return ultimoPartido; }
    
    public void refreshUltimoPartido() {
        partidoRepository.refreshUltimoPartido();
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
