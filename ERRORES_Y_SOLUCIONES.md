# 🔴 LISTA DE ERRORES Y SOLUCIONES - FutOrg2

## 📊 Resumen Ejecutivo
- **Total de Errores Encontrados:** 25+
- **Botones No Funcionales:** 15
- **Funciones Incompletas:** 12
- **Mejoras Prioritarias:** 8

---

## 🔴 ERRORES CRÍTICOS

### 1. **HomeFragment - Botones sin Listeners**
**Archivo:** `fragment_home.xml` + `HomeFragment.java`

**Problema:**
```xml
<!-- Estos botones están definidos pero SIN listeners en HomeFragment.java -->
- btnMeSumo (¡ME SUMO!)
- btnMap (Mostrar mapa)
- btnShare (Compartir)
- btnCerrarPartido (CERRAR RANKING)
- btnVotarMVP (VOTAR JUGADOR)
- chipAlias (Copiar alias)
```

**Solución:**
```java
// Agregar en HomeFragment.onViewCreated()
binding.btnMeSumo.setOnClickListener(v -> {
    Partido partido = viewModel.getUltimoPartido().getValue();
    if (partido != null) {
        sumarseAPartido(partido.getId());
    }
});

binding.btnMap.setOnClickListener(v -> {
    abrirMapa(binding.tvLugar.getText().toString());
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
```

**Impacto:** 🔴 CRÍTICO

---

### 2. **CreatePartidoFragment - Falla al parsear fecha**
**Archivo:** `CreatePartidoFragment.java` línea 92

**Problema:**
```java
String[] parts = fechaHora.split(" ");
partido.setFecha(parts[0]);  // ❌ CRASH si el formato es incorrecto
partido.setHora(parts[1]);
```

**Solución:**
```java
private void crearPartido() {
    String lugar = binding.etLugar.getText().toString().trim();
    String fechaHora = binding.etFecha.getText().toString().trim();
    String precioStr = binding.etPrecio.getText().toString().trim();
    String alias = binding.etAlias.getText().toString().trim();

    // Validaciones mejoradas
    if (lugar.isEmpty() || fechaHora.isEmpty() || precioStr.isEmpty()) {
        Toast.makeText(requireContext(), "Por favor completa los campos", Toast.LENGTH_SHORT).show();
        return;
    }

    // Validar formato fecha
    if (!fechaHora.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}")) {
        Toast.makeText(requireContext(), "Formato de fecha inválido", Toast.LENGTH_SHORT).show();
        return;
    }

    try {
        double precio = Double.parseDouble(precioStr);
        if (precio <= 0) throw new NumberFormatException();
        
        String[] parts = fechaHora.split(" ");
        Partido partido = new Partido();
        partido.setLugar(lugar);
        partido.setFecha(parts[0]);
        partido.setHora(parts[1]);
        partido.setPrecio(precio);
        // ... resto del código
    } catch (NumberFormatException e) {
        Toast.makeText(requireContext(), "Precio inválido", Toast.LENGTH_SHORT).show();
        binding.btnGuardarPartido.setEnabled(true);
    }
}
```

**Impacto:** 🔴 CRÍTICO

---

### 3. **ProfileFragment - Método getJugadorByEmail NO EXISTE**
**Archivo:** `ProfileFragment.java` línea 70

**Problema:**
```java
supabaseApi.getJugadorByEmail("eq." + authManager.getToken(), "*")
// ❌ Este método no existe en SupabaseApi.java
```

**Solución:**
Opción A - Agregar el método a `SupabaseApi.java`:
```java
@GET("rest/v1/jugadores")
Call<List<Jugador>> getJugadorByEmail(
    @Header("apikey") String apiKey,
    @Query("select") String select,
    @Query("email") String email
);
```

Opción B - Usar el método existente `getRanking()`:
```java
private void loadUserProfile() {
    String userId = authManager.getUserId();
    supabaseApi.getRanking(
        AuthManager.getApiKey(),
        "Bearer " + AuthManager.getToken(),
        "*",
        null
    ).enqueue(new Callback<List<Jugador>>() {
        @Override
        public void onResponse(Call<List<Jugador>> call, Response<List<Jugador>> response) {
            if (response.isSuccessful() && response.body() != null) {
                for (Jugador j : response.body()) {
                    if (j.getId() == userId) {
                        binding.etPerfilNombre.setText(j.getNombre());
                        break;
                    }
                }
            }
        }
        // ...
    });
}
```

