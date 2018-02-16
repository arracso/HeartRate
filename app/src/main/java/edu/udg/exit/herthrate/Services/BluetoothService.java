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
import edu.udg.exit.herthrate.Protocol;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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
    private final BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();;

    // Scan
    private final Map<String, BluetoothDevice> devices = new HashMap<>(10);

    private Boolean scanning;
    private IScanView scanView;
    private BluetoothAdapter.LeScanCallback scanCallback;

    // Connect
    private Boolean connecting;
    private BluetoothGatt connectGATT;
    private BluetoothGattCallback connectCallback;

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
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    Log.d("GATT", "Device connected");
                    connectGATT = gatt;

                    connectGATT.discoverServices();

                } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    // TODO - UNPAIR

                    Log.d("GATT", "Device disconnected");
                    connectGATT = null;
                }
            }

            @Override
            public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    //broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
                    Log.d("GATT", "Services discovered");

                    // TODO - PAIR

                    // Vibration test
                    BluetoothGattService service = connectGATT.getService(Protocol.UUID_SERVICE_VIBRATION);
                    BluetoothGattCharacteristic characteristic = service.getCharacteristic(Protocol.UUID_CHAR_VIBRATION);
                    characteristic.setValue(Protocol.VIBRATION_WITH_LED);
                    connectGATT.writeCharacteristic(characteristic);
                }
            }

            @Override
            public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    //broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
                    Log.d("GATT", "Characteristic read");
                }
            }
        };
    }

}
