package com.example.myapplication;

import android.app.Application;
import com.example.myapplication.BuildConfig;
import timber.log.Timber;

public class PichangaApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
    }
}
