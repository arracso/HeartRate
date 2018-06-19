package edu.udg.exit.heartrate.Views;

import android.app.Activity;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.transition.Transition;
import android.util.Log;
import android.view.View;
import edu.udg.exit.heartrate.R;
import edu.udg.exit.heartrate.TodoApp;
import edu.udg.exit.heartrate.Utils.DataBase;
import edu.udg.exit.heartrate.Utils.UserPreferences;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Date;

public class MainActivity extends Activity {

    ///////////////////////
    // Lifecycle methods //
    ///////////////////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    ////////////////////
    // Public methods //
    ////////////////////

    /**
     * Starts the ScanActivity.
     * @param view - MainActivity view
     */
    public void goToScan(View view) {
        Intent scan = new Intent(this,ScanActivity.class);
        startActivity(scan);
    }

    /**
     * Delete user information and redirect to Login Activity
     * @param view - MainActivity view
     */
    public void logout(View view) {
        // UNBIND MI BAND
        (((TodoApp) this.getApplication())).getBluetoothService().unbindDevice();
        // DELETE USER PREFERENCES
        UserPreferences.getInstance().remove(getApplicationContext(),UserPreferences.ACCESS_TOKEN);
        UserPreferences.getInstance().remove(getApplicationContext(),UserPreferences.REFRESH_TOKEN);
        UserPreferences.getInstance().remove(getApplicationContext(),UserPreferences.USER_PROFILE);
        // DELETE DATA BASE MEASUREMENTS
        DataBase dataBase = new DataBase(getApplicationContext());
        dataBase.deleteAllRecords();
        // Start login activity
        startLoginActivity();
    }

    /////////////////////
    // Private Methods //
    /////////////////////

    /**
     * Starts the login activity and finish this activity.
     */
    private void startLoginActivity() {
        Intent login = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(login);
        this.finish();
    }


    private void test(){
        DataBase db = new DataBase(this.getApplicationContext());

        File file = db.exportAsCSV(DataBase.RATE_TABLE_NAME, null, null, "HR.csv");
    }

}
