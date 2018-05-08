package edu.udg.exit.heartrate.Services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class ApiService extends Service {

    ///////////////
    // Constants //
    ///////////////

    public static final String BASE_URL = "localhost";

    ////////////////
    // Attributes //
    ////////////////

    // Service
    private final IBinder apiBinder = new ApiBinder();

    ////////////////////////////
    // Service implementation //
    ////////////////////////////

    @Override
    public void onCreate() {
        super.onCreate();

        // Log
        Log.d("ApiService", "create");


    }

    @Override
    public IBinder onBind(Intent intent) {
        return apiBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        // Log
        Log.d("ApiService", "destroy");

        super.onDestroy();
    }

    /**
     * Binder class that can return a reference to ApiService
     */
    public class ApiBinder extends Binder {
        public ApiService getService() {
            return ApiService.this;
        }
    }

    ////////////////////
    // Public Methods //
    ////////////////////



}
