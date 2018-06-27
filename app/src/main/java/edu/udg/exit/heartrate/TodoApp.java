package edu.udg.exit.heartrate;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;
import edu.udg.exit.heartrate.Model.User;
import edu.udg.exit.heartrate.Services.ApiService;
import edu.udg.exit.heartrate.Services.BluetoothService;
import edu.udg.exit.heartrate.Utils.UserPreferences;

public class TodoApp extends Application{

    ////////////////
    // Attributes //
    ////////////////

    // Services //
    private BluetoothService bluetoothService = null;
    private ApiService apiService = null;
    // Profile //
    private User user = new User();

    ///////////////////////
    // LifeCicle methods //
    ///////////////////////

    @Override
    public void onCreate() {
        super.onCreate();
        // Retrieve user profile from user preferences
        retrieveUser();
        // Connecting & start api service
        Intent apiServiceIntent = new Intent(this,ApiService.class);
        startService(apiServiceIntent);
        bindService(apiServiceIntent, apiServiceConnection, Context.BIND_AUTO_CREATE);
        // Start & bind bluetooth service
        Intent bluetoothServiceIntent = new Intent(this,BluetoothService.class);
        startService(bluetoothServiceIntent);
        bindService(bluetoothServiceIntent, bluetoothServiceConnection, Context.BIND_AUTO_CREATE);
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

    public void setUser(User user) {
        this.user = user;
        String userObj = Global.gson.toJson(user);
        Log.d("USER","set: " + userObj); // TODO - remove Log
        UserPreferences.getInstance().save(this, UserPreferences.USER_PROFILE, userObj);
    }

    public User getUser() {
        return this.user;
    }

    /////////////////////
    // Private Methods //
    /////////////////////

    private void retrieveUser() {
        String userObj = UserPreferences.getInstance().load(this, UserPreferences.USER_PROFILE);
        Log.d("USER","get: " + userObj); // TODO - remove Log
        this.user = Global.gson.fromJson(userObj, User.class);
    }

}
