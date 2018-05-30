package edu.udg.exit.heartrate;

import edu.udg.exit.heartrate.Model.User;

/**
 * Global class to save global variables and constants.
 */
public class Global {

    ///////////////
    // Constants //
    ///////////////

    // PASSWORD -> 1 digit, 1 lowercase, 1 uppercase, no whitespaces, no special characters, 8 to 32 characters long
    public static final String REGEX_PASSWORD = "^" + "(?=.*[0-9])" + "(?=.*[a-z])" + "(?=.*[A-Z])" + "(?=\\S+$)" + "(?=[^@#$%^&+=]+$)" + ".{8,32}"  + "$";
    // USERNAME -> alphanumeric, 4 to 16 characters
    public static final String REGEX_USERNAME = "^[0-9a-zA-Z]{4,16}$";
    // EMAIL -> http://emailregex.com/
    public static final String REGEX_EMAIL = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";

    ///////////////
    // Variables //
    ///////////////

    public static User user = new User();

}
