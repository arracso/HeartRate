package edu.udg.exit.heartrate.Services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.*;
import android.provider.Settings;
import android.util.Log;
import edu.udg.exit.heartrate.Devices.ConnectionManager;
import edu.udg.exit.heartrate.Interfaces.*;
import edu.udg.exit.heartrate.Devices.MiBand.MiBandConnectionManager;
import edu.udg.exit.heartrate.Devices.MiBand.MiBandConstants;
import edu.udg.exit.heartrate.Utils.DataBase;
import edu.udg.exit.heartrate.Utils.UserPreferences;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Bluetooth Low Energy Service
 */
public class BluetoothService extends Service implements IBluetoothService, IScanService, IPairService, IDeviceService {

    ///////////////
    // Constants //
    ///////////////

    public static final int REQUEST_ENABLE_BT_TO_SCAN = 1;
    public static final int REQUEST_ENABLE_BT_TO_PAIR = 2;

    ////////////////
    // Attributes //
    ////////////////

    // Wakelock
    private PowerManager.WakeLock wakeLock = null;

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

    // Measure
    private IDeviceView deviceView;

    // Data base
    private DataBase dataBase;

    // Connection
    private ConnectionManager connectionManager;

    ////////////////////////////
    // Service implementation //
    ////////////////////////////

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d("BluetoothService", "onCreate");

        // Wakelock
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        if(powerManager!= null) wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,"BluetoothWakeLock");
        if(wakeLock != null) wakeLock.acquire();

        // Scan
        scanning = false;
        scanView = null;
        scanCallback = initScanCallback();

        // Pair
        pairView = null;

        // Device
        deviceView = null;

        // Data Base
        dataBase = new DataBase(getApplicationContext());

        // Connection
        connectionManager = new MiBandConnectionManager(this);

        // Check user preferences for device address & connect to it
        String boundAddress = UserPreferences.getInstance().load(this, UserPreferences.BONDED_DEVICE_ADDRESS);
        if(boundAddress != null) connectRemoteDevice(getRemoteDevice(boundAddress));
    }

    @Override
    public IBinder onBind(Intent intent) {
        return bluetoothBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent,flags,startId);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d("BluetoothService", "onDestroy");
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

        // Release wakeLock
        wakeLock.release();

        // Restart Bluetooth
        Intent restartBluetooth = new Intent(".RestartBluetooth");
        sendBroadcast(restartBluetooth);
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
        device.connectGatt(this,true,connectionManager);
    }

    @Override
    public boolean isConnected() {
        return connectionManager != null && connectionManager.isConnected();
    }

    public boolean isWorking()  {
        return this.connectionManager.isWorking();
    }

    public void restartWork() {
        this.connectionManager.run();
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
    public void setDevicePaired() {
        String heartRateMeasure = UserPreferences.getInstance().load(getApplicationContext(),UserPreferences.HEART_RATE_MEASURE);
        if(heartRateMeasure != null && heartRateMeasure.equals("true")) startHeartRateMeasure();
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

    /*-------------------------*/
    /* IDeviceService methods */
    /*-------------------------*/

    @Override
    public void setDeviceView(IDeviceView view) {
        this.deviceView = view;
    }

    @Override
    public void unsetDeviceView() {
        this.deviceView = null;
    }

    @Override
    public void startHeartRateMeasure() {
        connectionManager.startHeartRateMeasure();
        // Enable receiver that restarts this service
        ComponentName receiver = new ComponentName(this, BluetoothRestarterBroadcastReceiver.class);
        PackageManager pm = getPackageManager();
        pm.setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
        // Set Alarm
        AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        Intent bluetoothRestarter = new Intent(".RestartBluetooth");
        PendingIntent alarmIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, bluetoothRestarter, 0);
        if(alarmManager != null){
            int milis = 10 * 60 * 1000;
            alarmManager.cancel(alarmIntent);
            alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,SystemClock.elapsedRealtime() + milis, milis, alarmIntent);
        }
        // Avoid doze mode
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Intent intent = new Intent();
            String packageName = getPackageName();
            PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
            if (powerManager != null && !powerManager.isIgnoringBatteryOptimizations(packageName)) {
                intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:" + packageName));
                startActivity(intent);
            }
        }
    }

    @Override
    public void stopHeartRateMeasure() {
        connectionManager.stopHeartRateMeasure();
        // Disable receiver that restarts this service
        ComponentName receiver = new ComponentName(this, BluetoothRestarterBroadcastReceiver.class);
        PackageManager pm = getPackageManager();
        pm.setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
        // Unset alarm
        AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        Intent bluetoothRestarter = new Intent(".RestartBluetooth");
        PendingIntent alarmIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, bluetoothRestarter, 0);
        if(alarmManager != null) alarmManager.cancel(alarmIntent);
    }

    @Override
    public void setHeartRateMeasure(Date date, Integer measure) {
        dataBase.insertRate(date.getTime(),measure);
        if(deviceView != null) deviceView.setHeartRateMeasure(measure);
    }

    @Override
    public void retrieveBatteryLevel() {
        connectionManager.retrieveBatteryLevel();
    }

    @Override
    public void setBatteryLevel(Integer battery) {
        if(deviceView != null) deviceView.setBatteryLevel(battery);
    }

    @Override
    public void setWearLocation(int wearLocation) {
        connectionManager.setWearLocation(wearLocation);
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
