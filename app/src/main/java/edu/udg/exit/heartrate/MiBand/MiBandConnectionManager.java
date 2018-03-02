package edu.udg.exit.heartrate.MiBand;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothProfile;
import android.util.Log;
import edu.udg.exit.heartrate.MiBand.Actions.Action;
import edu.udg.exit.heartrate.MiBand.Actions.ActionWithResponse;
import edu.udg.exit.heartrate.MiBand.Actions.ActionWithoutResponse;
import edu.udg.exit.heartrate.MiBand.Services.MiliService;
import edu.udg.exit.heartrate.MiBand.Services.VibrationService;
import edu.udg.exit.heartrate.MiBand.Utils.BatteryInfo;
import edu.udg.exit.heartrate.MiBand.Utils.DeviceInfo;
import edu.udg.exit.heartrate.MiBand.Utils.MiDate;
import edu.udg.exit.heartrate.MiBand.Utils.UserInfo;

import java.nio.charset.StandardCharsets;
import java.util.*;

import edu.udg.exit.heartrate.Utils.Queue;

public class MiBandConnectionManager extends BluetoothGattCallback {

    ///////////////
    // Constants //
    ///////////////

    private static final int SET_LOW_LATENCY = 0;
    private static final int ENABLE_NOTIFICATIONS = 1;
    private static final int PAIR = 2;
    private static final int READ_DATE = 3;
    private static final int REQUEST_DEVICE_INFO = 4;
    private static final int REQUEST_DEVICE_NAME = 5;
    private static final int SEND_USER_INFO = 6;

    ////////////////
    // Attributes //
    ////////////////

    // Connect
    private BluetoothGatt connectGATT;

    // MiBandServices
    private MiliService miliService;
    private VibrationService vibrationService;

    // Calls Queue
    private final Queue<Action> callQueue;
    private boolean allWorkIsDone;
    private boolean working;

    // Info
    private DeviceInfo deviceInfo;
    private UserInfo userInfo;

    ////////////////////////
    // Life Cycle Methods //
    ////////////////////////

    /**
     * Default constructor.
     */
    public MiBandConnectionManager() {
        super();

        // Connect
        connectGATT = null;

        // MiBandService
        miliService = null;
        vibrationService = null;

        // Calls Queue
        callQueue = new Queue<>();
        allWorkIsDone = true;
        working = false;

        // Info
        deviceInfo = null;
        userInfo = null;
    }

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

            userInfo = new UserInfo();
            userInfo.setUsername("Oscar");
            userInfo.setBlueToothAddress(connectGATT.getDevice().getAddress());

            // Init Services
            miliService = new MiliService(gatt);
            vibrationService = new VibrationService(gatt);

            // Initialize - TODO
            addCall(SET_LOW_LATENCY);
            addCall(ENABLE_NOTIFICATIONS);
            addCall(PAIR);
            addCall(REQUEST_DEVICE_INFO);
            addCall(REQUEST_DEVICE_NAME);
            addCall(SEND_USER_INFO); // deviceInfo will be set

