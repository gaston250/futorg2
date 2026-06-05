package com.example.myapplication.ui;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.myapplication.AuthManager;
import com.example.myapplication.R;
import com.example.myapplication.databinding.FragmentCreatePartidoBinding;
import com.example.myapplication.models.Partido;
import com.example.myapplication.network.RetrofitClient;
import com.example.myapplication.network.SupabaseApi;
import com.example.myapplication.utils.UiUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreatePartidoFragment extends Fragment {

    private FragmentCreatePartidoBinding binding;
    private MainViewModel viewModel;
    private Calendar calendar = Calendar.getInstance();
    private SupabaseApi supabaseApi;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCreatePartidoBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        supabaseApi = RetrofitClient.createService(SupabaseApi.class);

        setupDateTimePickers();
        
        binding.btnGuardarPartido.setOnClickListener(v -> crearPartido());

        viewModel.getIsLoading().observe(getViewLifecycleOwner(), loading -> {
            binding.pbCreate.setVisibility(loading ? View.VISIBLE : View.GONE);
            binding.btnGuardarPartido.setEnabled(!loading);
        });
    }

    private void setupDateTimePickers() {
        binding.etFecha.setFocusable(false);
        binding.etFecha.setOnClickListener(v -> {
            new DatePickerDialog(requireContext(), (view, year, month, dayOfMonth) -> {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                
                new TimePickerDialog(requireContext(), (view1, hourOfDay, minute) -> {
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    calendar.set(Calendar.MINUTE, minute);
                    
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
                    binding.etFecha.setText(sdf.format(calendar.getTime()));
                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
        });
    }

    private void crearPartido() {
        String lugar = binding.etLugar.getText().toString().trim();
        String fechaHora = binding.etFecha.getText().toString().trim();
        String precioStr = binding.etPrecio.getText().toString().trim();
        String alias = binding.etAlias.getText().toString().trim();

        // Validaciones mejoradas
        if (lugar.isEmpty() || fechaHora.isEmpty() || precioStr.isEmpty()) {
            UiUtils.mostrarToast(requireContext(), "Por favor completa los campos");
            return;
        }

        // Validar formato fecha (yyyy-MM-dd HH:mm)
        if (!fechaHora.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}")) {
            UiUtils.mostrarToast(requireContext(), "Formato de fecha inválido");
            return;
        }

        try {
            double precio = Double.parseDouble(precioStr);

            // Validación mejorada del rango de precio usando UiUtils
            if (!UiUtils.validarPrecio(precio)) {
                if (precio <= 0) {
                    UiUtils.mostrarToast(requireContext(), "El precio debe ser mayor a $0");
                } else {
                    UiUtils.mostrarToast(requireContext(), "Precio muy alto (máximo $10.000)");
                }
                return;
            }

            String[] parts = fechaHora.split(" ");
            int maxJugadores = binding.rbF5.isChecked() ? 10 : 14;

            Partido partido = new Partido();
            partido.setLugar(lugar);
            partido.setFecha(parts[0]);
            partido.setHora(parts[1]);
            partido.setPrecio(precio);
            partido.setMaxJugadores(maxJugadores);
            partido.setAliasPago(alias);
            partido.setOrganizadorId(AuthManager.getInstance(requireContext()).getUserId());

            binding.btnGuardarPartido.setEnabled(false);
            
            supabaseApi.createPartido(
                AuthManager.getApiKey(),
                "Bearer " + AuthManager.getInstance(requireContext()).getToken(),
                partido
            ).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                    if (response.isSuccessful()) {
                        UiUtils.mostrarToast(requireContext(), "Partido creado con éxito");
                        Navigation.findNavController(requireView()).navigateUp();
                    } else {
                        UiUtils.mostrarToast(requireContext(), "Error al crear partido: " + response.code());
                        binding.btnGuardarPartido.setEnabled(true);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                    UiUtils.mostrarToast(requireContext(), "Error de red: " + t.getMessage());
                    binding.btnGuardarPartido.setEnabled(true);
                }
            });
        } catch (NumberFormatException e) {
            UiUtils.mostrarToast(requireContext(), "Precio inválido");
            binding.btnGuardarPartido.setEnabled(true);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
