package edu.udg.exit.heartrate.Devices.MiBand.Services;

import android.bluetooth.BluetoothGatt;
import edu.udg.exit.heartrate.Devices.MiBand.MiBandConstants.*;

/**
 * Mi Band Vibration Service.
 */
public class VibrationService extends MiBandService {

    ////////////////////////
    // Life Cycle Methods //
    ////////////////////////

    /**
     * Constructor.
     * @param gatt - Connected GATT
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
        writeCharacteristic(UUID_SERVICE.VIBRATION, UUID_CHAR.VIBRATION, PROTOCOL.VIBRATION_WITH_LED);
    }

    /**
     *  Vibration without led.
     *  REQUIREMENT: ANY.
     */
    public void vibrationWithoutLed() {
        writeCharacteristic(UUID_SERVICE.VIBRATION, UUID_CHAR.VIBRATION, PROTOCOL.VIBRATION_WITHOUT_LED);
    }

    /**
     * Vibration 10 times with led.
     * REQUIREMENT: PAIR successful.
     */
    public void vibration10TimesWithLed() {
        writeCharacteristic(UUID_SERVICE.VIBRATION, UUID_CHAR.VIBRATION, PROTOCOL.VIBRATION_10_TIMES_WITH_LED);
    }

    /**
     * Vibraton Test
     * @param data - data test.
     */
    public void vibrationTest(byte[] data) {
        writeCharacteristic(UUID_SERVICE.VIBRATION, UUID_CHAR.VIBRATION, data);
    }
}
