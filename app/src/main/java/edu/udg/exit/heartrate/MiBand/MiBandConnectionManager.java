package edu.udg.exit.heartrate.MiBand;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothProfile;
import android.util.Log;

import java.nio.charset.StandardCharsets;
import java.util.*;
import android.os.Handler;
import java.util.logging.LogRecord;

import edu.udg.exit.heartrate.MiBand.Actions.Action;
import edu.udg.exit.heartrate.MiBand.Actions.ActionWithResponse;
import edu.udg.exit.heartrate.MiBand.Actions.ActionWithoutResponse;
import edu.udg.exit.heartrate.MiBand.Services.MiliService;
import edu.udg.exit.heartrate.MiBand.Services.VibrationService;
import edu.udg.exit.heartrate.MiBand.Utils.*;
import edu.udg.exit.heartrate.Utils.Queue;

import static edu.udg.exit.heartrate.MiBand.MiBandConstants.*;

/**
 * Class that performs a connection with a Mi Band and handles it.
 */
public class MiBandConnectionManager extends BluetoothGattCallback {

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
    private boolean userInfoSended;
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
        userInfoSended = false;
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

            // TODO - Move setup to the app
            userInfo = new UserInfo();
            userInfo.setUsername("Oscar");
            userInfo.setBlueToothAddress(connectGATT.getDevice().getAddress());

            // Init Services
            miliService = new MiliService(gatt);
            vibrationService = new VibrationService(gatt);

            // Initialize - TODO
            addCall(enableNotifications());
            addCall(setLowLatency()); // Set low latency to do a faster initialization
            //addCall(requestDate()); // Reading date for stability - TODO - Check this
            addCall(pair());
            addCall(requestDeviceInformation()); // Needed to send user info to the device
            addCall(requestDeviceName());
            addCall(sendUserInfo()); // Needed to authenticate
            addCall(checkAuthentication());
            addCall(sendCommand(COMMAND.SET_WEAR_LOCATION_RIGHT)); // Set wear location

            addCall(requestBattery());
            addCall(setHighLatency()); // Set high latency for an stable connection
            //addCall(setInitialized()); // Device is ready to make other calls

