package edu.udg.exit.heartrate.Services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
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

public class ApiService extends Service {

    ///////////////
    // Constants //
    ///////////////

    //public static final String BASE_URL = "http://84.88.154.119:12380";
    public static final String BASE_URL = "http://79.146.40.149:12380";

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
     *
     * @return
     */
    public AuthApi getAuthService() {
        return authService;
    }

    public UserApi getUserService() {
        return userService;
    }

    public FileApi getFileService() {
        return fileService;
    }
}
