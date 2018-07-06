package edu.udg.exit.heartrate.Interfaces;

public interface IPairService {

    /////////////
    // Methods //
    /////////////

    IPairView getPairView();

    /**
     * Sets the view that will be used to show the user pairing status.
     * @param view Pair view to be set.
     */
    void setPairView(IPairView view);

    /**
     * Unsets pair view.
     */
    void unSetPairView();

    /**
     * Tells the bluetooth service that the device its paired so it can make other initializations.
     */
    void setDevicePaired();

    /**
     * Bind the device using its address and add to user preferences.
     * @param address - MAC address of the device.
     */
    void bindDevice(String address);

    /**
     * Unbind the binded device and remove from user preferences.
     */
    void unbindDevice();

}
