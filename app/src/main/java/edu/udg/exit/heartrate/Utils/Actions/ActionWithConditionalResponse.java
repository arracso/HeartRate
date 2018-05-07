package edu.udg.exit.heartrate.Utils.Actions;

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
