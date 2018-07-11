package edu.udg.exit.heartrate.Services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BluetoothRestarterBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        context.startService(new Intent(context, BluetoothService.class));
    }
}