            // Start
            run();
        }
    }

    @Override
    public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        super.onCharacteristicRead(gatt,characteristic,status);

        if (status == BluetoothGatt.GATT_SUCCESS) {
            UUID characteristicUUID = characteristic.getUuid();
            if (UUID_CHAR.DEVICE_INFO.equals(characteristicUUID)) {
                deviceInfo = new DeviceInfo(characteristic.getValue());
                Log.d("GATTr", "Info -> " + deviceInfo);
            } else if (UUID_CHAR.DEVICE_NAME.equals(characteristicUUID)) {
                String name = new String(characteristic.getValue(), StandardCharsets.UTF_8); // TODO - Stop reading ���� at the beginning
                Log.d("GATTr", "Name -> " + name);
            } else if (UUID_CHAR.BATTERY.equals(characteristicUUID)) {
                BatteryInfo batteryInfo = new BatteryInfo(characteristic.getValue());
                Log.d("GATTr", "Battery -> " + batteryInfo);
            } else if (UUID_CHAR.DATE_TIME.equals(characteristicUUID)) {
                MiDate miDate = new MiDate(characteristic.getValue());
                Log.d("GATTr", "Date -> " + miDate);
            } else {
                if(characteristic.getValue().length>0){
                    Log.d("GATTr", "Characteristic -> " + characteristic.getValue()[0]);
                }else{
                    Log.d("GATTr", "Characteristic -> " + characteristic.getValue());
                }
            }

            // On reading a characteristic it always finish the work
            working = false;
            run();
        }else{
            Log.w("GATTr", "Failed to read characteristic");
        }

    }

    @Override
    public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        super.onCharacteristicWrite(gatt,characteristic,status);

        if (status == BluetoothGatt.GATT_SUCCESS) {
            if(UUID_CHAR.DEVICE_INFO.equals(characteristic.getUuid())){
                Log.d("GATTw", "Device information -> " + characteristic.getValue());
            }else if(UUID_CHAR.DEVICE_NAME.equals(characteristic.getUuid())){
                Log.d("GATTw", "Device name -> " + characteristic.getValue());
            }else if(UUID_CHAR.NOTIFICATION.equals(characteristic.getUuid())){
                Log.d("GATTw", "Notification -> " + characteristic.getValue());
            }else if(UUID_CHAR.USER_INFO.equals(characteristic.getUuid())){
                UserInfo userInfo = new UserInfo(characteristic.getValue());
                Log.d("GATTw","" + userInfo);
                userInfoSended = true;
            }else if(UUID_CHAR.CONTROL_POINT.equals(characteristic.getUuid())){
                Log.d("GATTw", "Control point -> " + characteristic.getValue()[0]);
            }else if(UUID_CHAR.REALTIME_STEPS.equals(characteristic.getUuid())){
                Log.d("GATTw", "Realtime steps -> " + characteristic.getValue());
            }else if(UUID_CHAR.LE_PARAMS.equals(characteristic.getUuid())){
                Latency latency = new Latency(characteristic.getValue());
                Log.d("GATTw", "Latency -> " + latency);
            }else if(UUID_CHAR.PAIR.equals(characteristic.getUuid())){
                Log.d("GATTw", "PAIR -> " + characteristic.getValue()[0]);
            }else if(UUID_CHAR.DATE_TIME.equals(characteristic.getUuid())){
                MiDate miDate = new MiDate(characteristic.getValue());
                Log.d("GATTw", "Date -> " + miDate);
            }else if(UUID_CHAR.BATTERY.equals(characteristic.getUuid())){
                BatteryInfo batteryInfo = new BatteryInfo(characteristic.getValue());
                Log.d("GATTw", "Battery -> " + batteryInfo);
            }else{
                if(characteristic.getValue().length>0){
                    Log.d("GATTw", "Characteristic -> " + characteristic.getValue()[0]);
                }else{
                    Log.d("GATTw", "Characteristic -> " + characteristic.getValue());
                }
            }

            if(!(UUID_CHAR.USER_INFO.equals(characteristic.getUuid()) && !authenticated)){
                working = false;
            }

            run();
        }else{
            Log.w("GATTw", "Failed to write characteristic");
        }
    }

    @Override
    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        super.onCharacteristicChanged(gatt,characteristic);

        if(UUID_CHAR.DEVICE_INFO.equals(characteristic.getUuid())){
            Log.d("GATTc", "Device information -> " + characteristic.getValue());
        }else if(UUID_CHAR.DEVICE_NAME.equals(characteristic.getUuid())){
            Log.d("GATTc", "Device name -> " + characteristic.getValue());
        }else if(UUID_CHAR.NOTIFICATION.equals(characteristic.getUuid())){
            handleNotification(characteristic.getValue());
        }else if(UUID_CHAR.USER_INFO.equals(characteristic.getUuid())){
            Log.d("GATTc", "User information -> " + characteristic.getValue());
        }else if(UUID_CHAR.CONTROL_POINT.equals(characteristic.getUuid())){
            Log.d("GATTc", "Control point -> " + characteristic.getValue());
        }else if(UUID_CHAR.REALTIME_STEPS.equals(characteristic.getUuid())){
            Log.d("GATTc", "Realtime steps -> " + characteristic.getValue());
        }else if(UUID_CHAR.ACTIVITY_DATA.equals(characteristic.getUuid())){
            Log.d("GATTc", "Activity -> " + characteristic.getValue());
        }else if(UUID_CHAR.PAIR.equals(characteristic.getUuid())){
            Log.d("GATTc", "PAIR -> " + characteristic.getValue()[0]);
        }else if(UUID_CHAR.DATE_TIME.equals(characteristic.getUuid())){
            MiDate miDate = new MiDate(characteristic.getValue());
            Log.d("GATTc", "Date -> " + miDate);
        }else if(UUID_CHAR.BATTERY.equals(characteristic.getUuid())){
            BatteryInfo batteryInfo = new BatteryInfo(characteristic.getValue());
            Log.d("GATTc", "Battery -> " + batteryInfo);
        }else{
            if(characteristic.getValue().length>0){
                Log.d("GATTc", "Characteristic -> " + characteristic.getValue()[0]);
            }else{
                Log.d("GATTc", "Characteristic -> " + characteristic.getValue());
            }
        }

        if(!UUID_CHAR.NOTIFICATION.equals(characteristic.getUuid())){
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

    /////////////////////
    // Private Methods //
    /////////////////////

    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            working = false;
            MiBandConnectionManager.this.run();
        }
    };

    /**
     * Runs the first action of the queue.
     */
    private void run() {
        handler.removeCallbacks(runnable);

        if(actionQueue.isEmpty()){
            allWorkIsDone = true;
        }else if(!working){
            Action action = actionQueue.poll();
            action.run();
            if(!action.expectsResult())
                handler.postDelayed(runnable,100); // TODO - Check (for the moment seems to be working nice)
            else {
                working = true;
                handler.postDelayed(runnable,5000); // TODO - Check if timer is too short or too large
            }
        }
    }

    /////////////
    // Actions //
    /////////////

    /* With response */

    /**
     * Get an action to set the lowest latency possible.
     * @return SetLowLatency Action
     */
    private Action setLowLatency() {
        return new ActionWithResponse() {
            @Override
            public void run() {
                miliService.setLowLatency();
            }
        };
    }

    /**
     * Get an action to set the highest latency possible.
     * @return SetHighLatency Action
     */
    private Action setHighLatency() {
        return new ActionWithResponse() {
            @Override
            public void run() {
                miliService.setHighLatency();
            }
        };
    }

    /**
     * TODO - Check expect result (I think the best would be to not expect any result when enable notifications)
     * Get an action to enable notifications from MiBand.
     * @return EnableNotifications Action
     */
    private Action enableNotifications() {
        return new Action() {
            private boolean expectsResult = true;

            @Override
            public void run() {
                this.expectsResult = miliService.enableNotifications();
            }

            @Override
            public boolean expectsResult() {
                return false;
            }
        };
    }

    /**
     * Get an action to request the date from MiBand.
     * @return RequestDate Action
     */
    private Action requestDate() {
        return new ActionWithResponse() {
            @Override
            public void run() {
                miliService.requestDate();
            }
        };
    }

    /**
     * Get an action to start pairing with the MiBand.
     * @return Pair Action
     */
    private Action pair() {
        return new ActionWithResponse() {
            @Override
            public void run() {
                miliService.pair();
            }
        };
    }

    /**
     * Get an action to request the MiBand device information.
     * @return RequestDeviceInformation Action
     */
    private Action requestDeviceInformation() {
        return new ActionWithResponse() {
            @Override
            public void run() {
                miliService.requestDeviceInformation();
            }
        };
    }

    /**
     * Get an action to request the name of the MiBand device.
     * @return RequestDeviceName Action
     */
    private Action requestDeviceName() {
        return new ActionWithResponse() {
            @Override
            public void run() {
                miliService.requestDeviceName();
            }
        };
    }

    /**
     * Get an action to send the user information to the MiBand.
     * @return SendUserInfo Action
     */
    private Action sendUserInfo() {
        return new ActionWithResponse() {
            @Override
            public void run() {
                sendUserInfo(userInfo,deviceInfo).run();
            }
        };
    }

    /**
     * Get an action to send the user information to the MiBand.
     * @param userInfo - Contains data to send.
     * @param deviceInfo - Contains data to send.
     * @return SendUserInfo Action
     */
    private Action sendUserInfo(final UserInfo userInfo, final DeviceInfo deviceInfo) {
        return new ActionWithResponse() {
            @Override
            public void run() {
                if(userInfo != null && deviceInfo != null){
                    miliService.sendUserInfo(userInfo.getData(deviceInfo));
                }
            }
        };
    }

    /**
     * Get an action to send a command to the MiBand.
     * @param command - Command to be sended to the MiBand
     * @return SendCommand Action
     */
    private Action sendCommand(final byte[] command) {
        return new ActionWithResponse() {
            @Override
            public void run() {
                miliService.sendCommand(command);
            }
        };
    }

    /**
     * Get an action to request the battery of the MiBand.
     * @return RequestBattery Action
     */
    private Action requestBattery() {
        return new ActionWithResponse() {
            @Override
            public void run() {
                miliService.requestBattery();
            }
        };
    }

    /* Without response */

    /**
     * Get an action that checks if the authentication was successful.
     * If authentication wasn't successful it aborts all operations to initialize the connection.
     * @return CheckAuthentication Action
     */
    private Action checkAuthentication() {
        return new ActionWithoutResponse() {
            @Override
            public void run() {
                if(!authenticated) actionQueue.clear();
            }
        };
    }

    /* Not tested yet */

    /**
     * TODO - Not working!! (But not even necessary)
     * Get an action to make the MiBand do "crazy" things.
     * @return SelfTest Action
     */
    private Action selfTest() {
        return new ActionWithResponse() {
            @Override
            public void run() {
                miliService.selfTest();
            }
        };
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
            case NOTIFICATION.UNKNOWN:
                Log.d("Notification", "Unknown");
                break;
            case NOTIFICATION.NORMAL:
                Log.d("Notification", "Normal");
                break;
            case NOTIFICATION.FIRMWARE_UPDATE_FAILED:
                Log.d("Notification", "Firmware update failed");
                break;
            case NOTIFICATION.FIRMWARE_UPDATE_SUCCESS:
                Log.d("Notification", "Firmware update success");
                break;
            case NOTIFICATION.CONN_PARAM_UPDATE_FAILED:
                Log.d("Notification", "Connection param update failed");
                break;
            case NOTIFICATION.CONN_PARAM_UPDATE_SUCCESS:
                Log.d("Notification", "Connection param update success");
                break;
            case NOTIFICATION.AUTHENTICATION_SUCCESS:
                Log.d("Notification", "Authentication Success");
                authenticated = true;
                if(userInfoSended) working = false;
                break;
            case NOTIFICATION.AUTHENTICATION_FAILED:
                Log.d("Notification", "Authentication Failed");
                authenticated = false;
                break;
            case NOTIFICATION.FITNESS_GOAL_ACHIEVED:
                Log.d("Notification", "Fitness goal achieved");
                break;
            case NOTIFICATION.SET_LATENCY_SUCCESS:
                Log.d("Notification", "Set Latency Success");
                //working = false;
                break;
            case NOTIFICATION.RESET_AUTHENTICATION_FAILED:
                Log.d("Notification", "Reset Authentication Failed");
                authenticated = false;
                working = false;
                break;
            case NOTIFICATION.RESET_AUTHENTICATION_SUCCESS:
                Log.d("Notification", "Reset Authentication Success");
                authenticated = true;
                working = false;
                break;
            case NOTIFICATION.FIRMWARE_CHECK_FAILED:
                Log.d("Notification", "Firmware Check Failed");
                break;
            case NOTIFICATION.FIRMWARE_CHECK_SUCCESS:
                Log.d("Notification", "Firmware Check Success");
                break;
            case NOTIFICATION.STATUS_MOTOR_NOTIFY:
                Log.d("Notification", "Motor NOTIFY");
                break;
            case NOTIFICATION.STATUS_MOTOR_CALL:
                Log.d("Notification", "Motor CALL");
                break;
            case NOTIFICATION.STATUS_MOTOR_DISCONNECT:
                Log.d("Notification", "Motor DISCONNECT");
                break;
            case NOTIFICATION.STATUS_MOTOR_SMART_ALARM:
                Log.d("Notification", "Motor SMART ALARM");
                break;
            case NOTIFICATION.STATUS_MOTOR_ALARM:
                Log.d("Notification", "Motor ALARM");
                break;
            case NOTIFICATION.STATUS_MOTOR_GOAL:
                Log.d("Notification", "Motor GOAL");
                break;
            case NOTIFICATION.STATUS_MOTOR_AUTH:
                Log.d("Notification", "Motor AUTH");
                authenticated = false;
                //authenticating = true;
                break;
            case NOTIFICATION.STATUS_MOTOR_SHUTDOWN:
                Log.d("Notification", "Motor SHUTDOWN");
                break;
            case NOTIFICATION.STATUS_MOTOR_AUTH_SUCCESS:
                Log.d("Notification", "Motor AUTH SUCCESS");
                authenticated = true;
                working = false;
                break;
            case NOTIFICATION.STATUS_MOTOR_TEST:
                Log.d("Notification", "Motor TEST");
                break;
            default:
                Log.d("Notification", "Code " + value[0]);
        }
    }

}
