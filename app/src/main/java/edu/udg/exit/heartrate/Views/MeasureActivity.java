package edu.udg.exit.heartrate.Views;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import edu.udg.exit.heartrate.Interfaces.IMeasureView;
import edu.udg.exit.heartrate.Model.User;
import edu.udg.exit.heartrate.R;
import edu.udg.exit.heartrate.Services.ApiService;
import edu.udg.exit.heartrate.Services.BluetoothService;
import edu.udg.exit.heartrate.TodoApp;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.*;
import java.util.Date;

public class MeasureActivity extends Activity implements IMeasureView {

    BluetoothService bluetoothService;
    ApiService apiService;

    FileWriter fileWriter;

    Boolean recording;

    Integer id = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measure);

        fileWriter = new FileWriter();

        recording = false;

        // Get services
        bluetoothService = ((TodoApp)getApplication()).getBluetoothService();
        apiService = ((TodoApp)getApplication()).getApiService();

        // Set view
        bluetoothService.setMeasureView(this);

        // Set button actions
        setButtonActions();

    }

    /////////////////////
    // Private Methods //
    /////////////////////

    private void setId(){
        if(id == null){
            apiService.getUserService().getUser().enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    id = response.body().getId();
                    setTextId(id);
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    Log.d("Api", "Error retrieving user!");
                }
            });
        }
    }

    private void setTextId(Integer id) {
        TextView textId = (TextView) findViewById(R.id.measure_text_id);
        textId.setText("Your id: " + id);
    }

    private void setButtonActions() {
        Button startBtn = (Button) findViewById(R.id.measure_btn_start);
        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startMeasure();
            }
        });

        Button stopBtn = (Button) findViewById(R.id.measure_btn_stop);
        stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopMeasure();
            }
        });
    }

    private void startMeasure() {
        if(!recording){
            recording = true;
            fileWriter.createFile();
            bluetoothService.startMeasure();
        }
    }

    private void stopMeasure() {
        if(recording){
            bluetoothService.stopMeasure();
            fileWriter.closeFile();
            fileWriter.sendFile();
            recording = false;
        }
    }



    ////////////////////
    // Public Methods //
    ////////////////////

    Handler handler = new Handler();

    @Override
    public void showText(final String text) {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MeasureActivity.this, text, Toast.LENGTH_SHORT).show();
            }
        }, 10);
    }

    @Override
    public void sendHeartrate(Date date, Integer heartrate) {
        showText("" + heartrate);
        // Save to file
        fileWriter.writeToFile("" + date.getTime() + " " + heartrate + "/n");
    }


    private class FileWriter {

        String name;

        FileOutputStream file;
        BufferedWriter bw;

        FileWriter(){
            name = "";
            file = null;
            bw = null;
        }


        public void createFile(){
            // Create file
            try {
                name = "HR_" + new Date().getTime() + ".csv";
                file = openFileOutput(name, MODE_PRIVATE);
                bw = new BufferedWriter(new OutputStreamWriter(file));
                writeToFile("Time HeartRate/n");
                Log.d("File", "file created");
            } catch(FileNotFoundException e) {
                file = null;
                Log.d("File", e.getMessage());
            }
        }

        public void closeFile(){
            // Close file and send
            if(file != null){
                try {
                    bw.close();
                    file.close();
                } catch (IOException e) {
                    Log.d("File", e.getMessage());
                }
            }
        }

        public void writeToFile(String text){
            try {
                for(int i=0; i<text.length(); i++){
                    bw.append(text.charAt(i));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void sendFile() {


            File file = new File(name);

            Uri fileUri = Uri.fromFile(file);

            // create RequestBody instance from file
            RequestBody requestFile = RequestBody.create(
                    MediaType.parse(getContentResolver().getType(fileUri)), file
            );

            RequestBody name = RequestBody.create(MediaType.parse("text/plain"), this.name);

            // MultipartBody.Part is used to send also the actual file name
            MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);

            // finally, execute the request
            Call<ResponseBody> call = apiService.getFileService().uploadFile(name, body);

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call,
                                       Response<ResponseBody> response) {
                    Log.v("Upload", "success");
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.e("Upload error:", t.getMessage());
                }
            });
        }
    }

}
