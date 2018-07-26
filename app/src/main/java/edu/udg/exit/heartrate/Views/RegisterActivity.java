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
import edu.udg.exit.heartrate.Global;
import edu.udg.exit.heartrate.Model.Auth;
import edu.udg.exit.heartrate.Model.ResponseBody;
import edu.udg.exit.heartrate.Model.Tokens;
import edu.udg.exit.heartrate.R;
import edu.udg.exit.heartrate.Services.ApiService;
import edu.udg.exit.heartrate.TodoApp;
import edu.udg.exit.heartrate.Utils.UserPreferences;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.IOException;

/**
 * Activity to perform a register to the application.
 */
public class RegisterActivity extends Activity {

    ///////////////////////
    // Lifecycle methods //
    ///////////////////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

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
        // Register Button
        Button registerBtn = (Button) findViewById(R.id.register_button);
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                register();
            }
        });
        // Login Link
        TextView loginLink = (TextView) findViewById(R.id.register_login);
        loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startLoginActivity();
            }
        });

    }

    /**
     * Checks if credentials are valid and attempts to make a login with them.
     */
    private void register() {
        ApiService apiService = ((TodoApp) getApplication()).getApiService();

        // Get email and password
        String email = ((EditText) findViewById(R.id.register_email)).getText().toString();;
        String password = ((EditText) findViewById(R.id.register_password)).getText().toString();
        String repassword = ((EditText) findViewById(R.id.register_repassword)).getText().toString();

        // Check email, username and password
        Boolean error = false;
        if(!checkEmail(email)) error = true;
        if(!checkPassword(password,repassword)) error = true;
        if(error) return;

        // Make the call to the server
        apiService.getAuthService().register(new Auth(email,password)).enqueue(new Callback<Tokens>() {
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

                    } catch (IOException e) {
                        Toast.makeText(RegisterActivity.this,"Unknown register error.",Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(RegisterActivity.this,"Fatal register error.",Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<Tokens> call, Throwable t) {
                Toast.makeText(RegisterActivity.this,"Failed to register!", Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Starts the login activity and closes this activity.
     * (only need to finish this activity, LoginActivity is under this activity)
     */
    private void startLoginActivity() {
        setResult(RESULT_CANCELED);
        this.finish();
    }

    /**
     * Starts the launch activity and closes this activity.
     */
    private void startLaunchActivity() {
        Intent launch = new Intent(RegisterActivity.this, LaunchActivity.class);
        startActivity(launch);
        setResult(RESULT_OK);
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
        EditText emailText = (EditText) findViewById(R.id.register_email);
        emailText.setError(error);

        return false;
    }

    /**
     * Checks if password is valid.
     * @param password - Password to be check
     * @param repassword - Repeated password to be check
     * @return True when password is valid, false otherwise.
     */
    private boolean checkPassword(String password, String repassword) {
        String error = null;
        String error2 = null;

        // Check password
        if(password == null || password.equals("")) error = "Password cannot be empty.";
        else if(password.length() < 8 || password.length() > 32) error = "Password must be 8 to 32 characters long.";
        else if(!password.matches(Global.REGEX_PASSWORD)) error = "Password must contain 1 digit, 1 lowercase and 1 uppercase, and cannot contain special characters or spaces.";
        else if(!password.equals(repassword)) error2 = "Passwords didn't match!";

        // All checks passed
        if(error == null && error2 == null) return true;

        // Show the error
        EditText passwordText = (EditText) findViewById(R.id.register_password);
        passwordText.setError(error);

        // Show the error2
        EditText repasswordText = (EditText) findViewById(R.id.register_repassword);
        repasswordText.setError(error2);

        return false;
    }
}