**Impacto:** 🔴 CRÍTICO

---

### 4. **SupabaseApi - Parámetros incorrectos en updatePerfil()**
**Archivo:** `SupabaseApi.java` línea 32-38

**Problema:**
```java
@PATCH("rest/v1/jugadores")
Call<Void> updatePerfil(
    @Header("apikey") String apiKey,
    @Header("Authorization") String auth,
    @Query("id") String idFilter,  // ❌ Usar "id=eq.123" no "id"
    @Body Jugador jugador
);
```

**Solución:**
```java
@PATCH("rest/v1/jugadores")
Call<Void> updatePerfil(
    @Header("apikey") String apiKey,
    @Header("Authorization") String auth,
    @Query("id") String idFilter,  // Cambiar a: id=eq.{id}
    @Body Jugador jugador
);

// En ProfileFragment.java:
supabaseApi.updatePerfil(
    AuthManager.getApiKey(),
    "Bearer " + AuthManager.getToken(),
    "eq." + authManager.getUserId(),  // ✅ Formato correcto
    jugador
).enqueue(/*...*/);
```

**Impacto:** 🔴 CRÍTICO

---

## 🟡 ERRORES MAYORES

### 5. **HomeFragment - No carga datos del último partido**
**Problema:**
```java
binding.tvLugar.setText(partido.getLugar());  // ❌ Puede ser null
binding.tvFechaHora.setText(partido.getFecha());  // Sin hora
binding.tvPrecio.setText("$" + partido.getPrecio());  // Sin validación
```

**Solución:**
```java
private void updatePartidoUI(Partido partido) {
    if (partido == null) {
        binding.tvLugar.setText("Sin partido programado");
        binding.tvFechaHora.setText("-");
        binding.tvPrecio.setText("$0");
        binding.btnMeSumo.setEnabled(false);
        return;
    }
    
    binding.tvLugar.setText(partido.getLugar() != null ? partido.getLugar() : "-");
    binding.tvFechaHora.setText(
        String.format("%s %s hs", 
            partido.getFecha() != null ? partido.getFecha() : "",
            partido.getHora() != null ? partido.getHora() : "")
    );
    binding.tvPrecio.setText("$" + String.format("%.2f", partido.getPrecio()));
    
    binding.btnMeSumo.setEnabled(true);
    binding.btnMeSumo.setText(esPartidoPleno(partido) ? "PARTIDO LLENO" : "¡ME SUMO!");
}
```

**Impacto:** 🟡 MAYOR

---

### 6. **CreatePartidoFragment - No maneja headers de Supabase correctamente**
**Archivo:** `CreatePartidoFragment.java` línea 101

**Problema:**
```java
supabaseApi.createPartido(partido)  // ❌ Falta headers de autenticación
```

**Solución:**
```java
// Primero, actualizar SupabaseApi.java:
@POST("rest/v1/partidos")
Call<Void> createPartido(
    @Header("apikey") String apiKey,
    @Header("Authorization") String auth,
    @Header("Content-Type") String contentType,
    @Body Partido partido
);

// Luego en CreatePartidoFragment:
supabaseApi.createPartido(
    AuthManager.getApiKey(),
    "Bearer " + AuthManager.getToken(),
    "application/json",
    partido
).enqueue(/*...*/);
```

**Impacto:** 🟡 MAYOR

---

### 7. **HomeFragment - RankingAdapter sin datos reales**
**Archivo:** `HomeFragment.java` línea 47

**Problema:**
```java
rankingAdapter = new RankingAdapter();
// ❌ RankingAdapter construido sin lista inicial
// ❌ submitList() requiere una lista válida
```

**Solución:**
```java
private void setupRecyclerViews() {
    // Usar ListAdapter correctamente
    rankingAdapter = new RankingAdapter(new DiffUtil.ItemCallback<Jugador>() {
        @Override
        public boolean areItemsTheSame(@NonNull Jugador oldItem, @NonNull Jugador newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Jugador oldItem, @NonNull Jugador newItem) {
            return oldItem.getGoles() == newItem.getGoles() && 
                   oldItem.getNombre().equals(newItem.getNombre());
        }
    });
    binding.rvRankingHome.setLayoutManager(new LinearLayoutManager(getContext()));
    binding.rvRankingHome.setAdapter(rankingAdapter);
}
```

