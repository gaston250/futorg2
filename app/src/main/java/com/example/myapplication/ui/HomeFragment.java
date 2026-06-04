package com.example.myapplication.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.myapplication.RankingAdapter;
import com.example.myapplication.ConfirmadosAdapter;
import com.example.myapplication.databinding.FragmentHomeBinding;
import com.example.myapplication.models.Partido;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private MainViewModel viewModel;
    private RankingAdapter rankingAdapter;
    private ConfirmadosAdapter confirmadosAdapter;
    private final List<String> listaConfirmadosNombres = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        setupRecyclerViews();
        observeViewModel();
    }

    private void setupRecyclerViews() {
        rankingAdapter = new RankingAdapter();
        binding.rvRankingHome.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvRankingHome.setAdapter(rankingAdapter);

        confirmadosAdapter = new ConfirmadosAdapter(listaConfirmadosNombres, false, null);
        binding.rvConfirmados.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.rvConfirmados.setAdapter(confirmadosAdapter);
    }

    private void observeViewModel() {
        viewModel.getRankingList().observe(getViewLifecycleOwner(), ranking -> {
            if (ranking != null) {
                rankingAdapter.submitList(ranking);
            }
        });

        viewModel.getUltimoPartido().observe(getViewLifecycleOwner(), this::updatePartidoUI);
    }

    private void updatePartidoUI(Partido partido) {
        if (partido == null) return;
        
        binding.tvLugar.setText(partido.getLugar());
        binding.tvFechaHora.setText(partido.getFecha());
        binding.tvPrecio.setText("$" + partido.getPrecio());
        
        if (partido.getAliasPago() == null || partido.getAliasPago().isEmpty()) {
            binding.chipAlias.setVisibility(View.GONE);
        } else {
            binding.chipAlias.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
