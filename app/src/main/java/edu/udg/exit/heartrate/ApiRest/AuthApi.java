package edu.udg.exit.heartrate.ApiRest;

import edu.udg.exit.heartrate.Model.Auth;
import edu.udg.exit.heartrate.Model.Tokens;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.*;

/**
 * Interface for authentication api rest.
 */
public interface AuthApi {

    @POST("auth/login")
    Call<Tokens> login(@Body Auth loginRequest);

    @POST("auth/refreshToken")
    Call<Tokens> refresh(@Body RequestBody refreshToken);

    @POST("auth/register")
    Call<Tokens> register(@Body Auth registerRequest);

    @GET("auth/guest")
    Call<Tokens> loginAsGuest();

}
