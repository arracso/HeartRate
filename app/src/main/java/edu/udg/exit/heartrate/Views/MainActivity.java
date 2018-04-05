package edu.udg.exit.heartrate.Views;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import edu.udg.exit.heartrate.Activities.BluetoothActivity;
import edu.udg.exit.heartrate.R;
import edu.udg.exit.heartrate.Services.BluetoothService;
import edu.udg.exit.heartrate.Utils.UserPreferences;

public class MainActivity extends BluetoothActivity {

    ///////////////
    // Constants //
    ///////////////

    private static final int REQUEST_ENABLE_BT_TO_START_SERVICE = BluetoothService.REQUEST_ENABLE_BT_TO_START_SERVICE;

    ///////////////////////
    // LifeCicle methods //
    ///////////////////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreateService(savedInstanceState);
        setContentView(R.layout.activity_main);

        UserPreferences.getInstance().remove(this.getApplicationContext(),"test");
    }

    @Override
    protected void onServiceConnected(ComponentName name, BluetoothService.BluetoothBinder binder) {
        super.onServiceConnected(name, binder);

        // TODO
    }

    @Override
    protected void onServiceDisconnected(ComponentName name) {
        // TODO

        super.onServiceDisconnected(name);
    }

    @Override
    protected void onDestroy() {
        if(!bluetoothService.isConnected()){
            super.onDestroyService();
        }else{
            super.onDestroy();
        }
    }

    ////////////////////
    // Public methods //
    ////////////////////

    /**
     * Starts the ScanActivity.
     * @param view - MainActivity view
     */
    public void goToScan(View view) {
        Intent scan = new Intent(this,ScanActivity.class);
        startActivity(scan);
    }

    /**
     * Unbind the current binded device.
     * @param view - MainActivity view
     */
    public void unbindDevice(View view) {
        bluetoothService.unbindDevice();
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_ENABLE_BT_TO_START_SERVICE:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){ // Locaion permissions given
                    startService(new Intent(getBaseContext(), BluetoothService.class));
                }
                break;
        }
    }
}
