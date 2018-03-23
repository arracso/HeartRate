package edu.udg.exit.heartrate.MiBand;

import android.bluetooth.*;
import android.util.Log;

import java.nio.charset.StandardCharsets;
import java.util.*;
import android.os.Handler;

import edu.udg.exit.heartrate.MiBand.Actions.Action;
import edu.udg.exit.heartrate.MiBand.Actions.ActionWithConditionalResponse;
import edu.udg.exit.heartrate.MiBand.Actions.ActionWithResponse;
import edu.udg.exit.heartrate.MiBand.Actions.ActionWithoutResponse;
import edu.udg.exit.heartrate.MiBand.Services.HeartRateService;
import edu.udg.exit.heartrate.MiBand.Services.MiliService;
import edu.udg.exit.heartrate.MiBand.Services.VibrationService;
import edu.udg.exit.heartrate.MiBand.Utils.*;
import edu.udg.exit.heartrate.Utils.Queue;

import static edu.udg.exit.heartrate.MiBand.MiBandConstants.*;

/**
 * Class that performs a connection with a Mi Band and handles it.
 */
public class MiBandConnectionManager extends BluetoothGattCallback {

    ///////////////
    // Constants //
    ///////////////

    private final static int DELAY_MAX = 10000;
    private final static int DELAY_MIN = 500;

    ////////////////
    // Attributes //
    ////////////////

    // Connect
    private BluetoothGatt connectGATT;
    private boolean isConnected;

    // MiBandServices
    private MiliService miliService;
    private VibrationService vibrationService;
    private HeartRateService heartRateService;

    // Calls Queue
    private final Queue<Action> actionQueue;
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
        isConnected = false;

        // MiBandService
        miliService = null;
        vibrationService = null;
        heartRateService = null;

