package edu.udg.exit.heartrate.Devices.Actions;

/**
 * Action that only waits for a result when "expectsResult" is true.
 */
public abstract class ActionWithConditionalResponse extends Action {
    protected boolean expectsResult = false;

    @Override
    public boolean expectsResult() {
        return expectsResult;
    }
}
