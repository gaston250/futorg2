package com.example.myapplication.data.local;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import com.example.myapplication.data.local.dao.JugadorDao;
import com.example.myapplication.data.local.dao.PartidoDao;
import com.example.myapplication.data.local.entities.JugadorEntity;
import com.example.myapplication.data.local.entities.PartidoEntity;

@Database(entities = {JugadorEntity.class, PartidoEntity.class}, version = 2, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract JugadorDao jugadorDao();
    public abstract PartidoDao partidoDao();

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "pichanga_database")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
