package edu.udg.exit.heartrate.Interfaces;

public interface IPairView {

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
