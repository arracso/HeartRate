package edu.udg.exit.heartrate.Utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBase extends SQLiteOpenHelper {

    ///////////////
    // Constants //
    ///////////////

    // DATABASE
    private static final int DATABASE_VERSION = 0;
    public static final String DATABASE_NAME = "measure.db";

    // RATE TABLE
    public static final String RATE_TABLE_NAME = "rate";
    public static final String RATE_TABLE_COLUMN_TIME = "time";
    public static final String RATE_TABLE_COLUMN_RATE = "rate";

    ///////////////////////
    // Lifecycle Methods //
    ///////////////////////

    /**
     * Constructor
     * @param context
     */
    public DataBase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + RATE_TABLE_NAME
            + "("
                + RATE_TABLE_COLUMN_TIME + " integer primary key, "
                + RATE_TABLE_COLUMN_RATE + " integer"
            + ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + RATE_TABLE_NAME);
        onCreate(db);
    }

    ////////////////////
    // Public Methods //
    ////////////////////

    /**
     * Executes an SQL command.
     * @param sqlCommand - Command to be executed.
     */
    public void execSQL(String sqlCommand) {
        this.getWritableDatabase().execSQL(sqlCommand);
    }

}
