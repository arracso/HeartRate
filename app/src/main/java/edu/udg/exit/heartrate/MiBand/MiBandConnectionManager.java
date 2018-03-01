package edu.udg.exit.heartrate.MiBand;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothProfile;
import android.util.Log;
import edu.udg.exit.heartrate.Constants;
import edu.udg.exit.heartrate.MiBand.Services.MiliService;
import edu.udg.exit.heartrate.MiBand.Services.VibrationService;
import edu.udg.exit.heartrate.MiBand.Utils.BatteryInfo;
import edu.udg.exit.heartrate.MiBand.Utils.DeviceInfo;
import edu.udg.exit.heartrate.MiBand.Utils.MiDate;
import edu.udg.exit.heartrate.MiBand.Utils.UserInfo;

import java.util.UUID;

public class MiBandConnectionManager extends BluetoothGattCallback {

    ///////////////
    // Constants //
    ///////////////



    ////////////////
    // Attributes //
    ////////////////

    // Connect
    private BluetoothGatt connectGATT;

    // MiBandServices
    private MiliService miliService;
    private VibrationService vibrationService;

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

            // Init Services
            miliService = new MiliService(gatt);
            vibrationService = new VibrationService(gatt);

            // Initialize - TODO
            miliService.setLowLatency(); // Very important to obtain a faster connection
        }
    }

    @Override
    public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        super.onCharacteristicRead(gatt,characteristic,status);

        if (status == BluetoothGatt.GATT_SUCCESS) {
            UUID characteristicUUID = characteristic.getUuid();
            if (Constants.UUID_CHAR.DEVICE_INFO.equals(characteristicUUID)) {
                deviceInfo = new DeviceInfo(characteristic.getValue());
                Log.d("GATTread", "Info: " + deviceInfo);

                // TODO - Extract
                miliService.requestDeviceName();
            } else if (Constants.UUID_CHAR.DEVICE_NAME.equals(characteristicUUID)) {
                String name = new String(characteristic.getValue());
                Log.d("GATTread", "Name: " + name);

                // TODO - Extract
                userInfo = new UserInfo();
                userInfo.setUsername("Oscar");
                userInfo.setBlueToothAddress(connectGATT.getDevice().getAddress());
                miliService.sendUserInfo(userInfo.getData(deviceInfo));
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
                miliService.enableNotifications();
            }else if(Constants.UUID_CHAR.PAIR.equals(characteristic.getUuid())){
                Log.d("GATTwrite", "PAIR: " + characteristic.getValue()[0]);

                // TODO - extract
                if(characteristic.getValue()[0] == 2) miliService.requestDeviceInformation();
            }else if(Constants.UUID_CHAR.DATE_TIME.equals(characteristic.getUuid())){
                MiDate miDate = new MiDate(characteristic.getValue());
                Log.d("GATTwrite", "Date: " + miDate);
            }else if(Constants.UUID_CHAR.BATTERY.equals(characteristic.getUuid())){
                BatteryInfo batteryInfo = new BatteryInfo(characteristic.getValue());
                Log.d("GATTwrite", "Battery: " + batteryInfo);
            }else{
                if(characteristic.getValue().length>0){
                    Log.d("GATTread", "Characteristic: " + characteristic.getValue()[0]);
                }else{
                    Log.d("GATTread", "Characteristic: " + characteristic.getValue());
                }
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

            // TODO - Extract
            if(characteristic.getValue()[0] == 8) miliService.pair();
            else if(characteristic.getValue()[0] == 5) vibrationService.vibration10TimesWithLed();
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

    ////////////////////
    // Public Methods //
    ////////////////////

}
