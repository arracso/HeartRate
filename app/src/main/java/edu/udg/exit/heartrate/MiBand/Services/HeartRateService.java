package edu.udg.exit.heartrate.MiBand.Services;

import android.bluetooth.BluetoothGatt;
import edu.udg.exit.heartrate.MiBand.MiBandConstants;

public class HeartRateService extends MiBandService {

    ////////////////////////
    // Life Cycle Methods //
    ////////////////////////

    /**
     * Constructor.
     * @param gatt - Connected GATT
     */
    public HeartRateService(BluetoothGatt gatt) {
        super(gatt);
    }

    ////////////////////
    // Public Methods //
    ////////////////////

    /**
     * Send a hearth rate command to the Mi Band via HEARTRATE_CONTROL_POINT characteristic.
     * REQUIREMENT : PAIR
     * @param command - Bytes to be written on Heartrate Control Point
     */
    public void sendCommand(byte[] command) {
        writeCharacteristic(MiBandConstants.UUID_SERVICE.HEARTRATE, MiBandConstants.UUID_CHAR.HEARTRATE_CONTROL_POINT, command);
    }

    /**
     * Enables notifications.
     * REQUIREMENT : Heart Rate Support
     */
    public boolean enableNotifications() {
        return setCharacteristicNotification(MiBandConstants.UUID_SERVICE.HEARTRATE, MiBandConstants.UUID_CHAR.HEARTRATE_NOTIFICATION, MiBandConstants.UUID_DESC.UPDATE_NOTIFICATION,true);
    }
}
