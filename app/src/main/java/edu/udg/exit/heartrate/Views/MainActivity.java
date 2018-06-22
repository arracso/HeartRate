package edu.udg.exit.heartrate.Views;

import android.app.Activity;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.transition.Transition;
import android.util.Log;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;
import edu.udg.exit.heartrate.Global;
import edu.udg.exit.heartrate.R;
import edu.udg.exit.heartrate.TodoApp;
import edu.udg.exit.heartrate.Utils.DataBase;
import edu.udg.exit.heartrate.Utils.UserPreferences;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class MainActivity extends Activity {

    private Integer id = null;

    ///////////////////////
    // Lifecycle methods //
    ///////////////////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set text ID
        setTextId();

        // Set birth year picker
        setBirthYearPicker();
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

    /**
     *
     */
    private void setTextId() {
        Integer id = ((TodoApp) getApplication()).getUser().getId();
        TextView textId = (TextView) findViewById(R.id.main_text_id);
        String text = "Your id:";
        if(id!=null) text += id;
        textId.setText(text);
    }

    /**
     *
     */
    private void setBirthYearPicker() {
        NumberPicker birthYearPicker = (NumberPicker) findViewById(R.id.user_birth_year);
        // Set range values
        birthYearPicker.setMinValue(1900);
        birthYearPicker.setMaxValue((new GregorianCalendar()).get(Calendar.YEAR));
        // Set user value
        Integer birthYear = ((TodoApp) getApplication()).getUser().getBirthYear();
        if(birthYear != null) birthYearPicker.setValue(birthYear);
        else birthYearPicker.setValue(2000);
        // Set listener
        birthYearPicker.setOnValueChangedListener(onBirthYearPickerListener);
        // Clear the focus
        birthYearPicker.clearFocus();
    }

    /**
     * Birth Year picker listener.
     */
    NumberPicker.OnValueChangeListener onBirthYearPickerListener = new NumberPicker.OnValueChangeListener(){
        @Override
        public void onValueChange(NumberPicker numberPicker, int i, int i1) {
            numberPicker.getValue();
        }
    };

    // DEBUG //

    private void test(){
        DataBase db = new DataBase(this.getApplicationContext());

        File file = db.exportAsCSV(DataBase.RATE_TABLE_NAME, null, null, "HR.csv");
    }

}
