package com.example.myapplication.network;

import com.example.myapplication.AuthManager;
import com.example.myapplication.BuildConfig;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Singleton class to manage Retrofit instance with custom configuration.
 * Addresses Section 2: Better exception handling and configuration.
 */
public class RetrofitClient {

    private static Retrofit retrofit = null;
    private static final int TIMEOUT_SECONDS = 30;

    public static void init(AuthManager authManager) {
        if (retrofit == null) {
            // Configure Logging Interceptor
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(BuildConfig.DEBUG ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE);

            // Configure OkHttpClient with timeouts and interceptors
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .addInterceptor(new AuthInterceptor(authManager))
                    .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                    .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                    .writeTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                    .retryOnConnectionFailure(true)
                    .build();

            // Build Retrofit instance
            try {
                retrofit = new Retrofit.Builder()
                        .baseUrl(BuildConfig.SUPABASE_URL)
                        .client(okHttpClient)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
            } catch (Exception e) {
                // Log and handle potential malformed URL or other initialization errors
                e.printStackTrace();
            }
        }
    }

    public static Retrofit getClient() {
        return retrofit;
    }

    public static <T> T createService(Class<T> serviceClass) {
        Retrofit client = getClient();
        if (client == null) {
            throw new RuntimeException("Retrofit client failed to initialize. Call init() first.");
        }
        return client.create(serviceClass);
    }
}
