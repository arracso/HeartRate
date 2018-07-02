package edu.udg.exit.heartrate.ApiRest;

import edu.udg.exit.heartrate.Model.User;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.*;

public interface UserApi {

    @GET("user/get")
    Call<User> getUser();

    @PUT("user/update")
    Call<ResponseBody> updateUser(@Body User user);

}
