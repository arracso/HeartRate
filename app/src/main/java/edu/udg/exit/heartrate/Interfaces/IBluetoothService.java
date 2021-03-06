package edu.udg.exit.heartrate.Interfaces;

import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;

/**
 * Interface of bluetooth service.
 */
public interface IBluetoothService {

    /////////////
    // Methods //
    /////////////

    /**
     * Check if the device has Bluetooth
     * @return boolean
     */
    boolean hasBluetooth();

    /**
     * Check if Bluetooth is Enabled
     * @return boolean
     */
    boolean isEnabled();

    /**
     * Gets a remote bluetooth device given its address
     * @param address
     * @return BluetoothDevice
     */
    BluetoothDevice getRemoteDevice(String address);

    /**
     * Connect to the GATT server of a device.
     * @param device
     */
    void connectRemoteDevice(BluetoothDevice device);

    /**
     * Check if service is connected to remote device.
     * @return True if service is connected to a remote device.
     */
    boolean isConnected();

    /**
     * Check if service is working with the remote device.
     * @return True if service is working.
     */
    boolean isWorking();

    /**
     * Restart the work that was being done by the service.
     */
    void restartWork();

}
