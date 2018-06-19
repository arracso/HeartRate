package edu.udg.exit.heartrate.Devices.MiBand;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.util.Log;
import edu.udg.exit.heartrate.Devices.ConnectionManager;
import edu.udg.exit.heartrate.Devices.MiBand.Services.HeartRateService;
import edu.udg.exit.heartrate.Devices.MiBand.Services.MiliService;
import edu.udg.exit.heartrate.Devices.MiBand.Services.VibrationService;
import edu.udg.exit.heartrate.Devices.MiBand.Utils.*;
import edu.udg.exit.heartrate.Global;
import edu.udg.exit.heartrate.Interfaces.IPairView;
import edu.udg.exit.heartrate.Model.User;
import edu.udg.exit.heartrate.Services.BluetoothService;
import edu.udg.exit.heartrate.Utils.Actions.Action;
import edu.udg.exit.heartrate.Utils.Actions.ActionWithConditionalResponse;
import edu.udg.exit.heartrate.Utils.Actions.ActionWithResponse;
import edu.udg.exit.heartrate.Utils.Actions.ActionWithoutResponse;
import edu.udg.exit.heartrate.Utils.UserPreferences;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

import static edu.udg.exit.heartrate.Devices.MiBand.MiBandConstants.*;

/**
 * Class that performs a connection with a Mi Band and handles it.
 */
public class MiBandConnectionManager extends ConnectionManager {

    ///////////////
    // CONSTANTS //
    ///////////////

    private static final int SYNC_PERIOD = 10000; // 10s

    ////////////////
    // Attributes //
    ////////////////

    // MiBandServices
    private MiliService miliService;
    private VibrationService vibrationService;
    private HeartRateService heartRateService;

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
    public MiBandConnectionManager(BluetoothService bluetoothService) {
        super(bluetoothService);

        // MiBandService
        miliService = null;
        vibrationService = null;
        heartRateService = null;

        // Info
        deviceInfo = null;
        userInfo = null;

        // Auth
        authenticated = false;
    }

    @Override
    protected void onServicesDiscovered(BluetoothGatt gatt) {
        Log.d("GATT", "Services discovered");

        // Add address to user information
        userInfo = retrieveUserInfo();
        userInfo.setBlueToothAddress(gatt.getDevice().getAddress());

        // Init Services
        miliService = new MiliService(gatt);
        vibrationService = new VibrationService(gatt);
        heartRateService = new HeartRateService(gatt);

        // Initialize
        initialize();
    }

    @Override
    protected void onCharacteristicRead(BluetoothGattCharacteristic characteristic) {
        UUID characteristicUUID = characteristic.getUuid();
        if (MiBandConstants.UUID_CHAR.DEVICE_INFO.equals(characteristicUUID)) {
            deviceInfo = new DeviceInfo(characteristic.getValue());
            Log.d("GATTr", "Info -> " + deviceInfo);
        } else if (MiBandConstants.UUID_CHAR.DEVICE_NAME.equals(characteristicUUID)) {
            String name = new String(characteristic.getValue(), StandardCharsets.UTF_8); // TODO - Stop reading ���� at the beginning
            Log.d("GATTr", "Name -> " + name);
        } else if (MiBandConstants.UUID_CHAR.BATTERY.equals(characteristicUUID)) {
            BatteryInfo batteryInfo = new BatteryInfo(characteristic.getValue());
            Log.d("GATTr", "Battery -> " + batteryInfo);
        } else if (MiBandConstants.UUID_CHAR.DATE_TIME.equals(characteristicUUID)) {
            MiDate miDate = new MiDate(characteristic.getValue());
            Log.d("GATTr", "Date -> " + miDate + " - " + convertBytesToString(characteristic.getValue()));
        } else {
            if(characteristic.getValue().length>0){
                Log.d("GATTr", "Characteristic -> " + characteristic.getValue()[0]);
            }else{
                Log.d("GATTr", "Characteristic -> " + characteristic.getValue().length);
            }
        }
    }

