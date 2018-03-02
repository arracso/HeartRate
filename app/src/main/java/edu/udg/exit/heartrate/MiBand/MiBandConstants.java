package edu.udg.exit.heartrate.MiBand;

import java.util.UUID;

public final class MiBandConstants {

    /**
     * Common on BLE devices.
     */
    private static final String BASE_UUID = "0000%s-0000-1000-8000-00805f9b34fb";

    //////////////
    // Services //
    //////////////

    public static final class UUID_SERVICE {
        public static final UUID MILI = UUID.fromString(String.format(BASE_UUID, "FEE0"));
        public static final UUID VIBRATION = UUID.fromString(String.format(BASE_UUID, "1802"));
        public static final UUID HEARTRATE = UUID.fromString("0000180d-0000-1000-8000-00805f9b34fb");
        // Unknown services
        public static final UUID UNKNOWN1 = UUID.fromString("00001800-0000-1000-8000-00805f9b34fb");
        public static final UUID UNKNOWN2 = UUID.fromString("00001801-0000-1000-8000-00805f9b34fb");
        public static final UUID UNKNOWN4 = UUID.fromString("0000fee1-0000-1000-8000-00805f9b34fb");
        public static final UUID UNKNOWN5 = UUID.fromString("0000fee7-0000-1000-8000-00805f9b34fb");
    }

    /////////////////////
    // Characteristics //
    /////////////////////

    public static final class UUID_CHAR {
        public static final UUID DEVICE_INFO = UUID.fromString(String.format(BASE_UUID, "FF01"));
        public static final UUID DEVICE_NAME = UUID.fromString(String.format(BASE_UUID, "FF02"));
        public static final UUID NOTIFICATION = UUID.fromString(String.format(BASE_UUID, "FF03"));
        public static final UUID USER_INFO = UUID.fromString(String.format(BASE_UUID, "FF04"));
        public static final UUID CONTROL_POINT = UUID.fromString(String.format(BASE_UUID, "FF05"));
        public static final UUID REALTIME_STEPS = UUID.fromString(String.format(BASE_UUID, "FF06"));
        public static final UUID ACTIVITY_DATA = UUID.fromString(String.format(BASE_UUID, "FF07"));
        public static final UUID FIRMWARE_DATA = UUID.fromString(String.format(BASE_UUID, "FF08"));
        public static final UUID LE_PARAMS = UUID.fromString(String.format(BASE_UUID, "FF09"));
        public static final UUID DATE_TIME = UUID.fromString(String.format(BASE_UUID, "FF0A"));
        public static final UUID STATISTICS = UUID.fromString(String.format(BASE_UUID, "FF0B"));
        public static final UUID BATTERY = UUID.fromString(String.format(BASE_UUID, "FF0C"));
        public static final UUID TEST = UUID.fromString(String.format(BASE_UUID, "FF0D"));
        public static final UUID SENSOR_DATA = UUID.fromString(String.format(BASE_UUID, "FF0E"));
        public static final UUID PAIR = UUID.fromString(String.format(BASE_UUID, "FF0F"));

        public static final UUID VIBRATION = UUID.fromString(String.format(BASE_UUID, "2A06"));
        public static final UUID HEARTRATE_NOTIFICATION = UUID.fromString(String.format(BASE_UUID, "2a37"));
        public static final UUID HEARTRATE_CONTROL_POINT = UUID.fromString(String.format(BASE_UUID, "2a39"));
    }

    /////////////////
    // Descriptors //
    /////////////////

    public static final class UUID_DESC {
        public static final UUID UPDATE_NOTIFICATION = UUID.fromString(String.format(BASE_UUID, "2902"));
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

    ///////////////////////////
    // NOTIFICATION PROTOCOL //
    ///////////////////////////

    public static final class NOTIFICATION {
        public static final byte UNKNOWN = -0x1;
        public static final byte NORMAL = 0x0;
        public static final byte FIRMWARE_UPDATE_FAILED = 0x1;
        public static final byte FIRMWARE_UPDATE_SUCCESS = 0x2;
        public static final byte CONN_PARAM_UPDATE_FAILED = 0x3;
        public static final byte CONN_PARAM_UPDATE_SUCCESS = 0x4;
        public static final byte AUTHENTICATION_SUCCESS = 0x5;
        public static final byte AUTHENTICATION_FAILED = 0x6;
        public static final byte FITNESS_GOAL_ACHIEVED = 0x7;
        public static final byte SET_LATENCY_SUCCESS = 0x8;
        public static final byte RESET_AUTHENTICATION_FAILED = 0x9;
        public static final byte RESET_AUTHENTICATION_SUCCESS = 0xa;
        public static final byte FIRMWARE_CHECK_FAILED = 0xb;
        public static final byte FIRMWARE_CHECK_SUCCESS = 0xc;
        public static final byte STATUS_MOTOR_NOTIFY = 0xd;
        public static final byte STATUS_MOTOR_CALL = 0xe;
        public static final byte STATUS_MOTOR_DISCONNECT = 0xf;
        public static final byte STATUS_MOTOR_SMART_ALARM = 0x10;
        public static final byte STATUS_MOTOR_ALARM = 0x11;
        public static final byte STATUS_MOTOR_GOAL = 0x12;
        public static final byte STATUS_MOTOR_AUTH = 0x13;
        public static final byte STATUS_MOTOR_SHUTDOWN = 0x14;
        public static final byte STATUS_MOTOR_AUTH_SUCCESS = 0x15;
        public static final byte STATUS_MOTOR_TEST = 0x16;
        public static final int PAIR_CANCEL = 0xef;
        public static final int DEVICE_MALFUNCTION = 0xff;
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
