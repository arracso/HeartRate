package edu.udg.exit.heartrate.Views;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import edu.udg.exit.heartrate.Components.ExpandItem;
import edu.udg.exit.heartrate.Interfaces.IDeviceView;
import edu.udg.exit.heartrate.R;
import edu.udg.exit.heartrate.Services.ApiService;
import edu.udg.exit.heartrate.Services.BluetoothService;
import edu.udg.exit.heartrate.TodoApp;
import edu.udg.exit.heartrate.Utils.CallBack;
import edu.udg.exit.heartrate.Utils.DataBase;
import edu.udg.exit.heartrate.Utils.Storage;
import edu.udg.exit.heartrate.Utils.UserPreferences;
import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.File;
import java.util.Date;

public class DeviceActivity extends Activity implements IDeviceView {

    ///////////////
    // Variables //
    ///////////////

    // Handler
    private Handler handler;

    // Service
    private BluetoothService bluetoothService;
    private ApiService apiService;

    // Content
    private int oldContent = -1;
    private ViewGroup container = null;

    // Measure heart rate
    private Boolean isMeasuring;
    private ExpandItem heartRateItem;

    // Battery
    private ExpandItem batteryItem;

    // Database
    private DataBase db;

    ///////////////////////
    // Lifecycle methods //
    ///////////////////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);

        // Handler
        handler = new Handler();

        // Get the bluetooth service & set measure view on it
        bluetoothService = ((TodoApp)getApplication()).getBluetoothService();
        bluetoothService.setDeviceView(this);

        // Get the api service
        apiService = ((TodoApp)getApplication()).getApiService();

        // Get the container
        container = (ViewGroup) findViewById(R.id.device_container);

        // Items
        heartRateItem = (ExpandItem) findViewById(R.id.device_heart_rate);
        batteryItem = (ExpandItem) findViewById(R.id.device_battery);

        // Database
        db = new DataBase(this.getApplicationContext());

