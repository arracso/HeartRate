package edu.udg.exit.heartrate.Services;

import android.app.Service;
import android.bluetooth.*;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;

import android.os.IBinder;
import android.util.Log;
import edu.udg.exit.heartrate.Interfaces.IBluetoothService;
import edu.udg.exit.heartrate.Interfaces.IScanService;
import edu.udg.exit.heartrate.Interfaces.IScanView;
import edu.udg.exit.heartrate.MiBand.MiBandConnectionManager;
import edu.udg.exit.heartrate.MiBand.MiBandConstants;

import java.util.*;

/**
 * Bluetooth Low Energy Service
 */
public class BluetoothService extends Service implements IBluetoothService, IScanService {

    ///////////////
    // Constants //
    ///////////////

    public static final int REQUEST_ENABLE_BT_TO_START_SERVICE = 1;
    public static final int REQUEST_ENABLE_BT_TO_SCAN = 2;

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

    // Connection
    private ConnectionManager connectionManager;

    ////////////////////////////
    // Service implementation //
    ////////////////////////////

    @Override
    public void onCreate() {
        // Log
        Log.d("BluetoothService", "create");

        // Scan
        scanning = false;
        scanView = null;
        scanCallback = initScanCallback();

        // Connection
        connectionManager = new MiBandConnectionManager();
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

        // Connection
        connectionManager = null;

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
        if(connectionManager.isConnected()){
            connectionManager.disconnect();
            connectionManager = new MiBandConnectionManager();
        }else{
            device.connectGatt(this,false,connectionManager);
        }
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
