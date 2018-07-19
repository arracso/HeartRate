package edu.udg.exit.heartrate.Services;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.*;
import android.provider.Settings;
import android.util.Log;
import edu.udg.exit.heartrate.Devices.ConnectionManager;
import edu.udg.exit.heartrate.Devices.MiBand.MiBandConstants;
import edu.udg.exit.heartrate.Interfaces.*;
import edu.udg.exit.heartrate.Devices.MiBand.MiBandConnectionManager;
import edu.udg.exit.heartrate.Utils.DataBase;
import edu.udg.exit.heartrate.Utils.UserPreferences;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Bluetooth Low Energy Service.
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

    // Service
    private final IBinder bluetoothBinder = new BluetoothBinder();

    // Bluetooth
    private final BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();

    // Wakelock
    private PowerManager.WakeLock wakeLock;

    // Scan
    private final Map<String, BluetoothDevice> devices = new HashMap<>(10);
    private Boolean scanning;
    private IScanView scanView;
    private ScanCallback scanCallback;

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
        wakeLock = null;
        if(powerManager!= null) wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,"BluetoothWakeLock");

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
        connectionManager = null;

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

        // Release wakeLock
        wakeLock.release();

        // Destroy the service
        super.onDestroy();

        // Restart the service (send message)
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
    public void connectRemoteDevice(final BluetoothDevice device) {
        if(connectionManager != null) connectionManager.disconnect();
        // TODO - Select Connection manager with device address (or device name)
        connectionManager = new MiBandConnectionManager(this);
        if(device != null) device.connectGatt(this,false,connectionManager);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(!isConnected()) connectRemoteDevice(device);
            }
        }, 5 * 60 * 1000);
    }

    @Override
    public boolean isConnected() {
        return connectionManager != null && connectionManager.isConnected();
    }

    @Override
    public boolean isWorking()  {
        return this.connectionManager.isWorking();
    }

    @Override
    public void restartWork() {
        // Clear all calls
        this.connectionManager.clearCalls();
        // Restart heart rate measurement
        String isMeasureOn = UserPreferences.getInstance().load(getApplicationContext(),UserPreferences.HEART_RATE_MEASURE);
        if(isMeasureOn != null && isMeasureOn.equals("true")) this.connectionManager.startHeartRateMeasure();
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
        // Start heart rate if needed
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
        if(connectionManager == null || !connectionManager.isConnected()){
            connectRemoteDevice(device);
            if(pairView != null){
                pairView.startLoadingAnimation();
                pairView.setMessage("pairing (" + address + ")");
            }
        }else if(pairView != null){
            pairView.stopLoadingAnimation();
            pairView.setMessage("Failed");
        }
    }

    @Override
    public void unbindDevice() {
        // Disconnect from remote device
        if(connectionManager != null) connectionManager.disconnect();
        // Unset device address & band settings from user preferences
        UserPreferences.getInstance().remove(this, UserPreferences.BONDED_DEVICE_ADDRESS);
        UserPreferences.getInstance().remove(this, UserPreferences.DEVICE_HAND);
        UserPreferences.getInstance().remove(this, UserPreferences.HEART_RATE_MEASURE);
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
        // Start the measure
        if(connectionManager != null) connectionManager.startHeartRateMeasure();
        // Acquire wake lock
        if(wakeLock != null) wakeLock.acquire(10 * 24 * 60 * 60 * 1000);
        // Enable receiver that restarts this service
        enableReceiver(BluetoothRestarterBroadcastReceiver.class);
        // Set alarm to check service status every 10 minutes
        setInexactRepeatingAlarm(0, ".RestartBluetooth", 10 * 60 * 1000);
        // Set battery optimizations to avoid Doze mode (needs permissions)
        setBatteryOptimizations();
        //getSpecialPermissions();
    }

    @Override
    public void stopHeartRateMeasure() {
        // Stop the measure
        if(connectionManager != null) connectionManager.stopHeartRateMeasure();
        // Release wake lock
        if(wakeLock != null) wakeLock.release();
        // Disable receiver that restarts this service
        disableReceiver(BluetoothRestarterBroadcastReceiver.class);
        // Unset alarm that checks this service status
        unsetAlarm(0, ".RestartBluetooth");
    }

    @Override
    public void setHeartRateMeasure(Date date, Integer measure) {
        if(dataBase == null) dataBase = new DataBase(getApplicationContext());
        dataBase.insertRate(date.getTime(),measure);
        if(deviceView != null) deviceView.setHeartRateMeasure(measure);
    }

    @Override
    public void retrieveBatteryLevel() {
        if(connectionManager != null) connectionManager.retrieveBatteryLevel();
    }

    @Override
    public void setBatteryLevel(Integer battery) {
        if(deviceView != null) deviceView.setBatteryLevel(battery);
    }

    @Override
    public void setWearLocation(int wearLocation) {
        if(connectionManager != null) connectionManager.setWearLocation(wearLocation);
    }

    /////////////////////
    // Private methods //
    /////////////////////

    /**
     * Enables a receiver.
     * @param receiverClass - Class of the receiver to be enabled
     */
    @SuppressWarnings("SameParameterValue")
    private void enableReceiver(Class receiverClass) {
        ComponentName receiver = new ComponentName(getApplicationContext(), receiverClass);
        PackageManager packageManager = getPackageManager();
        packageManager.setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
    }

    /**
     * Disables a receiver.
     * @param receiverClass - Class of the receiver to be disabled
     */
    @SuppressWarnings("SameParameterValue")
    private void disableReceiver(Class receiverClass) {
        ComponentName receiver = new ComponentName(getApplicationContext(), receiverClass);
        PackageManager packageManager = getPackageManager();
        packageManager.setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
    }

    /**
     * Sets a repeating alarm.
     * @param action - Action that performs the alarm when triggered
     * @param milis - Alarm period
     */
    @SuppressWarnings("SameParameterValue")
    private void setInexactRepeatingAlarm(int requestCode, String action, int milis){
        // Set action intent
        Intent actionIntent = new Intent(action);
        // Set alarm intent
        PendingIntent alarmIntent = PendingIntent.getBroadcast(getApplicationContext(), requestCode, actionIntent, 0);
        // Set alarm manager
        AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        // Set alarm
        if(alarmManager != null){
            alarmManager.cancel(alarmIntent);
            alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,SystemClock.elapsedRealtime() + milis, milis, alarmIntent);
        }
    }

    /**
     * Unsets a repeating alarm.
     * @param action - Action that performs the alarm when triggered
     */
    @SuppressWarnings("SameParameterValue")
    private void unsetAlarm(int requestCode, String action){
        // Set action intent
        Intent actionIntent = new Intent(action);
        // Set alarm intent
        PendingIntent alarmIntent = PendingIntent.getBroadcast(getApplicationContext(), requestCode, actionIntent, 0);
        // Set alarm manager
        AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        // Unset alarm
        if(alarmManager != null) alarmManager.cancel(alarmIntent);
    }

    /**
     * Set battery optimizations and ask the user for permissions.
     */
    private void setBatteryOptimizations() {
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

    /**
     * Gets special permissions from the user to keep services alive.
     */
    private void getSpecialPermissions() {
        String alertMessage = "Please allow this app to always run in the background, otherwise our services won't be accessed when your phone goes into sleep mode.";
        final String brand = Build.BRAND;

        // Create dialog builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
        builder.setMessage(alertMessage);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent();
                if (brand.equalsIgnoreCase("xiaomi")) {
                    intent.setComponent(new ComponentName("com.miui.securitycenter","com.miui.permcenter.autostart.AutoStartManagementActivity"));
                    startActivity(intent);
                } else if (brand.equalsIgnoreCase("Letv")) {
                    intent.setComponent(new ComponentName("com.letv.android.letvsafe","com.letv.android.letvsafe.AutobootManageActivity"));
                    startActivity(intent);
                } else if (brand.equalsIgnoreCase("Honor")) {
                    intent.setComponent(new ComponentName("com.huawei.systemmanager","com.huawei.systemmanager.optimize.process.ProtectActivity"));
                    startActivity(intent);
                } else if (Build.MANUFACTURER.equalsIgnoreCase("oppo")) {
                    try {
                        intent.setClassName("com.coloros.safecenter","com.coloros.safecenter.permission.startup.StartupAppListActivity");
                        startActivity(intent);
                    } catch (Exception e) {
                        try {
                            intent.setClassName("com.oppo.safe","com.oppo.safe.permission.startup.StartupAppListActivity");
                            startActivity(intent);
                        } catch (Exception ex) {
                            try {
                                intent.setClassName("com.coloros.safecenter","com.coloros.safecenter.startupapp.StartupAppListActivity");
                                startActivity(intent);
                            } catch (Exception exx) {
                                exx.printStackTrace();
                            }
                        }
                    }
                } else if (Build.MANUFACTURER.contains("vivo")) {
                    try {
                        intent.setComponent(new ComponentName("com.iqoo.secure","com.iqoo.secure.ui.phoneoptimize.AddWhiteListActivity"));
                        startActivity(intent);
                    } catch (Exception e) {
                        try {
                            intent.setComponent(new ComponentName("com.vivo.permissionmanager","com.vivo.permissionmanager.activity.BgStartUpManagerActivity"));
                            startActivity(intent);
                        } catch (Exception ex) {
                            try {
                                intent.setClassName("com.iqoo.secure","com.iqoo.secure.ui.phoneoptimize.BgStartUpManager");
                                startActivity(intent);
                            } catch (Exception exx) {
                                ex.printStackTrace();
                            }
                        }
                    }
                }
            }
        });

        // Create and show dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    /*--------------*/
    /* Scan methods */
    /*--------------*/

    private final Handler handler = new Handler();
    private final Runnable stopRun = new Runnable() {
        @Override
        public void run() { stopScan(); }
    };

    /**
     * Creates a Bluetooth Low Energy ScanCallback
     * @return ScanCallback
     */
    private ScanCallback initScanCallback() {
        return new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                super.onScanResult(callbackType, result);
                // Get the scanned device
                BluetoothDevice device = result.getDevice();
                // Check if device is supported b the app
                boolean isSupportedDevice = false;
                if(device.getAddress().startsWith(MiBandConstants.MODEL.MI1S)) isSupportedDevice = true;
                if(device.getAddress().startsWith(MiBandConstants.MODEL.MI1A)) isSupportedDevice = false; // Not supported
                // Add device onto the scanView
                if(isSupportedDevice && !devices.containsKey(device.toString())) {
                    devices.put(device.getAddress(),device);
                    if(scanView != null) scanView.addDevice(device);
                }
            }
        };
    }

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
            adapter.getBluetoothLeScanner().startScan(scanCallback);
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
            adapter.getBluetoothLeScanner().stopScan(scanCallback);
        }
    }
}
