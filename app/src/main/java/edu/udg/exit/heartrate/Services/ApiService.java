package edu.udg.exit.heartrate.Services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.IBinder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import edu.udg.exit.heartrate.ApiRest.*;
import edu.udg.exit.heartrate.Utils.UserPreferences;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;

/**
 * Service that handles api rest services initializations and access.
 */
public class ApiService extends Service {

    ///////////////
    // Constants //
    ///////////////

    private static final String BASE_URL = "http://leffe.udg.edu:11180";

    ////////////////
    // Attributes //
    ////////////////

    // Service
    private final IBinder apiBinder = new ApiBinder();

    // Services
    private AuthApi authService;
    private UserApi userService;
    private FileApi fileService;

    ////////////////////////////
    // Service implementation //
    ////////////////////////////

    @Override
    public void onCreate() {
        super.onCreate();

        // Create JSON converter
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create();

        // Create OkHttp Client to intercept
        OkHttpClient okHttpClient = new OkHttpClient().newBuilder().addInterceptor(new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                String accessToken = UserPreferences.getInstance().load(ApiService.this, UserPreferences.ACCESS_TOKEN);
                Request newRequest = chain.request().newBuilder().header("Authorization", "Bearer " + accessToken).build();

                return chain.proceed(newRequest);
            }
        }).build();

        // Create retrofit instance without interceptor
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        // Create retrofit instance with interceptor
        Retrofit retrofitWithInterceptor = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        // Create api services
        authService = retrofit.create(AuthApi.class);
        userService = retrofitWithInterceptor.create(UserApi.class);
        fileService = retrofitWithInterceptor.create(FileApi.class);
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

    /**
     * Gets the authentication api rest service.
     * @return Authentication Api Rest Service.
     */
    public AuthApi getAuthService() {
        return authService;
    }

    /**
     * Gets the user api rest service.
     * @return User Api Rest Service.
     */
    public UserApi getUserService() {
        return userService;
    }

    /**
     * Gets the file api rest service.
     * @return File Api Rest Service.
     */
    public FileApi getFileService() {
        return fileService;
    }

    /**
     * Check if wifi is on and connected.
     * @return True when wifi is on and connected.
     */
    public boolean isWifiOnAndConnected() {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifiManager != null && wifiManager.isWifiEnabled()) { // Wi-Fi adapter is ON
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            return wifiInfo.getNetworkId() != -1; // Connected to an access point
        }
        return false;
    }
}
