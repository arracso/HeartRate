package edu.udg.exit.heartrate.Views;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.Toast;
import edu.udg.exit.heartrate.Components.ExpandItem;
import edu.udg.exit.heartrate.R;
import edu.udg.exit.heartrate.TodoApp;
import edu.udg.exit.heartrate.Utils.DataBase;
import edu.udg.exit.heartrate.Utils.UserPreferences;

import java.io.File;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Main activity with user profile.
 */
public class MainActivity extends Activity {

    ///////////////
    // Variables //
    ///////////////

    private int oldContent = -1;
    private ViewGroup container = null;

    ///////////////////////
    // Lifecycle methods //
    ///////////////////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get the container
        container = (ViewGroup) findViewById(R.id.main_container);

        // Setup contents
        setupContents();
    }

    ////////////////////
    // Public methods //
    ////////////////////

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

    //*************************//
    // Activity Change Methods //
    //*************************//

    /**
     * Starts the ScanActivity.
     */
    private void startScanActivity() {
        Intent scan = new Intent(this,ScanActivity.class);
        startActivity(scan);
    }

    /**
     * Starts the ScanActivity.
     */
    private void startBandActivity() {
        Intent device = new Intent(this,MeasureActivity.class);
        startActivity(device);
    }

    /**
     * Starts the login activity and finish this activity.
     */
    private void startLoginActivity() {
        Intent login = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(login);
        this.finish();
    }

    //*********************************//
    // Content Expand/Collapse Methods //
    //*********************************//

    /**
     * Expands the new content and collapses the old one.
     * @param newContent - New content to be expanded
     */
    private void toggleContents(int newContent) {
        if(oldContent != -1) collapseContent(oldContent);
        if(oldContent == newContent) oldContent = -1;
        else {
            oldContent = newContent;
            expandContent(newContent);
        }
    }

    /**
     * Expands a content from the container.
     * @param content - Content to be expanded
     */
    private void expandContent(int content) {
        ExpandItem item = (ExpandItem) container.getChildAt(content);
        item.expand();
    }

    /**
     * Collapses a content from the container.
     * @param content - Content to be collapsed
     */
    private void collapseContent(int content) {
        ExpandItem item = (ExpandItem) container.getChildAt(content);
        item.collapse();
    }

    //***********************//
    // Content Setup Methods //
    //***********************//

    /**
     * Contents setup.
     */
    private void setupContents() {
        // Set old content as no one
        oldContent = -1;

        // Setup content of the container
        setupId();
        setupSex();
        setupBirthYear();
        setupWeight();
        setupHeight();
        setupBand();
    }

    /**
     * Sets the value of the user id and its listeners and callbacks.
     */
    private void setupId() {
        // Get user ID item
        final ExpandItem idItem = (ExpandItem) findViewById(R.id.user_id);
        // Set listeners and callbacks
        idItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleContents(container.indexOfChild(idItem));
            }
        });
        // Set user ID
        Integer id = ((TodoApp) getApplication()).getUser().getId();
        idItem.setLabelValue(""+id);
    }

    /**
     * Sets the value of the user sex and its listeners and callbacks.
     */
    private void setupSex() {
        // Get user sex item
        final ExpandItem sexItem = (ExpandItem) findViewById(R.id.user_sex);
        // Set listeners and callbacks
        sexItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleContents(container.indexOfChild(sexItem));
            }
        });
        sexItem.setOnCollapseCallback(new Runnable() {
            @Override
            public void run() {
                // TODO - Change sex on user
            }
        });
        // Set sex
        Integer sex = ((TodoApp) getApplication()).getUser().getSex();
        sexItem.setLabelValue(sex == null ? "" : (sex == 1 ? "Male" : (sex == 2 ? "Female" : "Other")));
        // TODO - Control radio buttons
    }

    /**
     * Sets the value of the user birth year and its listeners and callbacks.
     */
    private void setupBirthYear() {
        // Get user birth year item & picker
        final ExpandItem birthYearItem = (ExpandItem) findViewById(R.id.user_birth_year);
        final NumberPicker birthYearPicker = (NumberPicker) findViewById(R.id.user_birth_year_picker);
        // Set listeners and callbacks
        birthYearItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleContents(container.indexOfChild(birthYearItem));
            }
        });
        birthYearItem.setOnCollapseCallback(new Runnable() {
            @Override
            public void run() {
                // TODO - change birth year on user
            }
        });
        // Setup birth year
        Integer birthYear = ((TodoApp) getApplication()).getUser().getBirthYear();
        birthYearItem.setLabelValue(birthYear == null ? "" : ""+birthYear);
        // Setup birth year picker
        int actualYear = (new GregorianCalendar()).get(Calendar.YEAR);
        setNumberPicker(birthYearPicker,1900, actualYear,1900,true, null, null, null);
    }

    /**
     * Sets the value of the user weight and its listeners and callbacks.
     */
    private void setupWeight() {
        // Get user weight item & picker
        final ExpandItem weightItem = (ExpandItem) findViewById(R.id.user_weight);
        final NumberPicker weightPicker = (NumberPicker) findViewById(R.id.user_weight_picker);
        // Set listeners and callbacks
        weightItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleContents(container.indexOfChild(weightItem));
            }
        });
        weightItem.setOnCollapseCallback(new Runnable() {
            @Override
            public void run() {
                // TODO - change weight on user
            }
        });
        // Setup weight
        Integer weight = ((TodoApp) getApplication()).getUser().getWeight();
        weightItem.setLabelValue(weight == null ? "" : "" + weight + " Kg");
        // Setup weight picker
        setNumberPicker(weightPicker,30, 250,30,true, null, " Kg", null);
    }

    /**
     * Sets the value of the user height and its listeners and callbacks.
     */
    private void setupHeight() {
        // Get user height item & picker
        final ExpandItem heightItem = (ExpandItem) findViewById(R.id.user_height);
        final NumberPicker heightPicker = (NumberPicker) findViewById(R.id.user_height_picker);
        // Set listeners and callbacks
        heightItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleContents(container.indexOfChild(heightItem));
            }
        });
        heightItem.setOnCollapseCallback(new Runnable() {
            @Override
            public void run() {
                // TODO - change height on user
            }
        });
        // Setup weight
        Integer height = ((TodoApp) getApplication()).getUser().getHeight();
        heightItem.setLabelValue(height == null ? "" : "" + height + " cm");
        // Setup weight picker
        setNumberPicker(heightPicker,50, 250,50,true, null, " cm", null);
    }

    /**
     * Sets the value of the band and its listeners.
     */
    private void setupBand() {
        // Get band item & buttons
        final ExpandItem bandItem = (ExpandItem) findViewById(R.id.main_band);
        final Button scanButton = (Button) findViewById(R.id.main_band_scan);
        final Button bandButton = (Button) findViewById(R.id.main_band_settings);
        final Button unbindButton = (Button) findViewById(R.id.main_band_unbind);
        // Set listeners
        bandItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleContents(container.indexOfChild(bandItem));
            }
        });
        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bandItem.collapse();
                startScanActivity();
            }
        });
        bandButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bandItem.collapse();
                startBandActivity();
            }
        });
        unbindButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                (((TodoApp) getApplication())).getBluetoothService().unbindDevice();
                bandItem.setLabelValue("Not paired!");
                bandItem.collapse();
                scanButton.setVisibility(View.VISIBLE);
                bandButton.setVisibility(View.GONE);
                unbindButton.setVisibility(View.GONE);
            }
        });
        // Set visibility & state (not paired | not connected | connected)
        final Handler handler = new Handler();
        Runnable setupState = new Runnable() {
            @Override
            public void run() {
                String bondedDeviceAddress = UserPreferences.getInstance().load(getApplicationContext(),UserPreferences.BONDED_DEVICE_ADDRESS);
                if(((TodoApp) getApplication()).getBluetoothService().isConnected()){
                    bandItem.setLabelValue(bondedDeviceAddress);
                    scanButton.setVisibility(View.GONE);
                    bandButton.setVisibility(View.VISIBLE);
                    unbindButton.setVisibility(View.VISIBLE);
                }else if(bondedDeviceAddress != null){
                    bandItem.setLabelValue("Not connected!");
                    scanButton.setVisibility(View.GONE);
                    bandButton.setVisibility(View.GONE);
                    unbindButton.setVisibility(View.VISIBLE);
                }else{
                    bandItem.setLabelValue("Not paired!");
                    scanButton.setVisibility(View.VISIBLE);
                    bandButton.setVisibility(View.GONE);
                    unbindButton.setVisibility(View.GONE);
                }
                handler.postDelayed(this,3000);
            }
        };
        setupState.run();
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
    @SuppressWarnings("SameParameterValue")
    private void setNumberPicker(NumberPicker numberPicker, int minValue, int maxValue, int defaultValue, boolean firstAsNull, String prefix, String suffix, NumberPicker.OnValueChangeListener onValueChangeListener) {
        // Set range values
        numberPicker.setMinValue(minValue);
        numberPicker.setMaxValue(maxValue);
        // Set displayed values
        if(firstAsNull){
            int nValues = maxValue - minValue + 1;
            String[] displayedValues = new String[nValues];
            displayedValues[0] = " ";
            for (int i=1; i<nValues; i++) {
                String value = "";
                if(prefix != null) value = value + prefix;
                value = value + (minValue+i);
                if(suffix != null) value = value + suffix;
                displayedValues[i] = value;
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
