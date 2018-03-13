package edu.udg.exit.heartrate.MiBand.Services;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.util.Log;

import java.util.UUID;

/**
 * Class representing a Mi Band Service.
 */
public abstract class MiBandService {

    ////////////////
    // Attributes //
    ////////////////

    private BluetoothGatt connectGATT;

    ////////////////////////
    // Life Cycle Methods //
    ////////////////////////

    /**
     * Default Constructor
     */
    public MiBandService() {
        connectGATT = null;
    }

    /**
     * Constructor.
     * @param gatt - Connected GATT
     */
    public MiBandService(BluetoothGatt gatt) {
        connectGATT = gatt;
    }

    ////////////////////
    // Public Methods //
    ////////////////////

    /**
     * Sets connected GATT.
     * @param gatt - Connected GATT
     */
    public void setConnectGATT(BluetoothGatt gatt) {
        connectGATT = gatt;
    }

    /**
     * Gets the connected GATT.
     * @return Connected GATT
     */
    public BluetoothGatt getConnectGATT() {
        return connectGATT;
    }

    ////////////////////////////////
    // MiBand service methods     //
    ////////////////////////////////
    // Need to discover services  //
    // before using these methods //
    ////////////////////////////////

    /**
     * Enables or disables a notification
     * @param serviceUUID
     * @param characteristicUUID
     * @param descriptorUUID
     * @param enable
     */
    protected boolean setCharacteristicNotification(UUID serviceUUID, UUID characteristicUUID, UUID descriptorUUID, boolean enable){
        // Retrieve the service
        BluetoothGattService service = connectGATT.getService(serviceUUID);
        if(service == null) {
            Log.w("MiBandService", "Service not found: " + serviceUUID);
            return false;
        }

        // Retrieve  the characteristic
        BluetoothGattCharacteristic characteristic = service.getCharacteristic(characteristicUUID);
        if(characteristic == null) {
            Log.w("MiBandService", "Characteristic not found: " + characteristicUUID);
            return false;
        }

        // Enable or disable the notification
        if(connectGATT.setCharacteristicNotification(characteristic,enable)){
            // Retrieve the descriptor
            BluetoothGattDescriptor descriptor = characteristic.getDescriptor(descriptorUUID);
            if (descriptor == null) {
                Log.w("MiBandService","Descriptor not found:" + descriptorUUID);
                return false;
            }

            // Sets descriptor value
            int properties = characteristic.getProperties();
            if ((properties & BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
                descriptor.setValue(enable ? BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE : BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
            } else if ((properties & BluetoothGattCharacteristic.PROPERTY_INDICATE) > 0) {
                descriptor.setValue(enable ? BluetoothGattDescriptor.ENABLE_INDICATION_VALUE : BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
            } else return false;

            // Write the descriptor to the device
            connectGATT.writeDescriptor(descriptor);

            return true;
        }else{
            Log.e("MiBandService", "Unable to enable notifications.");
            return false;
        }
    }

    /**
     * Reads a value from a characteristic of the service.
     * @param serviceUUID
     * @param characteristicUUID
     */
    protected void readCharacteristic(UUID serviceUUID, UUID characteristicUUID) {
        // Retrieve the service
        BluetoothGattService service = connectGATT.getService(serviceUUID);
        if(service == null) {
            Log.d("MiBandService", "Service not found: " + serviceUUID);
            return;
        }
        // Retrive the characteristic
        BluetoothGattCharacteristic characteristic = service.getCharacteristic(characteristicUUID);
        if(characteristic == null) {
            Log.d("MiBandService", "Characteristic not found: " + characteristicUUID);
            return;
        }
        // Read the characteristic from the device
        connectGATT.readCharacteristic(characteristic);
    }

    /**
     * Writes a value to a characteristic of the service.
     * @param serviceUUID
     * @param characteristicUUID
     * @param value
     */
    protected void writeCharacteristic(UUID serviceUUID, UUID characteristicUUID, byte[] value) {
        // Retrieve the service
        BluetoothGattService service = connectGATT.getService(serviceUUID);
        if(service == null) {
            Log.d("MiBandService", "Service not found: " + serviceUUID);
            return;
        }
        // Retrive the characteristic
        BluetoothGattCharacteristic characteristic = service.getCharacteristic(characteristicUUID);
        if(characteristic == null) {
            Log.d("MiBandService", "Characteristic not found: " + characteristicUUID);
            return;
        }
        // Set the value of the characteristic
        characteristic.setValue(value);
        // Write the characteristic to the device
        connectGATT.writeCharacteristic(characteristic);
    }

}
