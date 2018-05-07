package edu.udg.exit.heartrate.ApiRest;

import edu.udg.exit.heartrate.Model.User;
import retrofit2.Call;
import retrofit2.http.*;


public interface AuthApi {

    @POST("auth/login")
    Call<String> login(@Body User loginRequest);

    @POST("auth/refreshToken")
    Call<String> refresh(@Body String refreshToken);

    @POST("auth/register")
    Call<String> register(@Body User registerRequest);

}
