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

    private static final int SELF_TEST = 99;

    private static final int SET_LOW_LATENCY = 0;
    private static final int ENABLE_NOTIFICATIONS = 1;
    private static final int PAIR = 2;
    private static final int READ_DATE = 3;
    private static final int REQUEST_DEVICE_INFO = 4;
    private static final int REQUEST_DEVICE_NAME = 5;
    private static final int SEND_USER_INFO = 6;
    private static final int CHECK_AUTHENTICATION = 7;


    ////////////////
    // Attributes //
    ////////////////

    // Connect
    private BluetoothGatt connectGATT;

    // MiBandServices
    private MiliService miliService;
    private VibrationService vibrationService;

    // Calls Queue
    private final Queue<Action> actionQueue;
    private boolean allWorkIsDone;
    private boolean working;

    // Info
    private DeviceInfo deviceInfo;
    private UserInfo userInfo;

    // Auth
    private boolean authenticated;

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
        actionQueue = new Queue<>();
        allWorkIsDone = true;
        working = false;

        // Info
        deviceInfo = null;
        userInfo = null;

        // Auth
        authenticated = false;
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
            addCall(CHECK_AUTHENTICATION, false);

            // Start
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
            } else if (MiBandConstants.UUID_CHAR.DEVICE_NAME.equals(characteristicUUID)) {
                String name = new String(characteristic.getValue(), StandardCharsets.UTF_8); // TODO - Stop reading ���� at the beginning
                Log.d("GATTread", "Name: " + name);
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

            // On reading a characteristic it always finish the work
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
                UserInfo userInfo = new UserInfo(characteristic.getValue());
                Log.d("GATTwrite","" + userInfo);
            }else if(MiBandConstants.UUID_CHAR.CONTROL_POINT.equals(characteristic.getUuid())){
                Log.d("GATTwrite", "Control point: " + characteristic.getValue());
            }else if(MiBandConstants.UUID_CHAR.REALTIME_STEPS.equals(characteristic.getUuid())){
                Log.d("GATTwrite", "Realtime steps: " + characteristic.getValue());
            }else if(MiBandConstants.UUID_CHAR.LE_PARAMS.equals(characteristic.getUuid())){
                Log.d("GATTwrite", "Latency: " + characteristic.getValue()[0]);
            }else if(MiBandConstants.UUID_CHAR.PAIR.equals(characteristic.getUuid())){
                Log.d("GATTwrite", "PAIR: " + characteristic.getValue()[0]);
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

            if(!MiBandConstants.UUID_CHAR.USER_INFO.equals(characteristic.getUuid())){
                working = false;
            }

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

        if(!MiBandConstants.UUID_CHAR.NOTIFICATION.equals(characteristic.getUuid())){
            working = false;
        }

        run();
    }

    ////////////////////
    // Public Methods //
    ////////////////////

    /**
     * Adds a call to the actionQueue.
     * @param call to be added to the actionQueue
     */
    public void addCall(Action call) {
        allWorkIsDone = false;
        actionQueue.add(call);
    }

    /**
     * Adds a call to the actionQueue.
     * @param callCode code of the call to be added
     */
    public void addCall(int callCode) {
        addCall(callCode, true, null);
    }

    /**
     * Adds a call to the actionQueue.
     * @param callCode code of the call to be added
     * @param expectsResult True to add an ActionWithResponse | False to add an ActionWithoutResponse
     */
    public void addCall(int callCode, boolean expectsResult) {
        addCall(callCode, expectsResult, null);
    }

    /**
     * Adds a call to the actionQueue.
     * @param callCode code of the call to be added
     * @param data to be write
     */
    public void addCall(int callCode, byte[] data) {
        addCall(callCode, true, data);
    }

    /**
     * Adds a call to the actionQueue.
     * @param callCode code of the call to bhe added
     * @param expectsResult True to add an ActionWithResponse | False to add an ActionWithoutResponse
     * @param data to be write on write calls (not always needed)
     */
    public void addCall(final int callCode, boolean expectsResult, byte[] data) {
        if(expectsResult){
            addCall(new ActionWithResponse() {
                @Override
                public void run() {
                    switch (callCode) {
                        case SET_LOW_LATENCY: miliService.setLowLatency();
                            break;
                        case ENABLE_NOTIFICATIONS: miliService.enableNotifications();
                            break;
                        case PAIR: miliService.pair();
                            break;
                        case REQUEST_DEVICE_INFO: miliService.requestDeviceInformation();
                            break;
                        case REQUEST_DEVICE_NAME:  miliService.requestDeviceName();
                            break;
                        case SEND_USER_INFO:
                            if(userInfo != null && deviceInfo != null)
                                miliService.sendUserInfo(userInfo.getData(deviceInfo));
                            break;
                        case SELF_TEST: // TODO - NOT WORKING
                            miliService.selfTest();
                            break;
                    }
                }
            });
        }else{
            addCall(new ActionWithoutResponse() {
                @Override
                public void run() {
                    switch (callCode) {
                        case CHECK_AUTHENTICATION:
                            if(!authenticated) actionQueue.clear();
                    }
                }
            });
        }
    }

    /////////////////////
    // Private Methods //
    /////////////////////

    /**
     * Runs the first action of the queue.
     */
    private void run() {
        if(actionQueue.isEmpty()){
            allWorkIsDone = true;
        }else if(!working){
            Action action = actionQueue.poll();
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

        // Handle value
        switch (value[0]) {
            case MiBandConstants.NOTIFICATION.UNKNOWN:
                Log.d("Notification", "Unknown");
                break;
            case MiBandConstants.NOTIFICATION.NORMAL:
                Log.d("Notification", "Normal");
                break;
            case MiBandConstants.NOTIFICATION.FIRMWARE_UPDATE_FAILED:
                Log.d("Notification", "Firmware update failed");
                break;
            case MiBandConstants.NOTIFICATION.FIRMWARE_UPDATE_SUCCESS:
                Log.d("Notification", "Firmware update success");
                break;
            case MiBandConstants.NOTIFICATION.CONN_PARAM_UPDATE_FAILED:
                Log.d("Notification", "Connection param update failed");
                break;
            case MiBandConstants.NOTIFICATION.CONN_PARAM_UPDATE_SUCCESS:
                Log.d("Notification", "Connection param update success");
                break;
            case MiBandConstants.NOTIFICATION.AUTHENTICATION_SUCCESS:
                Log.d("Notification", "Authentication Success");
                authenticated = true;
                working = false;
                break;
            case MiBandConstants.NOTIFICATION.AUTHENTICATION_FAILED:
                Log.d("Notification", "Authentication Failed");
                authenticated = false;
                break;
            case MiBandConstants.NOTIFICATION.FITNESS_GOAL_ACHIEVED:
                Log.d("Notification", "Fitness goal achieved");
                break;
            case MiBandConstants.NOTIFICATION.SET_LATENCY_SUCCESS:
                Log.d("Notification", "Set Latency Success");
                working = false;
                break;
            case MiBandConstants.NOTIFICATION.RESET_AUTHENTICATION_FAILED:
                Log.d("Notification", "Reset Authentication Failed");
                authenticated = false;
                working = false;
                break;
            case MiBandConstants.NOTIFICATION.RESET_AUTHENTICATION_SUCCESS:
                Log.d("Notification", "Reset Authentication Success");
                authenticated = true;
                working = false;
                break;
            case MiBandConstants.NOTIFICATION.FIRMWARE_CHECK_FAILED:
                Log.d("Notification", "Firmware Check Failed");
                break;
            case MiBandConstants.NOTIFICATION.FIRMWARE_CHECK_SUCCESS:
                Log.d("Notification", "Firmware Check Success");
                break;
            case MiBandConstants.NOTIFICATION.STATUS_MOTOR_NOTIFY:
                Log.d("Notification", "Motor NOTIFY");
                break;
            case MiBandConstants.NOTIFICATION.STATUS_MOTOR_CALL:
                Log.d("Notification", "Motor CALL");
                break;
            case MiBandConstants.NOTIFICATION.STATUS_MOTOR_DISCONNECT:
                Log.d("Notification", "Motor DISCONNECT");
                break;
            case MiBandConstants.NOTIFICATION.STATUS_MOTOR_SMART_ALARM:
                Log.d("Notification", "Motor SMART ALARM");
                break;
            case MiBandConstants.NOTIFICATION.STATUS_MOTOR_ALARM:
                Log.d("Notification", "Motor ALARM");
                break;
            case MiBandConstants.NOTIFICATION.STATUS_MOTOR_GOAL:
                Log.d("Notification", "Motor GOAL");
                break;
            case MiBandConstants.NOTIFICATION.STATUS_MOTOR_AUTH:
                Log.d("Notification", "Motor AUTH");
                authenticated = false;
                //authenticating = true;
                break;
            case MiBandConstants.NOTIFICATION.STATUS_MOTOR_SHUTDOWN:
                Log.d("Notification", "Motor SHUTDOWN");
                break;
            case MiBandConstants.NOTIFICATION.STATUS_MOTOR_AUTH_SUCCESS:
                Log.d("Notification", "Motor AUTH SUCCESS");
                authenticated = true;
                working = false;
                break;
            case MiBandConstants.NOTIFICATION.STATUS_MOTOR_TEST:
                Log.d("Notification", "Motor TEST");
                break;
            default:
                Log.d("Notification", "Code " + value[0]);
        }
    }

}
