package edu.udg.exit.heartrate.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;

/**
 * Data base maintaining the measures of the user.
 * NOTE: All tables must be indexed by a timestamp
 */
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

    /**
     * Select rows from a table and return its cursor.
     * @param tableName - name of the table to be selected
     * @param from - timestamp to start the selection
     * @param to - timestamp to stop the selection
     * @return Cursor with selected rows.
     */
    public Cursor select(String tableName, Long from, Long to) {
        if(from == null && to == null)
            return this.getReadableDatabase().rawQuery("SELECT * FROM " + tableName, null);
        else if(from == null)
            return this.getReadableDatabase().rawQuery("SELECT * FROM " + tableName + " WHERE time <= ?", new String[]{""+to});
        else if(to == null)
            return this.getReadableDatabase().rawQuery("SELECT * FROM " + tableName + " WHERE time >= ?", new String[]{""+from});
        else
            return this.getReadableDatabase().rawQuery("SELECT * FROM " + tableName + " WHERE time >= ? AND time <= ?", new String[]{""+from,""+to});
    }

    /**
     * Delete rows from a table.
     * @param tableName - name of the table
     * @param from - timestamp to start the deletion
     * @param to - timestamp to stop the deletion
     * @return Integer - Number of rows affected.
     */
    public Integer delete(String tableName, Long from, Long to) {
        if(from == null && to == null)
            return this.getWritableDatabase().delete(tableName,"1",null);
        else if(from == null)
            return this.getWritableDatabase().delete(tableName,"time <= ?", new String[]{""+to});
        else if(to == null)
            return this.getWritableDatabase().delete(tableName,"time >= ?", new String[]{""+from});
        else
            return this.getWritableDatabase().delete(tableName,"time >= ? AND time <=?", new String[]{""+from,""+to});
    }

    /**
     * Deletes all records from all tables of the data base.
     */
    public void deleteAllRecords() {
        delete(RATE_TABLE_NAME,null,null);
    }

    /**
     * Exports a table as a CSV.
     * @param tableName - name of the table to be exported.
     * @param from - timestamp to start the selection
     * @param to - timestamp to begin the selection
     * @param fileName - name of the exported csv file
     * @return File
     */
    public File exportAsCSV(String tableName, Long from, Long to, String fileName) {
        // Retrieve data from data base
        Cursor cursor = select(tableName, from, to);
        if(cursor == null || cursor.getCount() <= 0 ) return null;

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

    /**
     * Split the array of objects into coma separated values string format.
     * @param values - Array with diferent object values.
     * @return String
     */
    private String toComaSeparatedValues(Object[] values) {
        StringBuilder builder = new StringBuilder("");
        for(int i = 0; i < values.length; i++){
            if(i!=0) builder.append(", ");
            builder.append(values[i].toString());
        }
        Log.d("CSV", builder.toString());
        return builder.toString();
    }

    /**
     * Split the values of the cursor into coma separated values string format.
     * @param cursor - cursor pointing to a row
     * @return String
     */
    private String toComaSeparatedValues(Cursor cursor) {
        StringBuilder builder = new StringBuilder("\r\n");
        for(int i = 0; i < cursor.getColumnCount(); i++){
            if(i!=0) builder.append(", ");
            builder.append(cursor.getString(i));
        }
        return builder.toString();
    }

}
