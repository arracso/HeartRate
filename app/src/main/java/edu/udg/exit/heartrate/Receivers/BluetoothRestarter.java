package edu.udg.exit.heartrate.Receivers;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;
import edu.udg.exit.heartrate.Services.BluetoothService;
import edu.udg.exit.heartrate.TodoApp;
import edu.udg.exit.heartrate.Utils.UserPreferences;
import edu.udg.exit.heartrate.Utils.Utils;

/**
 * Broadcast Receiver that restarts the Bluetooth Service and his work.
 */
public class BluetoothRestarter extends WakefulBroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("RESTARTER", "Triggered");
        if(intent.getAction() != null) {
            switch (intent.getAction()) {
                case "android.intent.action.BOOT_COMPLETED":
                case "android.intent.action.QUICKBOOT_POWERON":
                case "android.bluetooth.device.action.ACL_DISCONNECTED":
                case ".RestartBluetooth":
                    if(Utils.isMyServiceRunning(context, BluetoothService.class)){
                        BluetoothService bluetoothService = ((TodoApp) context.getApplicationContext()).getBluetoothService();
                        if(bluetoothService != null && !bluetoothService.isConnected()){
                            String boundAddress = UserPreferences.getInstance().load(context, UserPreferences.BONDED_DEVICE_ADDRESS);
                            if(boundAddress != null) bluetoothService.connectRemoteDevice(bluetoothService.getRemoteDevice(boundAddress));
                        }else if(bluetoothService != null && bluetoothService.isConnected() && !bluetoothService.isWorking()){
                            bluetoothService.restartWork();
                        }
                    }
                    else startWakefulService(context, new Intent(context, BluetoothService.class));
                    break;
            }
        }
    }
}
