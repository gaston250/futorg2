package com.example.myapplication.ui;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myapplication.data.repository.JugadorRepository;
import com.example.myapplication.data.repository.PartidoRepository;
import com.example.myapplication.models.Jugador;
import com.example.myapplication.models.Mensaje;
import com.example.myapplication.models.Partido;

import java.util.List;

public class MainViewModel extends AndroidViewModel {

    private final JugadorRepository jugadorRepository;
    private final PartidoRepository partidoRepository;
    
    private final LiveData<List<Jugador>> rankingList;
    private final LiveData<Partido> ultimoPartido;
    
    private final MutableLiveData<List<Jugador>> confirmadosList = new MutableLiveData<>();
    private final MutableLiveData<List<Mensaje>> chatMensajes = new MutableLiveData<>();
    private final MutableLiveData<List<Jugador>> amigosList = new MutableLiveData<>();
    private final MutableLiveData<List<Partido>> misPartidosList = new MutableLiveData<>();

    public MainViewModel(@NonNull Application application) {
        super(application);
        jugadorRepository = new JugadorRepository(application);
        partidoRepository = new PartidoRepository(application);
        
        rankingList = jugadorRepository.getRanking();
        ultimoPartido = partidoRepository.getUltimoPartido();
    }

    public LiveData<List<Jugador>> getRankingList() { return rankingList; }
    
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
