package edu.udg.exit.heartrate.Interfaces;

import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;

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

}
