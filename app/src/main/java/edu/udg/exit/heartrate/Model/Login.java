package edu.udg.exit.heartrate.Model;

public class Login {

    ////////////////
    // Attributes //
    ////////////////

    private String username;
    private String password;

    ///////////////////////
    // Lifecycle Methods //
    ///////////////////////

    /**
     * Constructor by params.
     */
    public Login(String username, String password) {
        this.username = username;
        this.password = password;
    }

    ////////////////////
    // Public Methods //
    ////////////////////

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

}