        // Calls Queue
        actionQueue = new Queue<>();
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
            isConnected = true;
            connectGATT = gatt;
            connectGATT.discoverServices();
        } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
            Log.d("GATT", "Device disconnected");
            connectGATT.close();
            isConnected = false;
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
            heartRateService = new HeartRateService(gatt);

            // Show discovered services with their characteristics & descriptors (DEBUG)
            //showServices(gatt);

            // Initialize
            initialize();

            // Vibration test
            //testVibration();

            // Test
            test();

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
                Log.d("GATTr", "Date -> " + miDate + " - " + convertBytesToString(characteristic.getValue()));
            } else {
                if(characteristic.getValue().length>0){
                    Log.d("GATTr", "Characteristic -> " + characteristic.getValue()[0]);
                }else{
                    Log.d("GATTr", "Characteristic -> " + characteristic.getValue().length);
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
                Log.d("GATTw", "Notification -> " + characteristic.getValue().length);
            }else if(UUID_CHAR.USER_INFO.equals(characteristic.getUuid())){
                UserInfo userInfo = new UserInfo(characteristic.getValue());
                Log.d("GATTw","" + userInfo);
            }else if(UUID_CHAR.CONTROL_POINT.equals(characteristic.getUuid())){
                Log.d("GATTw", "Control point -> " + convertBytesToString(characteristic.getValue()));
            }else if(UUID_CHAR.REALTIME_STEPS.equals(characteristic.getUuid())){
                Log.d("GATTw", "Realtime steps -> " + characteristic.getValue().length);
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
                Log.d("GATTw", "Characteristic -> " + convertBytesToString(characteristic.getValue()));
            }

            working = false;
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
            Log.d("GATTc", "Activity -> " + convertBytesToString(characteristic.getValue()));
        }else if(UUID_CHAR.PAIR.equals(characteristic.getUuid())){
            Log.d("GATTc", "PAIR -> " + characteristic.getValue()[0]);
        }else if(UUID_CHAR.DATE_TIME.equals(characteristic.getUuid())){
            MiDate miDate = new MiDate(characteristic.getValue());
            Log.d("GATTc", "Date -> " + miDate);
        }else if(UUID_CHAR.BATTERY.equals(characteristic.getUuid())){
            BatteryInfo batteryInfo = new BatteryInfo(characteristic.getValue());
            Log.d("GATTc", "Battery -> " + batteryInfo);
        }else{
            Log.d("GATTc", "Characteristic -> " + characteristic.getUuid() + " - " + convertBytesToString(characteristic.getValue()));
        }
    }

    @Override
    public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
        super.onDescriptorRead(gatt,descriptor,status);

        if(status == BluetoothGatt.GATT_SUCCESS){
            Log.d("GATTd", "Read " + descriptor.getUuid());

            working = false;
            run();
        }
    }

    @Override
    public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
        super.onDescriptorWrite(gatt,descriptor,status);

        if(status == BluetoothGatt.GATT_SUCCESS){
            Log.d("GATTd", "Write " + descriptor.getUuid());

            working = false;
            run();
        }
    }

    ////////////////////
    // Public Methods //
    ////////////////////

    public boolean isConnected(){
        return isConnected;
    }

    public void disconnect(){
        connectGATT.disconnect();
    }

    /**
     * Adds a call to the actionQueue.
     * @param call to be added to the actionQueue
     */
    public void addCall(Action call) {
        actionQueue.add(call);
    }

    /////////////////////
    // Private Methods //
    /////////////////////

    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            MiBandConnectionManager.this.run();
        }
    };

    /**
     * Runs the first action of the queue.
     */
    private void run() {
        handler.removeCallbacks(runnable);
        int delayMilis = DELAY_MAX;

        if(actionQueue.isEmpty()){
            return;
        }else if(!working){
            Action action = actionQueue.poll();
            action.run();
            if(!action.expectsResult()) delayMilis = DELAY_MIN;
            else working = true;
        }

        handler.postDelayed(runnable, delayMilis);
    }

    /**
     * Adds initialization calls to the actionQueue.
     */
    private void initialize() {
        // Enable notifications
        addCall(enableNotifications());

        // Set low latency to do a faster initialization
        addCall(setLowLatency());

        // Reading date for stability - TODO - Check if this is really necessary
        addCall(requestDate());

        // Authentication
        addCall(pair());
        addCall(requestDeviceInformation()); // Needed to send user info to the device
        addCall(requestDeviceName());
        addCall(sendUserInfo()); // Needed to authenticate
        addCall(checkAuthentication()); // Clear the queue when not authenticated

        // Other Initializations
        addCall(setCurrentDate());
        addCall(requestBattery());
        addCall(sendCommand(COMMAND.SET_WEAR_LOCATION_LEFT)); // Set wear location
        addCall(setFitnessGoal(1000)); // TODO - Check and set fitness by the app

        // Enable other notifications // TODO - enable only when needed
        addCall(enableNotificationsFrom(UUID_CHAR.REALTIME_STEPS));
        addCall(enableNotificationsFrom(UUID_CHAR.ACTIVITY_DATA));
        addCall(enableNotificationsFrom(UUID_CHAR.BATTERY));
        addCall(enableNotificationsFrom(UUID_CHAR.SENSOR_DATA));

        // Enable Heart Rate notifications
        addCall(enableHeartRateNotifications());
        addCall(setHeartRateSleepSupport());

        // Set high latency to get an stable connection
        addCall(setHighLatency());

        // Initialization finished - device is ready to make other calls
        addCall(setInitialized());
    }

    private void test() {
        startRealtimeStepsMeasurement();
        //startHeartRateMeasurement();
        addCall(waitFor(9000)); // wait 9 seconds
        addCall(sync());
    }

    private void startHeartRateMeasurement() {
        addCall(enableHeartRateNotifications());
        addCall(sendHRCommand(COMMAND.START_HEART_RATE_MEASUREMENT_CONTINUOUS));
    }

    private void startRealtimeStepsMeasurement() {
        addCall(new ActionWithResponse() {
            @Override
            public void run() {
                miliService.read(UUID_CHAR.REALTIME_STEPS);
            }
        });
        addCall(enableNotificationsFrom(UUID_CHAR.REALTIME_STEPS));


        // TODO - Check use
        addCall(sendCommand(COMMAND.FETCH_DATA));
        // Check data received
        addCall(sendCommand(new byte[]{0x0a,0x12,0x02,0x17,0x0b,0x1c,0x0a,0x00,0x00})); // Confirm

        addCall(new ActionWithResponse() {
            @Override
            public void run() {
                miliService.read(UUID_CHAR.TEST);
            }
        });

    }


    // TODO
    private void testVibration() {
        addCall(sendCommand(COMMAND.START_VIBRATION));
        addCall(waitFor(5000)); // 5 sec
        addCall(sendCommand(COMMAND.STOP_MOTOR_VIBRATION));
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
     * Get an action to request the date from MiBand.
     * @return RequestDate Action
     */
    private Action requestDate() {
        return new ActionWithResponse() {
            @Override
            public void run() {
                miliService.readDate();
            }
        };
    }

    /**
     * Get an action to write the actual date on the Mi Band.
     * @return WriteDate Action
     */
    private Action setCurrentDate() {
        return writeDate(new MiDate());
    }

    /**
     * Get an action to write the date on the Mi Band.
     * @param date - Date to be written
     * @return WriteDate Action
     */
    private Action writeDate(final MiDate date) {
        return new ActionWithResponse() {
            @Override
            public void run() {
                miliService.writeDate(date.getData());
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
     * @param command - Command to be send to the MiBand
     * @return SendCommand Action
     */
    private Action sendCommand(final byte[] command) {
        return new ActionWithResponse() {
            @Override
            public void run() {
                Log.d("COMMAND", convertBytesToString(command));
                miliService.sendCommand(command);
            }
        };
    }

    /**
     * Get an action to send a heart rate command to the MiBand.
     * @param command - Command to be send to the MiBand
     * @return SendHRCommand Action
     */
    private Action sendHRCommand(final byte[] command) {
        return new ActionWithResponse() {
            @Override
            public void run() {
                Log.d("COMMAND_HR", convertBytesToString(command));
                heartRateService.sendCommand(command);
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

    private Action sync() {
        return new ActionWithResponse() {
            @Override
            public void run() {
                miliService.sendCommand(COMMAND.SYNC);
                addCall(waitFor(9000)); // wait 9 sec
                addCall(sync());
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
            // We will try 50 times to check the Authentication (about 25 seconds)
            private int timesOut = 50;

            @Override
            public void run() {
                if(!authenticated){
                    timesOut = timesOut - 1;
                    if(timesOut == 0){
                        actionQueue.clear();
                        // TODO - Set authentication failed
                    }
                    else actionQueue.addFirst(this);
                }
            }
        };
    }

    /**
     * Wait for an especific time before running the next action.
     * @param delayMilis - Time to be waiting in miliseconds
     * @return Wait Action
     */
    private Action waitFor(final int delayMilis) {
        return new ActionWithoutResponse() {
            @Override
            public void run() {
                if(delayMilis > 0) actionQueue.addFirst(waitFor(delayMilis - DELAY_MIN));
            }
        };
    }

    /**
     * Starts hearth rate measurement while sleep.
     * @return Action to support hearth rate mesurement on sleep
     */
    private Action setHeartRateSleepSupport() {
        return new ActionWithoutResponse() {
            @Override
            public void run() {
                if(deviceInfo.isMili1S()){
                    actionQueue.addFirst(sendHRCommand(COMMAND.START_HEART_RATE_MEASUREMENT_SLEEP));
                    actionQueue.addFirst(sendHRCommand(COMMAND.UNKNOWN_HR_COMMAND_TO_INIT));
                }
            }
        };
    }

    /**
     * Sets the number of steps that the user have as a Goal.
     * @return Action to set fitness goal.
     */
    private Action setFitnessGoal(final int fitnessGoal) {
        return new ActionWithoutResponse() {
            @Override
            public void run() {
                byte [] command = {
                    COMMAND.SET_FITNESS_GOAL[0],
                    0x0,
                    (byte) (fitnessGoal & 0xff),
                    (byte) ((fitnessGoal >>> 8) & 0xff)
                };
                actionQueue.addFirst(sendCommand(command));
            }
        };
    }

    /**
     * Sets the device as initialized.
     * @return Action to set the device as initialized.
     */
    private Action setInitialized() {
        return new ActionWithoutResponse() {
            @Override
            public void run() {
                // TODO
            }
        };
    }

    /* With response when needed */

    /**
     * Get an action to enable notifications from MiBand.
     * @return EnableNotifications Action
     */
    private Action enableNotifications() {
        return new ActionWithConditionalResponse() {
            @Override
            public void run() {
                this.expectsResult = miliService.enableNotifications();
            }
        };
    }

    private Action enableNotificationsFrom(final UUID characteristic) {
        return new ActionWithConditionalResponse() {
            @Override
            public void run() {
                this.expectsResult = miliService.enableNotificationsFrom(characteristic);
            }
        };
    }

    private Action enableHeartRateNotifications() {
        return new ActionWithConditionalResponse() {
            @Override
            public void run() {
                if(deviceInfo.supportsHeartRate()) this.expectsResult = heartRateService.enableNotifications();
            }
        };
    }

    /* Not tested yet - TODO */

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

    /**
     * Get an action to perform a remote disconnection.
     * @return RemoteDisconnect Action
     */
    private Action remoteDisconect() {
        return new ActionWithoutResponse() {
            @Override
            public void run() {
                miliService.remoteDisconnect();
            }
        };
    }

    //////////////
    // Handlers //
    //////////////

    /**
     * Handles notification response.
     * @param value - Notification value
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
                break;
            case NOTIFICATION.RESET_AUTHENTICATION_FAILED:
                Log.d("Notification", "Reset Authentication Failed");
                authenticated = false;
                break;
            case NOTIFICATION.RESET_AUTHENTICATION_SUCCESS:
                Log.d("Notification", "Reset Authentication Success");
                authenticated = true;
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
                break;
            case NOTIFICATION.STATUS_MOTOR_TEST:
                Log.d("Notification", "Motor TEST");
                break;
            default:
                Log.d("Notification", "Code " + value[0]);
        }
    }


    // DEBUG
    private String convertBytesToString(byte[] data) {
        String str = "[";
        for(int i = 0; i < data.length; i++){
            if(i != 0) str = str + ", ";
            str = str + (data[i] & 0x0ff);
        }
        str = str + "]";
        return str;
    }

    private void showServices(BluetoothGatt gatt) {
        for(BluetoothGattService service : gatt.getServices()) {
            Log.d("SERVICE", "" + service.getUuid() + " - " + service.getType());
            for(BluetoothGattCharacteristic characteristic : service.getCharacteristics()) {
                if(canRead(characteristic.getProperties())){
                    Log.d("CHARACTERISTIC", "\t" + characteristic.getUuid() + " - READABLE");
                    addCall(read(service.getUuid(),characteristic.getUuid()));
                }else{
                    Log.d("CHARACTERISTIC", "\t" + characteristic.getUuid() + " - " + characteristic.getPermissions()
                            + " - " +  characteristic.getProperties());
                }
                for(BluetoothGattDescriptor descriptor : characteristic.getDescriptors()) {
                    Log.d("DESCRIPTOR", "\t\t" + descriptor.getUuid() + " - " + descriptor.getPermissions());
                }
            }
        }
    }

    private boolean canRead(int properties){
        if(properties >= 64) properties = properties - 64;
        if(properties >= 32) properties = properties - 32;
        if(properties >= 16) properties = properties - 16;
        if(properties >= 8) properties = properties - 8;
        if(properties >= 4) properties = properties - 4;
        if(properties >= 2) return true;
        return false;
    }

    private Action read(final UUID serviceUUID, final UUID characteristicUUID){
        return new ActionWithResponse() {
            @Override
            public void run() {
                miliService.read(serviceUUID,characteristicUUID);
            }
        };
    }
}
