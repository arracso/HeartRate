package edu.udg.exit.heartrate.MiBand;

import java.util.UUID;

public final class MiBandConstants {

    /*
    Properties
        0x01 (1)   - Broadcast
        0x02 (2)   - Read
        0x04 (4)   - Write Without Response
        0x08 (8)   - Write
        0x10 (16)  - Notify
        0x20 (32)  - Indicate
        0x40 (64)  - Authenticated Signed Writes
        0x80 (128) - Extended Properties

    SERVICES & CHARACTERISTICS (UUID - Handle - Properties)

        UNKNOWN1: 00001800-0000-1000-8000-00805f9b34fb
            CHARACTERISTIC: 	00002a00-0000-1000-8000-00805f9b34fb - 0x0003 - READ
            CHARACTERISTIC: 	00002a01-0000-1000-8000-00805f9b34fb - 0x0005 - READ
            CHARACTERISTIC: 	00002a02-0000-1000-8000-00805f9b34fb - 0x0007 - READ & WRITE
            CHARACTERISTIC: 	00002a04-0000-1000-8000-00805f9b34fb - 0x0009 - READ

        UNKNOWN2: 00001801-0000-1000-8000-00805f9b34fb
            CHARACTERISTIC: 	00002a05-0000-1000-8000-00805f9b34fb - 0x000e - READ & INDICATE
                DESCRIPTOR: 		00002902-0000-1000-8000-00805f9b34fb - 0x000f

        MILI: 0000fee0-0000-1000-8000-00805f9b34fb
            DEVICE_INFO: 	0000ff01-0000-1000-8000-00805f9b34fb - 0x0012 - READ
            DEVICE_NAME: 	0000ff02-0000-1000-8000-00805f9b34fb - 0x0014 - READ & WRITE
            NOTIFICATION: 	0000ff03-0000-1000-8000-00805f9b34fb - 0x0016 - READ & NOTIFY
                DESCRIPTOR: 		00002902-0000-1000-8000-00805f9b34fb - 0x0017
            USER_INFO: 	0000ff04-0000-1000-8000-00805f9b34fb - 0x0019 - READ & WRITE
            CONTROL_POINT: 	0000ff05-0000-1000-8000-00805f9b34fb - 0x001b - WRITE
            REALTIME_STEPS: 	0000ff06-0000-1000-8000-00805f9b34fb - 0x001d - READ & NOTIFY
                DESCRIPTOR: 		00002902-0000-1000-8000-00805f9b34fb - 0x001e
            ACTIVITY_DATA: 	0000ff07-0000-1000-8000-00805f9b34fb - 0x0020 - READ & NOTIFY
                DESCRIPTOR: 		00002902-0000-1000-8000-00805f9b34fb - 0x0021
            FIRMWARE_DATA: 	0000ff08-0000-1000-8000-00805f9b34fb - 0x0023 - WRITE NO RESPONSE
            LE_PARAMS: 	0000ff09-0000-1000-8000-00805f9b34fb - 0x0025 - READ, WRITE & NOTIFY
                DESCRIPTOR: 		00002902-0000-1000-8000-00805f9b34fb - 0x0026
            DATE_TIME: 	0000ff0a-0000-1000-8000-00805f9b34fb - 0x0028 - READ & WRITE
            STATISTICS: 	0000ff0b-0000-1000-8000-00805f9b34fb - 0x002a - READ & WRITE
            BATTERY: 	0000ff0c-0000-1000-8000-00805f9b34fb - 0x002c - READ & NOTIFY
                DESCRIPTOR: 		00002902-0000-1000-8000-00805f9b34fb - - 0x002d
            TEST: 	0000ff0d-0000-1000-8000-00805f9b34fb - 0x002f - READ & WRITE
            SENSOR_DATA: 	0000ff0e-0000-1000-8000-00805f9b34fb - 0x0031 - READ, WRITE & NOTIFY
                DESCRIPTOR: 		00002902-0000-1000-8000-00805f9b34fb - 0x0032
            PAIR: 	0000ff0f-0000-1000-8000-00805f9b34fb - 0x0034 - READ & WRITE
            CHARACTERISTIC: 	0000ff10-0000-1000-8000-00805f9b34fb - 0x0036 - NOTIFY
                DESCRIPTOR: 		00002902-0000-1000-8000-00805f9b34fb - 0x0037
            CHARACTERISTIC: 	0000fec9-0000-1000-8000-00805f9b34fb - 0x0039 - READ

        UNKNOWN3: 0000fee1-0000-1000-8000-00805f9b34fb
            CHARACTERISTIC: 	0000fedd-0000-1000-8000-00805f9b34fb - 0x003c - WRITE
            CHARACTERISTIC: 	0000fede-0000-1000-8000-00805f9b34fb - 0x003e - READ
            CHARACTERISTIC: 	0000fedf-0000-1000-8000-00805f9b34fb - 0x0040 - READ
            CHARACTERISTIC: 	0000fed0-0000-1000-8000-00805f9b34fb - 0x0042 - WRITE
            CHARACTERISTIC: 	0000fed1-0000-1000-8000-00805f9b34fb - 0x0044 - WRITE
            CHARACTERISTIC: 	0000fed2-0000-1000-8000-00805f9b34fb - 0x0046 - READ
            CHARACTERISTIC: 	0000fed3-0000-1000-8000-00805f9b34fb - 0x0048 - WRITE

        HEARTRATE: 0000180d-0000-1000-8000-00805f9b34fb
            HR_NOTIFICATION: 	00002a37-0000-1000-8000-00805f9b34fb - 0x004b - NOTIFY
                DESCRIPTOR: 		00002902-0000-1000-8000-00805f9b34fb - 0x004c
            HR_CONTROL_POINT: 	00002a39-0000-1000-8000-00805f9b34fb - 0x004e - READ & WRITE

        VIBRATION: 00001802-0000-1000-8000-00805f9b34fb
            VIBRATION: 	00002a06-0000-1000-8000-00805f9b34fb - 0x0051 - WRITE NO RESPONSE
     */


