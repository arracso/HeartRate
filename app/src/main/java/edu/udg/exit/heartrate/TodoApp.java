package edu.udg.exit.heartrate;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import edu.udg.exit.heartrate.Services.ApiService;
import edu.udg.exit.heartrate.Services.BluetoothService;

public class TodoApp extends Application{

    ////////////////
    // Attributes //
    ////////////////

    private BluetoothService bluetoothService;
    private ApiService apiService;

    ///////////////////////
    // LifeCicle methods //
    ///////////////////////

    @Override
    public void onCreate() {
        // Start & bind bluetooth service
        Intent bluetoothServiceIntent = new Intent(this,BluetoothService.class);
        startService(bluetoothServiceIntent);
        bindService(bluetoothServiceIntent, bluetoothServiceConnection, Context.BIND_AUTO_CREATE);
        // Connecting & start api service
        Intent apiServiceIntent = new Intent(this,ApiService.class);
        startService(apiServiceIntent);
        bindService(apiServiceIntent, apiServiceConnection, Context.BIND_AUTO_CREATE);
    }



    ////////////////////////
    // Service Connection //
    ////////////////////////

    /**
     * Bluetooth Service Connection handler.
     */
    private ServiceConnection bluetoothServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            bluetoothService = ((BluetoothService.BluetoothBinder) binder).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            bluetoothService = null;
        }
    };

    /**
     * Api Service Connection handler.
     */
    private ServiceConnection apiServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            apiService = ((ApiService.ApiBinder) binder).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            apiService = null;
        }
    };

    ////////////////////
    // Public Methods //
    ////////////////////

    /**
     * Get bluetooth service from the application.
     * @return BluetoothService
     */
    public BluetoothService getBluetoothService() {
        return bluetoothService;
    }

    /**
     * Get api service from the application.
     * @return ApiService
     */
    public ApiService getApiService() {
        return apiService;
    }

}
