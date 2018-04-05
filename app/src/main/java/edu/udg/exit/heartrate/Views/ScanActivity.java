package edu.udg.exit.heartrate.Views;

import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.*;
import edu.udg.exit.heartrate.Activities.BluetoothActivity;
import edu.udg.exit.heartrate.Interfaces.IScanView;
import edu.udg.exit.heartrate.R;
import edu.udg.exit.heartrate.Services.BluetoothService;

import java.util.*;

public class ScanActivity extends BluetoothActivity implements IScanView {

    ///////////////
    // Constants //
    ///////////////

    private static final int REQUEST_ENABLE_BT_TO_SCAN = BluetoothService.REQUEST_ENABLE_BT_TO_SCAN;

    private static final String ATTRIBUTE_DEVICE_NAME = "device_name";
    private static final String ATTRIBUTE_DEVICE_ADDRESS = "device_address";

    ////////////////
    // Attributes //
    ////////////////

    private final List<Map<String, String>> devices = new ArrayList<>();
    private SimpleAdapter adapter;

    private ProgressBar progressBar;

    ///////////////////////
    // LifeCicle methods //
    ///////////////////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        // Set List view
        setListView();

        // Set Button actions
        setButtonActions();

        // Set Progress animation
        setProgressAnimation();
    }

    @Override
    protected void onServiceConnected(ComponentName name, BluetoothService.BluetoothBinder binder) {
        super.onServiceConnected(name, binder);

        // Set this activity as the scan view on bluetoothService
        bluetoothService.setScanView(ScanActivity.this);
        // Start scan devices
        startScanDevices();
    }

    @Override
    protected void onServiceDisconnected(ComponentName name) {
        // Unset scanView from bluetoothService
        if(bluetoothService != null) bluetoothService.unSetScanView();

        super.onServiceDisconnected(name);
    }

    @Override
    protected void onDestroy() {
        // Unset scanView from bluetoothService
        if(bluetoothService != null) bluetoothService.unSetScanView();

        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_ENABLE_BT_TO_SCAN:
                if(resultCode == -1){ // Bluetooth enabled
                    if(checkBluetooth(REQUEST_ENABLE_BT_TO_SCAN)){
                        bluetoothService.scanLeDevice(true);
                    }
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_ENABLE_BT_TO_SCAN:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){ // Location permissions granted
                    bluetoothService.scanLeDevice(true);
                }
                break;
        }
    }

    /////////////////////
    // Private methods //
    /////////////////////

    /**
     * Set the list view to an adapter that will be used to add devices.
     */
    private void setListView(){
        // Set Adapter
        String[] from = {ATTRIBUTE_DEVICE_NAME,ATTRIBUTE_DEVICE_ADDRESS};
        int[] to = {R.id.device_name,R.id.device_address};
        adapter = new SimpleAdapter(this,devices,R.layout.list_item_scan,from,to);

        // Set List view with the Adapter
        ListView lv_devices = (ListView) findViewById(R.id.lv_devices);
        lv_devices.setAdapter(adapter);

        // Set Item Click Listener
        lv_devices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Map<?, ?> item = (Map<?, ?>) parent.getAdapter().getItem(position); // Cannot cast to (Map<String,String>) without a warning
                bluetoothService.bindDevice((String) item.get(ATTRIBUTE_DEVICE_ADDRESS));
            }
        });
    }

    /**
     * Set the onClick listeners from the start button and the stop button.
     */
    private void setButtonActions() {
        Button startBtn = (Button) findViewById(R.id.btn_start);
        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startScanDevices();
            }
        });

        Button stopBtn = (Button) findViewById(R.id.btn_stop);
        stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopScanDevices();
            }
        });
    }

    /**
     * Set the animation for progress bar.
     */
    private void setProgressAnimation() {
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.INVISIBLE);
    }

    /**
     * Start scanning for Low Energy Bluetooth Devices.
     * Before all it checks if Bluetooth and Location are available.
     */
    private void startScanDevices() {
        if(checkBluetooth(REQUEST_ENABLE_BT_TO_SCAN)){
            if(checkLocation(REQUEST_ENABLE_BT_TO_SCAN)){
                bluetoothService.scanLeDevice(true);
            }
        }
    }

    /**
     * Stop scanning for Low Energy Bluetooth Devices.
     */
    private void stopScanDevices() {
        bluetoothService.scanLeDevice(false);
    }

    ////////////////////
    // Public methods //
    ////////////////////

    @Override
    public void addDevice(BluetoothDevice device) {
        HashMap<String,String> deviceInfo = new HashMap<>();
        deviceInfo.put(ATTRIBUTE_DEVICE_ADDRESS,device.getAddress());
        deviceInfo.put(ATTRIBUTE_DEVICE_NAME,device.getName());
        devices.add(deviceInfo);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void clearView() {
        devices.clear();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void startLoadingAnimation() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void stopLoadingAnimation() {
        progressBar.setVisibility(View.INVISIBLE);
    }
}