package edu.udg.exit.heartrate.Views;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import edu.udg.exit.heartrate.Global;
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

    private BluetoothService bluetoothService;
    private ApiService apiService;

    private FileWriter fileWriter;

    private Boolean recording;

    private Integer id = Global.user.getId();

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

        if(id!=null) setTextId();

    }

    /////////////////////
    // Private Methods //
    /////////////////////

    private void setTextId() {
        TextView textId = (TextView) findViewById(R.id.measure_text_id);
        String text = "Your id: " + id;
        textId.setText(text);
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

    private Handler handler = new Handler();

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
        fileWriter.writeToFile("\r\n" + date.getTime() + ", " + heartrate);
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


        private void createFile(){
            // Create file
            try {
                name = "HR_" + Global.user.getId() + "_" + new Date().getTime() + ".csv";
                file = openFileOutput(name, MODE_PRIVATE);
                bw = new BufferedWriter(new OutputStreamWriter(file));
                writeToFile("Time, HeartRate");
                Log.d("File", "file created");
            } catch(FileNotFoundException e) {
                file = null;
                Log.d("File", e.getMessage());
            }
        }

        private void closeFile(){
            // Close file and send
            if(file != null){
                try {
                    bw.close();
                    bw = null;
                    file.close();
                    file = null;
                } catch (IOException e) {
                    Log.d("File", e.getMessage());
                }
            }
        }

        private void writeToFile(String text){
            if(bw != null){
                try {
                    for(int i=0; i<text.length(); i++){
                        bw.append(text.charAt(i));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private String getMimeType(String url) {
            String type = null;
            String extension = MimeTypeMap.getFileExtensionFromUrl(url);
            if (extension != null) {
                type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
            }
            return type;
        }

        private void uploadFile(File file){
            Log.d("File", "start upload");
            if (file != null){
                String type = getMimeType(file.getPath());
                Log.d("File", "type: " + type);
                MediaType mediaType = MediaType.parse(type);
                RequestBody reqFile = RequestBody.create(mediaType, file);
                MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), reqFile);

                Call<ResponseBody> call = apiService.getFileService().uploadFile(body);
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if(response.isSuccessful())
                            Toast.makeText(MeasureActivity.this, "File uploaded!", Toast.LENGTH_LONG).show();
                        else
                            Toast.makeText(MeasureActivity.this, "Failed to upload the file!", Toast.LENGTH_LONG).show();
                    }
                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        System.out.println(call.toString());
                        System.out.println(t.getMessage());
                        System.out.println(t.getLocalizedMessage());
                        t.printStackTrace();
                        Toast.makeText(MeasureActivity.this, "BAD CONNECTION", Toast.LENGTH_LONG).show();
                    }
                });
            } else {
                Toast.makeText(MeasureActivity.this, "FAILED TO UPLOAD THE FILE", Toast.LENGTH_LONG).show();
            }
        }


        private void sendFile() {
            String path = getApplicationContext().getFilesDir() + "/" + name;
            File file = new File(path);
            if(file.exists()) uploadFile(file);
        }
    }

}
