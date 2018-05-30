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

        // Get & check username and password
        String username = ((EditText) findViewById(R.id.login_username)).getText().toString();
        String password = ((EditText) findViewById(R.id.login_password)).getText().toString();

        Boolean error = false;
        if(!checkUsername(username)) error = true;
        if(!checkPassword(password)) error = true;
        if(error) return;

        // Make the call to the server
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

    /**
     * Checks if username is valid (it can be an email)
     * @param username - Username to be check
     * @return
     */
    private boolean checkUsername(String username) {
        String error = null;

        // Check username (can be email to)
        if(username == null || username.equals("")) error = "Username cannot be empty.";
        else if(username.length() < 4 || username.length() > 16) error = "Username must be 4 to 16 characters long.";
        else if(!username.matches(Global.REGEX_USERNAME) && !username.matches(Global.REGEX_EMAIL)) error = "Invalid username or email.";

        // All checks passed
        if(error == null) return true;

        // Show the error
        EditText usernameText = (EditText) findViewById(R.id.login_username);
        usernameText.setError(error);

        return false;
    }

    /**
     * Checks if password is valid
     * @param password - Password to be check
     * @return
     */
    private boolean checkPassword(String password) {
        String error = null;

        // Check password
        if(password == null || password.equals("")) error = "Password cannot be empty.";
        else if(password.length() < 8 || password.length() > 32) error = "Password must be 8 to 32 characters long.";
        else if(!password.matches(Global.REGEX_PASSWORD)) error = "Password must contain 1 digit, 1 lowercase and 1 uppercase, and cannot contain special characters or spaces.";

        // All checks passed
        if(error == null) return true;

        // Show the error
        EditText passwordText = (EditText) findViewById(R.id.login_password);
        passwordText.setError(error);

        return false;
    }

}
