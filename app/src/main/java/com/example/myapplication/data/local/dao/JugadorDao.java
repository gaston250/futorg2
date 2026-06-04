package com.example.myapplication.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import com.example.myapplication.data.local.entities.JugadorEntity;
import java.util.List;

@Dao
public interface JugadorDao {
    @Query("SELECT * FROM jugadores ORDER BY goles DESC")
    LiveData<List<JugadorEntity>> getAllRanking();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<JugadorEntity> jugadores);

    @Query("DELETE FROM jugadores")
    void deleteAll();
}
