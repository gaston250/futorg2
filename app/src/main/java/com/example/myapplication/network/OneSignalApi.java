package com.example.myapplication.network;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface OneSignalApi {
    @POST("notifications")
    Call<Void> sendNotification(
        @Header("Authorization") String auth,
        @Body RequestBody body
    );
}
