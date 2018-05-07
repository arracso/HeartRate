package edu.udg.exit.heartrate.ApiRest;

import edu.udg.exit.heartrate.Model.User;
import retrofit2.Call;
import retrofit2.http.*;

public interface UserApi {

    @GET("user/get")
    Call<User> getUser(@Header("Authorization") String authorization);

    @PUT("user/update")
    Call<String> updateUser(
            @Header("Authorization") String authorization,
            @Query("sex") Integer sex,
            @Query("birthday") String birthday,
            @Query("weight") Integer weight,
            @Query("height") Integer height
    );

    @PUT("user/changeEmail")
    Call<String> changeEmail(
            @Header("Authorization") String authorization,
            @Query("password") String password,
            @Query("email") String email
    );

    @PUT("user/changePassword")
    Call<String> changePassword(
            @Header("Authorization") String authorization,
            @Query("password") String password,
            @Query("newPassword") String newPassword
    );

    @PUT("user/changeUsername")
    Call<String> changeUsername(
            @Header("Authorization") String authorization,
            @Query("password") String password,
            @Query("username") String username
    );

}
