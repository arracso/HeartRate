package edu.udg.exit.heartrate.Interfaces;

/**
 * Interface of pair view.
 */
public interface IPairView {

    ///////////////
    // Constants //
    ///////////////

    public static final int STATUS_WORKING = 0;
    public static final int STATUS_SUCCESS = 1;
    public static final int STATUS_FAILED = 2;

    /////////////
    // Methods //
    /////////////

    /**
     * Starts the loading animation while scanning.
     */
    void startLoadingAnimation();

    /**
     * Stops the loading animation.
     */
    void stopLoadingAnimation();

    /**
     * Sets a message on the view.
     * @param message to be set
     */
    void setMessage(String message);

    /**
     * Sends the pairing status to the view.
     * @param status - pairing status
     */
    void setPairStatus(Integer status);

}
