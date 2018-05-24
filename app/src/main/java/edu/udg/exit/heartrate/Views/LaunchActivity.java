package edu.udg.exit.heartrate.Views;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.Image;
import android.os.Bundle;
import android.os.LocaleList;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import edu.udg.exit.heartrate.Activities.BluetoothActivity;
import edu.udg.exit.heartrate.Global;
import edu.udg.exit.heartrate.Model.User;
import edu.udg.exit.heartrate.R;
import edu.udg.exit.heartrate.Services.ApiService;
import edu.udg.exit.heartrate.Services.BluetoothService;
import edu.udg.exit.heartrate.TodoApp;
import edu.udg.exit.heartrate.Utils.UserPreferences;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LaunchActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        // Launch animation
        loadLaunchAnimation();

        // Check if user is logged in
        checkLogin();
    }

    /////////////////////
    // Private Methods //
    /////////////////////

    /**
     * Load the launch animation into the screen.
     */
    private void loadLaunchAnimation() {
        ImageView launchAnimation = (ImageView) findViewById(R.id.launch_animation);
        Glide.with(this).load(R.drawable.loading_heart_rate).into(new GlideDrawableImageViewTarget(launchAnimation));
    }

    private void checkLogin() {
        String accessToken = UserPreferences.getInstance().load(getApplicationContext(),UserPreferences.ACCESS_TOKEN);
        if(accessToken != null) getUser();
    }

    private void getUser() {
        ApiService apiService = ((TodoApp) getApplication()).getApiService();

        if(apiService == null) return;

        apiService.getUserService().getUser().enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if(response.isSuccessful()){
                    User user = response.body();
                    Global.user = user;
                    Toast.makeText(getApplicationContext(), "User id: " + user.getId(), Toast.LENGTH_LONG).show();
                }else if(response.code() == 401){ // Unauthorized
                    Toast.makeText(getApplicationContext(), "Unauthorized!", Toast.LENGTH_LONG).show();
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
