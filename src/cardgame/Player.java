package cardgame;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;

/**
 * Represents player
 *
 * Plays the game
 * @version 1.1
 */
public class Player implements Runnable, CardReceiver {
    private int playerNumber;
    private GameRunner game;
    //Player's hand made up of two arrays:
    private ArrayList<Card> favouredHand = new ArrayList<>();
    private ArrayDeque<Card> unfavouredHand = new ArrayDeque<>();
    private CardDeckInterface leftDeck;
    private CardDeckInterface rightDeck;
    //Flag used to Stop thread from running.
    private Boolean gameOver = false;
    private ArrayList<String> log = new ArrayList<>();


    /**
     * Constructs a player with the given number, left deck and right deck.
     * @param number    player number
     * @param game      GameRunner instance player is associated with
     * @param leftDeck  deck to the left of the player
     * @param rightDeck deck to the right of the player
     */
    public Player(int number, GameRunner game, CardDeckInterface leftDeck, CardDeckInterface rightDeck) {
        this.leftDeck = leftDeck;
        this.rightDeck = rightDeck;
        this.playerNumber = number;
        this.game = game;
    }

    /**
     * Runs the player thread.
     *
     * Checks if player has won or lost and Invokes relevant code. Uses checkIfWon method.
     * Implements player algorithm to pick up and put down cards and invokes actions being added to the log.
     */
    public void run() {
        //Adds initial hand to log.
        log.add("Player "+ playerNumber + " initial hand: " + handToString());

        while (!gameOver){
            //checks player hand To see if won.
            if (checkIfWon()){
                //Checks if the atomic winner is set to 0. if it is sets to player's number to win the game.
                // Returns False if winner variable is no longer 0.
                boolean success = game.winner.compareAndSet(0, playerNumber);
                if (success){
                    winAndExit();
                }else{
                    loseAndExit();
                }
                //Ends the thread
                break;
            //checks if player has lost to another player
            } else if(game.winner.get() != 0){
                loseAndExit();
                break;
            }
            //draws from leftDeck
            if (leftDeck.isNotEmpty()) {
                //Draws card
                Card drawnCard = leftDeck.takeCard();
                appendCard(drawnCard);
                //Discards unfavoured card
                Card discardCard = unfavouredHand.remove();
                rightDeck.addCard(discardCard);
                //Action passed to the log.
                markAction(drawnCard, discardCard);
            }else{
                //If Left deck empty will wait until cards have been added.
                synchronized (leftDeck){
                    try {
                        leftDeck.wait();
                    }catch (InterruptedException e){
                        break;
                    }
                }
            }
        }

    }

    /**
     * Adds player draw and discard actions to log.
     * Uses hand string method to convert the players current hand into a string.
     *
     * @param drawn     Card which player drew
     * @param discarded Card which player discarded
     */
    private void markAction(Card drawn, Card discarded) {
       log.add(String.format("Player %d draws a %d from deck %d", playerNumber, drawn.getNumber(), leftDeck.getDeckNumber()));
       log.add(String.format("Player %d discards a %d to deck %d", playerNumber, discarded.getNumber(), rightDeck.getDeckNumber()));
       log.add(String.format("Player %d current hand: %s", playerNumber, handToString()));
    }

    /**
     * Converts the player's current hand into a string.
     * @return All card numbers in players current favoured and unfavoured hand as a string
     */
    private String handToString() {
        String handString = "";
        for (Card card : favouredHand) {
            handString += card.getNumber() + " ";
        }
        for (Card card : unfavouredHand) {
            handString += card.getNumber() + " ";
        }
        return handString;
    }

    /**
     * Adds final lines detailing win to log, creates log and notifies any waiting players of win.
     * uses create log and hand to string method
     */
    private void winAndExit() {
        gameOver = true;
        //Logs players final hand
        log.add(String.format("Player %d wins \nPlayer %d Exits\nPlayer %d final hand %s",
                              playerNumber, playerNumber, playerNumber, handToString()));
        createLog();
        //Notifys any players waiting on a deck that the game has finished.
        game.notifyAllPlayers();
    }

    /**
     * Adds final lines detailing loss to log and creates log.
     * Uses hand to string method and create log methods.
     */
    private void loseAndExit() {
        gameOver = true;
        //Gets winner player's number and adds to log.
        log.add(String.format("Player %d has informed player %d that player %d has won",
                              game.winner.get(), playerNumber, game.winner.get()));
        log.add(String.format("Player %d Exits\nPlayer %d final hand %s",
                              playerNumber, playerNumber, handToString()));
        createLog();
    }

    /**
     * Creates a log file for player and writes the log to the file.
     * Uses the log Array list.
     */
    private void createLog() {
        FileWriter myWriter = null;
        try {
            myWriter = new FileWriter("player" + playerNumber + "_output.txt");
            //System independent line separator
            String newline = System.lineSeparator();
            //Writes all lines in the log to file.
            for (String line : log) {
                myWriter.write(line + newline);
            }
        }catch (IOException e){
            System.out.print("IOException while trying to make output file.");
            return;
        } finally {
            try {
                myWriter.close();
            }catch(IOException e){}
        }
    }

    /**
     * Receives a card and sorts to either favoured or unfavoured hand.
     * @param card card to add
     */
    public void appendCard(Card card) {
        int cardValue = card.getNumber();
        //Adds card favoured hand if it is the players number.
        if (cardValue == playerNumber) {
            favouredHand.add(card);
        } else {
            unfavouredHand.add(card);
        }
    }

    /**
     * Checks if the player has won.
     *
     * If a player has four cards of the same denomination they have a winning
     * hand.
     * @return true if player has won the game, false otherwise.
     */
    private boolean checkIfWon() {
        if (favouredHand.size() == 4){
            // Only cards of the same value are in the favoured hand.
            // Having four cards in the favoured list must be a winning hand.
            return true;
            // Checks if all the cards in the unfavoured hand are the same.
        } else if(unfavouredHand.size()==4) {
            int value = unfavouredHand.peekFirst().getNumber();
            for (Card i : unfavouredHand) {
                if (i.getNumber() != value) {
                    return false;
                }
            }
            return true;
        // If both hands have less than four cards it is not possible to win.
        } else {return false;}
    }


}
