package edu.udg.exit.heartrate.Model;

/**
 * POJO class to retrieve errorBody from server response.
 */
public class ErrorBody {

    ///////////////
    // Constants //
    ///////////////

    public static final int ERROR = -1;
    public static final int WRONG_CREDENTIALS = 1;
    public static final int ACCOUNT_ALREADY_EXIST = 2;
    public static final int ACCESS_NOT_ALLOWED = 3;
    public static final int EXPIRED_TOKEN = 4;
    public static final int INVALID_TOKEN = 5;
    public static final int NULL_TOKEN = 6;
    public static final int UNKNOWN_USER = 7;

    ////////////////
    // Attributes //
    ////////////////

    private int code;
    private String message;

    ////////////////////
    // Public Methods //
    ////////////////////

    public int getCode() {
        return code;
    }
    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }

}
