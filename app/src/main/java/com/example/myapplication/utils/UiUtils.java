package com.example.myapplication.utils;

import android.content.Context;
import android.widget.Toast;

public class UiUtils {
    
    public static void mostrarToast(Context ctx, String msg) {
        if (ctx != null) {
            Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show();
        }
    }
    
    public static boolean validarEmail(String email) {
        return email != null && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
    
    public static boolean validarPrecio(double precio) {
        return precio > 0 && precio <= 10000;
    }
}
