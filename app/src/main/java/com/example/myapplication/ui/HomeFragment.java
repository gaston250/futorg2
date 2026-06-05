package com.example.myapplication.ui;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.myapplication.R;
import com.example.myapplication.AuthManager;
import com.example.myapplication.RankingAdapter;
import com.example.myapplication.ConfirmadosAdapter;
import com.example.myapplication.databinding.FragmentHomeBinding;
import com.example.myapplication.models.Jugador;
import com.example.myapplication.models.Partido;
import com.example.myapplication.network.SupabaseApi;
import com.example.myapplication.utils.UiUtils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
        setupClickListeners();
        observeViewModel();
    }

    private void setupClickListeners() {
        binding.btnMeSumo.setOnClickListener(v -> {
            Partido partido = viewModel.getUltimoPartido().getValue();
            if (partido != null) {
                sumarseAPartido(partido.getId());
            }
        });

        binding.btnMap.setOnClickListener(v -> {
            Partido partido = viewModel.getUltimoPartido().getValue();
            if (partido != null && partido.getLugar() != null) {
                abrirMapa(partido.getLugar());
            }
        });

        binding.btnShare.setOnClickListener(v -> {
            compartirPartido();
        });

        binding.btnCerrarPartido.setOnClickListener(v -> {
            cerrarPartidoYMostrarRanking();
        });

        binding.btnVotarMVP.setOnClickListener(v -> {
            mostrarDialogoVotacionMVP();
        });

        binding.chipAlias.setOnClickListener(v -> {
            copiarAliasPago();
        });
    }

    private void sumarseAPartido(int partidoId) {
        AuthManager authManager = AuthManager.getInstance(requireContext());
        String userName = authManager.getUserName();
        binding.btnMeSumo.setEnabled(false);

        viewModel.sumarseAPartido(partidoId, userName, authManager.getToken()).observe(getViewLifecycleOwner(), success -> {
            if (success) {
                UiUtils.mostrarToast(requireContext(), "¡Te sumaste al partido! ⚽");
                binding.btnMeSumo.setText("CONFIRMADO ✅");
                viewModel.refreshUltimoPartido();
            } else {
                binding.btnMeSumo.setEnabled(true);
                UiUtils.mostrarToast(requireContext(), "Error al sumarte");
            }
        });
    }

    private void abrirMapa(String lugar) {
        String uri = "geo:0,0?q=" + Uri.encode(lugar);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        mapIntent.setPackage("com.google.android.apps.maps");

        if (mapIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
            startActivity(mapIntent);
        } else {
            UiUtils.mostrarToast(getContext(), "Google Maps no instalado");
        }
    }

    private void compartirPartido() {
        Partido partido = viewModel.getUltimoPartido().getValue();
        if (partido == null) return;

        String texto = String.format(
                "🔥 ¡Únete a nuestro partido!\n" +
                "📍 %s\n" +
                "📅 %s a las %s\n" +
                "💰 $%.0f por persona\n" +
                "Descarga la app FutOrg",
                partido.getLugar() != null ? partido.getLugar() : "-",
                partido.getFecha() != null ? partido.getFecha() : "-",
                partido.getHora() != null ? partido.getHora() : "-",
                partido.getPrecio()
        );

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, texto);
        startActivity(Intent.createChooser(shareIntent, "Compartir partido"));
    }

    private void cerrarPartidoYMostrarRanking() {
        Partido partido = viewModel.getUltimoPartido().getValue();
        if (partido == null) return;

        try {
            JSONObject json = new JSONObject();
            json.put("is_closed", true);
            json.put("estado", "finalizado");
            
            RequestBody body = RequestBody.create(json.toString(), MediaType.get("application/json"));
            
            viewModel.closeMatch(partido.getId(), body).observe(getViewLifecycleOwner(), success -> {
                if (Boolean.TRUE.equals(success)) {
                    UiUtils.mostrarToast(getContext(), "Partido finalizado");
                    viewModel.refreshUltimoPartido();
                    binding.cardVotacionMVP.setVisibility(View.VISIBLE);
                }
            });
        } catch (Exception e) {
            Log.e("HomeFragment", "Error cerrando partido", e);
        }
    }

    private void mostrarDialogoVotacionMVP() {
        List<Jugador> jugadores = viewModel.getRankingList().getValue();
        if (jugadores == null || jugadores.isEmpty()) {
            UiUtils.mostrarToast(requireContext(), "No hay jugadores para votar");
            return;
        }

        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_votacion_mvp, null);
        androidx.recyclerview.widget.RecyclerView rv = dialogView.findViewById(R.id.rvVotacionMVP);
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));
        
        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .setTitle("¿Quién fue el MVP?")
                .setNegativeButton("Cancelar", null)
                .create();

        com.example.myapplication.VotacionAdapter votacionAdapter = new com.example.myapplication.VotacionAdapter(jugadores, jugador -> {
            enviarVotoMVP(jugador.getNombre());
            dialog.dismiss();
        });
        
        rv.setAdapter(votacionAdapter);
        dialog.show();
    }

    private void enviarVotoMVP(String nombreJugador) {
        Partido partido = viewModel.getUltimoPartido().getValue();
        if (partido == null) return;

        try {
            com.example.myapplication.models.Voto voto = new com.example.myapplication.models.Voto();
            voto.setVotoJugadorNombre(nombreJugador);
            voto.setVotoPorNombre(AuthManager.getInstance(requireContext()).getUserName());
            voto.setPartidoId(partido.getId());

            viewModel.voteMVP(voto).observe(getViewLifecycleOwner(), success -> {
                if (Boolean.TRUE.equals(success)) {
                    UiUtils.mostrarToast(getContext(), "Voto registrado para " + nombreJugador);
                    binding.cardVotacionMVP.setVisibility(View.GONE);
                }
            });
        } catch (Exception e) {
            Log.e("HomeFragment", "Error enviando voto", e);
        }
    }

    private void copiarAliasPago() {
        Partido partido = viewModel.getUltimoPartido().getValue();
        if (partido == null || partido.getAliasPago() == null) return;

        ClipboardManager clipboard = (ClipboardManager) requireContext().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Alias Pago", partido.getAliasPago());
        clipboard.setPrimaryClip(clip);

        UiUtils.mostrarToast(getContext(), "Alias copiado: " + partido.getAliasPago() + " ✅");
    }

    private void setupRecyclerViews() {
        // Usar ListAdapter con callback explícito para evitar problemas de datos iniciales
        rankingAdapter = new RankingAdapter(new androidx.recyclerview.widget.DiffUtil.ItemCallback<Jugador>() {
            @Override
            public boolean areItemsTheSame(@NonNull Jugador oldItem, @NonNull Jugador newItem) {
                return java.util.Objects.equals(oldItem.getId(), newItem.getId());
            }

            @Override
            public boolean areContentsTheSame(@NonNull Jugador oldItem, @NonNull Jugador newItem) {
                return oldItem.getGoles() == newItem.getGoles() && 
                       java.util.Objects.equals(oldItem.getNombre(), newItem.getNombre());
            }
        });

        rankingAdapter.setOnItemClickListener(jugador -> {
            UiUtils.mostrarToast(getContext(), "Jugador: " + jugador.getNombre() + " - Goles: " + jugador.getGoles());
        });
        binding.rvRankingHome.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvRankingHome.setAdapter(rankingAdapter);

        confirmadosAdapter = new ConfirmadosAdapter(false, (nombreJugador, pagado, totalPagados) -> {
            actualizarEstadoPago(nombreJugador, pagado);
        });
        binding.rvConfirmados.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.rvConfirmados.setAdapter(confirmadosAdapter);
    }

    private void actualizarEstadoPago(String nombreJugador, boolean pagado) {
        Partido partido = viewModel.getUltimoPartido().getValue();
        if (partido == null) return;

        try {
            JSONObject json = new JSONObject();
            json.put("pagado", pagado);
            
            RequestBody body = RequestBody.create(json.toString(), MediaType.get("application/json"));
            com.example.myapplication.network.SupabaseApi api = com.example.myapplication.network.RetrofitClient.createService(com.example.myapplication.network.SupabaseApi.class);
            
            // Usamos PATCH para actualizar el estado de pago del jugador en ese partido
            // En Supabase: rest/v1/confirmados?partido_id=eq.X&jugador_nombre=eq.Y
            api.updatePagoConfirmado("eq." + partido.getId(), "eq." + nombreJugador, body).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                    if (response.isSuccessful()) {
                        cargarConfirmados(partido.getId());
                    }
                }
                @Override public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                    Log.e("HomeFragment", "Error onFailure actualizando pago", t);
                }
            });
        } catch (Exception e) {
            Log.e("HomeFragment", "Error actualizando pago", e);
        }
    }

    private void observeViewModel() {
        viewModel.getRankingList().observe(getViewLifecycleOwner(), ranking -> {
            if (ranking != null && !ranking.isEmpty()) {
                rankingAdapter.submitList(new ArrayList<>(ranking));
            } else {
                rankingAdapter.submitList(new ArrayList<>());
            }
        });

        viewModel.getUltimoPartido().observe(getViewLifecycleOwner(), this::updatePartidoUI);
        
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), loading -> 
            binding.pbHome.setVisibility(loading ? View.VISIBLE : View.GONE)
        );

        viewModel.getConfirmadosList().observe(getViewLifecycleOwner(), confirmados -> {
            if (confirmados != null) {
                listaConfirmadosNombres.clear();
                List<String> pagados = new ArrayList<>();
                for (Jugador j : confirmados) {
                    listaConfirmadosNombres.add(j.getNombre());
                    if (j.isPagado()) {
                        pagados.add(j.getNombre());
                    }
                }
                confirmadosAdapter.submitList(new ArrayList<>(listaConfirmadosNombres));
                confirmadosAdapter.setPagados(pagados);
                
                binding.tvCountConfirmados.setText("Confirmados (" + confirmados.size() + "/10)");

                // Calcular recaudado
                double totalRecaudado = 0;
                Partido actual = viewModel.getUltimoPartido().getValue();
                for (Jugador j : confirmados) {
                    if (j.isPagado() && actual != null) {
                        totalRecaudado += actual.getPrecio();
                    }
                }
                binding.tvTotalRecaudado.setText(String.format(Locale.getDefault(), "Total: $%.0f", totalRecaudado));

                // Actualizar botón Me Sumo si el partido está lleno
                if (actual != null) {
                    if (confirmados.size() >= actual.getMaxJugadores()) {
                        binding.btnMeSumo.setEnabled(false);
                        binding.btnMeSumo.setText("PARTIDO LLENO");
                    } else {
                        // Check if user is already in the list
                        boolean alreadyJoined = false;
                        String currentUserName = AuthManager.getInstance(requireContext()).getUserName();
                        for (String n : listaConfirmadosNombres) {
                            if (n.equals(currentUserName)) {
                                alreadyJoined = true;
                                break;
                            }
                        }
                        if (alreadyJoined) {
                            binding.btnMeSumo.setEnabled(false);
                            binding.btnMeSumo.setText("CONFIRMADO ✅");
                        } else {
                            binding.btnMeSumo.setEnabled(true);
                            binding.btnMeSumo.setText("¡ME SUMO!");
                        }
                    }
                }
            }
        });
    }

    private void updatePartidoUI(Partido partido) {
        if (partido == null) {
            binding.tvLugar.setText("Sin partido programado");
            binding.tvFechaHora.setText("-");
            binding.tvPrecio.setText("$0");
            binding.btnMeSumo.setEnabled(false);
            binding.chipAlias.setVisibility(View.GONE);
            binding.btnCerrarPartido.setVisibility(View.GONE);
            return;
        }

        AuthManager authManager = AuthManager.getInstance(requireContext());
        boolean isOrganizer = authManager.getUserId().equals(partido.getOrganizadorId());
        
        // Re-inicializar adapter con permisos de admin si corresponde
        confirmadosAdapter = new ConfirmadosAdapter(isOrganizer, (nombreJugador, pagado, totalPagados) -> {
            actualizarEstadoPago(nombreJugador, pagado);
        });
        binding.rvConfirmados.setAdapter(confirmadosAdapter);

        binding.tvLugar.setText(partido.getLugar() != null ? partido.getLugar() : "-");
        binding.tvFechaHora.setText(
            String.format("%s %s hs", 
                partido.getFecha() != null ? partido.getFecha() : "",
                partido.getHora() != null ? partido.getHora() : "")
        );
        binding.tvPrecio.setText(String.format(Locale.getDefault(), "$%.0f", partido.getPrecio()));
        
        binding.btnMeSumo.setEnabled(true);
        
        if (partido.getAliasPago() == null || partido.getAliasPago().isEmpty()) {
            binding.chipAlias.setVisibility(View.GONE);
        } else {
            binding.chipAlias.setVisibility(View.VISIBLE);
        }

        if ("finalizado".equals(partido.getEstado())) {
            binding.btnMeSumo.setVisibility(View.GONE);
            binding.cardVotacionMVP.setVisibility(View.VISIBLE);
            binding.btnCerrarPartido.setVisibility(View.GONE);
        } else {
            binding.btnMeSumo.setVisibility(View.VISIBLE);
            binding.cardVotacionMVP.setVisibility(View.GONE);
            binding.btnCerrarPartido.setVisibility(isOrganizer ? View.VISIBLE : View.GONE);
        }

        // Cargar confirmados del partido
        cargarConfirmados(partido.getId());
    }

    private void cargarConfirmados(int partidoId) {
        viewModel.fetchConfirmados(partidoId);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
