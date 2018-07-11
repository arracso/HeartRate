package edu.udg.exit.heartrate.Views;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.RadioGroup;
import edu.udg.exit.heartrate.Components.ExpandItem;
import edu.udg.exit.heartrate.R;
import edu.udg.exit.heartrate.Services.BluetoothService;
import edu.udg.exit.heartrate.TodoApp;
import edu.udg.exit.heartrate.Utils.DataBase;
import edu.udg.exit.heartrate.Utils.UserPreferences;

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

    @Override
    protected void onDestroy() {
        stopService(new Intent(getApplicationContext(),BluetoothService.class));
        super.onDestroy();
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
        UserPreferences.getInstance().remove(getApplicationContext(),UserPreferences.HEART_RATE_MEASURE);
        // DELETE DATA BASE MEASUREMENTS
        DataBase dataBase = new DataBase(getApplicationContext());
        dataBase.deleteAllRecords();
        // Start login activity
        startLoginActivity();
    }

    public void closeApp(View view) {
        this.finishAndRemoveTask();
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
     * Starts the DeviceActivity.
     */
    private void startDeviceActivity() {
        Intent device = new Intent(this,DeviceActivity.class);
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
        final RadioGroup sexPicker = (RadioGroup) findViewById(R.id.user_sex_picker);
        // Setup sex
        Integer sex = ((TodoApp) getApplication()).getUser().getSex();
        sexItem.setLabelValue(sex == null ? "" : (sex == 1 ? "Male" : (sex == 2 ? "Female" : "Other")));
        // Setup sex picker
        if(sex == null) sexPicker.clearCheck();
        else sexPicker.check(sex == 1 ? R.id.user_radio_male : (sex == 2 ? R.id.user_radio_female : R.id.user_radio_other));
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
                ((TodoApp) getApplication()).updateUser();
            }
        });
        sexPicker.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int sexValue = 0;
                if(checkedId == R.id.user_radio_male) sexValue = 1;
                else if(checkedId == R.id.user_radio_female) sexValue = 2;
                sexItem.setLabelValue(sexValue == 1 ? "Male" : (sexValue == 2 ? "Female" : "Other"));
                ((TodoApp) getApplication()).getUser().setSex(sexValue);
                ((TodoApp) getApplication()).refreshUser();
            }
        });
    }

    /**
     * Sets the value of the user birth year and its listeners and callbacks.
     */
    private void setupBirthYear() {
        // Constants
        final int MIN_VALUE = 1900;
        final int MAX_VALUE = (new GregorianCalendar()).get(Calendar.YEAR);
        // Get user birth year item & picker
        final ExpandItem birthYearItem = (ExpandItem) findViewById(R.id.user_birth_year);
        final NumberPicker birthYearPicker = (NumberPicker) findViewById(R.id.user_birth_year_picker);
        // Setup birth year
        Integer birthYear = ((TodoApp) getApplication()).getUser().getBirthYear();
        birthYearItem.setLabelValue(birthYear == null ? "" : ""+birthYear);
        // Setup birth year picker
        if(birthYear == null) birthYear = MIN_VALUE;
        setNumberPicker(birthYearPicker, MIN_VALUE, MAX_VALUE, birthYear, true, null, null, new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                Integer birthYearValue = newVal;
                if(birthYearValue == MIN_VALUE) birthYearValue = null;
                birthYearItem.setLabelValue(birthYearValue == null ? "" : ""+birthYearValue);
                ((TodoApp) getApplication()).getUser().setBirthYear(birthYearValue);
                ((TodoApp) getApplication()).refreshUser();
            }
        });
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
                ((TodoApp) getApplication()).updateUser();
            }
        });
    }

    /**
     * Sets the value of the user weight and its listeners and callbacks.
     */
    private void setupWeight() {
        // Constants
        final int MIN_VALUE = 30;
        final int MAX_VALUE = 250;
        // Get user weight item & picker
        final ExpandItem weightItem = (ExpandItem) findViewById(R.id.user_weight);
        final NumberPicker weightPicker = (NumberPicker) findViewById(R.id.user_weight_picker);
        // Setup weight value
        Integer weight = ((TodoApp) getApplication()).getUser().getWeight();
        weightItem.setLabelValue(weight == null ? "" : "" + weight + " Kg");
        // Setup weight picker
        if(weight == null) weight = MIN_VALUE;
        setNumberPicker(weightPicker, MIN_VALUE, MAX_VALUE, weight, true, null, " Kg", new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                Integer weightValue = newVal;
                if(weightValue == MIN_VALUE) weightValue = null;
                weightItem.setLabelValue(weightValue == null ? "" : "" + weightValue + " Kg");
                ((TodoApp) getApplication()).getUser().setWeight(weightValue);
                ((TodoApp) getApplication()).refreshUser();
            }
        });
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
                ((TodoApp) getApplication()).updateUser();
            }
        });

    }

    /**
     * Sets the value of the user height and its listeners and callbacks.
     */
    private void setupHeight() {
        // Constants
        final int MIN_VALUE = 50;
        final int MAX_VALUE = 250;
        // Get user height item & picker
        final ExpandItem heightItem = (ExpandItem) findViewById(R.id.user_height);
        final NumberPicker heightPicker = (NumberPicker) findViewById(R.id.user_height_picker);
        // Setup weight
        Integer height = ((TodoApp) getApplication()).getUser().getHeight();
        heightItem.setLabelValue(height == null ? "" : "" + height + " cm");
        // Setup weight picker
        if(height == null) height = MIN_VALUE;
        setNumberPicker(heightPicker, MIN_VALUE, MAX_VALUE, height, true, null, " cm", new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                Integer heightValue = newVal;
                if(heightValue == MIN_VALUE) heightValue = null;
                heightItem.setLabelValue(heightValue == null ? "" : "" + heightValue + " cm");
                ((TodoApp) getApplication()).getUser().setHeight(heightValue);
                ((TodoApp) getApplication()).refreshUser();
            }
        });
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
                ((TodoApp) getApplication()).updateUser();
            }
        });
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
                startDeviceActivity();
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
                BluetoothService bluetoothService = ((TodoApp) getApplication()).getBluetoothService();
                if(bluetoothService != null && bluetoothService.isConnected()){
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
     * @param value - Value set to the number picker
     * @param firstAsNull - When this is true first value is used to define null value
     * @param onValueChangeListener - Listener of the number picker
     */
    @SuppressWarnings("SameParameterValue")
    private void setNumberPicker(NumberPicker numberPicker, int minValue, int maxValue, int value, boolean firstAsNull, String prefix, String suffix, NumberPicker.OnValueChangeListener onValueChangeListener) {
        // Set range values
        numberPicker.setMinValue(minValue);
        numberPicker.setMaxValue(maxValue);
        // Set displayed values
        if(firstAsNull){
            int nValues = maxValue - minValue + 1;
            String[] displayedValues = new String[nValues];
            displayedValues[0] = " ";
            for (int i=1; i<nValues; i++) {
                String auxValue = "";
                if(prefix != null) auxValue = auxValue + prefix;
                auxValue = auxValue + (minValue+i);
                if(suffix != null) auxValue = auxValue + suffix;
                displayedValues[i] = auxValue;
            }
            numberPicker.setDisplayedValues(displayedValues);
        }
        // Set user value
        numberPicker.setValue(value);
        // Set listener
        numberPicker.setOnValueChangedListener(onValueChangeListener);
        // Clear the focus
        numberPicker.clearFocus();
    }

}