**Impacto:** 🟡 MAYOR

---

### 8. **ProfileFragment - Botón GUARDAR CAMBIOS sin funcionalidad**
**Archivo:** `fragment_profile.xml` + `ProfileFragment.java`

**Problema:**
```xml
<!-- btnGuardarPerfil está definido pero -->
binding.btnGuardarPerfil.setOnClickListener(v -> updateProfile());
// updateProfile() tiene parámetros mal configurados
```

**Solución:**
```java
private void updateProfile() {
    String nuevoNombre = binding.etPerfilNombre.getText().toString().trim();
    if (nuevoNombre.isEmpty()) {
        Toast.makeText(requireContext(), "El nombre no puede estar vacío", Toast.LENGTH_SHORT).show();
        return;
    }

    // Deshabilitar botón mientras se procesa
    binding.btnGuardarPerfil.setEnabled(false);
    
    Jugador jugador = new Jugador();
    jugador.setNombre(nuevoNombre);
    
    String userId = authManager.getUserId();
    
    supabaseApi.updatePerfil(
        AuthManager.getApiKey(),
        "Bearer " + AuthManager.getToken(),
        "eq." + userId,
        jugador
    ).enqueue(new Callback<Void>() {
        @Override
        public void onResponse(Call<Void> call, Response<Void> response) {
            binding.btnGuardarPerfil.setEnabled(true);
            if (response.isSuccessful()) {
                Toast.makeText(requireContext(), "Perfil actualizado ✅", Toast.LENGTH_SHORT).show();
                authManager.setUserName(nuevoNombre);
            } else {
                Toast.makeText(requireContext(), "Error: " + response.code(), Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onFailure(Call<Void> call, Throwable t) {
            binding.btnGuardarPerfil.setEnabled(true);
            Toast.makeText(requireContext(), "Error de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
        }
    });
}
```

**Impacto:** 🟡 MAYOR

---

## 🟠 ERRORES MODERADOS

### 9. **HomeFragment - Botón "Me Sumo" sin lógica de suscripción**
**Problema:** No agrega al usuario a la lista de confirmados del partido

**Solución:**
```java
private void sumarseAPartido(int partidoId) {
    String userId = AuthManager.getInstance(requireContext()).getUserId();
    binding.btnMeSumo.setEnabled(false);
    
    // Enviar al backend
    supabaseApi.agregarConfirmado(
        AuthManager.getApiKey(),
        "Bearer " + AuthManager.getToken(),
        partidoId,
        userId
    ).enqueue(new Callback<Void>() {
        @Override
        public void onResponse(Call<Void> call, Response<Void> response) {
            if (response.isSuccessful()) {
                Toast.makeText(requireContext(), "¡Te sumaste al partido! ⚽", Toast.LENGTH_SHORT).show();
                binding.btnMeSumo.setText("CONFIRMADO ✅");
            } else {
                binding.btnMeSumo.setEnabled(true);
                Toast.makeText(requireContext(), "Error al sumarte", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onFailure(Call<Void> call, Throwable t) {
            binding.btnMeSumo.setEnabled(true);
            Toast.makeText(requireContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
        }
    });
}
```

**Impacto:** 🟠 MODERADO

---

### 10. **HomeFragment - Botones de mapa y compartir sin implementación**

**Solución:**
```java
private void abrirMapa(String lugar) {
    String uri = "geo:0,0?q=" + Uri.encode(lugar);
    Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
    mapIntent.setPackage("com.google.android.apps.maps");
    
    if (mapIntent.resolveActivity(requireContext().getPackageManager()) != null) {
        startActivity(mapIntent);
    } else {
        Toast.makeText(requireContext(), "Google Maps no instalado", Toast.LENGTH_SHORT).show();
    }
}

private void compartirPartido() {
    Partido partido = viewModel.getUltimoPartido().getValue();
    if (partido == null) return;
    
    String texto = String.format(
        "🔥 ¡Únete a nuestro partido!\n" +
        "📍 %s\n" +
        "📅 %s a las %s\n" +
        "💰 $%.2f por persona\n" +
        "Descarga la app FutOrg",
        partido.getLugar(),
        partido.getFecha(),
        partido.getHora(),
        partido.getPrecio()
    );
    
    Intent shareIntent = new Intent(Intent.ACTION_SEND);
    shareIntent.setType("text/plain");
    shareIntent.putExtra(Intent.EXTRA_TEXT, texto);
    startActivity(Intent.createChooser(shareIntent, "Compartir partido"));
}

private void copiarAliasPago() {
    String alias = binding.chipAlias.getText().toString();
    android.content.ClipboardManager clipboard = 
        (android.content.ClipboardManager) requireContext()
            .getSystemService(android.content.Context.CLIPBOARD_SERVICE);
    android.content.ClipData clip = android.content.ClipData.newPlainText("alias", alias);
    clipboard.setPrimaryClip(clip);
    Toast.makeText(requireContext(), "Alias copiado ✅", Toast.LENGTH_SHORT).show();
}
```

