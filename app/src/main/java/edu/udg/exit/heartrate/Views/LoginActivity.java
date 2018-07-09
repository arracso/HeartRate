package edu.udg.exit.heartrate.Views;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import edu.udg.exit.heartrate.Model.*;
import edu.udg.exit.heartrate.Global;
import edu.udg.exit.heartrate.R;
import edu.udg.exit.heartrate.Services.ApiService;
import edu.udg.exit.heartrate.TodoApp;
import edu.udg.exit.heartrate.Utils.UserPreferences;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.IOException;

/**
 * Activity to perform a login into the application.
 */
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

    /**
     * Sets the actions of all buttons and links of the view.
     */
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
        // Register button
        TextView registerLink = (TextView) findViewById(R.id.login_register);
        registerLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startRegisterActivity();
            }
        });
    }

    /**
     * Checks if credentials are valid and attempts to make a login with them.
     */
    private void login() {
        ApiService apiService = ((TodoApp) getApplication()).getApiService();

        // Get & check username and password
        String email = ((EditText) findViewById(R.id.login_email)).getText().toString();
        String password = ((EditText) findViewById(R.id.login_password)).getText().toString();

        Boolean error = false;
        if(!checkEmail(email)) error = true;
        if(!checkPassword(password)) error = true;
        if(error) return;

        // Make the call to the server
        apiService.getAuthService().login(new Auth(email,password)).enqueue(new Callback<Tokens>() {
            @Override
            public void onResponse(Call<Tokens> call, Response<Tokens> response) {
                if(response.isSuccessful()){
                    UserPreferences.getInstance().save(getApplicationContext(),UserPreferences.ACCESS_TOKEN,response.body().getAccessToken());
                    UserPreferences.getInstance().save(getApplicationContext(),UserPreferences.REFRESH_TOKEN,response.body().getRefreshToken());
                    startLaunchActivity();
                }else{
                    try {
                        ResponseBody errorBody = Global.gson.fromJson(response.errorBody().string(), ResponseBody.class);
                        Toast.makeText(LoginActivity.this,errorBody.getMessage(),Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        Toast.makeText(LoginActivity.this,"Unknown login error.",Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(LoginActivity.this,"Fatal login error.",Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<Tokens> call, Throwable t) {
                Toast.makeText(LoginActivity.this,"Failed to connect to the server!", Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Attempts to make a login as a guest.
     */
    private void loginAsGuest() {
        ((TodoApp) getApplication()).getApiService().getAuthService().loginAsGuest().enqueue(new Callback<Tokens>() {
            @Override
            public void onResponse(Call<Tokens> call, Response<Tokens> response) {
                if(response.isSuccessful()){
                    UserPreferences.getInstance().save(getApplicationContext(),UserPreferences.ACCESS_TOKEN,response.body().getAccessToken());
                    UserPreferences.getInstance().save(getApplicationContext(),UserPreferences.REFRESH_TOKEN,response.body().getRefreshToken());
                    startLaunchActivity();
                }else{
                    try {
                        Gson gson = new Gson();
                        ResponseBody errorBody = gson.fromJson(response.errorBody().string(), ResponseBody.class);
                        Toast.makeText(LoginActivity.this,errorBody.getMessage(),Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        Toast.makeText(LoginActivity.this,"Unknown login error.",Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(LoginActivity.this,"Fatal login error.",Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<Tokens> call, Throwable t) {
                Toast.makeText(LoginActivity.this,"Failed to Login!", Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Starts the register activity and closes this activity. (no need to close this activity)
     */
    private void startRegisterActivity() {
        Intent register = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(register);
    }

    /**
     * Starts the launch activity and closes this activity.
     */
    private void startLaunchActivity() {
        Intent launch = new Intent(LoginActivity.this, LaunchActivity.class);
        startActivity(launch);
        this.finish();
    }

    /**
     * Checks if email is valid.
     * @param email - Email to be check
     * @return True when email is valid, false otherwise.
     */
    private boolean checkEmail(String email) {
        String error = null;

        // Check username (can be email to)
        if(email == null || email.equals("")) error = "Email cannot be empty.";
        else if(!email.matches(Global.REGEX_EMAIL)) error = "Invalid email.";

        // All checks passed
        if(error == null) return true;

        // Show the error
        EditText emailText = (EditText) findViewById(R.id.login_email);
        emailText.setError(error);

        return false;
    }

    /**
     * Checks if password is valid.
     * @param password - Password to be check
     * @return True when password is valid, false otherwise.
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
