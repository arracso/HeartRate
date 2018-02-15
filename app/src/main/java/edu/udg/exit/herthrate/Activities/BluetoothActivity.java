package edu.udg.exit.herthrate.Activities;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;

import edu.udg.exit.herthrate.Services.BluetoothService;

/**
 * Activity class that handles the connection with bluetooth service.
 */
public abstract class BluetoothActivity extends AppCompatActivity {

    ///////////////////////
    // LifeCicle methods //
    ///////////////////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Connecting to the service
        Intent intent = new Intent(this, BluetoothService.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    ///////////////////////
    // Protected methods //
    ///////////////////////

    /**
     * Check if the Bluetooth is available and ask for permission if needed. (onActivityResult)
     * @param requestCode - Code for the command that needs the Bluetooth
     * @return boolean
     */
    protected boolean checkBluetooth(int requestCode) {
        if(!bluetoothService.hasBluetooth()) {
            return false;
        }else if(!bluetoothService.isEnabled()){
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, requestCode);
            return false;
        }else{
            return true;
        }
    }

    /**
     * Check if the Location is available and ask for permission if needed. (onRequestPermissionsResult)
     * @param requestCode - Code for the command that needs the Location
     * @return boolean
     */
    protected boolean checkLocation(int requestCode) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M
                || checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }else{
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, requestCode);
            return false;
        }
    }

    ////////////////////////
    // Service Connection //
    ////////////////////////

    protected BluetoothService bluetoothService = null;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            BluetoothActivity.this.onServiceConnected(name, (BluetoothService.BluetoothBinder) binder);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            BluetoothActivity.this.onServiceDisconnected(name);
        }
    };

    /**
     * Function called when service is connected.
     * @param name
     * @param binder
     */
    protected void onServiceConnected(ComponentName name, BluetoothService.BluetoothBinder binder){
        bluetoothService = binder.getService();
    }

    /**
     * Function called when service is disconnected
     * @param name
     */
    protected void onServiceDisconnected(ComponentName name){
        bluetoothService = null;
    }
}