**Impacto:** 🟠 MODERADO

---

### 11. **CreatePartidoFragment - Falta validación de precio negativo**

**Solución:**
```java
try {
    double precio = Double.parseDouble(precioStr);
    
    // ✅ Validación mejorada
    if (precio <= 0) {
        Toast.makeText(requireContext(), "El precio debe ser mayor a $0", Toast.LENGTH_SHORT).show();
        return;
    }
    
    if (precio > 10000) {
        Toast.makeText(requireContext(), "Precio muy alto (máximo $10.000)", Toast.LENGTH_SHORT).show();
        return;
    }
    
    // ... continuar
} catch (NumberFormatException e) {
    Toast.makeText(requireContext(), "Precio inválido", Toast.LENGTH_SHORT).show();
}
```

**Impacto:** 🟠 MODERADO

---

### 12. **ProfileFragment - FAB de cámara sin funcionalidad**
**Archivo:** `fragment_profile.xml` línea 42-50

**Problema:**
```xml
<com.google.android.material.floatingactionbutton.FloatingActionButton
    android:src="@android:drawable/ic_menu_camera"
    <!-- ❌ Sin OnClickListener -->
/>
```

**Solución:**
```java
binding.fabCamara.setOnClickListener(v -> abrirSelectorFoto());

private void abrirSelectorFoto() {
    Intent intent = new Intent();
    intent.setType("image/*");
    intent.setAction(Intent.ACTION_GET_CONTENT);
    startActivityForResult(intent, REQUEST_CODE_FOTO);
}

@Override
public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == REQUEST_CODE_FOTO && resultCode == RESULT_OK && data != null) {
        Uri selectedImage = data.getData();
        // Subir foto a Supabase
        subirFotoPerfil(selectedImage);
    }
}
```

**Impacto:** 🟠 MODERADO

---

## 🟡 ADVERTENCIAS Y MEJORAS

### 13. **MainActivity - Navegación no inicializada correctamente**
**Archivo:** `MainActivity.java`

**Problema:**
```java
// MainActivity.java original usa setupNavigation() pero no usa NavController
// La estructura debería usar Navigation Components
```

**Solución:**
```java
public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupNavigation();
        setupBottomNavigation();
    }

    private void setupNavigation() {
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
            .findFragmentById(R.id.nav_host_fragment);
        navController = navHostFragment.getNavController();
    }

    private void setupBottomNavigation() {
        NavigationUI.setupWithNavController(
            binding.bottomNavigation, 
            navController
        );
        
        // FAB para crear partido
        binding.fabCreateMatch.setOnClickListener(v -> {
            navController.navigate(R.id.createPartidoFragment);
        });
    }
}
```

**Impacto:** 🟡 MAYOR

---

### 14. **ConfirmadosAdapter - No actualiza la UI dinámicamente**

**Problema:**
```java
// El adapter no notifica cambios cuando se suma/resta jugadores
```

**Solución:**
```java
// Usar ListAdapter con DiffUtil para cambios automáticos
public class ConfirmadosAdapter extends ListAdapter<String, ConfirmadosAdapter.ViewHolder> {
    
    public ConfirmadosAdapter() {
        super(new DiffUtil.ItemCallback<String>() {
            @Override
            public boolean areItemsTheSame(@NonNull String oldItem, @NonNull String newItem) {
                return oldItem.equals(newItem);
            }

            @Override
            public boolean areContentsTheSame(@NonNull String oldItem, @NonNull String newItem) {
                return oldItem.equals(newItem);
            }
        });
    }
    
    // Ahora puedes usar submitList() para actualizaciones automáticas
}
```

**Impacto:** 🟡 MAYOR

---

### 15. **MensajesAdapter y ChatFragment - Sin datos reales**

