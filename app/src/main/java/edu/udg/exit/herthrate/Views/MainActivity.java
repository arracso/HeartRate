package edu.udg.exit.herthrate.Views;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import edu.udg.exit.herthrate.Global;
import edu.udg.exit.herthrate.R;
import edu.udg.exit.herthrate.Services.BluetoothService;

public class MainActivity extends AppCompatActivity {

    ///////////////
    // Constants //
    ///////////////

    private static final int REQUEST_ENABLE_BT_TO_START_SERVICE = Global.REQUEST_ENABLE_BT_TO_START_SERVICE;

    ///////////////////////
    // LifeCicle methods //
    ///////////////////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    ////////////////////
    // Public methods //
    ////////////////////

    /**
     * Starts the ScanActivity.
     * @param view
     */
    public void goToScan(View view) {
        Intent scan = new Intent(this,ScanActivity.class);
        startActivity(scan);
    }

    /////////////////////
    // Activity Result //
    /////////////////////

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_ENABLE_BT_TO_START_SERVICE:
                if(resultCode == -1){ // Bluetooth enabled
                    startService(new Intent(getBaseContext(), BluetoothService.class));
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_ENABLE_BT_TO_START_SERVICE:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){ // Locaion permissions given
                    startService(new Intent(getBaseContext(), BluetoothService.class));
                }
                break;
        }
    }
}