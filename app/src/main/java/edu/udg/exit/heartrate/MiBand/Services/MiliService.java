package edu.udg.exit.heartrate.MiBand.Services;

import android.bluetooth.BluetoothGatt;
import edu.udg.exit.heartrate.MiBand.MiBandConstants;
import edu.udg.exit.heartrate.MiBand.Utils.Latency;

public class MiliService extends MiBandService {

    ////////////////////////
    // Life Cycle Methods //
    ////////////////////////

    /**
     * Constructor.
     * @param gatt
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
    public void enableNotifications() {
        setCharacteristicNotification(MiBandConstants.UUID_SERVICE.MILI, MiBandConstants.UUID_CHAR.NOTIFICATION, MiBandConstants.UUID_DESC.UPDATE_NOTIFICATION,true);
    }

    /**
     * Disables notifications.
     * REQUIREMENT : ANY
     * WARNING :  IT doesn't reply
     */
    public void disableNotifications() {
        setCharacteristicNotification(MiBandConstants.UUID_SERVICE.MILI, MiBandConstants.UUID_CHAR.NOTIFICATION, MiBandConstants.UUID_DESC.UPDATE_NOTIFICATION,false);
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
        int advertisementInterval = 0;

        // Get low latency params in bytes
        byte[] latencyBytes = new Latency(minConnectionInterval, maxConnectionInterval, latency, timeout, advertisementInterval).getLatencyBytes();

        // Write latency bytes to miBand
        writeCharacteristic(MiBandConstants.UUID_SERVICE.MILI, MiBandConstants.UUID_CHAR.LE_PARAMS,latencyBytes);
    }

    /**
     * Read date time.
     * REQUIREMENT : TODO - It isn't reading.
     */
    public void readDate() {
        readCharacteristic(MiBandConstants.UUID_SERVICE.MILI, MiBandConstants.UUID_CHAR.DATE_TIME);
    }

    /**
     * Pair.
     * REQUIREMENT : TODO - Read data time???? (for the moment seems to be working).
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
    public void readBattery() {
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
     * User may need to put his finger on the device to confirm.
     */
    public void sendUserInfo(byte[] data){
        writeCharacteristic(MiBandConstants.UUID_SERVICE.MILI, MiBandConstants.UUID_CHAR.USER_INFO,data);
    }

    /**
     * Self test - Mi Band will do crazy things.
     * REQUIREMENT : TODO - PAIR ???? Maybe another service.
     * WARNING : Will need to unlink miband from bluetooth.
     */
    public void selfTest() {
        writeCharacteristic(MiBandConstants.UUID_SERVICE.MILI, MiBandConstants.UUID_CHAR.TEST, MiBandConstants.PROTOCOL.SELF_TEST);
    }

}
