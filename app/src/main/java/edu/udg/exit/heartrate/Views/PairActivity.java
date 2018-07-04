package edu.udg.exit.heartrate.Views;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import edu.udg.exit.heartrate.Activities.BluetoothActivity;
import edu.udg.exit.heartrate.Interfaces.IPairView;
import edu.udg.exit.heartrate.R;
import edu.udg.exit.heartrate.Services.BluetoothService;

public class PairActivity extends BluetoothActivity implements IPairView {

    ///////////////
    // Constants //
    ///////////////

    private static final int REQUEST_ENABLE_BT_TO_PAIR = BluetoothService.REQUEST_ENABLE_BT_TO_PAIR;
    static final String DEVICE_ADDRESS = "device_address";

    ////////////////
    // Attributes //
    ////////////////

    private ProgressBar progressBar;
    private TextView textView;

    ///////////////////////
    // LifeCicle methods //
    ///////////////////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pair);

        // Set Progress animation
        setProgressAnimation();

        // Set Text view
        setTextView();
    }

    @Override
    protected void onServiceConnected(ComponentName name, BluetoothService.BluetoothBinder binder) {
        super.onServiceConnected(name, binder);

        // Set this activity as the pair view on bluetoothService
        bluetoothService.setPairView(PairActivity.this);
        // Start pairing to the device
        bluetoothService.bindDevice(getIntent().getStringExtra(DEVICE_ADDRESS));
    }

    @Override
    protected void onServiceDisconnected(ComponentName name) {
        // Unset scanView from bluetoothService
        if(bluetoothService != null) bluetoothService.unSetPairView();

        super.onServiceDisconnected(name);
    }

    @Override
    protected void onDestroy() {
        // Unset scanView from bluetoothService
        if(bluetoothService != null) bluetoothService.unSetPairView();

        super.onDestroy();
    }

    /////////////////////
    // Private methods //
    /////////////////////

    /**
     * Set the animation for progress bar.
     */
    private void setProgressAnimation() {
        progressBar = (ProgressBar) findViewById(R.id.pair_progress_bar);
        progressBar.setVisibility(View.INVISIBLE);
    }

    /**
     * Set text view
     */
    private void setTextView() {
        textView = (TextView) findViewById(R.id.pair_text);
        textView.setVisibility(View.INVISIBLE);
    }

    private void handleSuccessToPair() {
        Intent device = new Intent(PairActivity.this, DeviceActivity.class);
        startActivity(device);
        PairActivity.this.finish();
    }

    private void handleFailedToPair(){
        PairActivity.this.finish();
    }

    ////////////////////
    // Public methods //
    ////////////////////

    @Override
    public void startLoadingAnimation() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void stopLoadingAnimation() {
        progressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void setMessage(String message) {
        if(message == null){
            textView.setText("");
            textView.setVisibility(View.INVISIBLE);
        }
        textView.setText(message);
        textView.setVisibility(View.VISIBLE);
    }

    @Override
    public void setPairStatus(Integer status) {
        switch (status) {
            case STATUS_WORKING:
                startLoadingAnimation();
                setMessage("pairing...");
                break;
            case STATUS_SUCCESS:
                this.handleSuccessToPair();
                break;
            case STATUS_FAILED:
                this.handleFailedToPair();
                break;
        }
    }

}
