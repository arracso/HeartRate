package edu.udg.exit.heartrate.Utils.Actions;

/**
 * Action that expects a result.
 */
public abstract class ActionWithResponse extends Action {
    @Override
    public boolean expectsResult() {
        return true;
    }
}