    @Override
    protected void onCharacteristicWrite(BluetoothGattCharacteristic characteristic) {
        if(MiBandConstants.UUID_CHAR.DEVICE_INFO.equals(characteristic.getUuid())){
            Log.d("GATTw", "Device information -> " + convertBytesToString(characteristic.getValue()));
        }else if(MiBandConstants.UUID_CHAR.DEVICE_NAME.equals(characteristic.getUuid())){
            Log.d("GATTw", "Device name -> " + convertBytesToString(characteristic.getValue()));
        }else if(MiBandConstants.UUID_CHAR.NOTIFICATION.equals(characteristic.getUuid())){
            Log.d("GATTw", "Notification -> " + characteristic.getValue().length);
        }else if(MiBandConstants.UUID_CHAR.USER_INFO.equals(characteristic.getUuid())){
            UserInfo userInfo = new UserInfo(characteristic.getValue());
            Log.d("GATTw","" + userInfo);
        }else if(MiBandConstants.UUID_CHAR.CONTROL_POINT.equals(characteristic.getUuid())){
            Log.d("GATTw", "Control point -> " + convertBytesToString(characteristic.getValue()));
        }else if(MiBandConstants.UUID_CHAR.REALTIME_STEPS.equals(characteristic.getUuid())){
            Log.d("GATTw", "Realtime steps -> " + characteristic.getValue().length);
        }else if(MiBandConstants.UUID_CHAR.LE_PARAMS.equals(characteristic.getUuid())){
            Latency latency = new Latency(characteristic.getValue());
            Log.d("GATTw", "Latency -> " + latency);
        }else if(MiBandConstants.UUID_CHAR.PAIR.equals(characteristic.getUuid())){
            Log.d("GATTw", "PAIR -> " + characteristic.getValue()[0]);
        }else if(MiBandConstants.UUID_CHAR.DATE_TIME.equals(characteristic.getUuid())){
            MiDate miDate = new MiDate(characteristic.getValue());
            Log.d("GATTw", "Date -> " + miDate);
        }else if(MiBandConstants.UUID_CHAR.BATTERY.equals(characteristic.getUuid())){
            BatteryInfo batteryInfo = new BatteryInfo(characteristic.getValue());
            Log.d("GATTw", "Battery -> " + batteryInfo);
        }else{
            Log.d("GATTw", "Characteristic -> " + convertBytesToString(characteristic.getValue()));
        }
    }

    @Override
    protected void onCharacteristicChanged(BluetoothGattCharacteristic characteristic) {
        if(MiBandConstants.UUID_CHAR.DEVICE_INFO.equals(characteristic.getUuid())){
            Log.d("GATTc", "Device information -> " + convertBytesToString(characteristic.getValue()));
        }else if(MiBandConstants.UUID_CHAR.DEVICE_NAME.equals(characteristic.getUuid())){
            Log.d("GATTc", "Device name -> " + convertBytesToString(characteristic.getValue()));
        }else if(MiBandConstants.UUID_CHAR.NOTIFICATION.equals(characteristic.getUuid())){
            handleNotification(characteristic.getValue());
        }else if(MiBandConstants.UUID_CHAR.USER_INFO.equals(characteristic.getUuid())){
            Log.d("GATTc", "User information -> " + convertBytesToString(characteristic.getValue()));
        }else if(MiBandConstants.UUID_CHAR.CONTROL_POINT.equals(characteristic.getUuid())){
            Log.d("GATTc", "Control point -> " + convertBytesToString(characteristic.getValue()));
        }else if(MiBandConstants.UUID_CHAR.REALTIME_STEPS.equals(characteristic.getUuid())){
            Log.d("GATTc", "Realtime steps -> " + convertBytesToString(characteristic.getValue()));
        }else if(MiBandConstants.UUID_CHAR.ACTIVITY_DATA.equals(characteristic.getUuid())){
            Log.d("GATTc", "Activity -> " + convertBytesToString(characteristic.getValue()));
        }else if(MiBandConstants.UUID_CHAR.PAIR.equals(characteristic.getUuid())){
            Log.d("GATTc", "PAIR -> " + characteristic.getValue()[0]);
        }else if(MiBandConstants.UUID_CHAR.DATE_TIME.equals(characteristic.getUuid())){
            MiDate miDate = new MiDate(characteristic.getValue());
            Log.d("GATTc", "Date -> " + miDate);
        }else if(MiBandConstants.UUID_CHAR.BATTERY.equals(characteristic.getUuid())){
            BatteryInfo batteryInfo = new BatteryInfo(characteristic.getValue());
            Log.d("GATTc", "Battery -> " + batteryInfo);
        }else if(UUID_CHAR.HEARTRATE_NOTIFICATION.equals(characteristic.getUuid())){
            handleHeartrateNotification(characteristic.getValue());
        }else{
            Log.d("GATTc", "Characteristic -> " + characteristic.getUuid() + " - " + convertBytesToString(characteristic.getValue()));
        }
    }

