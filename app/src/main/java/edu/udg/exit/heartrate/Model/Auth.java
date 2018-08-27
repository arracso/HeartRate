package edu.udg.exit.heartrate.Model;

/**
 * Authentication Model Object to make rest requests.
 */
public class Auth {

    ////////////////
    // Attributes //
    ////////////////

    private String email;
    private String password;

    ///////////////////////
    // Lifecycle Methods //
    ///////////////////////

    /**
     * Constructor by params.
     */
    public Auth(String email, String password) {
        this.email = email;
        this.password = password;
    }

    ////////////////////
    // Public Methods //
    ////////////////////

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
