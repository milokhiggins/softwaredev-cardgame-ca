package cardgame;

/**
 * Executable class which starts the game
 *
 * @version 1.1
 */
public class CardGame {

    /**
     * Executable main method
     *
     * Creates an instance of a game and executes the run method.
     * @param args command line arguments
     */
    public static void main(String[] args) {
        GameRunner game = new GameRunner();
        game.run();
    }
}