    /**
     * Common on BLE devices.
     */
    private static final String BASE_UUID = "0000%s-0000-1000-8000-00805f9b34fb";

    //////////////
    // Services //
    //////////////

    public static final class UUID_SERVICE {
        public static final UUID MILI = UUID.fromString(String.format(BASE_UUID, "FEE0"));
        public static final UUID VIBRATION = UUID.fromString(String.format(BASE_UUID, "1802")); // Inmediate Alert
        public static final UUID HEARTRATE = UUID.fromString(String.format(BASE_UUID, "180D")); // Heart Rate

        // Unknown services
        public static final UUID UNKNOWN1 = UUID.fromString(String.format(BASE_UUID, "1800")); // Generic Access
        public static final UUID UNKNOWN2 = UUID.fromString(String.format(BASE_UUID, "1801")); // Generic Attribute
        public static final UUID UNKNOWN3 = UUID.fromString(String.format(BASE_UUID, "FEE1"));
    }

    /////////////////////
    // Characteristics //
    /////////////////////

    public static final class UUID_CHAR {
        // MILI SERVICE //
        public static final UUID DEVICE_INFO = UUID.fromString(String.format(BASE_UUID, "FF01")); // Read
        public static final UUID DEVICE_NAME = UUID.fromString(String.format(BASE_UUID, "FF02")); // Read & Write
        public static final UUID NOTIFICATION = UUID.fromString(String.format(BASE_UUID, "FF03")); // Read & Notify (DESC)
        public static final UUID USER_INFO = UUID.fromString(String.format(BASE_UUID, "FF04")); // Read & Write
        public static final UUID CONTROL_POINT = UUID.fromString(String.format(BASE_UUID, "FF05")); // Write
        public static final UUID REALTIME_STEPS = UUID.fromString(String.format(BASE_UUID, "FF06")); // Read & Notify (DESC)
        public static final UUID ACTIVITY_DATA = UUID.fromString(String.format(BASE_UUID, "FF07")); // Read & Notify (DESC)
        public static final UUID FIRMWARE_DATA = UUID.fromString(String.format(BASE_UUID, "FF08")); // Write without response
        public static final UUID LE_PARAMS = UUID.fromString(String.format(BASE_UUID, "FF09")); // Read, Write & Notify (DESC)
        public static final UUID DATE_TIME = UUID.fromString(String.format(BASE_UUID, "FF0A")); // Read & Write
        public static final UUID STATISTICS = UUID.fromString(String.format(BASE_UUID, "FF0B")); // Read & Write
        public static final UUID BATTERY = UUID.fromString(String.format(BASE_UUID, "FF0C")); // Read & Notify (DESC)
        public static final UUID TEST = UUID.fromString(String.format(BASE_UUID, "FF0D")); // Read & Write
        public static final UUID SENSOR_DATA = UUID.fromString(String.format(BASE_UUID, "FF0E")); // Read, Write & Notify (DESC)
        public static final UUID PAIR = UUID.fromString(String.format(BASE_UUID, "FF0F")); // Read & Write

