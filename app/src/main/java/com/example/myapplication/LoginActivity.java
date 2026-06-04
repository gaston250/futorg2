package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.network.RetrofitClient;
import com.example.myapplication.network.SupabaseApi;
import com.example.myapplication.BuildConfig;
import com.example.myapplication.R;
import com.example.myapplication.utils.UiUtils;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONObject;

import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {

    private static final String SUPABASE_URL = BuildConfig.SUPABASE_URL;
    private static final String SUPABASE_KEY = BuildConfig.SUPABASE_KEY;
    private SupabaseApi supabaseApi;

    private boolean isLoginMode = true;

    private TextInputLayout tilUsername;
    private TextInputEditText etUsername, etEmail, etPassword;
    private Button btnLogin;
    private TextView tvToggleMode, tvLoginTitle, tvForgotPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setupRetrofit();
        initViews();

        tvToggleMode.setOnClickListener(v -> toggleMode());

        btnLogin.setOnClickListener(v -> {
            String email = Objects.requireNonNull(etEmail.getText()).toString().trim();
            String pass = Objects.requireNonNull(etPassword.getText()).toString().trim();

            if (isLoginMode) {
                if (!email.isEmpty() && !pass.isEmpty()) {
                    if (UiUtils.validarEmail(email)) {
                        iniciarSesionReal(email, pass);
                    } else {
                        UiUtils.mostrarToast(this, "Email inválido");
                    }
                } else {
                    UiUtils.mostrarToast(this, "Completa Email y Contraseña");
                }
            } else {
                String username = Objects.requireNonNull(etUsername.getText()).toString().trim();
                if (!email.isEmpty() && !pass.isEmpty() && !username.isEmpty()) {
                    if (UiUtils.validarEmail(email)) {
                        registrarUsuarioReal(username, email, pass);
                    } else {
                        UiUtils.mostrarToast(this, "Email inválido");
                    }
                } else {
                    UiUtils.mostrarToast(this, "Completa todos los campos");
                }
            }
        });

        tvForgotPassword.setOnClickListener(v -> showForgotPasswordDialog());
    }

    private void initViews() {
        tilUsername = findViewById(R.id.tilUsername);
        etUsername = findViewById(R.id.etLoginUsername);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvToggleMode = findViewById(R.id.tvToggleMode);
        tvLoginTitle = findViewById(R.id.tvLoginTitle);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        findViewById(R.id.btnGoogleLogin).setOnClickListener(v -> startMainActivity("google-id", "google_user@gmail.com", "Google User"));
    }

    private void toggleMode() {
        isLoginMode = !isLoginMode;
        if (isLoginMode) {
            tvLoginTitle.setText("Bienvenido");
            tilUsername.setVisibility(View.GONE);
            btnLogin.setText("INGRESAR");
            tvToggleMode.setText("¿No tienes cuenta? Regístrate");
            tvForgotPassword.setVisibility(View.VISIBLE);
        } else {
            tvLoginTitle.setText("Crea tu cuenta");
            tilUsername.setVisibility(View.VISIBLE);
            btnLogin.setText("REGISTRARME");
            tvToggleMode.setText("¿Ya tienes cuenta? Ingresa");
            tvForgotPassword.setVisibility(View.GONE);
        }
    }

    private void setupRetrofit() {
        AuthManager authManager = AuthManager.getInstance(this);
        RetrofitClient.init(authManager);
        supabaseApi = RetrofitClient.createService(SupabaseApi.class);
    }

    private void registrarUsuarioReal(String nombre, String email, String password) {
        try {
            JSONObject json = new JSONObject();
            json.put("email", email);
            json.put("password", password);
            
            JSONObject metadata = new JSONObject();
            metadata.put("full_name", nombre);
            json.put("data", metadata);

            RequestBody body = RequestBody.create(MediaType.parse("application/json"), json.toString());

            supabaseApi.signUp(body).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        UiUtils.mostrarToast(LoginActivity.this, "Registro exitoso. ¡Bienvenido!");
                        toggleMode();
                    } else {
                        UiUtils.mostrarToast(LoginActivity.this, "Error en el registro");
                    }
                }
                @Override public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {}
            });
        } catch (Exception ignored) {}
    }

    private void iniciarSesionReal(String email, String password) {
        try {
            JSONObject json = new JSONObject();
            json.put("email", email);
            json.put("password", password);
            RequestBody body = RequestBody.create(MediaType.parse("application/json"), json.toString());

            supabaseApi.login(body).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        try {
                            String result = response.body().string();
                            JSONObject jsonResponse = new JSONObject(result);
                            String accessToken = jsonResponse.optString("access_token");
                            JSONObject user = jsonResponse.optJSONObject("user");
                            if (user != null && accessToken != null) {
                                String uid = user.getString("id");
                                String fullName = user.optJSONObject("user_metadata") != null ? 
                                        user.getJSONObject("user_metadata").optString("full_name", email.split("@")[0]) : 
                                        email.split("@")[0];
                                
                                // Persistir sesión
                                AuthManager.getInstance(LoginActivity.this).saveSession(accessToken, uid, email, fullName);
                                
                                // Inicializar Retrofit con el nuevo token
                                RetrofitClient.init(AuthManager.getInstance(LoginActivity.this));
                                
                                startMainActivity(uid, email, fullName);
                            }
                        } catch (Exception e) {
                            Log.e("Login", "Error parseando respuesta", e);
                        }
                    } else {
                        UiUtils.mostrarToast(LoginActivity.this, "Error al ingresar");
                    }
                }
                @Override public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {}
            });
        } catch (Exception ignored) {}
    }

    private void showForgotPasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Recuperar Contraseña");
        
        final View customLayout = getLayoutInflater().inflate(R.layout.dialog_forgot_password, null);
        builder.setView(customLayout);

        builder.setPositiveButton("Enviar", (dialog, which) -> {
            TextInputEditText etResetEmail = customLayout.findViewById(R.id.etResetEmail);
            String email = Objects.requireNonNull(etResetEmail.getText()).toString();
            if (UiUtils.validarEmail(email)) {
                recuperarPassword(email);
            } else {
                UiUtils.mostrarToast(this, "Email inválido");
            }
        });
        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }

    private void recuperarPassword(String email) {
        try {
            JSONObject json = new JSONObject();
            json.put("email", email);
            RequestBody body = RequestBody.create(MediaType.parse("application/json"), json.toString());
            supabaseApi.resetPassword(body).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                    UiUtils.mostrarToast(LoginActivity.this, "Si el correo existe, recibirás un link");
                }
                @Override public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {}
            });
        } catch (Exception ignored) {}
    }

    private void startMainActivity(String uid, String email, String username) {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
