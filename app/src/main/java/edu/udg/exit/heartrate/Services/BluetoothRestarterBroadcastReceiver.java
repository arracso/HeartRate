package edu.udg.exit.heartrate.Services;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;
import edu.udg.exit.heartrate.TodoApp;
import edu.udg.exit.heartrate.Utils.UserPreferences;

public class BluetoothRestarterBroadcastReceiver extends WakefulBroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("RESTARTER", "Triggered");
        if(intent.getAction() != null) {
            switch (intent.getAction()) {
                case "android.intent.action.BOOT_COMPLETED":
                case "android.intent.action.QUICKBOOT_POWERON":
                case "android.bluetooth.device.action.ACL_DISCONNECTED":
                case ".RestartBluetooth":
                    if(!isMyServiceRunning(BluetoothService.class,context)){
                        startWakefulService(context, new Intent(context, BluetoothService.class));
                    }else{
                        BluetoothService bluetoothService = ((TodoApp) context.getApplicationContext()).getBluetoothService();
                        if(bluetoothService != null && !bluetoothService.isConnected()){
                            String boundAddress = UserPreferences.getInstance().load(context, UserPreferences.BONDED_DEVICE_ADDRESS);
                            if(boundAddress != null) bluetoothService.connectRemoteDevice(bluetoothService.getRemoteDevice(boundAddress));
                        }else if(bluetoothService != null && bluetoothService.isConnected() && !bluetoothService.isWorking()){
                            bluetoothService.restartWork();
                        }
                    }
                    break;
            }
        }
    }

    /**
     * Checks if service is already running.
     * @param serviceClass - class of the service that we want to check
     * @return True if service is running
     */
    @SuppressWarnings("SameParameterValue")
    private boolean isMyServiceRunning(Class<?> serviceClass, Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if(manager != null){
            for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
                if (serviceClass.getName().equals(service.service.getClassName())) return true;
            }
        }
        return false;
    }
}
