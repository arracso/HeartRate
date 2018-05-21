package edu.udg.exit.heartrate.Views;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import edu.udg.exit.heartrate.Global;
import edu.udg.exit.heartrate.Interfaces.IMeasureView;
import edu.udg.exit.heartrate.R;
import edu.udg.exit.heartrate.Services.BluetoothService;
import edu.udg.exit.heartrate.TodoApp;

import java.util.Date;

public class MeasureActivity extends Activity implements IMeasureView {

    private BluetoothService bluetoothService;

    private Boolean recording;

    private Integer id = Global.user.getId();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measure);

        recording = false;

        // Get services
        bluetoothService = ((TodoApp)getApplication()).getBluetoothService();

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
            //fileWriter.createFile();
            bluetoothService.startMeasure();
        }
    }

    private void stopMeasure() {
        if(recording){
            bluetoothService.stopMeasure();
            //fileWriter.closeFile();
            //fileWriter.sendFile();
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
    public void setHeartRate(int heartrate) {
        showText("" + heartrate);
    }

}
