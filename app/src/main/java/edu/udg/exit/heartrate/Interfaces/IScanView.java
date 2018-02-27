package edu.udg.exit.heartrate.Interfaces;

import android.bluetooth.BluetoothDevice;

public interface IScanView {

    /////////////
    // Methods //
    /////////////

    /**
     * Add a device to the scan view.
     * @param device
     */
    void addDevice(BluetoothDevice device);

    /**
     * Clear all devices from scan view.
     */
    void clearView();

    /**
     * Starts the loading animation while scanning.
     */
    void startLoadingAnimation();

    /**
     * Stops the loading animation.
     */
    void stopLoadingAnimation();
}
