package edu.udg.exit.heartrate.MiBand.Services;

import android.bluetooth.BluetoothGatt;
import edu.udg.exit.heartrate.MiBand.MiBandConstants;

public class VibrationService extends MiBandService {

    ////////////////////////
    // Life Cycle Methods //
    ////////////////////////

    public VibrationService() {
        super();
    }

    /**
     * Constructor.
     * @param gatt
     */
    public VibrationService(BluetoothGatt gatt) {
        super(gatt);
    }

    ////////////////////
    // Public Methods //
    ////////////////////

    /**
     *  Vibration with led.
     *  REQUIREMENT: ANY.
     */
    public void vibrationWithLed() {
        writeCharacteristic(MiBandConstants.UUID_SERVICE.VIBRATION, MiBandConstants.UUID_CHAR.VIBRATION, MiBandConstants.PROTOCOL.VIBRATION_WITH_LED);
    }

    /**
     *  Vibration without led.
     *  REQUIREMENT: ANY.
     */
    public void vibrationWithoutLed() {
        writeCharacteristic(MiBandConstants.UUID_SERVICE.VIBRATION, MiBandConstants.UUID_CHAR.VIBRATION, MiBandConstants.PROTOCOL.VIBRATION_WITHOUT_LED);
    }

    /**
     * Vibration 10 times with led.
     * REQUIREMENT: PAIR successful.
     */
    public void vibration10TimesWithLed() {
        writeCharacteristic(MiBandConstants.UUID_SERVICE.VIBRATION, MiBandConstants.UUID_CHAR.VIBRATION, MiBandConstants.PROTOCOL.VIBRATION_10_TIMES_WITH_LED);
    }
}
