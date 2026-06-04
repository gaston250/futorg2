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

        if (lugar.isEmpty() || fechaHora.isEmpty() || precioStr.isEmpty()) {
            Toast.makeText(requireContext(), "Por favor completa los campos obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        double precio = Double.parseDouble(precioStr);
        int maxJugadores = binding.rbF5.isChecked() ? 10 : 14;

        Partido partido = new Partido();
        partido.setLugar(lugar);
        String[] parts = fechaHora.split(" ");
        partido.setFecha(parts[0]);
        partido.setHora(parts[1]);
        partido.setPrecio(precio);
        partido.setMaxJugadores(maxJugadores);
        partido.setAliasPago(alias);
        partido.setOrganizadorId(AuthManager.getInstance(requireContext()).getUserId());

        binding.btnGuardarPartido.setEnabled(false);
        supabaseApi.createPartido(partido).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(requireContext(), "Partido creado con éxito", Toast.LENGTH_SHORT).show();
                    Navigation.findNavController(requireView()).navigateUp();
                } else {
                    Toast.makeText(requireContext(), "Error al crear partido: " + response.code(), Toast.LENGTH_SHORT).show();
                    binding.btnGuardarPartido.setEnabled(true);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(requireContext(), "Error de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                binding.btnGuardarPartido.setEnabled(true);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