    @Override
    protected void onDescriptorRead(BluetoothGattDescriptor descriptor) {
        Log.d("MiBand descriptor", "Read " + descriptor.getUuid());
    }

    @Override
    protected void onDescriptorWrite(BluetoothGattDescriptor descriptor) {
        Log.d("MiBand descriptor", "Write " + descriptor.getUuid());
    }

    ////////////////////
    // Public Methods //
    ////////////////////

    @Override
    public void startMeasure() {
        startMeasurement();
        run();
    }

    @Override
    public void stopMeasure() {
        clearCalls();
        stopHeartRateMeasurement();
        run();
    }

    /////////////////////
    // Private Methods //
    /////////////////////

    /**
     * Retrieve user information from user preferences.
     */
    private UserInfo retrieveUserInfo(){
        String userOnj = UserPreferences.getInstance().load(bluetoothService.getApplicationContext(),UserPreferences.USER_PROFILE);
        User user = Global.gson.fromJson(userOnj, User.class);
        UserInfo userInfo = new UserInfo();
        if(user != null){
            if(user.getId() != null) userInfo.setUsername(""+user.getId());
            if(user.getHeight() != null) userInfo.setHeight(user.getHeight());
            if(user.getWeight() != null) userInfo.setWeight(user.getWeight());
            if(user.getSex() != null) userInfo.setGender(user.getSex());
            if(user.getBirthYear() != null) userInfo.setAge((new Date()).getYear() - user.getBirthYear());
        }
        return userInfo;
    }

    /**
     * Adds initialization calls to the actionQueue.
     */
    private void initialize() {
        // Enable notifications
        addCall(enableNotifications());

        // Set low latency to do a faster initialization
        addCall(setLowLatency());

        // Reading date for stability
        addCall(requestDate()); // TODO - Check if this is really necessary

        // Authentication
        addCall(requestDeviceInformation()); // Needed to send user info to the device
        addCall(requestDeviceName()); // Not needed to pair
        addCall(sendUserInfo()); // Needed to authenticate
        addCall(checkAuthentication()); // Clear the queue when not authenticated

        // Other Initializations
        addCall(setCurrentDate());
        addCall(requestBattery());
        addCall(sendCommand(COMMAND.SET_WEAR_LOCATION_LEFT)); // Set wear location // TODO - Set by the app

        // Enable battery notifications
        addCall(enableNotificationsFrom(UUID_CHAR.BATTERY));

        // Set high latency to get an stable connection
        addCall(setHighLatency());

        // Initialization finished - device is ready to make other calls
        addCall(setInitialized());
    }