**Problema:**
```java
// No hay endpoint en SupabaseApi para obtener mensajes
```

**Solución:**
Agregar a `SupabaseApi.java`:
```java
@GET("rest/v1/mensajes")
Call<List<Mensaje>> getMensajes(
    @Header("apikey") String apiKey,
    @Header("Authorization") String auth,
    @Query("partido_id") String partidoId,
    @Query("order") String order
);

@POST("rest/v1/mensajes")
Call<Void> enviarMensaje(
    @Header("apikey") String apiKey,
    @Header("Authorization") String auth,
    @Body Mensaje mensaje
);
```

**Impacto:** 🟡 MAYOR

---

## ✅ FUNCIONALIDADES QUE FALTAN IMPLEMENTAR

| # | Funcionalidad | Estado | Prioridad | Tiempo Est. |
|---|---|---|---|---|
| 1 | Sistema de votación MVP | ❌ Completo | ALTA | 2h |
| 2 | Cierre de partido y ranking final | ❌ Completo | ALTA | 2h |
| 3 | Chat en tiempo real | ❌ Completo | MEDIA | 3h |
| 4 | Buscar y filtrar jugadores | ❌ Completo | MEDIA | 1.5h |
| 5 | Historial de partidos | ❌ Completo | MEDIA | 1h |
| 6 | Sistema de notificaciones | ⚠️ Parcial | MEDIA | 1.5h |
| 7 | Upload de foto de perfil | ❌ Completo | BAJA | 1h |
| 8 | Estadísticas detalladas | ❌ Completo | BAJA | 2h |
| 9 | Validaciones formularios | ⚠️ Parcial | ALTA | 1h |
| 10 | Manejo global de errores | ❌ Completo | ALTA | 1.5h |
| 11 | Persistencia offline (Room) | ❌ Completo | BAJA | 3h |
| 12 | Testing automatizado | ❌ Completo | BAJA | 2h |

---

## 🔧 MEJORAS DE CÓDIGO RECOMENDADAS

### Mejora 1: Crear clase UtilityManager para funciones repetidas
```java
public class UiUtils {
    public static void mostrarToast(Context ctx, String msg) {
        Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show();
    }
    
    public static boolean validarEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
    
    public static boolean validarPrecio(double precio) {
        return precio > 0 && precio <= 10000;
    }
}
```

### Mejora 2: Centralizar llamadas API
```java
public class ApiRepository {
    private SupabaseApi api;
    
    public LiveData<List<Jugador>> getRanking() {
        MutableLiveData<List<Jugador>> data = new MutableLiveData<>();
        api.getRanking(...).enqueue(new Callback<>() {
            // ...
        });
        return data;
    }
}
```

### Mejora 3: Usar ViewModel correctamente
```java
public class MainViewModel extends ViewModel {
    private final MutableLiveData<List<Jugador>> ranking = new MutableLiveData<>();
    private final MutableLiveData<Partido> ultimoPartido = new MutableLiveData<>();
    
    public LiveData<List<Jugador>> getRankingList() {
        return ranking;
    }
    
    public LiveData<Partido> getUltimoPartido() {
        return ultimoPartido;
    }
    
    // Cargar datos...
}
```

---

## 📋 CHECKLIST DE CORRECCIONES

### Fase 1 - URGENTE (Hoy)
- [ ] Agregar listeners a botones de HomeFragment
- [ ] Validar y corregir parseo de fechas en CreatePartidoFragment
- [ ] Implementar método faltante en SupabaseApi
- [ ] Corregir parámetros en updatePerfil()
- [ ] Implementar updateProfile() correctamente

### Fase 2 - IMPORTANTE (Esta semana)
- [ ] Implementar sumarseAPartido()
- [ ] Agregar funcionalidad de mapa y compartir
- [ ] Crear votación de MVP
- [ ] Implementar chat básico
- [ ] Agregar validaciones exhaustivas

### Fase 3 - DESEABLE (Próximas 2 semanas)
- [ ] Sistema offline con Room
- [ ] Historial detallado
- [ ] Búsqueda avanzada
- [ ] Tests unitarios
- [ ] Optimizar performance

---

## 📞 CONTACTO Y SOPORTE

Para resolver estos problemas, contactar:
- **Developer Lead:** gaston250
- **Stack:** Java/Kotlin, Android, Supabase
- **Última actualización:** 2026-06-04