        // Setup contents
        setupContents();
    }

    @Override
    protected void onDestroy() {
        bluetoothService.unsetDeviceView();
        super.onDestroy();
    }

    ////////////////////
    // Public methods //
    ////////////////////

    /**
     * Unbind the current binded device.
     * @param view - MainActivity view
     */
    public void unbindDevice(View view) {
        (((TodoApp) this.getApplication())).getBluetoothService().unbindDevice();
        this.finish();
    }

    @Override
    public Context getContext(){
        return DeviceActivity.this;
    }

    @Override
    public void setHeartRateMeasure(final int heartRate) {
        if(isMeasuring) handler.post(new Runnable() {
                @Override
                public void run() {
                    heartRateItem.setLabelValue("- " + heartRate + " -");
                }
            });
    }

    @Override
    public void setBatteryLevel(final int battery) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                batteryItem.setLabelValue("" + battery + "%");
            }
        });
    }

    /////////////////////
    // Private Methods //
    /////////////////////

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
        setupMAC();
        setupName();
        setupBattery();
        setupDeviceHand();
        setupHeartRate();
    }

    /**
     * Sets the value of the device MAC and its listeners and callbacks.
     */
    private void setupMAC() {
        // Get MAC item
        final ExpandItem macItem = (ExpandItem) findViewById(R.id.device_mac);
        // Set listeners and callbacks
        macItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleContents(container.indexOfChild(macItem));
            }
        });
        // Setup MAC
        String address = UserPreferences.getInstance().load(getApplicationContext(), UserPreferences.BONDED_DEVICE_ADDRESS);
        macItem.setLabelValue(address);
    }

    /**
     * Sets the value of the device name and its listeners and callbacks.
     */
    private void setupName() {
        // Get name item
        final ExpandItem nameItem = (ExpandItem) findViewById(R.id.device_name);
        // Set listeners and callbacks
        nameItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleContents(container.indexOfChild(nameItem));
            }
        });
        // Setup name
        String address = UserPreferences.getInstance().load(getApplicationContext(), UserPreferences.BONDED_DEVICE_ADDRESS);
        nameItem.setLabelValue(bluetoothService.getRemoteDevice(address).getName());
    }

    /**
     * Sets the value of the device battery and its listeners and callbacks.
     */
    private void setupBattery() {
        // Get battery item
        final ExpandItem batteryItem = (ExpandItem) findViewById(R.id.device_battery);
        // Set listeners and callbacks
        batteryItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleContents(container.indexOfChild(batteryItem));
                bluetoothService.retrieveBatteryLevel();
            }
        });
        // Setup battery
        bluetoothService.retrieveBatteryLevel();
    }

    /**
     * Sets the value of the device hand item and its listeners.
     */
    private void setupDeviceHand() {
        // Gets device hand item
        final ExpandItem deviceHandItem = (ExpandItem) findViewById(R.id.device_hand);
        final RadioGroup deviceHandPicker = (RadioGroup) findViewById(R.id.device_hand_picker);
        // Sets device hand item
        final String deviceHand = UserPreferences.getInstance().load(bluetoothService.getApplicationContext(),UserPreferences.DEVICE_HAND);
        deviceHandItem.setLabelValue(deviceHand == null ? "left" : deviceHand);
        // Setup device hand picker
        deviceHandPicker.check((deviceHand == null || deviceHand.equals("left")) ? R.id.device_radio_left : R.id.device_radio_right);
        // Set listeners and callbacks
        deviceHandItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleContents(container.indexOfChild(deviceHandItem));
            }
        });
        deviceHandPicker.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int wearLocation = 0;
                if(checkedId == R.id.device_radio_right) wearLocation = 1;
                deviceHandItem.setLabelValue(wearLocation == 0 ? "left" : "right");
                bluetoothService.setWearLocation(wearLocation);
            }
        });
    }

    /**
     * Sets the value of heart rate measurement and its listeners and callbacks.
     */
    private void setupHeartRate() {
        // Get heart rate item & switch
        final ExpandItem heartRateItem = (ExpandItem) findViewById(R.id.device_heart_rate);
        final Switch heartRateSwitch = (Switch) findViewById(R.id.device_activate_heart_rate);
        Button heartRateUpload = (Button) findViewById(R.id.device_upload_heart_rate);
        // Setup heart rate
        heartRateItem.setLabelValue("-  -");
        String heartRateIsOn = UserPreferences.getInstance().load(getApplicationContext(),UserPreferences.HEART_RATE_MEASURE);
        isMeasuring = heartRateIsOn != null && !heartRateIsOn.equals("false");
        heartRateSwitch.setChecked(isMeasuring);
        // Set listeners and callbacks
        heartRateItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleContents(container.indexOfChild(heartRateItem));
            }
        });
        heartRateSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isMeasuring = isChecked;
                heartRateItem.setLabelValue("-  -");
                UserPreferences.getInstance().save(getApplicationContext(),UserPreferences.HEART_RATE_MEASURE, isChecked ? "true" : "false");
                if(isChecked) bluetoothService.startHeartRateMeasure();
                else bluetoothService.stopHeartRateMeasure();
            }
        });
        heartRateUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadHeartRate();
            }
        });
    }

    /**
     * Uploads heart rate measurements to the server.
     */
    private void uploadHeartRate() {
        final Date date = new Date();
        File file = db.exportAsCSV(DataBase.RATE_TABLE_NAME, null, date.getTime(), "HR");
        if(file != null){
            uploadFile(file, new CallBack() {
                @Override
                public void onSuccess(int code) {
                    Toast.makeText(DeviceActivity.this, "File uploaded!", Toast.LENGTH_LONG).show();
                    db.delete(DataBase.RATE_TABLE_NAME, null, date.getTime());
                }
                @Override
                public void onFailure(int code) {
                    switch (code){
                        case CallBack.BAD_CONNECTION:
                            Toast.makeText(DeviceActivity.this, "BAD CONNECTION", Toast.LENGTH_LONG).show();
                            break;
                        case CallBack.ERROR:
                        default:
                            Toast.makeText(DeviceActivity.this, "FAILED TO UPLOAD THE FILE", Toast.LENGTH_LONG).show();
                            break;
                    }
                }
            });

        }
    }

    /**
     * Upload a file to the server.
     * @param file - file to be upload
     * @param callback - callback to be run on success or on failure
     */
    private void uploadFile(File file, final CallBack callback) {
        if (file != null){
            MultipartBody.Part body = Storage.getMultipartBody("file", file);

            apiService.getFileService().uploadFile(body).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if(response.isSuccessful()) callback.onSuccess(CallBack.SUCCESS);
                    else callback.onFailure(CallBack.ERROR);
                }
                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    callback.onFailure(CallBack.BAD_CONNECTION);
                }
            });
        }
        else callback.onFailure(CallBack.ERROR);
    }

}
