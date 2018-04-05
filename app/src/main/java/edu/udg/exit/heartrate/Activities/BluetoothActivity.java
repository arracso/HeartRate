package edu.udg.exit.heartrate.Activities;

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

import edu.udg.exit.heartrate.Services.BluetoothService;

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
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    /**
     * OnCreate function creating the service.
     * @param savedInstanceState - Context
     */
    protected void onCreateService(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Intent of the service
        Intent intent = new Intent(this,BluetoothService.class);
        // Starts the service
        startService(intent);
        // Bind to the service
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }


    @Override
    protected void onDestroy() {
        // Disconnecting from the service
        unbindService(serviceConnection);

        super.onDestroy();
    }

    /**
     * OnDestroy function destroying the service.
     */
    protected void onDestroyService() {
        // Disconnecting from the service
        unbindService(serviceConnection);
        // Destroying the service
        Intent intent = new Intent(this,BluetoothService.class);
        stopService(intent);

        super.onDestroy();
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

    private ServiceConnection serviceConnection = new ServiceConnection() {
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
