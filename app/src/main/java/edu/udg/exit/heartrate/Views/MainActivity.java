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

        // Set number pickers
        setBirthYearPicker();
        setWeightPicker();
        setHeightPicker();

        // Set radio buttons
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
     * Sets birth year picker
     */
    private void setBirthYearPicker() {
        // Listener
        NumberPicker.OnValueChangeListener onBirthYearPickerListener = new NumberPicker.OnValueChangeListener(){
            @Override
            public void onValueChange(NumberPicker numberPicker, int oldValue, int newValue) {
                Log.d("PICKER", "" + numberPicker.getValue());
            }
        };

        NumberPicker birthYearPicker = (NumberPicker) findViewById(R.id.user_birth_year);
        int actualYear = (new GregorianCalendar()).get(Calendar.YEAR);
        setNumberPicker(birthYearPicker,1900, actualYear,1900,true, onBirthYearPickerListener);
    }

    /**
     * Sets birth year picker
     */
    private void setWeightPicker() {
        // Listener
        NumberPicker.OnValueChangeListener onWeightPickerListener = new NumberPicker.OnValueChangeListener(){
            @Override
            public void onValueChange(NumberPicker numberPicker, int oldValue, int newValue) {
                Log.d("PICKER", "" + numberPicker.getValue());
            }
        };

        NumberPicker birthYearPicker = (NumberPicker) findViewById(R.id.user_weight);
        setNumberPicker(birthYearPicker,30, 250,30,true, onWeightPickerListener);
    }

    /**
     * Sets birth year picker
     */
    private void setHeightPicker() {
        // Listener
        NumberPicker.OnValueChangeListener onHeightPickerListener = new NumberPicker.OnValueChangeListener(){
            @Override
            public void onValueChange(NumberPicker numberPicker, int oldValue, int newValue) {
                Log.d("PICKER", "" + numberPicker.getValue());
            }
        };

        NumberPicker birthYearPicker = (NumberPicker) findViewById(R.id.user_height);
        setNumberPicker(birthYearPicker,50, 250,50,true, onHeightPickerListener);
    }

    /**
     * Sets number picker configurations
     * @param numberPicker - Number picker to be configured
     * @param minValue - Min value of the number picker
     * @param maxValue - Max value of the number picker
     * @param defaultValue - Default value of the number picker
     * @param firstAsNull - When this is true first value is used to define null value
     * @param onValueChangeListener - Listener of the number picker
     */
    private void setNumberPicker(NumberPicker numberPicker, int minValue, int maxValue, int defaultValue, boolean firstAsNull, NumberPicker.OnValueChangeListener onValueChangeListener) {
        // Set range values
        numberPicker.setMinValue(minValue);
        numberPicker.setMaxValue(maxValue);
        // Set displayed values
        if(firstAsNull){
            int nValues = maxValue - minValue + 1;
            String[] displayedValues = new String[nValues];
            displayedValues[0] = " ";
            for (int i=1; i<nValues; i++) {
                displayedValues[i] = String.format("%d",(minValue+i));
            }
            numberPicker.setDisplayedValues(displayedValues);
        }
        // Set user value
        numberPicker.setValue(defaultValue);
        // Set listener
        numberPicker.setOnValueChangedListener(onValueChangeListener);
        // Clear the focus
        numberPicker.clearFocus();
    }


    // DEBUG //

    private void test(){
        DataBase db = new DataBase(this.getApplicationContext());

        File file = db.exportAsCSV(DataBase.RATE_TABLE_NAME, null, null, "HR.csv");
    }

}
