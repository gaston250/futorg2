package com.example.myapplication.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.myapplication.AuthManager;
import com.example.myapplication.MensajesAdapter;
import com.example.myapplication.databinding.FragmentChatBinding;
import com.example.myapplication.models.Mensaje;
import com.example.myapplication.models.Partido;
import com.example.myapplication.network.RetrofitClient;
import com.example.myapplication.network.SupabaseApi;
import com.example.myapplication.utils.UiUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatFragment extends Fragment {

    private FragmentChatBinding binding;
    private MainViewModel viewModel;
    private MensajesAdapter adapter;
    private List<Mensaje> listaMensajes = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentChatBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        setupRecyclerView();
        setupClickListeners();
        observeViewModel();
    }

    private void setupRecyclerView() {
        adapter = new MensajesAdapter(listaMensajes);
        binding.rvChatMensajes.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvChatMensajes.setAdapter(adapter);
    }

    private void setupClickListeners() {
        binding.btnChatSend.setOnClickListener(v -> {
            String texto = binding.etChatMessage.getText().toString().trim();
            if (!texto.isEmpty()) {
                enviarMensaje(texto, "texto");
                binding.etChatMessage.setText("");
            }
        });

        binding.chipConfirmar.setOnClickListener(v -> enviarMensaje("¡Confirmado! ✅", "accion"));
        binding.chipTarde.setOnClickListener(v -> enviarMensaje("Llego un toque tarde ⏰", "accion"));
        binding.chipNoVoy.setOnClickListener(v -> enviarMensaje("Esta no voy, mala mía ❌", "accion"));
    }

    private void observeViewModel() {
        viewModel.getChatMensajes().observe(getViewLifecycleOwner(), mensajes -> {
            if (mensajes != null) {
                listaMensajes.clear();
                listaMensajes.addAll(mensajes);
                adapter.notifyDataSetChanged();
                binding.rvChatMensajes.scrollToPosition(listaMensajes.size() - 1);
            }
        });

        viewModel.getUltimoPartido().observe(getViewLifecycleOwner(), this::updateMatchInfo);
        
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), loading -> {
            binding.pbChat.setVisibility(loading ? View.VISIBLE : View.GONE);
        });
    }

    private void updateMatchInfo(Partido partido) {
        if (partido == null) return;
        binding.tvChatMatchPlace.setText(partido.getLugar());
        binding.tvChatMatchStatus.setText(partido.getFecha() + " " + partido.getHora());
        
        // Iniciar polling en tiempo real
        AuthManager authManager = AuthManager.getInstance(requireContext());
        viewModel.startRealtimeChat(partido.getId(), authManager.getToken());
    }

    private void enviarMensaje(String texto, String tipo) {
        Partido partido = viewModel.getUltimoPartido().getValue();
        if (partido == null) return;

        AuthManager authManager = AuthManager.getInstance(requireContext());
        Mensaje mensaje = new Mensaje(authManager.getUserName(), texto, partido.getId(), tipo);

        viewModel.sendMessage(mensaje, authManager.getToken());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        viewModel.stopRealtimeChat();
        binding = null;
    }
}
