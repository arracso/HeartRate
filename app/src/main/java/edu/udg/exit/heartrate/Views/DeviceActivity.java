package edu.udg.exit.heartrate.Views;

import android.app.Activity;
import android.os.Bundle;
import edu.udg.exit.heartrate.R;
import edu.udg.exit.heartrate.Utils.DataBase;

import java.io.File;

public class DeviceActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);
    }

    // DEBUG //

    private void test(){
        DataBase db = new DataBase(this.getApplicationContext());

        File file = db.exportAsCSV(DataBase.RATE_TABLE_NAME, null, null, "HR.csv");
    }
}
