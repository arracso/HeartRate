package edu.udg.exit.heartrate.Utils.Actions;

/**
 * Action that doesn't expects a result.
 */
public abstract class ActionWithoutResponse extends Action {
    @Override
    public boolean expectsResult() {
        return false;
    }
}
