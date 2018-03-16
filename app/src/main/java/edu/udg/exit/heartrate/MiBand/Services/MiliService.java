package edu.udg.exit.heartrate.MiBand.Services;

import android.bluetooth.BluetoothGatt;

import edu.udg.exit.heartrate.MiBand.MiBandConstants.*;
import edu.udg.exit.heartrate.MiBand.Utils.Latency;
import edu.udg.exit.heartrate.MiBand.Utils.MiDate;

import java.util.UUID;

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
     */
    public boolean enableNotifications() {
        return enableNotificationsFrom(UUID_CHAR.NOTIFICATION);
    }

    /**
     * Disables notifications.
     * REQUIREMENT : ANY
     * WARNING :  It doesn't reply
     */
    public boolean disableNotifications() {
        return disableNotificationsFrom(UUID_CHAR.NOTIFICATION);
    }

    /**
     * Enables notifications from a characteristic.
     * REQUIREMENT : ANY
     */
    public boolean enableNotificationsFrom(UUID characteristic) {
        return setCharacteristicNotification(UUID_SERVICE.MILI, characteristic, UUID_DESC.UPDATE_NOTIFICATION,true);
    }

    /**
     * Disables notifications from a characteristic.
     * REQUIREMENT : ANY
     * WARNING :  It doesn't reply
     */
    public boolean disableNotificationsFrom(UUID characteristic) {
        return setCharacteristicNotification(UUID_SERVICE.MILI, characteristic, UUID_DESC.UPDATE_NOTIFICATION,false);
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
        writeCharacteristic(UUID_SERVICE.MILI, UUID_CHAR.LE_PARAMS,latencyBytes);
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
        writeCharacteristic(UUID_SERVICE.MILI, UUID_CHAR.LE_PARAMS,latencyBytes);
    }

    /**
     * Read date time.
     * REQUIREMENT : Write date.
     * WARNING : If date isn't set this will return empty byte array.
     */
    public void readDate() {
        readCharacteristic(UUID_SERVICE.MILI, UUID_CHAR.DATE_TIME);
    }

    /**
     * Write date time.
     * REQUIREMENT : ANY
     * @param data - Data to be written
     */
    public void writeDate(byte[] data) {
        writeCharacteristic(UUID_SERVICE.MILI, UUID_CHAR.DATE_TIME, data);
    }

    /**
     * Pair.
     * REQUIREMENT : ANY
     */
    public void pair() {
        writeCharacteristic(UUID_SERVICE.MILI, UUID_CHAR.PAIR, PROTOCOL.PAIR);
    }

    /**
     * Unpair.
     * REQUIREMENT : TODO - ANY ??
     * TODO - Not working
     */
    public void unpair() {
        writeCharacteristic(UUID_SERVICE.MILI, UUID_CHAR.PAIR,new byte[]{0x0});
    }

    /**
     * Read pair.
     * REQUIREMENT : ANY
     */
    public void readPair() {
        readCharacteristic(UUID_SERVICE.MILI, UUID_CHAR.PAIR);
    }

    /**
     * Read battery.
     * REQUIREMENT : ANY
     */
    public void requestBattery() {
        readCharacteristic(UUID_SERVICE.MILI, UUID_CHAR.BATTERY);
    }

    /**
     * Read Device Information.
     * REQUIREMENT : ANY
     */
    public void requestDeviceInformation() {
        readCharacteristic(UUID_SERVICE.MILI, UUID_CHAR.DEVICE_INFO);
    }

    /**
     * Read Device Name.
     * REQUIREMENT : ANY
     */
    public void requestDeviceName() {
        readCharacteristic(UUID_SERVICE.MILI, UUID_CHAR.DEVICE_NAME);
    }

    /**
     * Send User information to the device.
     * REQUIREMENT : Read Device Information
     * REQUIREMENT : Needs notifications to authenticate
     * User may need to put his finger on the device to confirm (only if not authenticated already).
     */
    public void sendUserInfo(byte[] data) {
        writeCharacteristic(UUID_SERVICE.MILI, UUID_CHAR.USER_INFO, data);
    }

    /**
     * Send a command to the Mi Band via CONTROL_POINT characteristic.
     * REQUIREMENT : PAIR
     * @param command - Bytes to be written on Control Point
     */
    public void sendCommand(byte[] command) {
        writeCharacteristic(UUID_SERVICE.MILI,UUID_CHAR.CONTROL_POINT, command);
    }

    /**
     * Self test - Mi Band will do crazy things.
     * REQUIREMENT : TODO - NOT WORKING.
     * WARNING : Will need to unlink Mi Band from bluetooth.
     */
    public void selfTest() {
        writeCharacteristic(UUID_SERVICE.MILI, UUID_CHAR.TEST, PROTOCOL.SELF_TEST);
    }

    /**
     * Remote disconnect from the Mi Band.
     * REQUIREMENT :  TODO - NOT WORKING
     * WARNING : Will need to unlink Mi Band from bluetooth.
     */
    public void remoteDisconnect() {
        writeCharacteristic(UUID_SERVICE.MILI, UUID_CHAR.TEST, PROTOCOL.REMOTE_DISCONNECT);
    }

}
