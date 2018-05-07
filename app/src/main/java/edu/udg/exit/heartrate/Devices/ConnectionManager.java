package edu.udg.exit.heartrate.Devices;

import android.bluetooth.*;
import android.os.Handler;
import android.util.Log;

import edu.udg.exit.heartrate.Devices.Actions.Action;
import edu.udg.exit.heartrate.Devices.Actions.ActionWithoutResponse;
import edu.udg.exit.heartrate.Utils.Queue;

import java.util.UUID;

/**
 * Abstract class that performs a connection with a devices and handles it.
 * This class must be extended in order to handle communication with an specific device.
 */
public abstract class ConnectionManager extends BluetoothGattCallback {

    ///////////////
    // Constants //
    ///////////////

    protected final static int DELAY_MAX = 10000;
    protected final static int DELAY_MIN = 500;

    ////////////////
    // Attributes //
    ////////////////

    // Connect
    private BluetoothGatt connectGATT;
    private boolean isConnected;

    // Action Queue
    private final Queue<Action> actionQueue;
    private boolean working;

    // Run
    private final Handler handler = new Handler();
    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            ConnectionManager.this.run();
        }
    };

    ////////////////////////
    // Life Cycle Methods //
    ////////////////////////

    /**
     * Default constructor.
     */
    public ConnectionManager() {
        super();

        // Connect
        connectGATT = null;
        isConnected = false;

        // Calls Queue
        actionQueue = new Queue<>();
        working = false;
    }

    @Override
    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
        super.onConnectionStateChange(gatt,status,newState);

        if (newState == BluetoothProfile.STATE_CONNECTED) {
            Log.d("GATT manager", "Device connected");
            isConnected = true;
            connectGATT = gatt;
            connectGATT.discoverServices();
        } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
            Log.d("GATT manager", "Device disconnected");
            connectGATT.close();
            isConnected = false;
            connectGATT = null;
        }
    }

    @Override
    public void onServicesDiscovered(BluetoothGatt gatt, int status) {
        super.onServicesDiscovered(gatt,status);

        if(status == BluetoothGatt.GATT_SUCCESS) {
            //showServices(gatt); // Show discovered services with their characteristics & descriptors (DEBUG)
            onServicesDiscovered(gatt); // Handle services discovered.
            run(); // Runs next action of the queue.
        }else{
            Log.w("GATT manager", "Failed to discover services");
        }
    }

    @Override
    public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        super.onCharacteristicRead(gatt,characteristic,status);

        if(status == BluetoothGatt.GATT_SUCCESS) {
            onCharacteristicRead(characteristic); // Handle characteristic read.
            working = false; // We always finish the current work when reading a characteristic.
            run(); // Run next action of the queue.
        }else{
            Log.w("GATT manager", "Failed to read characteristic");
        }

    }

    @Override
    public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        super.onCharacteristicWrite(gatt,characteristic,status);

        if (status == BluetoothGatt.GATT_SUCCESS) {
            onCharacteristicWrite(characteristic); // Handle characteristic write.
            working = false; // We always finish the current work when writing on a characteristic.
            run(); // Run next action of the queue.
        }else{
            Log.w("GATT manager", "Failed to write characteristic");
        }
    }

    @Override
    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        super.onCharacteristicChanged(gatt,characteristic);
        onCharacteristicChanged(characteristic); // Handle characteristic changed.
    }

    @Override
    public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
        super.onDescriptorRead(gatt,descriptor,status);

        if(status == BluetoothGatt.GATT_SUCCESS){
            onDescriptorRead(descriptor); // Handle descriptor read.
            working = false; // We always finish the current work when reading a descriptor.
            run(); // Run next action of the queue.
        }
    }

    @Override
    public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
        super.onDescriptorWrite(gatt,descriptor,status);

        if(status == BluetoothGatt.GATT_SUCCESS){
            onDescriptorWrite(descriptor); // Handle descriptor write.
            working = false; // We always finish the current work when reading a descriptor.
            run(); // Run next action of the queue.
        }
    }

    //*****************************//
    // Life cycle methods handlers //
    //*****************************//

    /**
     * Handles services discoverd.
     * @param gatt - Connected gatt.
     */
    protected abstract void onServicesDiscovered(BluetoothGatt gatt);

    /**
     * Handles characteristic read.
     * @param characteristic - Characteristic read.
     */
    protected abstract void onCharacteristicRead(BluetoothGattCharacteristic characteristic);

    /**
     * Handles characteristic write.
     * @param characteristic - Characteristic write.
     */
    protected abstract void onCharacteristicWrite(BluetoothGattCharacteristic characteristic);

    /**
     * Handles characteristic changed.
     * @param characteristic - Characteristic changed.
     */
    protected abstract void onCharacteristicChanged(BluetoothGattCharacteristic characteristic);

    /**
     * Handles descriptor read.
     * @param descriptor - Descriptor read.
     */
    protected abstract void onDescriptorRead(BluetoothGattDescriptor descriptor);

    /**
     * Handles descriptor write.
     * @param descriptor - Descriptor write.
     */
    protected abstract void onDescriptorWrite(BluetoothGattDescriptor descriptor);

    ////////////////////
    // Public Methods //
    ////////////////////

    /**
     * Check if a device is connected or not to the GATT.
     * @return True if a device is connected
     */
    public boolean isConnected(){
        return isConnected;
    }

    /**
     * Disconnects the device from the connected GATT.
     */
    public void disconnect(){
        clearCalls();
        if(connectGATT != null) connectGATT.disconnect();
    }

    /**
     * Adds a call to the action queue.
     * @param call to be added to the actionQueue
     */
    public void addCall(Action call) {
        actionQueue.add(call);
    }

    /**
     * Adds a call to the first position of the action queue.
     * @param call - Action to be added to the action queue.
     */
    public void addCallFirst(Action call) {
        actionQueue.addFirst(call);
    }

    /**
     * Clears the calls of the action queue.
     */
    public void clearCalls() {
        actionQueue.clear();
    }

    /**
     * Runs the first action of the queue.
     */
    public void run() {
        Log.d("Manager", "Run");
        handler.removeCallbacks(runnable);
        int delayMilis = DELAY_MAX;

        if(actionQueue.isEmpty()){
            Log.d("Manager", "Empty queue.");
            return;
        }else if(!working){
            Action action = actionQueue.poll();
            action.run();
            if(!action.expectsResult()) delayMilis = DELAY_MIN;
            else working = true;
        }

        handler.postDelayed(runnable, delayMilis);
    }

    ///////////////
    // Protected //
    ///////////////

    /**
     * Wait for an especific time before running the next action.
     * @param delayMilis - Time to be waiting in miliseconds
     * @return Wait Action
     */
    protected Action waitMilis(final int delayMilis) {
        return new ActionWithoutResponse() {
            @Override
            public void run() {
                if(delayMilis > 0) addCallFirst(waitMilis(delayMilis - DELAY_MIN));
            }
        };
    }

    ///////////
    // DEBUG //
    ///////////

    protected String convertBytesToString(byte[] data) {
        StringBuilder str = new StringBuilder("[");
        for(int i = 0; i < data.length; i++){
            if(i != 0) str.append(", ");
            str.append(data[i] & 0x0ff);
        }
        str.append("]");
        return str.toString();
    }

    protected void showServices(BluetoothGatt gatt) {
        for(BluetoothGattService service : gatt.getServices()) {
            Log.d("SERVICE", "" + service.getUuid() + " - " + service.getType());
            for(BluetoothGattCharacteristic characteristic : service.getCharacteristics()) {
                if(canRead(characteristic.getProperties())){
                    Log.d("CHARACTERISTIC", "\t" + characteristic.getUuid() + " - READABLE");
                    addCall(read(service.getUuid(),characteristic.getUuid()));
                }else{
                    Log.d("CHARACTERISTIC", "\t" + characteristic.getUuid() + " - " + characteristic.getPermissions()
                            + " - " +  characteristic.getProperties());
                }
                for(BluetoothGattDescriptor descriptor : characteristic.getDescriptors()) {
                    Log.d("DESCRIPTOR", "\t\t" + descriptor.getUuid() + " - " + descriptor.getPermissions());
                }
            }
        }
    }

    protected abstract Action read(final UUID serviceUUID, final UUID characteristicUUID);

    private boolean canRead(int properties){
        if(properties >= 64) properties = properties - 64;
        if(properties >= 32) properties = properties - 32;
        if(properties >= 16) properties = properties - 16;
        if(properties >= 8) properties = properties - 8;
        if(properties >= 4) properties = properties - 4;
        return properties >= 2;
    }
}