        public static final UUID UNKNOWN_CHAR1 = UUID.fromString(String.format(BASE_UUID, "FF10")); // Notify (DESC)
        public static final UUID UNKNOWN_CHAR2 = UUID.fromString(String.format(BASE_UUID, "FFc9")); // Read

        // VIBRATION SERVICE //
        public static final UUID VIBRATION = UUID.fromString(String.format(BASE_UUID, "2A06")); // Write without response

        // HEART RATE SERVICE //
        public static final UUID HEARTRATE_NOTIFICATION = UUID.fromString(String.format(BASE_UUID, "2A37")); // Notify (DESC)
        public static final UUID HEARTRATE_CONTROL_POINT = UUID.fromString(String.format(BASE_UUID, "2A39")); // Read & Write

        // UNKNOWN1 SERVICE //
        public static final UUID UNKNOWN1_CHAR1 = UUID.fromString(String.format(BASE_UUID, "2A00")); // Read
        public static final UUID UNKNOWN1_CHAR2 = UUID.fromString(String.format(BASE_UUID, "2A01")); // Read
        public static final UUID UNKNOWN1_CHAR3 = UUID.fromString(String.format(BASE_UUID, "2A02")); // Read & Write
        public static final UUID UNKNOWN1_CHAR4 = UUID.fromString(String.format(BASE_UUID, "2A04")); // Read

        // UNKNOWN2 SERVICE //
        public static final UUID UNKNOWN2_CHAR1 = UUID.fromString(String.format(BASE_UUID, "2A05")); // Read & Indicate (DESC)

        // UNKNOWN3 SERVICE //
        public static final UUID UNKNOWN3_CHAR1 = UUID.fromString(String.format(BASE_UUID, "FEDD")); // Write
        public static final UUID UNKNOWN3_CHAR2 = UUID.fromString(String.format(BASE_UUID, "FEDE")); // Read
        public static final UUID UNKNOWN3_CHAR3 = UUID.fromString(String.format(BASE_UUID, "FEDF")); // Read
        public static final UUID UNKNOWN3_CHAR4 = UUID.fromString(String.format(BASE_UUID, "FED0")); // Write
        public static final UUID UNKNOWN3_CHAR5 = UUID.fromString(String.format(BASE_UUID, "FED1")); // Write
        public static final UUID UNKNOWN3_CHAR6 = UUID.fromString(String.format(BASE_UUID, "FED2")); // Read
        public static final UUID UNKNOWN3_CHAR7 = UUID.fromString(String.format(BASE_UUID, "FED3")); // Write
    }

    /////////////////
    // Descriptors //
    /////////////////

    public static final class UUID_DESC {
        // GATT descriptors
        public static final UUID GATT_CLIENT_CHARACTERISTIC_CONFIGURATION = UUID.fromString(String.format(BASE_UUID, "2902"));

        // Mi Band
        public static final UUID UPDATE_NOTIFICATION = UUID_DESC.GATT_CLIENT_CHARACTERISTIC_CONFIGURATION; // Used to enable or disable different notifications.
    }

    //////////////
    // PROTOCOL //
    //////////////

    public static final class PROTOCOL {
        // PAIR //
        public static final byte[] PAIR = {2}; // Works

        public static final byte[] STOP_VIBRATION = {0};

        // TEST //
        public static final byte[] REMOTE_DISCONNECT = {1}; // TODO - Doesn't works
        public static final byte[] SELF_TEST = {2}; // TODO - Doesn't works

