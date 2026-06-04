package com.example.myapplication.utils;

import timber.log.Timber;

public class AppLogger {
    
    public static void d(String message, Object... args) {
        Timber.d(message, args);
    }

    public static void i(String message, Object... args) {
        Timber.i(message, args);
    }

    public static void w(String message, Object... args) {
        Timber.w(message, args);
    }

    public static void e(String message, Object... args) {
        Timber.e(message, args);
    }

    public static void e(Throwable t, String message, Object... args) {
        Timber.e(t, message, args);
    }
}
