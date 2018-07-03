package edu.udg.exit.heartrate.Views;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import edu.udg.exit.heartrate.Components.ExpandItem;
import edu.udg.exit.heartrate.R;
import edu.udg.exit.heartrate.TodoApp;
import edu.udg.exit.heartrate.Utils.DataBase;

import java.io.File;

public class DeviceActivity extends Activity {

    ///////////////
    // Variables //
    ///////////////

    private int oldContent = -1;
    private ViewGroup container = null;

    ///////////////////////
    // Lifecycle methods //
    ///////////////////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);

        // Get the container
        container = (ViewGroup) findViewById(R.id.device_container);

        // Setup contents
        setupContents();
    }

    ////////////////////
    // Public methods //
    ////////////////////

    /**
     * Unbind the current binded device.
     * @param view - MainActivity view
     */
    public void unbindDevice(View view) {
        (((TodoApp) this.getApplication())).getBluetoothService().unbindDevice();
        this.finish();
    }

    /////////////////////
    // Private Methods //
    /////////////////////

    //*********************************//
    // Content Expand/Collapse Methods //
    //*********************************//

    /**
     * Expands the new content and collapses the old one.
     * @param newContent - New content to be expanded
     */
    private void toggleContents(int newContent) {
        if(oldContent != -1) collapseContent(oldContent);
        if(oldContent == newContent) oldContent = -1;
        else {
            oldContent = newContent;
            expandContent(newContent);
        }
    }

    /**
     * Expands a content from the container.
     * @param content - Content to be expanded
     */
    private void expandContent(int content) {
        ExpandItem item = (ExpandItem) container.getChildAt(content);
        item.expand();
    }

    /**
     * Collapses a content from the container.
     * @param content - Content to be collapsed
     */
    private void collapseContent(int content) {
        ExpandItem item = (ExpandItem) container.getChildAt(content);
        item.collapse();
    }

    //***********************//
    // Content Setup Methods //
    //***********************//

    /**
     * Contents setup.
     */
    private void setupContents() {
        // Set old content as no one
        oldContent = -1;

        // Setup content of the container
        setupMAC();
        setupName();
        setupBattery();
        setupHeartRate();
    }

    private void setupMAC() {
        // Get MAC item
        final ExpandItem macItem = (ExpandItem) findViewById(R.id.device_mac);
        // Set listeners and callbacks
        macItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleContents(container.indexOfChild(macItem));
            }
        });
        // Setup MAC
        // TODO
    }

    private void setupName() {
        // Get name item
        final ExpandItem nameItem = (ExpandItem) findViewById(R.id.device_name);
        // Set listeners and callbacks
        nameItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleContents(container.indexOfChild(nameItem));
            }
        });
        // Setup name
        // TODO
    }

    private void setupBattery(){
        // Get battery item
        final ExpandItem batteryItem = (ExpandItem) findViewById(R.id.device_battery);
        // Set listeners and callbacks
        batteryItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleContents(container.indexOfChild(batteryItem));
            }
        });
        // Setup battery
        // TODO - Use runnable
    }

    private void setupHeartRate(){
        // Get name item
        final ExpandItem heartRateItem = (ExpandItem) findViewById(R.id.device_heart_rate);
        // Set listeners and callbacks
        heartRateItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleContents(container.indexOfChild(heartRateItem));
            }
        });
        // Setup name
        // TODO
    }

    //*******//
    // DEBUG //
    //*******//

    private void test(){
        DataBase db = new DataBase(this.getApplicationContext());

        File file = db.exportAsCSV(DataBase.RATE_TABLE_NAME, null, null, "HR.csv");
    }

}
