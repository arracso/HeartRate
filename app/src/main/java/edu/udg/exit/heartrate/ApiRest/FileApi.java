package edu.udg.exit.heartrate.ApiRest;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.*;

public interface FileApi {

    @POST("file/upload")
    Call<String> uploadFile(
            @Header("Authorization") String authorization,
            @Query("file") MultipartBody.Part file
    );

}
