package edu.udg.exit.herthrate.MiBand.Services;

import android.bluetooth.BluetoothGatt;
import edu.udg.exit.herthrate.Constants;

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
        writeCharacteristic(Constants.UUID_SERVICE.VIBRATION,Constants.UUID_CHAR.VIBRATION,Constants.PROTOCOL.VIBRATION_WITH_LED);
    }

    /**
     *  Vibration without led.
     *  REQUIREMENT: ANY.
     */
    public void vibrationWithoutLed() {
        writeCharacteristic(Constants.UUID_SERVICE.VIBRATION,Constants.UUID_CHAR.VIBRATION,Constants.PROTOCOL.VIBRATION_WITHOUT_LED);
    }

    /**
     * Vibration 10 times with led.
     * REQUIREMENT: TODO - PAIR ????.
     */
    public void vibration10TimesWithLed() {
        writeCharacteristic(Constants.UUID_SERVICE.VIBRATION,Constants.UUID_CHAR.VIBRATION,Constants.PROTOCOL.VIBRATION_10_TIMES_WITH_LED);
    }
}
