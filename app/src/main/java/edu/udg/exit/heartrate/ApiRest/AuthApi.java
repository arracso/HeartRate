package edu.udg.exit.heartrate.ApiRest;

import edu.udg.exit.heartrate.Model.Login;
import edu.udg.exit.heartrate.Model.Register;
import edu.udg.exit.heartrate.Model.Tokens;
import retrofit2.Call;
import retrofit2.http.*;

public interface AuthApi {

    @POST("auth/login")
    Call<Tokens> login(@Body Login loginRequest);

    @POST("auth/refreshToken")
    Call<Tokens> refresh(@Body String refreshToken);

    @POST("auth/register")
    Call<Tokens> register(@Body Register registerRequest);

    @GET("auth/guest")
    Call<Tokens> loginAsGuest();

}
