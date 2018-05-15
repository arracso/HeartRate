package edu.udg.exit.heartrate;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.view.View;
import android.widget.Toast;
import edu.udg.exit.heartrate.Model.Tokens;
import edu.udg.exit.heartrate.Model.User;
import edu.udg.exit.heartrate.Services.ApiService;
import edu.udg.exit.heartrate.Services.BluetoothService;
import edu.udg.exit.heartrate.Utils.UserPreferences;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TodoApp extends Application{

    ////////////////
    // Attributes //
    ////////////////

    private BluetoothService bluetoothService = null;
    private ApiService apiService = null;

    ///////////////////////
    // LifeCicle methods //
    ///////////////////////

    @Override
    public void onCreate() {
        super.onCreate();
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
            login();
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

    /////////////////////
    // Private Methods //
    /////////////////////

    private void login(){
        String accessToken = UserPreferences.getInstance().load(getApplicationContext(),UserPreferences.ACCESS_TOKEN);
        if(accessToken == null) loginAsGuest();
        else getUser();
    }

    private void loginAsGuest() {
        if(apiService == null) return;
        apiService.getAuthService().loginAsGuest().enqueue(new Callback<Tokens>() {
            @Override
            public void onResponse(Call<Tokens> call, Response<Tokens> response) {
                UserPreferences.getInstance().save(getApplicationContext(),UserPreferences.ACCESS_TOKEN, ((Tokens)response.body()).getAccessToken());
                getUser();
            }

            @Override
            public void onFailure(Call<Tokens> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Connection failed.", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void getUser() {
        if(apiService == null) return;
        apiService.getUserService().getUser().enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if(response.isSuccessful()){
                    User user = response.body();
                    Global.user = user;
                    Toast.makeText(getApplicationContext(), "User id: " + user.getId(), Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(getApplicationContext(), "Login failed!", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_LONG).show();
            }
        });
    }

}
