package edu.udg.exit.heartrate;

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
import edu.udg.exit.heartrate.Views.LaunchActivity;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.IOException;

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
        refreshUser();
    }

    public void refreshUser(){
        String userObj = Global.gson.toJson(user);
        UserPreferences.getInstance().save(this, UserPreferences.USER_PROFILE, userObj);
    }

    public User getUser() {
        return this.user;
    }

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

    private void retrieveUser() {
        String userObj = UserPreferences.getInstance().load(this, UserPreferences.USER_PROFILE);
        this.user = Global.gson.fromJson(userObj, User.class);
        if(this.user == null) this.user = new User();
    }

}
