package cardgame;

/**
 * GameRunnerInterface describes the desired behaviour of GameRunner
 */
public interface GameRunnerInterface {

    /**
     * Get the value of the flag which indicates which player (if any) has won
     * @return value of winner flag
     */
    int getWinner();

    /**
     * Set the winner flag only if it is equal to a given value.
     * @param expected  value to compare against
     * @param value     value to set the flag too
     * @return true if the value was set; flag otherwise
     */
    boolean compareAndSetWinner(int expected, int value);

    /**
     * Stops players waiting to pick up continuing to run when the game is finished.
     * Is called by winning player.
     */
    void notifyAllPlayers();
}
