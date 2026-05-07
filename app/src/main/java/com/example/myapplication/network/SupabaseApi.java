package com.example.myapplication.network;

import com.example.myapplication.models.Jugador;
import com.example.myapplication.models.Partido;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface SupabaseApi {
    @GET("rest/v1/jugadores")
    Call<List<Jugador>> getRanking(
        @Header("apikey") String apiKey,
        @Header("Authorization") String auth,
        @Query("select") String select,
        @Query("order") String order
    );

    @POST("rest/v1/partidos")
    Call<Void> createPartido(
        @Header("apikey") String apiKey,
        @Header("Authorization") String auth,
        @Body Partido partido
    );

    @PATCH("rest/v1/jugadores")
    Call<Void> updatePerfil(
        @Header("apikey") String apiKey,
        @Header("Authorization") String auth,
        @Query("id") String idFilter,
        @Body Jugador jugador
    );
}
