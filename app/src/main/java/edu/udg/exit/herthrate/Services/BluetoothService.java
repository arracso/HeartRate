package edu.udg.exit.herthrate.Services;

import android.app.Service;
import android.bluetooth.*;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;

import android.os.IBinder;
import android.text.format.DateUtils;
import android.util.Log;
import android.widget.Toast;
import edu.udg.exit.herthrate.Interfaces.IBluetoothService;
import edu.udg.exit.herthrate.Interfaces.IScanService;
import edu.udg.exit.herthrate.Interfaces.IScanView;
import edu.udg.exit.herthrate.Constants;
import edu.udg.exit.herthrate.MiBand.BatteryInfo;
import edu.udg.exit.herthrate.MiBand.MiDate;

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
                    // Initialize - TODO

                    setLowLatency(); // Very important to obtain a faster connection

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
                        enableNotifications();
                    }else if(Constants.UUID_CHAR.PAIR.equals(characteristic.getUuid())){
                        Log.d("GATTwrite", "PAIR: " + characteristic.getValue()[0]);

                        // TODO - extract
                        readPair();
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
                    pair();
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

    ////////////////////////////////
    // MiBand services methods    //
    ////////////////////////////////
    // Need to discover services  //
    // before using these methods //
    ////////////////////////////////

    /**
     * Enables or disables a notification
     * @param serviceUUID
     * @param characteristicUUID
     * @param descriptorUUID
     * @param enable
     */
    public void setCharacteristicNotification(UUID serviceUUID, UUID characteristicUUID, UUID descriptorUUID, boolean enable){
        // Retrieve the service
        BluetoothGattService service = connectGATT.getService(serviceUUID);
        if(service == null) {
            Log.w("GATT", "Service not found: " + serviceUUID);
            return;
        }

        // Retrieve  the characteristic
        BluetoothGattCharacteristic characteristic = service.getCharacteristic(characteristicUUID);
        if(characteristic == null) {
            Log.w("GATT", "Characteristic not found: " + characteristicUUID);
            return;
        }

        // Enable or disable the notification
        if(connectGATT.setCharacteristicNotification(characteristic,enable)){
            // Retrieve the descriptor
            BluetoothGattDescriptor descriptor = characteristic.getDescriptor(descriptorUUID);
            if (descriptor == null) {
                Log.w("GATT","Descriptor not found:" + descriptorUUID);
                return;
            }

            // Sets descriptor value
            int properties = characteristic.getProperties();
            if ((properties & BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
                descriptor.setValue(enable ? BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE : BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
            } else if ((properties & BluetoothGattCharacteristic.PROPERTY_INDICATE) > 0) {
                descriptor.setValue(enable ? BluetoothGattDescriptor.ENABLE_INDICATION_VALUE : BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
            } else return;

            // Write the descriptor to the device
            connectGATT.writeDescriptor(descriptor);
        }else{
            Log.e("GATT", "Unable to enable notifications.");
        }
    }

    /**
     * Reads a value from a characteristic of the service.
     * @param serviceUUID
     * @param characteristicUUID
     */
    public void readCharacteristic(UUID serviceUUID, UUID characteristicUUID) {
        // Retrieve the service
        BluetoothGattService service = connectGATT.getService(serviceUUID);
        if(service == null) {
            Log.d("GATT", "Service not found: " + serviceUUID);
            return;
        }
        // Retrive the characteristic
        BluetoothGattCharacteristic characteristic = service.getCharacteristic(characteristicUUID);
        if(characteristic == null) {
            Log.d("GATT", "Characteristic not found: " + characteristicUUID);
            return;
        }
        // Read the characteristic from the device
        connectGATT.readCharacteristic(characteristic);
    }

    /**
     * Writes a value to a characteristic of the service.
     * @param serviceUUID
     * @param characteristicUUID
     * @param value
     */
    public void writeCharacteristic(UUID serviceUUID, UUID characteristicUUID, byte[] value) {
        // Retrieve the service
        BluetoothGattService service = connectGATT.getService(serviceUUID);
        if(service == null) {
            Log.d("GATT", "Service not found: " + serviceUUID);
            return;
        }
        // Retrive the characteristic
        BluetoothGattCharacteristic characteristic = service.getCharacteristic(characteristicUUID);
        if(characteristic == null) {
            Log.d("GATT", "Characteristic not found: " + characteristicUUID);
            return;
        }
        // Set the value of the characteristic
        characteristic.setValue(value);
        // Write the characteristic to the device
        connectGATT.writeCharacteristic(characteristic);
    }

    /*--------------*/
    /* MILI SERVICE */
    /*--------------*/

    /**
     * Enables notifications
     * REQUIREMENT : ANY
     */
    public void enableNotifications() {
        setCharacteristicNotification(Constants.UUID_SERVICE.MILI,Constants.UUID_CHAR.NOTIFICATION,Constants.UUID_DESC.UPDATE_NOTIFICATION,true);
    }

    /**
     * Sets the lowest latency possible.
     * REQUIREMENT : ANY
     */
    public void setLowLatency() {
        writeCharacteristic(Constants.UUID_SERVICE.MILI,Constants.UUID_CHAR.LE_PARAMS,getLowLatency());
    }

    private byte[] getLatency(int minConnectionInterval, int maxConnectionInterval, int latency, int timeout, int advertisementInterval) {
        byte result[] = new byte[12];
        result[0] = (byte) (minConnectionInterval & 0xff);
        result[1] = (byte) (0xff & minConnectionInterval >> 8);
        result[2] = (byte) (maxConnectionInterval & 0xff);
        result[3] = (byte) (0xff & maxConnectionInterval >> 8);
        result[4] = (byte) (latency & 0xff);
        result[5] = (byte) (0xff & latency >> 8);
        result[6] = (byte) (timeout & 0xff);
        result[7] = (byte) (0xff & timeout >> 8);
        result[8] = 0;
        result[9] = 0;
        result[10] = (byte) (advertisementInterval & 0xff);
        result[11] = (byte) (0xff & advertisementInterval >> 8);

        return result;
    }

    private byte[] getLowLatency() {
        int minConnectionInterval = 39;
        int maxConnectionInterval = 49;
        int latency = 0;
        int timeout = 500;
        int advertisementInterval = 0;

        return getLatency(minConnectionInterval, maxConnectionInterval, latency, timeout, advertisementInterval);
    }

    /**
     * Read date time
     * REQUIREMENT: TODO - It isn't reading.
     */
    public void readDate() {
        readCharacteristic(Constants.UUID_SERVICE.MILI,Constants.UUID_CHAR.DATE_TIME);
    }

    /**
     * Write Pair
     * REQUIREMENT: TODO - Read data time???? (for the moment seems to be working).
     */
    public void pair() {
        writeCharacteristic(Constants.UUID_SERVICE.MILI,Constants.UUID_CHAR.PAIR,Constants.PROTOCOL.PAIR);
    }

    /**
     * Read pair
     * REQUIREMENT : ANY
     */
    public void readPair() {
        readCharacteristic(Constants.UUID_SERVICE.MILI,Constants.UUID_CHAR.PAIR);
    }

    /**
     * Read battery
     * REQUIREMENT: ANY
     */
    public void readBattery() {
        readCharacteristic(Constants.UUID_SERVICE.MILI,Constants.UUID_CHAR.BATTERY);
    }

    /**
     * Self test - Mi Band will do crazy things.
     * REQUIREMENT -> TODO - PAIR ????.
     * WARNING -> Will need to unlink miband from bluetooth.
     */
    public void selfTest() {
        writeCharacteristic(Constants.UUID_SERVICE.MILI,Constants.UUID_CHAR.TEST,Constants.PROTOCOL.SELF_TEST);
    }

    /*-------------------*/
    /* VIBRATION SERVICE */
    /*-------------------*/

    /**
     *  Vibration with led.
     *  REQUIREMENT: ANY.
     */
    public void vibrationWithLed() {
        writeCharacteristic(Constants.UUID_SERVICE.VIBRATION,Constants.UUID_CHAR.VIBRATION,Constants.PROTOCOL.VIBRATION_WITH_LED);
    }

    /**
     *  Vibration without led.
     *  REQUIREMENT: ANY.
     */
    public void vibrationWithoutLed() {
        writeCharacteristic(Constants.UUID_SERVICE.VIBRATION,Constants.UUID_CHAR.VIBRATION,Constants.PROTOCOL.VIBRATION_WITHOUT_LED);
    }

    /**
     * Vibration 10 times with led.
     * REQUIREMENT: TODO - PAIR ????.
     */
    public void vibration10TimesWithLed() {
        writeCharacteristic(Constants.UUID_SERVICE.VIBRATION,Constants.UUID_CHAR.VIBRATION,Constants.PROTOCOL.VIBRATION_10_TIMES_WITH_LED);
    }



}
