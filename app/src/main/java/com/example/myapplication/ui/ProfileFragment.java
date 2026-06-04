package com.example.myapplication.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.myapplication.AuthManager;
import com.example.myapplication.LoginActivity;
import com.example.myapplication.databinding.FragmentProfileBinding;
import com.example.myapplication.models.Jugador;
import com.example.myapplication.network.RetrofitClient;
import com.example.myapplication.network.SupabaseApi;
import com.example.myapplication.utils.UiUtils;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {

    private static final int REQUEST_CODE_FOTO = 1001;
    private FragmentProfileBinding binding;
    private MainViewModel viewModel;
    private SupabaseApi supabaseApi;
    private AuthManager authManager;
    private Jugador currentJugador;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        supabaseApi = RetrofitClient.createService(SupabaseApi.class);
        authManager = AuthManager.getInstance(requireContext());

        loadUserProfile();

        binding.fabCamara.setOnClickListener(v -> abrirSelectorFoto());
        binding.btnGuardarPerfil.setOnClickListener(v -> updateProfile());
        
        binding.ivPerfilFotoPro.setOnLongClickListener(v -> {
            logout();
            return true;
        });
    }

    private void loadUserProfile() {
        String email = authManager.getUserEmail();
        if (email == null) return;
        
        binding.etPerfilNombre.setText(authManager.getUserName());
        
        supabaseApi.getJugadorByEmail("*", "email.eq." + email).enqueue(new Callback<List<Jugador>>() {
            @Override
            public void onResponse(@NonNull Call<List<Jugador>> call, @NonNull Response<List<Jugador>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    currentJugador = response.body().get(0);
                    binding.etPerfilNombre.setText(currentJugador.getNombre());
                    binding.tvPerfilSubtitulo.setText(currentJugador.getPosicion() + " • Nivel " + currentJugador.getNivel());
                    binding.chipDisponible.setText(currentJugador.getDisponibilidad());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Jugador>> call, @NonNull Throwable t) {
            }
        });
    }

    private void updateProfile() {
        String nuevoNombre = binding.etPerfilNombre.getText().toString().trim();
        if (nuevoNombre.isEmpty()) {
            UiUtils.mostrarToast(requireContext(), "El nombre no puede estar vacío");
            return;
        }

        binding.btnGuardarPerfil.setEnabled(false);
        
        // Si no cargamos el perfil aún, creamos uno básico, 
        // pero lo ideal es esperar a que currentJugador no sea null
        Jugador jugadorUpdate = currentJugador != null ? currentJugador : new Jugador();
        jugadorUpdate.setNombre(nuevoNombre);
        // Asegurarse de no enviar el ID en el cuerpo del PATCH si es null o problemático,
        // aunque GSON no envía nulls por defecto.
        
        supabaseApi.updatePerfil(
            "eq." + authManager.getUserEmail(),
            jugadorUpdate
        ).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                binding.btnGuardarPerfil.setEnabled(true);
                if (response.isSuccessful()) {
                    UiUtils.mostrarToast(requireContext(), "Perfil actualizado ✅");
                    authManager.setUserName(nuevoNombre);
                } else {
                    UiUtils.mostrarToast(requireContext(), "Error: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                binding.btnGuardarPerfil.setEnabled(true);
                UiUtils.mostrarToast(requireContext(), "Error de red: " + t.getMessage());
            }
        });
    }

    private void logout() {
        authManager.logout();
        Intent intent = new Intent(requireActivity(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
    }

    private void abrirSelectorFoto() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, REQUEST_CODE_FOTO);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_FOTO && resultCode == android.app.Activity.RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            if (selectedImage != null) {
                binding.ivPerfilFotoPro.setImageURI(selectedImage);
                subirFotoPerfil(selectedImage);
            }
        }
    }

    private void subirFotoPerfil(Uri selectedImage) {
        // En una app real, aquí se subiría la imagen a Supabase Storage
        // y se obtendría la URL para actualizar el campo foto_url del jugador.
        UiUtils.mostrarToast(requireContext(), "Foto seleccionada (Pendiente subir a Storage)");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
