package edu.udg.exit.herthrate.Services;

import android.app.Service;
import android.bluetooth.*;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;

import android.os.IBinder;
import android.util.Log;
import edu.udg.exit.herthrate.Interfaces.IBluetoothService;
import edu.udg.exit.herthrate.Interfaces.IScanService;
import edu.udg.exit.herthrate.Interfaces.IScanView;
import edu.udg.exit.herthrate.Constants;
import edu.udg.exit.herthrate.MiBand.Services.MiBandService;
import edu.udg.exit.herthrate.MiBand.Services.MiliService;
import edu.udg.exit.herthrate.MiBand.Services.VibrationService;
import edu.udg.exit.herthrate.MiBand.Utils.BatteryInfo;
import edu.udg.exit.herthrate.MiBand.Utils.Latency;
import edu.udg.exit.herthrate.MiBand.Utils.MiDate;

import java.util.*;

/**
 * Bluetooth Low Energy Service
 */
public class BluetoothService extends Service implements IBluetoothService, IScanService {

    ////////////////
    // Attributes //
    ////////////////

    // Service
    private final IBinder bluetoothBinder = new BluetoothBinder();

    // Bluetooth
    private final BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();

    // Scan
    private final Map<String, BluetoothDevice> devices = new HashMap<>(10);
    private Boolean scanning;
    private IScanView scanView;
    private BluetoothAdapter.LeScanCallback scanCallback;

    // Connect
    private Boolean connecting;
    private BluetoothGatt connectGATT;
    private BluetoothGattCallback connectCallback;

    // MiBandServices
    private MiliService miliService;
    private VibrationService vibrationService;

    ////////////////////////////
    // Service implementation //
    ////////////////////////////

    @Override
    public void onCreate() {
        // Scan
        scanning = false;
        scanView = null;
        scanCallback = initScanCallback();

        // Connect
        connecting = false;
        connectGATT = null;
        connectCallback = initConnectCallback();

        // MiBandService
        miliService = null;
        vibrationService = null;

        // Log
        Log.d("BluetoothService", "onCreate()");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return bluetoothBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        // Scan
        devices.clear();
        scanning = null;
        scanView = null;
        scanCallback = null;

        super.onDestroy();
    }

    /**
     * Binder class that can return a reference to BluetoothService
     */
    public class BluetoothBinder extends Binder {
        public BluetoothService getService() {
            return BluetoothService.this;
        }
    }

    ////////////////////
    // Public methods //
    ////////////////////

    @Override
    public boolean hasBluetooth(){
        return adapter != null;
    }

    @Override
    public boolean isEnabled(){
        return adapter != null && adapter.isEnabled();
    }

    @Override
    public BluetoothDevice getRemoteDevice(String address) {
        return adapter.getRemoteDevice(address);
    }

    @Override
    public void connectRemoteDevice(BluetoothDevice device) {
        device.connectGatt(this,false,connectCallback);
    }

    /*----------------------*/
    /* IScanService methods */
    /*----------------------*/

    @Override
    public void setScanView(IScanView view){
        scanView = view;
    }

    @Override
    public void unSetScanView() {
        scanView = null;
    }

    @Override
    public void scanLeDevice(final boolean enable) {
        if (enable) startScan();
        else stopScan();
    }

    @Override
    public boolean isScanning(){
        return scanning;
    }

    @Override
    public void bindDevice(String address) {
        BluetoothDevice device = devices.get(address);

        String name = device.getName();
        if(name == null) name = "NO NAME";
        Log.d("BIND",name);

        connectRemoteDevice(device);


        // TODO - Set device address to user preferences
    }

    @Override
    public void unbindDevice() {
        // TODO - UNPAIR
        // TODO - Disconnect from remote device
        // TODO - Unset device address from user preferences
    }

    /////////////////////
    // Private methods //
    /////////////////////

    /*------------------*/
    /* Scanning methods */
    /*------------------*/