        // VIBRATION //
        public static final byte[] VIBRATION_WITH_LED = {1}; // Works
        public static final byte[] VIBRATION_10_TIMES_WITH_LED = {2}; // Works
        public static final byte[] VIBRATION_WITH_LED2 = {3}; // Works (same as 1)
        public static final byte[] VIBRATION_WITHOUT_LED = {4}; // Works
    }

    //////////////
    // COMMANDS //
    //////////////

    /**
     * Commands usually send to {@link UUID_CHAR#CONTROL_POINT CONTROL_POINT} characteristic.
     * Some commands are send to {@link UUID_CHAR#HEARTRATE_CONTROL_POINT HEARTRATE_CONTROL_POINT} characteristic.
     */
    public static  final class COMMAND {
        // REAL TIME STEPS //
        public static final byte[] START_REAL_TIME_STEPS_NOTIFICATIONS = {3, 1};
        public static final byte[] STOP_REAL_TIME_STEPS_NOTIFICATIONS = {3, 0};

        // TIMER //
        public static final byte[] SET_TIMER = {4}; // {4, TIME (12 bytes)}

        // FITNESS GOAL //
        public static final byte[] SET_FITNESS_GOAL = {5}; // {5, 0x0, (GOAL & 0xff), ((GOAL >>> 8) & 0xff)}

        // FETCH DATA //
        public static final byte[] FETCH_DATA = {6};

        // FIRMWARE //
        public static final byte[] SEND_FIRMWARE = {7}; // {7, INFO}

        // NOTIFICATION //
        public static final byte[] SEND_NOTIFICATION = {8, 0x0, 0x0, 0x0}; // {8, xxx}

        // VIBRATION // TODO - Not working
        public static final byte[] START_VIBRATION = {8, 1};

        public static final byte[] FACTORY_RESET = {9};
        public static final byte[] CONFIRM_ACTIVITY_DATA_TRANSFER_COMPLETE = {10};
        public static final byte[] SYNC = {11};
        public static final byte[] REBOOT = {12}; // works

        // COLORS // 1s has no colors
        public static final byte[] SET_COLOR_RED = {14, 6, 1, 2, 1};
        public static final byte[] SET_COLOR_BLUE = {14, 0, 6, 6, 1};
        public static final byte[] SET_COLOR_ORANGE = {14, 6, 2, 0, 1};
        public static final byte[] SET_COLOR_GREEN = {14, 4, 5, 0, 1};

        // WEAR LOCATION //
        public static final byte[] SET_WEAR_LOCATION_LEFT = {15, 0};
        public static final byte[] SET_WEAR_LOCATION_RIGHT = {15, 1};
        public static final byte[] SET_WEAR_LOCATION_NECK = {15, 2};

        public static final byte[] SET_REAL_TIME_STEPS = {16, 0x0, 0x0}; // {16, (STEPS & 0xff), ((STEPS >>> 8) & 0xff)}

        public static final byte[] STOP_SYNC = {17};

        // SENSOR DATA //
        private static final byte[] START_SENSOR_DATA_NOTIFICATIONS = {18, 1};
        private static final byte[] STOP_SENSOR_DATA_NOTIFICATIONS = {18, 0};

        // MOTOR VIBRATE //
        public static final byte[] START_MOTOR_VIBRATION = {19, 1};
        public static final byte[] STOP_MOTOR_VIBRATION = {19, 0};

        /////////////////////////
        // HEART RATE COMMANDS //
        /////////////////////////

        public static final byte[] START_HEART_RATE_MEASUREMENT_SLEEP = {21, 0, 1};
        public static final byte[] STOP_HEART_RATE_MEASUREMENT_SLEEP = {21, 0, 0};
        public static final byte[] START_HEART_RATE_MEASUREMENT_CONTINUOUS = {21, 1, 1}; // works
        public static final byte[] STOP_HEART_RATE_MEASUREMENT_CONTINUOUS = {21, 1, 0};
        public static final byte[] START_HEART_RATE_MEASUREMENT_MANUAL = {21, 2, 1}; // works
        public static final byte[] STOP_HEART_RATE_MEASUREMENT_MANUAL = {21, 2, 0};

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
