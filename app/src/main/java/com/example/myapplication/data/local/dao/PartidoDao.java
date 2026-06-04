package com.example.myapplication.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import com.example.myapplication.data.local.entities.PartidoEntity;
import java.util.List;

@Dao
public interface PartidoDao {
    @Query("SELECT * FROM partidos ORDER BY id DESC LIMIT 1")
    LiveData<PartidoEntity> getUltimoPartido();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(PartidoEntity partido);

    @Query("DELETE FROM partidos")
    void deleteAll();
}