    /**
     * Creates a Bluetooth Low Energy ScanCallback
     * @return BluetoothAdapter.LeScanCallback
     */
    private BluetoothAdapter.LeScanCallback initScanCallback() {
        return new BluetoothAdapter.LeScanCallback() {
            @Override
            public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
                if(!devices.containsKey(device.toString())) {
                    devices.put(device.getAddress(),device);
                    if(scanView != null) scanView.addDevice(device);
                }
            }
        };
    }

    private final Handler handler = new Handler();
    private final Runnable stopRun = new Runnable() {
        @Override
        public void run() { stopScan(); }
    };

    /**
     * Starts scanning Bluetooth Low Energy Devices
     */
    private void startScan() {
        if(scanning) {
            stopScan();
            startScan();
        } else {
            scanning = true;
            devices.clear();
            if(scanView != null) scanView.clearView();
            if(scanView != null) scanView.startLoadingAnimation();
            handler.postDelayed(stopRun, SCAN_PERIOD);
            adapter.startLeScan(scanCallback);
        }
    }

    /**
     * Stops scanning Bluetooth Low Energy Devices
     */
    private void stopScan(){
        if(scanning) {
            scanning = false;
            handler.removeCallbacks(stopRun);
            if(scanView != null) scanView.stopLoadingAnimation();
            adapter.stopLeScan(scanCallback);
        }
    }

    /*--------------------*/
    /* Connecting methods */
    /*--------------------*/

    /**
     * Creates a Bluetooth GATT server callback
     * @return BluetoothGattCallback
     */
    private BluetoothGattCallback initConnectCallback() {
        return new BluetoothGattCallback() {
            @Override
            public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                super.onConnectionStateChange(gatt,status,newState);

                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    Log.d("GATT", "Device connected");
                    connectGATT = gatt;
                    connectGATT.discoverServices();
                } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    Log.d("GATT", "Device disconnected");
                    connectGATT = null;
                }
            }

            @Override
            public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                super.onServicesDiscovered(gatt,status);

                if (status == BluetoothGatt.GATT_SUCCESS) {
                    Log.d("GATT", "Services discovered");

                    // Init Services
                    miliService = new MiliService(gatt);
                    vibrationService = new VibrationService(gatt);

                    // Initialize - TODO
                    miliService.setLowLatency(); // Very important to obtain a faster connection
                }
            }

            @Override
            public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                super.onCharacteristicRead(gatt,characteristic,status);

                if (status == BluetoothGatt.GATT_SUCCESS) {
                    UUID characteristicUUID = characteristic.getUuid();
                    if (Constants.UUID_CHAR.DEVICE_INFO.equals(characteristicUUID)) {
                        Log.d("GATTread", "Info: " + characteristic.getValue());
                    } else if (Constants.UUID_CHAR.DEVICE_NAME.equals(characteristicUUID)) {
                        Log.d("GATTread", "Name: " + characteristic.getValue());
                    } else if (Constants.UUID_CHAR.BATTERY.equals(characteristicUUID)) {
                        BatteryInfo batteryInfo = new BatteryInfo(characteristic.getValue());
                        Log.d("GATTread", "Battery: " + batteryInfo);
                    } else if (Constants.UUID_CHAR.DATE_TIME.equals(characteristicUUID)) {
                        MiDate miDate = new MiDate(characteristic.getValue());
                        Log.d("GATTread", "Date: " + miDate);
                    } else {
                        if(characteristic.getValue().length>0){
                            Log.d("GATTread", "Characteristic: " + characteristic.getValue()[0]);
                        }else{
                            Log.d("GATTread", "Characteristic: " + characteristic.getValue());
                        }
                    }
                }

            }

            @Override
            public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                super.onCharacteristicWrite(gatt,characteristic,status);

                if (status == BluetoothGatt.GATT_SUCCESS) {
                    if(Constants.UUID_CHAR.DEVICE_INFO.equals(characteristic.getUuid())){
                        Log.d("GATTwrite", "Device information: " + characteristic.getValue());
                    }else if(Constants.UUID_CHAR.DEVICE_NAME.equals(characteristic.getUuid())){
                        Log.d("GATTwrite", "Device name: " + characteristic.getValue());
                    }else if(Constants.UUID_CHAR.NOTIFICATION.equals(characteristic.getUuid())){
                        Log.d("GATTwrite", "Notification: " + characteristic.getValue());
                    }else if(Constants.UUID_CHAR.USER_INFO.equals(characteristic.getUuid())){
                        Log.d("GATTwrite", "User information: " + characteristic.getValue());
                    }else if(Constants.UUID_CHAR.CONTROL_POINT.equals(characteristic.getUuid())){
                        Log.d("GATTwrite", "Control point: " + characteristic.getValue());
                    }else if(Constants.UUID_CHAR.REALTIME_STEPS.equals(characteristic.getUuid())){
                        Log.d("GATTwrite", "Realtime steps: " + characteristic.getValue());
                    }else if(Constants.UUID_CHAR.LE_PARAMS.equals(characteristic.getUuid())){
                        Log.d("GATTwrite", "Latency: " + characteristic.getValue()[0]);

                        //TODO - extract
                        miliService.enableNotifications();
                    }else if(Constants.UUID_CHAR.PAIR.equals(characteristic.getUuid())){
                        Log.d("GATTwrite", "PAIR: " + characteristic.getValue()[0]);

                        // TODO - extract
                        miliService.readPair();
                    }else if(Constants.UUID_CHAR.DATE_TIME.equals(characteristic.getUuid())){
                        MiDate miDate = new MiDate(characteristic.getValue());
                        Log.d("GATTwrite", "Date: " + miDate);
                    }else if(Constants.UUID_CHAR.BATTERY.equals(characteristic.getUuid())){
                        BatteryInfo batteryInfo = new BatteryInfo(characteristic.getValue());
                        Log.d("GATTwrite", "Battery: " + batteryInfo);
                    }else{
                        Log.d("GATTwrite", "Characteristic: " + characteristic.getValue()[0]);
                    }
                }
            }

            @Override
            public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
                super.onCharacteristicChanged(gatt,characteristic);

                if(Constants.UUID_CHAR.DEVICE_INFO.equals(characteristic.getUuid())){
                    Log.d("GATTchange", "Device information: " + characteristic.getValue());
                }else if(Constants.UUID_CHAR.DEVICE_NAME.equals(characteristic.getUuid())){
                    Log.d("GATTchange", "Device name: " + characteristic.getValue());
                }else if(Constants.UUID_CHAR.NOTIFICATION.equals(characteristic.getUuid())){
                    Log.d("GATTchange", "Notification: " + characteristic.getValue()[0]);
                    miliService.pair();
                }else if(Constants.UUID_CHAR.USER_INFO.equals(characteristic.getUuid())){
                    Log.d("GATTchange", "User information: " + characteristic.getValue());
                }else if(Constants.UUID_CHAR.CONTROL_POINT.equals(characteristic.getUuid())){
                    Log.d("GATTchange", "Control point: " + characteristic.getValue());
                }else if(Constants.UUID_CHAR.REALTIME_STEPS.equals(characteristic.getUuid())){
                    Log.d("GATTchange", "Realtime steps: " + characteristic.getValue());
                }else if(Constants.UUID_CHAR.ACTIVITY.equals(characteristic.getUuid())){
                    Log.d("GATTchange", "Activity: " + characteristic.getValue());
                }else if(Constants.UUID_CHAR.PAIR.equals(characteristic.getUuid())){
                    Log.d("GATTchange", "PAIR: " + characteristic.getValue()[0]);
                }else if(Constants.UUID_CHAR.DATE_TIME.equals(characteristic.getUuid())){
                    MiDate miDate = new MiDate(characteristic.getValue());
                    Log.d("GATTchange", "Date: " + miDate);
                }else if(Constants.UUID_CHAR.BATTERY.equals(characteristic.getUuid())){
                    BatteryInfo batteryInfo = new BatteryInfo(characteristic.getValue());
                    Log.d("GATTchange", "Battery: " + batteryInfo);
                }else{
                    Log.d("GATTchange", "Characteristic: " + characteristic.getValue());
                }

            }
        };
    }

}
