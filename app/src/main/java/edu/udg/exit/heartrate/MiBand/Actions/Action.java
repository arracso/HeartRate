package edu.udg.exit.heartrate.MiBand.Actions;

/**
 * Class to perform an action controlled by a Manager.
 */
public abstract class Action {
    /**
     * Action to be performed.
     */
    public abstract void run();

    /**
     * Checks if the action performed expects or not a result.
     * @return True if the action performed expectes a result, False otherwise
     */
    public abstract boolean expectsResult();
}
