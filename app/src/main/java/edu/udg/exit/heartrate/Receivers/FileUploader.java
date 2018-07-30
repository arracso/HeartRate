package edu.udg.exit.heartrate.Receivers;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;
import edu.udg.exit.heartrate.Services.ApiService;
import edu.udg.exit.heartrate.TodoApp;
import edu.udg.exit.heartrate.Utils.*;
import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.File;
import java.util.Date;


/**
 * BroadcastReceiver that uses ApiService to upload the measurements.
 */
public class FileUploader extends WakefulBroadcastReceiver {

    ///////////////
    // Variables //
    ///////////////

    private Context ctx = null;
    private ApiService apiService = null;
    private Handler handler = new Handler();

    ///////////////////////
    // Triggered Methods //
    ///////////////////////

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("FileUploader", "Triggered");

        ctx = context;

        if(intent.getAction() != null) {
            switch (intent.getAction()) {
                case "android.intent.action.BOOT_COMPLETED":
                case "android.intent.action.QUICKBOOT_POWERON":
                case ".UploadMeasurements":
                    if(!Utils.isMyServiceRunning(context, ApiService.class))
                        startWakefulService(context, new Intent(context, ApiService.class));
                    uploadMeasurements.run();
                    break;
            }
        }
    }

    /////////////////////
    // Private Methods //
    /////////////////////

    /**
     * Runnable to upload measurements
     */
    private Runnable uploadMeasurements = new Runnable() {
        @Override
        public void run() {
            if(apiService == null) apiService = ((TodoApp) ctx.getApplicationContext()).getApiService();
            if(apiService != null && apiService.isWifiOnAndConnected()) uploadHeartRate(ctx);
            else handler.postDelayed(uploadMeasurements,60 * 1000); // 60s
        }
    };

    /**
     * Uploads heart rate measurements to the server.
     */
    private void uploadHeartRate(Context ctx) {
        final DataBase db = new DataBase(ctx);
        final Date date = new Date();
        File file = db.exportAsCSV(DataBase.RATE_TABLE_NAME, null, date.getTime(), "HR");
        if(file != null){
            uploadFile(file, new CallBack() {
                @Override
                public void onSuccess(int code) {
                    db.delete(DataBase.RATE_TABLE_NAME, null, date.getTime());
                    checkAlarm();
                }
                @Override
                public void onFailure(int code) {
                    handler.postDelayed(uploadMeasurements, 30 * 60 * 1000); // 30m
                }
            });
        }
    }

    /**
     * Upload a file to the server.
     * @param file - file to be upload
     * @param callback - callback to be run on success or on failure
     */
    private void uploadFile(File file, final CallBack callback) {
        if (file != null){
            MultipartBody.Part body = Storage.getMultipartBody("file", file);

            apiService.getFileService().uploadFile(body).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if(response.isSuccessful()) callback.onSuccess(CallBack.SUCCESS);
                    else callback.onFailure(CallBack.ERROR);
                }
                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    callback.onFailure(CallBack.BAD_CONNECTION);
                }
            });
        }
        else callback.onFailure(CallBack.ERROR);
    }

    /**
     * Checks if the alarm that triggers this receiver is still necessary.
     * If not, it disables this receiver and unset the alarm.
     */
    private void checkAlarm(){
        String measure = UserPreferences.getInstance().load(ctx,UserPreferences.HEART_RATE_MEASURE);
        if(measure == null || measure.equals("false")){
            Utils.unsetAlarm(ctx,1,".UploadMeasurements");
            Utils.disableReceiver(ctx,FileUploader.class);
        }
    }

}
