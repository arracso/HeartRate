package edu.udg.exit.heartrate.MiBand.Services;

import android.bluetooth.BluetoothGatt;
import edu.udg.exit.heartrate.MiBand.MiBandConstants;
import edu.udg.exit.heartrate.MiBand.Utils.Latency;

/**
 * Mi Band Mili Service.
 */
public class MiliService extends MiBandService {

    ////////////////////////
    // Life Cycle Methods //
    ////////////////////////

    /**
     * Constructor.
     * @param gatt - Connected GATT
     */
    public MiliService(BluetoothGatt gatt) {
        super(gatt);
    }

    ////////////////////
    // Public Methods //
    ////////////////////

    /**
     * Enables notifications.
     * REQUIREMENT : ANY
     * WARNING : It doesn't reply
     */
    public boolean enableNotifications() {
        return setCharacteristicNotification(MiBandConstants.UUID_SERVICE.MILI, MiBandConstants.UUID_CHAR.NOTIFICATION, MiBandConstants.UUID_DESC.UPDATE_NOTIFICATION,true);
    }

    /**
     * Disables notifications.
     * REQUIREMENT : ANY
     * WARNING :  It doesn't reply
     */
    public boolean disableNotifications() {
        return setCharacteristicNotification(MiBandConstants.UUID_SERVICE.MILI, MiBandConstants.UUID_CHAR.NOTIFICATION, MiBandConstants.UUID_DESC.UPDATE_NOTIFICATION,false);
    }

    /**
     * Sets the lowest latency possible.
     * REQUIREMENT : ANY
     */
    public void setLowLatency() {
        // Low latency params
        int minConnectionInterval = 39;
        int maxConnectionInterval = 49;
        int latency = 0;
        int timeout = 500;
        int connectionInterval = 0;
        int advertisementInterval = 0;

        // Get low latency params in bytes
        byte[] latencyBytes = new Latency(minConnectionInterval, maxConnectionInterval, latency, timeout, connectionInterval, advertisementInterval).getLatencyBytes();

        // Write latency bytes to miBand
        writeCharacteristic(MiBandConstants.UUID_SERVICE.MILI, MiBandConstants.UUID_CHAR.LE_PARAMS,latencyBytes);
    }

    /**
     * Sets the highest latency possible.
     * REQUIREMENT : ANY
     */
    public void setHighLatency() {
        // High latency params
        int minConnectionInterval = 460;
        int maxConnectionInterval = 500;
        int latency = 0;
        int timeout = 500;
        int connectionInterval = 0;
        int advertisementInterval = 0;

        // Get high latency params in bytes
        byte[] latencyBytes = new Latency(minConnectionInterval, maxConnectionInterval, latency, timeout, connectionInterval, advertisementInterval).getLatencyBytes();

        // Write latency bytes to miBand
        writeCharacteristic(MiBandConstants.UUID_SERVICE.MILI, MiBandConstants.UUID_CHAR.LE_PARAMS,latencyBytes);
    }

    /**
     * Read date time.
     * REQUIREMENT : TODO - NOT READING ANYTHING
     */
    public void requestDate() {
        readCharacteristic(MiBandConstants.UUID_SERVICE.MILI, MiBandConstants.UUID_CHAR.DATE_TIME);
    }

    /**
     * Pair.
     * REQUIREMENT : ANY
     */
    public void pair() {
        writeCharacteristic(MiBandConstants.UUID_SERVICE.MILI, MiBandConstants.UUID_CHAR.PAIR, MiBandConstants.PROTOCOL.PAIR);
    }

    /**
     * Unpair.
     * REQUIREMENT : TODO - ANY ??
     * TODO - Not working
     */
    public void unpair() {
        writeCharacteristic(MiBandConstants.UUID_SERVICE.MILI, MiBandConstants.UUID_CHAR.PAIR, MiBandConstants.PROTOCOL.REMOTE_DISCONNECT);
    }

    /**
     * Read pair.
     * REQUIREMENT : ANY
     */
    public void readPair() {
        readCharacteristic(MiBandConstants.UUID_SERVICE.MILI, MiBandConstants.UUID_CHAR.PAIR);
    }

    /**
     * Read battery.
     * REQUIREMENT : ANY
     */
    public void requestBattery() {
        readCharacteristic(MiBandConstants.UUID_SERVICE.MILI, MiBandConstants.UUID_CHAR.BATTERY);
    }

    /**
     * Read Device Information.
     * REQUIREMENT : ANY
     */
    public void requestDeviceInformation() {
        readCharacteristic(MiBandConstants.UUID_SERVICE.MILI, MiBandConstants.UUID_CHAR.DEVICE_INFO);
    }

    /**
     * Read Device Name.
     * REQUIREMENT : ANY
     */
    public void requestDeviceName() {
        readCharacteristic(MiBandConstants.UUID_SERVICE.MILI, MiBandConstants.UUID_CHAR.DEVICE_NAME);
    }

    /**
     * Send User information to the device.
     * REQUIREMENT : Read Device Information
     * REQUIREMENT : Needs notifications to authenticate
     * User may need to put his finger on the device to confirm (only if not authenticated already).
     */
    public void sendUserInfo(byte[] data) {
        writeCharacteristic(MiBandConstants.UUID_SERVICE.MILI, MiBandConstants.UUID_CHAR.USER_INFO, data);
    }

    /**
     * Send a command to the Mi Band via CONTROL_POINT characteristic.
     * REQUIREMENT : PAIR
     * @param data - Bytes to be written on Control Point
     */
    public void sendCommand(byte[] data) {
        writeCharacteristic(MiBandConstants.UUID_SERVICE.MILI, MiBandConstants.UUID_CHAR.CONTROL_POINT, data);
    }

    /**
     * Send a hearth rate command to the Mi Band via HEARTRATE_CONTROL_POINT characteristic.
     * REQUIREMENT : PAIR
     * @param data - Byte to be written on Heartrate Control Point
     */
    public void sendHRCommand(byte[] data) {
        writeCharacteristic(MiBandConstants.UUID_SERVICE.MILI, MiBandConstants.UUID_CHAR.HEARTRATE_CONTROL_POINT, data);
    }

    /**
     * Self test - Mi Band will do crazy things.
     * REQUIREMENT : TODO - NOT WORKING.
     * WARNING : Will need to unlink miband from bluetooth.
     */
    public void selfTest() {
        writeCharacteristic(MiBandConstants.UUID_SERVICE.MILI, MiBandConstants.UUID_CHAR.TEST, MiBandConstants.PROTOCOL.SELF_TEST);
    }


}
