package edu.udg.exit.heartrate.Interfaces;

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

    void setMessage(String message);

    void setPairStatus(Integer status);

}