    /**
     * Adds further initialization calls to the actionQueue.
     */
    private void initializeAll() {
        // Enable notifications
        addCall(enableNotifications());

        // Set low latency to do a faster initialization
        addCall(setLowLatency());

        // Reading date for stability
        addCall(requestDate());

        // Authentication
        addCall(pair()); // TODO - Check if this is necessary
        addCall(requestDeviceInformation()); // Needed to send user info to the device
        addCall(requestDeviceName()); // Not needed to pair
        addCall(sendUserInfo()); // Needed to authenticate
        addCall(checkAuthentication()); // Clear the queue when not authenticated

        // Other Initializations
        addCall(setCurrentDate());
        addCall(requestBattery());
        addCall(sendCommand(COMMAND.SET_WEAR_LOCATION_LEFT)); // Set wear location
        addCall(setFitnessGoal(1000)); // TODO - Check and set fitness by the app

        // Enable other notifications // Enable only when needed
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

    /**
     * Start the measurements.
     * Needs to sync periodically in order to receive the notifications.
     */
    private void startMeasurement() {
        startHeartRateMeasurement();
        addCall(syncPeriodically());
    }

    /**
     * Start to measure heart rate.
     * Needs to call (sync) periodically in order to keep receiving notifications.
     */
    private void startHeartRateMeasurement() {
        addCall(enableHeartRateNotifications());
        addCall(sendHRCommand(COMMAND.START_HEART_RATE_MEASUREMENT_CONTINUOUS));
    }

    private void stopHeartRateMeasurement() {
        addCall(sendHRCommand(COMMAND.STOP_HEART_RATE_MEASUREMENT_CONTINUOUS));
        addCall(dissableHeartRateNotifications());
    }

    /**
     * Start to measure steps
     * Needs to call (sync) periodically in order to keep receiving notifications.
     */
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
        // TODO - Check data received
        addCall(sendCommand(new byte[]{0x0a,0x12,0x02,0x17,0x0b,0x1c,0x0a,0x00,0x00})); // Confirm

        addCall(new ActionWithResponse() {
            @Override
            public void run() {
                miliService.read(UUID_CHAR.TEST);
            }
        });
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

    /**
     * Get an action to request synchronization with the MiBand once.
     * @return Synchronization Action
     */
    private Action sync() {
        return new ActionWithResponse() {
            @Override
            public void run() {
                miliService.sendCommand(COMMAND.SYNC);
            }
        };
    }

    /* Without response */

    /**
     * Get an action to request synchronization with the MiBand periodically.
     * @return SyncPeriodically Action
     */
    private Action syncPeriodically() {
        return new ActionWithoutResponse() {
            @Override
            public void run() {
                int latencySync = (2 * 480);
                addCall(waitMilis(SYNC_PERIOD - (3 * DELAY_MIN) - latencySync));
                addCall(sync());
                addCall(syncPeriodically());
            }
        };
    }

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
                        clearCalls();
                    }
                    else addCallFirst(this);
                }
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
                    addCallFirst(sendHRCommand(COMMAND.START_HEART_RATE_MEASUREMENT_SLEEP));
                    addCallFirst(sendHRCommand(COMMAND.UNKNOWN_HR_COMMAND_TO_INIT));
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
                addCallFirst(sendCommand(command));
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
                bluetoothService.getPairView().setPairStatus(IPairView.STATUS_SUCCESS);
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

    private Action dissableHeartRateNotifications() {
        return new ActionWithConditionalResponse() {
            @Override
            public void run() {
                if(deviceInfo.supportsHeartRate()) this.expectsResult = heartRateService.dissableNotifications();
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

    private void handleHeartrateNotification(byte[] value) {
        // Check if value is 2 byte long.
        if(value.length != 2){
            Log.e("Notification", "Received " + value.length + " bytes");
            return ;
        }

        // Handle value
        switch (value[0]) {
            case 6:
                Log.d("Notification", "Heartrate: " + Parse.BytesToInt(value,1,1));
                bluetoothService.setMeasure(new Date(),Parse.BytesToInt(value,1,1));
                break;
            default:
                Log.d("Notification", "Code " + value[0] + " value: " + value[1]);
        }
    }

    // DEBUG
    @Override
    protected Action read(final UUID serviceUUID, final UUID characteristicUUID){
        return new ActionWithResponse() {
            @Override
            public void run() {
                miliService.read(serviceUUID,characteristicUUID);
            }
        };
    }

}
