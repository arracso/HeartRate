package edu.udg.exit.heartrate.Views;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import edu.udg.exit.heartrate.Global;
import edu.udg.exit.heartrate.Model.Login;
import edu.udg.exit.heartrate.Model.Register;
import edu.udg.exit.heartrate.Model.Tokens;
import edu.udg.exit.heartrate.Model.User;
import edu.udg.exit.heartrate.R;
import edu.udg.exit.heartrate.Services.ApiService;
import edu.udg.exit.heartrate.TodoApp;
import edu.udg.exit.heartrate.Utils.UserPreferences;
import junit.framework.Test;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends Activity {

    ///////////////////////
    // Lifecycle methods //
    ///////////////////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Set button actions
        setButtonActions();
    }

    /////////////////////
    // Private Methods //
    /////////////////////

    private void setButtonActions() {
        // Login Button
        Button loginBtn = (Button) findViewById(R.id.login_button);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });
        // Login as guest button
        TextView guestLink = (TextView) findViewById(R.id.login_guest);
        guestLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginAsGuest();
            }
        });
        // Reset password button
        TextView resetLink = (TextView) findViewById(R.id.login_reset_password);
        resetLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetPassword();
            }
        });
        // Register button
        TextView registerLink = (TextView) findViewById(R.id.login_register);
        registerLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startRegisterActivity();
            }
        });
    }

    private void login() {
        ApiService apiService = ((TodoApp) getApplication()).getApiService();

        String username = ((EditText) findViewById(R.id.login_username)).getText().toString();
        String password = ((EditText) findViewById(R.id.login_password)).getText().toString();

        apiService.getAuthService().login(new Login(username,password)).enqueue(new Callback<Tokens>() {
            @Override
            public void onResponse(Call<Tokens> call, Response<Tokens> response) {
                if(response.isSuccessful()){
                    UserPreferences.getInstance().save(getApplicationContext(),UserPreferences.ACCESS_TOKEN,response.body().getAccessToken());
                    UserPreferences.getInstance().save(getApplicationContext(),UserPreferences.REFRESH_TOKEN,response.body().getRefreshToken());
                    startLaunchActivity();
                }
            }

            @Override
            public void onFailure(Call<Tokens> call, Throwable t) {
                Toast.makeText(LoginActivity.this,"Failed to Login!", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void loginAsGuest() {

    }

    private void resetPassword() {

    }

    private void startRegisterActivity() {
        Intent register = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(register);
        this.finish();
    }

    private void startLaunchActivity() {
        Intent launch = new Intent(LoginActivity.this, LaunchActivity.class);
        startActivity(launch);
        this.finish();
    }

}
