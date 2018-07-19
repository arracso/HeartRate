package edu.udg.exit.heartrate.Interfaces;

import android.bluetooth.BluetoothDevice;

/**
 * Interface of scan view.
 */
public interface IScanView {

    /////////////
    // Methods //
    /////////////

    /**
     * Add a device to the scan view.
     * @param device - Device to be added
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
