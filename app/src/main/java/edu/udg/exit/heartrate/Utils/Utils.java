package edu.udg.exit.heartrate.Utils;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.SystemClock;

/**
 * Class containng some utility functions.
 */
public class Utils {

    /**
     * Checks if a service is already running.
     * @param serviceClass - class of the service that we want to check
     * @return True if service is running
     */
    public static boolean isMyServiceRunning(Context context, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if(manager != null){
            for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
                if (serviceClass.getName().equals(service.service.getClassName())) return true;
            }
        }
        return false;
    }

    /**
     * Enables a receiver.
     * @param context - context
     * @param receiverClass - Class of the receiver to be enabled
     */
    public static void enableReceiver(Context context, Class receiverClass) {
        ComponentName receiver = new ComponentName(context, receiverClass);
        PackageManager packageManager = context.getPackageManager();
        packageManager.setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
    }

    /**
     * Disables a receiver.
     * @param context - context
     * @param receiverClass - Class of the receiver to be disabled
     */
    public static void disableReceiver(Context context, Class receiverClass) {
        ComponentName receiver = new ComponentName(context, receiverClass);
        PackageManager packageManager = context.getPackageManager();
        packageManager.setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
    }

    /**
     * Sets a repeating alarm.
     * @param context - context
     * @param action - Action that performs the alarm when triggered
     * @param milis - Alarm period
     */
    public static void setInexactRepeatingAlarm(Context context, int requestCode, String action, int milis){
        // Set action intent
        Intent actionIntent = new Intent(action);
        // Set alarm intent
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, requestCode, actionIntent, 0);
        // Set alarm manager
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        // Set alarm
        if(alarmManager != null){
            alarmManager.cancel(alarmIntent);
            alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,SystemClock.elapsedRealtime() + milis, milis, alarmIntent);
        }
    }

    /**
     * Unsets a repeating alarm.
     * @param context - context
     * @param action - Action that performs the alarm when triggered
     */
    public static void unsetAlarm(Context context, int requestCode, String action){
        // Set action intent
        Intent actionIntent = new Intent(action);
        // Set alarm intent
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, requestCode, actionIntent, 0);
        // Set alarm manager
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        // Unset alarm
        if(alarmManager != null) alarmManager.cancel(alarmIntent);
    }

}
