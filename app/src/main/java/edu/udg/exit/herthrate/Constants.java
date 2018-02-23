package edu.udg.exit.herthrate;

import java.util.UUID;

public final class Constants {

    /**
     * Common on BLE devices.
     */
    private static final String BASE_UUID = "0000%s-0000-1000-8000-00805f9b34fb";

    //////////////
    // Services //
    //////////////

    public static final class UUID_SERVICE {
        public static final UUID MILI = UUID.fromString(String.format(BASE_UUID, "FEE0"));
        public static final UUID VIBRATION = UUID.fromString(String.format(BASE_UUID, "1802")); // Verified
        public static final UUID HEARTRATE = UUID.fromString("0000180d-0000-1000-8000-00805f9b34fb");
        // Unknown services
        public static final UUID UNKNOWN1 = UUID.fromString("00001800-0000-1000-8000-00805f9b34fb");
        public static final UUID UNKNOWN2 = UUID.fromString("00001801-0000-1000-8000-00805f9b34fb");
        public static final UUID UNKNOWN4 = UUID.fromString("0000fee1-0000-1000-8000-00805f9b34fb");
        public static final UUID UNKNOWN5 = UUID.fromString("0000fee7-0000-1000-8000-00805f9b34fb");
    }

    /////////////////
    // Descriptors //
    /////////////////

    public static final class UUID_DESC {
        public static final UUID UPDATE_NOTIFICATION = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
    }

    /////////////////////
    // Characteristics //
    /////////////////////

    public static final class UUID_CHAR {
        public static final UUID DEVICE_INFO = UUID.fromString("0000ff01-0000-1000-8000-00805f9b34fb");
        public static final UUID DEVICE_NAME = UUID.fromString("0000ff02-0000-1000-8000-00805f9b34fb");

        public static final UUID NOTIFICATION = UUID.fromString("0000ff03-0000-1000-8000-00805f9b34fb");
        public static final UUID USER_INFO = UUID.fromString("0000ff04-0000-1000-8000-00805f9b34fb");
        public static final UUID CONTROL_POINT = UUID.fromString("0000ff05-0000-1000-8000-00805f9b34fb");
        public static final UUID REALTIME_STEPS = UUID.fromString("0000ff06-0000-1000-8000-00805f9b34fb");

        public static final UUID ACTIVITY = UUID.fromString("0000ff07-0000-1000-8000-00805f9b34fb");
        public static final UUID FIRMWARE_DATA = UUID.fromString("0000ff08-0000-1000-8000-00805f9b34fb");
        public static final UUID LE_PARAMS = UUID.fromString("0000ff09-0000-1000-8000-00805f9b34fb");
        public static final UUID DATE_TIME = UUID.fromString("0000ff0a-0000-1000-8000-00805f9b34fb");
        public static final UUID STATISTICS = UUID.fromString("0000ff0b-0000-1000-8000-00805f9b34fb");

        public static final UUID BATTERY = UUID.fromString("0000ff0c-0000-1000-8000-00805f9b34fb");
        public static final UUID TEST = UUID.fromString("0000ff0d-0000-1000-8000-00805f9b34fb");
        public static final UUID SENSOR_DATA = UUID.fromString("0000ff0e-0000-1000-8000-00805f9b34fb");
        public static final UUID PAIR = UUID.fromString("0000ff0f-0000-1000-8000-00805f9b34fb");
        public static final UUID VIBRATION = UUID.fromString("00002a06-0000-1000-8000-00805f9b34fb"); // Verified
        public static final UUID HEARTRATE_CONTROL_POINT = UUID.fromString("00002a39-0000-1000-8000-00805f9b34fb");
        public static final UUID HEARTRATE_NOTIFICATION = UUID.fromString("00002a37-0000-1000-8000-00805f9b34fb");
    }

    ////////////
    // Values //
    ////////////

    public static final class PROTOCOL {
        public static final byte[] PAIR = {2};
        public static final byte[] VIBRATION_WITH_LED = {1}; // Works
        public static final byte[] VIBRATION_10_TIMES_WITH_LED = {2}; // Doesn't works (unpaired)
        public static final byte[] VIBRATION_WITHOUT_LED = {4}; // Works
        public static final byte[] STOP_VIBRATION = {0};
        public static final byte[] ENABLE_REALTIME_STEPS_NOTIFY = {3, 1};
        public static final byte[] DISABLE_REALTIME_STEPS_NOTIFY = {3, 0};
        public static final byte[] ENABLE_SENSOR_DATA_NOTIFY = {18, 1};
        public static final byte[] DISABLE_SENSOR_DATA_NOTIFY = {18, 0};
        public static final byte[] SET_COLOR_RED = {14, 6, 1, 2, 1}; // 1s has no colors
        public static final byte[] SET_COLOR_BLUE = {14, 0, 6, 6, 1}; // 1s has no colors
        public static final byte[] SET_COLOR_ORANGE = {14, 6, 2, 0, 1}; // 1s has no colors
        public static final byte[] SET_COLOR_GREEN = {14, 4, 5, 0, 1}; // 1s has no colors
        public static final byte[] START_HEART_RATE_SCAN = {21, 2, 1};

        public static final byte[] REBOOT = {12};
        public static final byte[] REMOTE_DISCONNECT = {1};
        public static final byte[] FACTORY_RESET = {9};
        public static final byte[] SELF_TEST = {2}; // Doesn't works (unpaired)
    }

    ///////////
    // Model //
    ///////////

    /**
     * MAC address start.
     */
    public static final class MODEL {
        public static final String MI1A = "88:0F:10";
        public static final String MI1S = "C8:0F:10";
    }

}
