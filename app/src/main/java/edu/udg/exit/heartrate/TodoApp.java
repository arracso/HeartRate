package edu.udg.exit.heartrate;

import android.app.ActivityManager;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;
import edu.udg.exit.heartrate.Model.User;
import edu.udg.exit.heartrate.Services.ApiService;
import edu.udg.exit.heartrate.Services.BluetoothService;
import edu.udg.exit.heartrate.Utils.UserPreferences;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.IOException;

/**
 * Class that overrides Application.
 * Saves the user profile and a reference to all services that it starts.
 */
public class TodoApp extends Application {

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
        if (!isMyServiceRunning(apiServiceIntent.getClass())) startService(apiServiceIntent);
        bindService(apiServiceIntent, apiServiceConnection, Context.BIND_AUTO_CREATE);
        // Start & bind bluetooth service
        Intent bluetoothServiceIntent = new Intent(this,BluetoothService.class);
        if (!isMyServiceRunning(bluetoothServiceIntent.getClass())) startService(bluetoothServiceIntent);
        bindService(bluetoothServiceIntent, bluetoothServiceConnection, Context.BIND_AUTO_CREATE);
    }

    ////////////////////////
    // Service Connection //
    ////////////////////////

    /**
     * Checks if service is already running.
     * @param serviceClass - class of the service that we want to check
     * @return True if service is running
     */
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if(manager != null){
            for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
                if (serviceClass.getName().equals(service.service.getClassName())) return true;
            }
        }
        return false;
    }

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

    /**
     * Sets the user profile.
     * @param user - profile
     */
    public void setUser(User user) {
        this.user = user;
        refreshUser();
    }

    /**
     * Save the user profile to persistent memory.
     */
    public void refreshUser(){
        String userObj = Global.gson.toJson(user);
        UserPreferences.getInstance().save(this, UserPreferences.USER_PROFILE, userObj);
    }

    /**
     * Gets the usre profile.
     * @return User.
     */
    public User getUser() {
        return this.user;
    }

    /**
     * Update the user stored on the remote data base with the user stored locally.
     */
    public void updateUser() {
        if(apiService == null) return;
        if(user == null) return;
        apiService.getUserService().updateUser(user).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.isSuccessful()) Log.d("USER", "Updated!");
                else if(response.code() == 401) { // Unauthorized
                    try {
                        edu.udg.exit.heartrate.Model.ResponseBody errorBody = Global.gson.fromJson(response.errorBody().string(), edu.udg.exit.heartrate.Model.ResponseBody.class);
                        Toast.makeText(getApplicationContext(),errorBody.getMessage(),Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        Toast.makeText(getApplicationContext(),"Unknown error.",Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(),"Fatal error.",Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Update failed!", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Failed to connect. Try it later.", Toast.LENGTH_LONG).show();
            }
        });
    }

    /////////////////////
    // Private Methods //
    /////////////////////

    /**
     * Retrieve the user form persistent storage.
     */
    private void retrieveUser() {
        String userObj = UserPreferences.getInstance().load(this, UserPreferences.USER_PROFILE);
        this.user = Global.gson.fromJson(userObj, User.class);
        if(this.user == null) this.user = new User();
    }

}
