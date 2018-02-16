package edu.udg.exit.herthrate;

import java.util.UUID;

public class Protocol {

    //////////////
    // Services //
    //////////////

    public static final UUID UUID_SERVICE_MILI = UUID.fromString("0000fee0-0000-1000-8000-00805f9b34fb");
    public static final UUID UUID_SERVICE_VIBRATION = UUID.fromString("00001802-0000-1000-8000-00805f9b34fb");
    public static final UUID UUID_SERVICE_HEARTRATE = UUID.fromString("0000180d-0000-1000-8000-00805f9b34fb");

    public static final UUID UUID_SERVICE_UNKNOWN1 = UUID.fromString("00001800-0000-1000-8000-00805f9b34fb");
    public static final UUID UUID_SERVICE_UNKNOWN2 = UUID.fromString("00001801-0000-1000-8000-00805f9b34fb");
    public static final UUID UUID_SERVICE_UNKNOWN4 = UUID.fromString("0000fee1-0000-1000-8000-00805f9b34fb");
    public static final UUID UUID_SERVICE_UNKNOWN5 = UUID.fromString("0000fee7-0000-1000-8000-00805f9b34fb");

    //////////////////
    // Notification //
    //////////////////

    public static final UUID UUID_DESCRIPTOR_UPDATE_NOTIFICATION = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
    public static final UUID UUID_NOTIFICATION_HEARTRATE = UUID.fromString("00002a37-0000-1000-8000-00805f9b34fb");

    /////////////////////
    // Characteristics //
    /////////////////////

    public static final UUID UUID_CHAR_DEVICE_INFO = UUID.fromString("0000ff01-0000-1000-8000-00805f9b34fb");
    public static final UUID UUID_CHAR_DEVICE_NAME = UUID.fromString("0000ff02-0000-1000-8000-00805f9b34fb");

    public static final UUID UUID_CHAR_NOTIFICATION = UUID.fromString("0000ff03-0000-1000-8000-00805f9b34fb");
    public static final UUID UUID_CHAR_USER_INFO = UUID.fromString("0000ff04-0000-1000-8000-00805f9b34fb");
    public static final UUID UUID_CHAR_CONTROL_POINT = UUID.fromString("0000ff05-0000-1000-8000-00805f9b34fb");
    public static final UUID UUID_CHAR_REALTIME_STEPS = UUID.fromString("0000ff06-0000-1000-8000-00805f9b34fb");

    public static final UUID UUID_CHAR_ACTIVITY = UUID.fromString("0000ff07-0000-1000-8000-00805f9b34fb");
    public static final UUID UUID_CHAR_FIRMWARE_DATA = UUID.fromString("0000ff08-0000-1000-8000-00805f9b34fb");
    public static final UUID UUID_CHAR_LE_PARAMS = UUID.fromString("0000ff09-0000-1000-8000-00805f9b34fb");
    public static final UUID UUID_CHAR_DATA_TIME = UUID.fromString("0000ff0a-0000-1000-8000-00805f9b34fb");
    public static final UUID UUID_CHAR_STATISTICS = UUID.fromString("0000ff0b-0000-1000-8000-00805f9b34fb");

    public static final UUID UUID_CHAR_BATTERY = UUID.fromString("0000ff0c-0000-1000-8000-00805f9b34fb");
    public static final UUID UUID_CHAR_TEST = UUID.fromString("0000ff0d-0000-1000-8000-00805f9b34fb");
    public static final UUID UUID_CHAR_SENSOR_DATA = UUID.fromString("0000ff0e-0000-1000-8000-00805f9b34fb");
    public static final UUID UUID_CHAR_PAIR = UUID.fromString("0000ff0f-0000-1000-8000-00805f9b34fb");
    public static final UUID UUID_CHAR_VIBRATION = UUID.fromString("00002a06-0000-1000-8000-00805f9b34fb");
    public static final UUID UUID_CHAR_HEARTRATE = UUID.fromString("00002a39-0000-1000-8000-00805f9b34fb");

    ////////////
    // Values //
    ////////////

    public static final byte[] PAIR = {2};
    public static final byte[] VIBRATION_WITH_LED = {1};
    public static final byte[] VIBRATION_10_TIMES_WITH_LED = {2};
    public static final byte[] VIBRATION_WITHOUT_LED = {4};
    public static final byte[] STOP_VIBRATION = {0};
    public static final byte[] ENABLE_REALTIME_STEPS_NOTIFY = {3, 1};
    public static final byte[] DISABLE_REALTIME_STEPS_NOTIFY = {3, 0};
    public static final byte[] ENABLE_SENSOR_DATA_NOTIFY = {18, 1};
    public static final byte[] DISABLE_SENSOR_DATA_NOTIFY = {18, 0};
    public static final byte[] SET_COLOR_RED = {14, 6, 1, 2, 1};
    public static final byte[] SET_COLOR_BLUE = {14, 0, 6, 6, 1};
    public static final byte[] SET_COLOR_ORANGE = {14, 6, 2, 0, 1};
    public static final byte[] SET_COLOR_GREEN = {14, 4, 5, 0, 1};
    public static final byte[] START_HEART_RATE_SCAN = {21, 2, 1};

    public static final byte[] REBOOT = {12};
    public static final byte[] REMOTE_DISCONNECT = {1};
    public static final byte[] FACTORY_RESET = {9};
    public static final byte[] SELF_TEST = {2};

}
