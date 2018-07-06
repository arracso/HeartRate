package edu.udg.exit.heartrate.Utils;

import android.content.Context;

/**
 * Class that handles user preferences file (save, load & remove).
 */
public class UserPreferences {

    ///////////////
    // Constants //
    ///////////////

    // Filename //
    private static final String FILENAME = "HearthRate";

    // Fields //
    public static final String USER_PROFILE = "user_profile";
    public static final String HEART_RATE_MEASURE = "heart_rate_measure";
    public static final String DEVICE_HAND = "device_hand";
    public static final String BONDED_DEVICE_ADDRESS = "bounded_device_address";
    public static final String ACCESS_TOKEN = "access_token";
    public static final String REFRESH_TOKEN = "refresh_token";

    ////////////////
    // Attributes //
    ////////////////

    private static UserPreferences instance;

    ////////////////////
    // Public Methods //
    ////////////////////

    /**
     * Gets an instance of UserPreferences (creates it when needed).
     * @return UserPreferences instance.
     */
    public synchronized static UserPreferences getInstance() {
        if(instance == null) instance = new UserPreferences();
        return instance;
    }

    /**
     * Saves a value into a field.
     * @param context - Application context
     * @param field - Field that will hold the value.
     * @param value - Value to be save.
     */
    public void save(Context context, String field, String value) {
        context.getSharedPreferences(FILENAME, Context.MODE_PRIVATE).edit().putString(field, value).apply();
    }

    /**
     * Removes the value of a field.
     * @param context - Application context
     * @param field - Field to remove its value
     */
    public void remove(Context context, String field) {
        context.getSharedPreferences(FILENAME, Context.MODE_PRIVATE).edit().remove(field).apply();
    }

    /**
     * Removes all the values of user preferences.
     * @param context - Application context
     */
    public void removeAll(Context context) {
        context.getSharedPreferences(FILENAME, Context.MODE_PRIVATE).edit().clear().apply();
    }

    /**
     * Gets the value of a field.
     * @param context - Application context
     * @param field - Field to get its value
     * @return The value of the field, NULL if the field doesn't exist.
     */
    public String load(Context context, String field) {
        return context.getSharedPreferences(FILENAME, Context.MODE_PRIVATE).getString(field, null);
    }

}
