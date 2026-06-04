package com.example.myapplication.network;

import com.example.myapplication.models.Clima;
import com.example.myapplication.models.Jugador;
import com.example.myapplication.models.Mensaje;
import com.example.myapplication.models.Partido;

import java.util.List;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface SupabaseApi {
    
    @GET("rest/v1/jugadores")
    Call<List<Jugador>> getRanking(
        @Query("select") String select,
        @Query("order") String order
    );

    @POST("rest/v1/partidos")
    Call<Void> createPartido(@Body Partido partido);

    @GET("rest/v1/partidos")
    Call<List<Partido>> getUltimoPartido(
        @Query("select") String select,
        @Query("order") String order,
        @Query("limit") int limit
    );

    @POST("rest/v1/jugadores")
    Call<Void> createJugador(@Body Jugador jugador);

    @PATCH("rest/v1/jugadores")
    Call<Void> updatePerfil(
            @Query("email") String emailFilter,
            @Body Jugador jugador
    );

    @GET("rest/v1/jugadores")
    Call<List<Jugador>> getJugadorByEmail(
        @Query("select") String select,
        @Query("email") String emailFilter
    );

    // CHAT METHODS
    @GET("rest/v1/mensajes")
    Call<List<Mensaje>> getMensajes(
            @Query("partido_id") String matchIdFilter,
            @Query("select") String select,
            @Query("order") String order
    );

    @POST("rest/v1/mensajes")
    Call<Void> enviarMensaje(@Body Mensaje mensaje);

    // AUTH METHODS
    @POST("auth/v1/signup")
    Call<ResponseBody> signUp(@Body RequestBody body);

    @POST("auth/v1/token?grant_type=password")
    Call<ResponseBody> login(@Body RequestBody body);

    @POST("auth/v1/recover")
    Call<Void> resetPassword(@Body RequestBody body);

    // WEATHER METHODS
    @GET("rest/v1/clima")
    Call<List<Clima>> getClimaActual(
            @Query("select") String select,
            @Query("order") String order,
            @Query("limit") int limit
    );

    // CONFIRMADOS METHODS
    @GET("rest/v1/confirmados")
    Call<List<Jugador>> getConfirmados(
            @Query("select") String select,
            @Query("partido_id") String partidoIdFilter
    );

    @POST("rest/v1/confirmados")
    Call<Void> agregarConfirmado(@Body RequestBody body);

    @PATCH("rest/v1/confirmados")
    Call<Void> updatePagoConfirmado(
            @Query("partido_id") String partidoIdFilter,
            @Query("jugador_nombre") String nombreFilter,
            @Body RequestBody body
    );

    @DELETE("rest/v1/confirmados")
    Call<Void> deleteConfirmado(
            @Query("partido_id") String partidoIdFilter,
            @Query("jugador_nombre") String nombreFilter
    );

    // VOTOS
    @POST("rest/v1/votos")
    Call<Void> emitirVoto(@Body com.example.myapplication.models.Voto voto);

    @PATCH("rest/v1/partidos")
    Call<Void> cerrarPartido(
            @Query("id") String idFilter,
            @Body RequestBody body
    );

    @GET("rest/v1/confirmados")
    Call<List<ResponseBody>> getPartidosUsuario(
            @Query("jugador_nombre") String filter,
            @Query("select") String select
    );
}
