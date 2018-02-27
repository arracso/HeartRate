package edu.udg.exit.heartrate.MiBand.Services;

import android.bluetooth.BluetoothGatt;
import edu.udg.exit.heartrate.Constants;
import edu.udg.exit.heartrate.MiBand.Utils.Latency;

public class MiliService extends MiBandService {

    ////////////////////////
    // Life Cycle Methods //
    ////////////////////////

    public MiliService() {
        super();
    }

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
        setCharacteristicNotification(Constants.UUID_SERVICE.MILI,Constants.UUID_CHAR.NOTIFICATION,Constants.UUID_DESC.UPDATE_NOTIFICATION,true);
    }

    /**
     * Disables notifications.
     * REQUIREMENT : ANY
     * WARNING :  IT doesn't reply
     */
    public void disableNotifications() {
        setCharacteristicNotification(Constants.UUID_SERVICE.MILI,Constants.UUID_CHAR.NOTIFICATION,Constants.UUID_DESC.UPDATE_NOTIFICATION,false);
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
        writeCharacteristic(Constants.UUID_SERVICE.MILI,Constants.UUID_CHAR.LE_PARAMS,latencyBytes);
    }

    /**
     * Read date time.
     * REQUIREMENT : TODO - It isn't reading.
     */
    public void readDate() {
        readCharacteristic(Constants.UUID_SERVICE.MILI,Constants.UUID_CHAR.DATE_TIME);
    }

    /**
     * Write Pair.
     * REQUIREMENT : TODO - Read data time???? (for the moment seems to be working).
     */
    public void pair() {
        writeCharacteristic(Constants.UUID_SERVICE.MILI,Constants.UUID_CHAR.PAIR,Constants.PROTOCOL.PAIR);
    }

    /**
     * Read pair.
     * REQUIREMENT : ANY
     */
    public void readPair() {
        readCharacteristic(Constants.UUID_SERVICE.MILI,Constants.UUID_CHAR.PAIR);
    }

    /**
     * Read battery.
     * REQUIREMENT : ANY
     */
    public void readBattery() {
        readCharacteristic(Constants.UUID_SERVICE.MILI,Constants.UUID_CHAR.BATTERY);
    }

    /**
     * Read Device Information.
     * REQUIREMENT : ANY
     */
    public void requestDeviceInformation() {
        readCharacteristic(Constants.UUID_SERVICE.MILI,Constants.UUID_CHAR.DEVICE_INFO);
    }

    /**
     * Read Device Name.
     * REQUIREMENT : ANY
     */
    public void requestDeviceName() {
        readCharacteristic(Constants.UUID_SERVICE.MILI,Constants.UUID_CHAR.DEVICE_NAME);
    }

    /**
     * Send User information to the device.
     * REQUIREMENT : Read Device Information TODO
     */
    public void sendUserInfo(byte[] data){
        writeCharacteristic(Constants.UUID_SERVICE.MILI,Constants.UUID_CHAR.USER_INFO,data);
    }

    /**
     * Self test - Mi Band will do crazy things.
     * REQUIREMENT : TODO - PAIR ???? Maybe another service.
     * WARNING : Will need to unlink miband from bluetooth.
     */
    public void selfTest() {
        writeCharacteristic(Constants.UUID_SERVICE.MILI,Constants.UUID_CHAR.TEST,Constants.PROTOCOL.SELF_TEST);
    }

}
