package edu.udg.exit.heartrate.ApiRest;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.*;

/**
 * Interface for file api rest.
 */
public interface FileApi {

    @Multipart
    @POST("file/upload")
    Call<ResponseBody> uploadFile(@Part MultipartBody.Part file);

}
