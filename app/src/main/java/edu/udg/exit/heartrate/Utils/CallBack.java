package edu.udg.exit.heartrate.Utils;

/**
 * Interface to implement callbacks to some rest calls.
 */
public interface CallBack {

    ///////////////
    // Constants //
    ///////////////

    int BAD_CONNECTION = -2;
    int ERROR  = -1;
    int SUCCESS = 0;

    /////////////
    // Methods //
    /////////////

    /**
     * Called when the rest call is successful
     * @param code - successful code
     */
    void onSuccess(int code);

    /**
     * Called when the rest call is successful
     * @param code - error code
     */
    void onFailure(int code);

}
