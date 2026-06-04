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
import com.example.myapplication.PartidosAdapter;
import com.example.myapplication.databinding.FragmentPartidosBinding;
import com.example.myapplication.models.Partido;
import com.example.myapplication.network.RetrofitClient;
import com.example.myapplication.network.SupabaseApi;
import com.example.myapplication.utils.UiUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PartidosFragment extends Fragment {

    private FragmentPartidosBinding binding;
    private MainViewModel viewModel;
    private List<Partido> listaPartidos = new ArrayList<>();
    private PartidosAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentPartidosBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        adapter = new PartidosAdapter(listaPartidos, partido -> {
            // Mostrar detalle
            UiUtils.mostrarToast(getContext(), "Partido en " + partido.getLugar());
        });
        binding.rvMisPartidos.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvMisPartidos.setAdapter(adapter);
        
        observeViewModel();
        cargarMisPartidos();
    }

    private void observeViewModel() {
        viewModel.getMisPartidosList().observe(getViewLifecycleOwner(), partidos -> {
            if (partidos != null) {
                listaPartidos.clear();
                listaPartidos.addAll(partidos);
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void cargarMisPartidos() {
        AuthManager authManager = AuthManager.getInstance(requireContext());
        viewModel.fetchMisPartidos(authManager.getUserName());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
