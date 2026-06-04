package com.example.myapplication.network;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.lang.ref.WeakReference;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Base Callback to handle common HTTP errors and network failures.
 * Uses WeakReference to avoid memory leaks.
 */
public abstract class BaseCallback<T> implements Callback<T> {

    private final WeakReference<Context> contextRef;

    public BaseCallback(Context context) {
        this.contextRef = new WeakReference<>(context);
    }

    @Override
    public void onResponse(@NonNull Call<T> call, @NonNull Response<T> response) {
        if (response.isSuccessful()) {
            onSuccess(response.body());
        } else {
            handleHttpError(response.code());
        }
    }

    @Override
    public void onFailure(@NonNull Call<T> call, @NonNull Throwable t) {
        String errorMessage;
        if (t instanceof IOException) {
            errorMessage = "Error de red. Revisa tu conexión.";
        } else {
            errorMessage = "Error inesperado: " + t.getMessage();
        }
        showError(errorMessage);
        onError(t);
    }

    private void handleHttpError(int errorCode) {
        String message;
        switch (errorCode) {
            case 401:
                message = "Sesión no autorizada. Por favor, ingresa de nuevo.";
                break;
            case 403:
                message = "No tienes permiso para realizar esta acción.";
                break;
            case 404:
                message = "Recurso no encontrado.";
                break;
            case 500:
                message = "Error en el servidor. Inténtalo más tarde.";
                break;
            default:
                message = "Error del servidor (" + errorCode + ")";
                break;
        }
        showError(message);
        onHttpError(errorCode);
    }

    private void showError(String message) {
        Context context = contextRef.get();
        if (context != null) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }
    }

    public abstract void onSuccess(T result);
    
    public void onError(Throwable t) {}
    public void onHttpError(int errorCode) {}
}