            run();
        }
    }

    @Override
    public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        super.onCharacteristicRead(gatt,characteristic,status);

        if (status == BluetoothGatt.GATT_SUCCESS) {
            UUID characteristicUUID = characteristic.getUuid();
            if (MiBandConstants.UUID_CHAR.DEVICE_INFO.equals(characteristicUUID)) {
                deviceInfo = new DeviceInfo(characteristic.getValue());
                Log.d("GATTread", "Info: " + deviceInfo);

                // TODO - Extract
                //miliService.requestDeviceName();
            } else if (MiBandConstants.UUID_CHAR.DEVICE_NAME.equals(characteristicUUID)) {
                String name = new String(characteristic.getValue(), StandardCharsets.UTF_8); // TODO - Stop reading ���� at the beginning
                Log.d("GATTread", "Name: " + name);

                // TODO - Extract
                //miliService.sendUserInfo(userInfo.getData(deviceInfo));
            } else if (MiBandConstants.UUID_CHAR.BATTERY.equals(characteristicUUID)) {
                BatteryInfo batteryInfo = new BatteryInfo(characteristic.getValue());
                Log.d("GATTread", "Battery: " + batteryInfo);
            } else if (MiBandConstants.UUID_CHAR.DATE_TIME.equals(characteristicUUID)) {
                MiDate miDate = new MiDate(characteristic.getValue());
                Log.d("GATTread", "Date: " + miDate);
            } else {
                if(characteristic.getValue().length>0){
                    Log.d("GATTread", "Characteristic: " + characteristic.getValue()[0]);
                }else{
                    Log.d("GATTread", "Characteristic: " + characteristic.getValue());
                }
            }
            working = false;
            run();
        }

    }

    @Override
    public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        super.onCharacteristicWrite(gatt,characteristic,status);

        if (status == BluetoothGatt.GATT_SUCCESS) {
            if(MiBandConstants.UUID_CHAR.DEVICE_INFO.equals(characteristic.getUuid())){
                Log.d("GATTwrite", "Device information: " + characteristic.getValue());
            }else if(MiBandConstants.UUID_CHAR.DEVICE_NAME.equals(characteristic.getUuid())){
                Log.d("GATTwrite", "Device name: " + characteristic.getValue());
            }else if(MiBandConstants.UUID_CHAR.NOTIFICATION.equals(characteristic.getUuid())){
                Log.d("GATTwrite", "Notification: " + characteristic.getValue());
            }else if(MiBandConstants.UUID_CHAR.USER_INFO.equals(characteristic.getUuid())){
                Log.d("GATTwrite", "User information: " + characteristic.getValue());
            }else if(MiBandConstants.UUID_CHAR.CONTROL_POINT.equals(characteristic.getUuid())){
                Log.d("GATTwrite", "Control point: " + characteristic.getValue());
            }else if(MiBandConstants.UUID_CHAR.REALTIME_STEPS.equals(characteristic.getUuid())){
                Log.d("GATTwrite", "Realtime steps: " + characteristic.getValue());
            }else if(MiBandConstants.UUID_CHAR.LE_PARAMS.equals(characteristic.getUuid())){
                Log.d("GATTwrite", "Latency: " + characteristic.getValue()[0]);
            }else if(MiBandConstants.UUID_CHAR.PAIR.equals(characteristic.getUuid())){
                Log.d("GATTwrite", "PAIR: " + characteristic.getValue()[0]);

                // TODO - extract
                //if(characteristic.getValue()[0] == 2) miliService.requestDeviceInformation();
            }else if(MiBandConstants.UUID_CHAR.DATE_TIME.equals(characteristic.getUuid())){
                MiDate miDate = new MiDate(characteristic.getValue());
                Log.d("GATTwrite", "Date: " + miDate);
            }else if(MiBandConstants.UUID_CHAR.BATTERY.equals(characteristic.getUuid())){
                BatteryInfo batteryInfo = new BatteryInfo(characteristic.getValue());
                Log.d("GATTwrite", "Battery: " + batteryInfo);
            }else{
                if(characteristic.getValue().length>0){
                    Log.d("GATTwrite", "Characteristic: " + characteristic.getValue()[0]);
                }else{
                    Log.d("GATTwrite", "Characteristic: " + characteristic.getValue());
                }
            }
            working = false;
            run();
        }
    }

    @Override
    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        super.onCharacteristicChanged(gatt,characteristic);

        if(MiBandConstants.UUID_CHAR.DEVICE_INFO.equals(characteristic.getUuid())){
            Log.d("GATTchange", "Device information: " + characteristic.getValue());
        }else if(MiBandConstants.UUID_CHAR.DEVICE_NAME.equals(characteristic.getUuid())){
            Log.d("GATTchange", "Device name: " + characteristic.getValue());
        }else if(MiBandConstants.UUID_CHAR.NOTIFICATION.equals(characteristic.getUuid())){
            handleNotification(characteristic.getValue());
        }else if(MiBandConstants.UUID_CHAR.USER_INFO.equals(characteristic.getUuid())){
            Log.d("GATTchange", "User information: " + characteristic.getValue());
        }else if(MiBandConstants.UUID_CHAR.CONTROL_POINT.equals(characteristic.getUuid())){
            Log.d("GATTchange", "Control point: " + characteristic.getValue());
        }else if(MiBandConstants.UUID_CHAR.REALTIME_STEPS.equals(characteristic.getUuid())){
            Log.d("GATTchange", "Realtime steps: " + characteristic.getValue());
        }else if(MiBandConstants.UUID_CHAR.ACTIVITY_DATA.equals(characteristic.getUuid())){
            Log.d("GATTchange", "Activity: " + characteristic.getValue());
        }else if(MiBandConstants.UUID_CHAR.PAIR.equals(characteristic.getUuid())){
            Log.d("GATTchange", "PAIR: " + characteristic.getValue()[0]);
        }else if(MiBandConstants.UUID_CHAR.DATE_TIME.equals(characteristic.getUuid())){
            MiDate miDate = new MiDate(characteristic.getValue());
            Log.d("GATTchange", "Date: " + miDate);
        }else if(MiBandConstants.UUID_CHAR.BATTERY.equals(characteristic.getUuid())){
            BatteryInfo batteryInfo = new BatteryInfo(characteristic.getValue());
            Log.d("GATTchange", "Battery: " + batteryInfo);
        }else{
            Log.d("GATTchange", "Characteristic: " + characteristic.getValue());
        }

        working = false;
        run();
    }

    ////////////////////
    // Public Methods //
    ////////////////////

    public void addCall(int call) {
        addCall(call, true, null);
    }

    public void addCall(final int call, boolean expectsResult, byte[] data) {
        allWorkIsDone = false;

        if(expectsResult){
            callQueue.add(new ActionWithResponse() {
                @Override
                public void run() {
                    switch (call) {
                        case SET_LOW_LATENCY: miliService.setLowLatency();
                            Log.d("Call", "latency");
                            break;
                        case ENABLE_NOTIFICATIONS: miliService.enableNotifications();
                            Log.d("Call", "noti");
                            break;
                        case PAIR: miliService.pair();
                            Log.d("Call", "pair");
                            break;
                        case REQUEST_DEVICE_INFO: miliService.requestDeviceInformation();
                            Log.d("Call", "devInf");
                            break;
                        case REQUEST_DEVICE_NAME:  miliService.requestDeviceName();
                            Log.d("Call", "devNam");
                            break;
                        case SEND_USER_INFO:
                            Log.d("Call", "sendUser");
                            if(userInfo != null && deviceInfo != null)
                                miliService.sendUserInfo(userInfo.getData(deviceInfo));
                            break;
                    }
                }
            });
        }else{
            callQueue.add(new ActionWithoutResponse() {
                @Override
                public void run() {
                    switch (call) {

                    }
                }
            });
        }
    }

    /////////////////////
    // Private Methods //
    /////////////////////

    private void run() {
        if(callQueue.isEmpty()){
            allWorkIsDone = true;
        }else if(!working){
            Action action = callQueue.poll();
            action.run();
            if(!action.expectsResult()) run(); // TODO - Maybe will need to apply a timer
            else working = true;
        }
    }

    //////////////
    // Handlers //
    //////////////

    /**
     * Handles notification response.
     * @param value
     */
    private void handleNotification(byte[] value) {
        // Check if value is 1 byte long.
        if(value.length != 1){
            Log.e("Notification", "Received " + value.length + " bytes");
            return ;
        }

        // Log value
        Log.d("Notification", "Value: " + value[0]);

        // Handle value
        switch (value[0]) {
            case MiBandConstants.NOTIFICATION.NORMAL:
                break;
            case MiBandConstants.NOTIFICATION.FIRMWARE_UPDATE_FAILED:
                break;
            case MiBandConstants.NOTIFICATION.FIRMWARE_UPDATE_SUCCESS:
                break;
            case MiBandConstants.NOTIFICATION.CONN_PARAM_UPDATE_FAILED:
                break;
            case MiBandConstants.NOTIFICATION.CONN_PARAM_UPDATE_SUCCESS:
                break;
            case MiBandConstants.NOTIFICATION.AUTHENTICATION_SUCCESS:
                Log.d("Notification", "Success");
                break;
            case MiBandConstants.NOTIFICATION.AUTHENTICATION_FAILED:
                break;
            case MiBandConstants.NOTIFICATION.SET_LATENCY_SUCCESS:
                break;
            case MiBandConstants.NOTIFICATION.RESET_AUTHENTICATION_FAILED:
                break;
            case MiBandConstants.NOTIFICATION.RESET_AUTHENTICATION_SUCCESS:
                break;
        }
    }

}
