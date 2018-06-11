package edu.udg.exit.heartrate.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;

public class DataBase extends SQLiteOpenHelper {

    ///////////////
    // Constants //
    ///////////////

    // DATABASE
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "measure.db";

    // RATE TABLE
    public static final String RATE_TABLE_NAME = "rate";
    private static final String RATE_TABLE_COLUMN_TIME = "time";
    private static final String RATE_TABLE_COLUMN_RATE = "rate";

    ///////////////
    // Variables //
    ///////////////

    private Context ctx;

    ///////////////////////
    // Lifecycle Methods //
    ///////////////////////

    /**
     * Constructor
     * @param ctx - Context
     */
    public DataBase(Context ctx) {
        super(ctx, DATABASE_NAME, null, DATABASE_VERSION);
        this.ctx = ctx;
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
     * Adds heart rate for a timestamp into the data base
     * @param time - timestamp
     * @param rate - heart rate
     */
    public void insertRate(long time, int rate) {
        ContentValues values = new ContentValues();
        values.put(RATE_TABLE_COLUMN_TIME, time);
        values.put(RATE_TABLE_COLUMN_RATE, rate);
        this.getWritableDatabase().insert(RATE_TABLE_NAME, null, values);
    }

    public Cursor selectRate() {
        return this.getReadableDatabase().rawQuery("SELECT * FROM " + RATE_TABLE_NAME, null);
    }

    public Cursor selectRate(long to) {
        return this.getReadableDatabase().rawQuery("SELECT * FROM " + RATE_TABLE_NAME + " WHERE time <= ?", new String[]{""+to});
    }

    public Cursor selectRate(long from, long to) {
        return this.getReadableDatabase().rawQuery("SELECT * FROM " + RATE_TABLE_NAME + " WHERE time >= ? AND time <= ?", new String[]{""+from,""+to});
    }

    public Cursor select(String tableName) {
        return this.getReadableDatabase().rawQuery("SELECT * FROM " + tableName, null);
    }

    public File exportAsCSV(String tableName, String fileName) {
        // Retrieve data from data base
        Cursor cursor = select(tableName);

        // Create File
        Storage storage = new Storage();
        storage.createFile(ctx, fileName);

        // Write column names
        String[] columns = cursor.getColumnNames();
        storage.writeToFile(toComaSeparatedValues(columns));

        // Write rows
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            storage.writeToFile(toComaSeparatedValues(cursor));
            cursor.moveToNext();
        }

        // Close the file
        storage.closeFile();

        return storage.getFile(ctx, fileName);
    }

    /////////////////////
    // Private Methods //
    /////////////////////

    private String toComaSeparatedValues(Object[] values) {
        StringBuilder builder = new StringBuilder("");
        for(int i = 0; i < values.length; i++){
            if(i!=0) builder.append(", ");
            builder.append(values[i].toString());
        }
        Log.d("CSV", builder.toString());
        return builder.toString();
    }

    private String toComaSeparatedValues(Cursor cursor) {
        StringBuilder builder = new StringBuilder("\r\n");
        for(int i = 0; i < cursor.getColumnCount(); i++){
            if(i!=0) builder.append(", ");
            builder.append(cursor.getString(i));
        }
        Log.d("CSV", builder.toString());
        return builder.toString();
    }

}
