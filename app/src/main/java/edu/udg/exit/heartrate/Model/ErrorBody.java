package edu.udg.exit.heartrate.Model;

/**
 * POJO class to retrieve errorBody from server response.
 */
public class ErrorBody {

    ///////////////
    // Constants //
    ///////////////

    public static final int WRONG_USERNAME = 0;
    public static final int WRONG_PASSWORD = 1;
    public static final int EMAIL_ALREADY_USED = 2;
    public static final int USERNAME_ALREADY_USED = 3;
    public static final int EXPIRED_TOKEN = 4;
    public static final int INVALID_TOKEN = 5;
    public static final int ACCESS_NOT_ALLOWED = 6;

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
