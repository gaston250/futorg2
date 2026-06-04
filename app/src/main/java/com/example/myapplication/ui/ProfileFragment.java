package com.example.myapplication.ui;

import android.content.Intent;
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

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private MainViewModel viewModel;
    private SupabaseApi supabaseApi;
    private AuthManager authManager;

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

        binding.btnGuardarPerfil.setOnClickListener(v -> updateProfile());
        
        // Asumiendo que hay una forma de hacer logout, la agregaremos aquí.
        // Como el layout es complejo con includes, usaremos un long click en la foto por ahora
        // o buscaremos el botón de logout si estuviera definido.
        binding.ivPerfilFotoPro.setOnLongClickListener(v -> {
            logout();
            return true;
        });
    }

    private void loadUserProfile() {
        String email = authManager.getUserId(); // O el email si lo guardamos
        // En este caso, buscaremos por el email guardado en AuthManager si existe
        // o por el nombre de usuario. Supongamos que guardamos el email.
        
        // Mocking behavior if API fails or for demo
        binding.etPerfilNombre.setText(authManager.getUserName());
        
        supabaseApi.getJugadorByEmail("eq." + authManager.getToken(), "*").enqueue(new Callback<List<Jugador>>() {
            @Override
            public void onResponse(Call<List<Jugador>> call, Response<List<Jugador>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    Jugador jugador = response.body().get(0);
                    binding.etPerfilNombre.setText(jugador.getNombre());
                    binding.tvPerfilSubtitulo.setText(jugador.getPosicion() + " • Nivel " + jugador.getNivel());
                    binding.chipDisponible.setText(jugador.getDisponibilidad());
                }
            }

            @Override
            public void onFailure(Call<List<Jugador>> call, Throwable t) {
                // Ignore error, keep local data
            }
        });
    }

    private void updateProfile() {
        String nuevoNombre = binding.etPerfilNombre.getText().toString().trim();
        if (nuevoNombre.isEmpty()) return;

        Jugador jugador = new Jugador();
        jugador.setNombre(nuevoNombre);

        supabaseApi.updatePerfil("eq." + authManager.getToken(), jugador).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(requireContext(), "Perfil actualizado", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(requireContext(), "Error al actualizar", Toast.LENGTH_SHORT).show();
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
