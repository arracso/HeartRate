package edu.udg.exit.heartrate.Services;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import edu.udg.exit.heartrate.Devices.ConnectionManager;
import edu.udg.exit.heartrate.Interfaces.*;
import edu.udg.exit.heartrate.Devices.MiBand.MiBandConnectionManager;
import edu.udg.exit.heartrate.Devices.MiBand.MiBandConstants;
import edu.udg.exit.heartrate.Utils.UserPreferences;

import java.util.HashMap;
import java.util.Map;

/**
 * Bluetooth Low Energy Service
 */
public class BluetoothService extends Service implements IBluetoothService, IScanService, IPairService {

    ///////////////
    // Constants //
    ///////////////

    public static final int REQUEST_ENABLE_BT_TO_SCAN = 1;
    public static final int REQUEST_ENABLE_BT_TO_PAIR = 2;

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

    // Pair
    private IPairView pairView;

    // Connection
    private ConnectionManager connectionManager;

    ////////////////////////////
    // Service implementation //
    ////////////////////////////

    @Override
    public void onCreate() {
        super.onCreate();

        // Log
        Log.d("BluetoothService", "create");

        // Scan
        scanning = false;
        scanView = null;
        scanCallback = initScanCallback();

        // Connection
        connectionManager = new MiBandConnectionManager(this);

        // Check user preferences for device address
        String boundAddress = UserPreferences.getInstance().load(this, UserPreferences.BONDED_DEVICE_ADDRESS);
        Log.d("BluetoothService", "Address " + boundAddress);
        if(boundAddress != null) connectRemoteDevice(getRemoteDevice(boundAddress));
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
        // Log
        Log.d("BluetoothService", "destroy");

        // Scan
        devices.clear();
        scanning = null;
        scanView = null;
        scanCallback = null;

        // Connection
        if(connectionManager != null){
            connectionManager.disconnect();
            connectionManager = null;
        }

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
        device.connectGatt(this,false,connectionManager);
    }

    @Override
    public boolean isConnected() {
        return connectionManager != null && connectionManager.isConnected();
    }

    /*----------------------*/
    /* IScanService methods */
    /*----------------------*/

    @Override
    public IScanView getScanView() {
        return scanView;
    }

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

    /*----------------------*/
    /* IPairService methods */
    /*----------------------*/

    @Override
    public IPairView getPairView() {
        return pairView;
    }

    @Override
    public void setPairView(IPairView view) {
        pairView = view;
    }

    @Override
    public void unSetPairView() {
        pairView = null;
    }

    @Override
    public void bindDevice(String address) {
        // Get the device
        BluetoothDevice device = devices.get(address);
        // Save device address to user preferences
        UserPreferences.getInstance().save(this, UserPreferences.BONDED_DEVICE_ADDRESS, address);
        // Connect to remote device
        if(!connectionManager.isConnected()){
            connectRemoteDevice(device);
            pairView.startLoadingAnimation();
            pairView.setMessage("pairing (" + address + ")");
        }else{
            pairView.stopLoadingAnimation();
            pairView.setMessage("Failed");
        }
    }

    @Override
    public void unbindDevice() {
        // TODO - UNPAIR
        // Disconnect from remote device
        if(connectionManager != null) connectionManager.disconnect();
        // Unset device address from user preferences
        UserPreferences.getInstance().remove(this, UserPreferences.BONDED_DEVICE_ADDRESS);
    }

    /////////////////////
    // Private methods //
    /////////////////////

    /**
     * Creates a Bluetooth Low Energy ScanCallback
     * @return BluetoothAdapter.LeScanCallback
     */
    private BluetoothAdapter.LeScanCallback initScanCallback() {
        return new BluetoothAdapter.LeScanCallback() {
            @Override
            public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
                boolean supportedDevice = false;
                if(device.getAddress().startsWith(MiBandConstants.MODEL.MI1A)) supportedDevice = false;// TODO - Support (or remove)
                if(device.getAddress().startsWith(MiBandConstants.MODEL.MI1S)) supportedDevice = true;

                if(supportedDevice && !devices.containsKey(device.toString())) {
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
}
