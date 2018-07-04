package edu.udg.exit.heartrate.Views;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;
import edu.udg.exit.heartrate.Components.ExpandItem;
import edu.udg.exit.heartrate.Interfaces.IMeasureView;
import edu.udg.exit.heartrate.R;
import edu.udg.exit.heartrate.Services.ApiService;
import edu.udg.exit.heartrate.Services.BluetoothService;
import edu.udg.exit.heartrate.TodoApp;
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

public class DeviceActivity extends Activity implements IMeasureView {

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
    private ExpandItem heartRateItem;
    private Boolean isMeasuring;

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
        bluetoothService.setMeasureView(this);

        // Get the api service
        apiService = ((TodoApp)getApplication()).getApiService();

        // Get the container
        container = (ViewGroup) findViewById(R.id.device_container);

        // Measure
        heartRateItem = (ExpandItem) findViewById(R.id.device_heart_rate);

        // Database
        db = new DataBase(this.getApplicationContext());

        // Setup contents
        setupContents();
    }

    @Override
    protected void onDestroy() {
        bluetoothService.unsetMeasureView();
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
    public void setHeartRate(final int heartRate) {
        if(isMeasuring) handler.post(new Runnable() {
                @Override
                public void run() {
                    heartRateItem.setLabelValue("" + heartRate);
                }
            });
    }

    @Override
    public void showText(final String text) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(DeviceActivity.this, text, Toast.LENGTH_SHORT).show();
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
        // TODO
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
        // TODO
    }

    /**
     * Sets the value of the device battery and its listeners and callbacks.
     */
    private void setupBattery(){
        // Get battery item
        final ExpandItem batteryItem = (ExpandItem) findViewById(R.id.device_battery);
        // Set listeners and callbacks
        batteryItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleContents(container.indexOfChild(batteryItem));
            }
        });
        // Setup battery
        // TODO - Use runnable
    }

    /**
     * Sets the value of heart rate measurement and its listeners and callbacks.
     */
    private void setupHeartRate(){
        // Get heart rate item & switch
        final ExpandItem heartRateItem = (ExpandItem) findViewById(R.id.device_heart_rate);
        final Switch heartRateSwitch = (Switch) findViewById(R.id.device_activate_heart_rate);
        Button heartRateUpload = (Button) findViewById(R.id.device_upload_heart_rate);
        // Setup heart rate
        String heartRateIsOn = UserPreferences.getInstance().load(getApplicationContext(),UserPreferences.HEART_RATE_MEASURE);
        if(heartRateIsOn == null || heartRateIsOn.equals("false")){
            heartRateSwitch.setChecked(false);
            isMeasuring = false;
        }else{
            heartRateSwitch.setChecked(true);
            isMeasuring = true;
        }
        // Set listeners and callbacks
        heartRateItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleContents(container.indexOfChild(heartRateItem));
            }
        });
        heartRateItem.setOnCollapseCallback(new Runnable() {
            @Override
            public void run() {
                if(heartRateSwitch.isChecked()){
                    isMeasuring = true;
                    UserPreferences.getInstance().save(getApplicationContext(),UserPreferences.HEART_RATE_MEASURE,"true");
                    bluetoothService.startMeasure();
                }else{
                    isMeasuring = false;
                    UserPreferences.getInstance().save(getApplicationContext(),UserPreferences.HEART_RATE_MEASURE,"false");
                    bluetoothService.stopMeasure();
                    heartRateItem.setLabelValue(null);
                }
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
    private void uploadHeartRate(){
        Date date = new Date();
        File file = db.exportAsCSV(DataBase.RATE_TABLE_NAME, null, date.getTime(), "HR_" + date.getTime() + ".csv");
        if(file != null){
            uploadFile(getApplicationContext(),file);
            db.delete(DataBase.RATE_TABLE_NAME, null, date.getTime());
        }

    }

    private void uploadFile(final Context ctx, File file){
        if (file != null){
            MultipartBody.Part body = Storage.getMultipartBody("file", file);

            apiService.getFileService().uploadFile(body).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if(response.isSuccessful())
                        Toast.makeText(ctx, "File uploaded!", Toast.LENGTH_LONG).show();
                    else
                        Toast.makeText(ctx, "Failed to upload the file!", Toast.LENGTH_LONG).show();
                }
                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    System.out.println(call.toString());
                    System.out.println(t.getMessage());
                    System.out.println(t.getLocalizedMessage());
                    t.printStackTrace();
                    Toast.makeText(ctx, "BAD CONNECTION", Toast.LENGTH_LONG).show();
                }
            });
        } else {
            Toast.makeText(ctx, "FAILED TO UPLOAD THE FILE", Toast.LENGTH_LONG).show();
        }
    }

}
