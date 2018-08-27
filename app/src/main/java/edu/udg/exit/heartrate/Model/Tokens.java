package edu.udg.exit.heartrate.Model;

/**
 * Tokens Model Object to retrieve response from rest requests.
 */
public class Tokens {

    ////////////////
    // Attributes //
    ////////////////

    private String accessToken;
    private String refreshToken;

    ////////////////////
    // Public Methods //
    ////////////////////

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
