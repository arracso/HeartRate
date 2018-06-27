package edu.udg.exit.heartrate.Interfaces;

import java.util.Date;

public interface IMeasureService {

    /////////////
    // Methods //
    /////////////

    IMeasureView getMeasureView();

    /**
     * Sets the view that will be used to show the measurement.
     * @param view - Measure view to be set.
     */
    void setMeasureView(IMeasureView view);

    /**
     * Unset measure view.
     */
    void unsetMeasureView();

    void startMeasure();

    void stopMeasure();

    void setMeasure(Date date, Integer measure);

}
