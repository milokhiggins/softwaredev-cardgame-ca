package cardgame;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

/**
 * Player class (something something something)
 *
 * @version 1.1
 * @author SN690024245, SN69********
 */
public class Player implements Runnable, CardReceiver {

    private ArrayList<CardGame.Card> favouredHand = new ArrayList<>();
    private ArrayList<CardGame.Card> unfavouredHand = new ArrayList<>();
    private int playerNumber;
    private CardDeckInterface leftDeck;
    private CardDeckInterface rightDeck;
    private CardGame game;
    private Random rand = new Random();
    private Boolean gameOver = false;
    private ArrayList<String> log = new ArrayList<>();


    /**
     * Constructs a player with the given number.
     * @param number    player number
     * @param leftDeck  deck to the left of the player
     * @param rightDeck deck to the right of the player
     */
    public Player(int number, CardGame game, CardDeckInterface leftDeck, CardDeckInterface rightDeck) {
        this.leftDeck = leftDeck;
        this.rightDeck = rightDeck;
        this.playerNumber = number;
        this.game = game;
    }

    /**
     * Run the player thread.
     */
    public void run() {
        log.add("Player "+ playerNumber + " initial hand: " + handToString());
        while (!gameOver){
            if (checkIfWon()){
                boolean success = game.winner.compareAndSet(0, playerNumber);
                if (success){
                    winAndExit();
                }else{
                    loseAndExit();
                }
                break;
            } else if(game.winner.get() != 0){
                loseAndExit();
                break;
            }
            if (leftDeck.isNotEmpty()) {
                CardGame.Card drawnCard = leftDeck.takeCard();
                appendCard(drawnCard);
                int index = rand.nextInt(unfavouredHand.size());
                CardGame.Card discardCard = unfavouredHand.remove(index);
                rightDeck.addCard(discardCard);
                markAction(drawnCard, discardCard);
            }else{
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

    private void markAction(CardGame.Card drawn, CardGame.Card discarded) {
       log.add(String.format("Player %d draws a %d from deck %d", playerNumber, drawn.getNumber(), leftDeck.getDeckNumber()));
       log.add(String.format("Player %d discards a %d to deck %d", playerNumber, discarded.getNumber(), rightDeck.getDeckNumber()));
       log.add(String.format("Player %d current hand: %s", playerNumber, handToString()));
    }

    private String handToString() {
        String handString = "";
        for (CardGame.Card card : favouredHand) {
            handString += card.getNumber() + " ";
        }
        for (CardGame.Card card : unfavouredHand) {
            handString += card.getNumber() + " ";
        }
        return handString;
    }

    private void winAndExit() {
        gameOver = true;
        log.add(String.format("Player %d wins \nPlayer %d Exits\nPlayer %d final hand %s",
                              playerNumber, playerNumber, playerNumber, handToString()));
        createLog();
    }

    private void loseAndExit() {
        gameOver = true;
        log.add(String.format("Player %d has informed player %d that player %d has won",
                              game.winner.get(), playerNumber, game.winner.get()));
        log.add(String.format("Player %d Exits\nPlayer %d final hand %s",
                              playerNumber, playerNumber, handToString()));
        createLog();
    }

    private void createLog() {
        FileWriter myWriter = null;
        try {
            myWriter = new FileWriter("player" + playerNumber + "_output.txt");
            String newline = System.lineSeparator();
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
     * Receive a card.
     * @param card card to add
     */
    public void appendCard(CardGame.Card card) {
        int cardValue = card.getNumber();
        if (cardValue == playerNumber) {
            favouredHand.add(card);
        } else {
            unfavouredHand.add(card);
        }
    }

    /**
     * Check if the player has won.
     *
     * If a player has four cards of the same denomination the have a winning
     * hand.
     * @return true if player has won the game, false otherwise.
     */
    private Boolean checkIfWon() {
        if (favouredHand.size() == 4){
            // Only cards of the same value are in the favoured hand.
            // Having four cards in the favoured list must be a winning hand.
            return true;
            // Checks if all the cards in the unfavoured hand are the same.
        } else if(unfavouredHand.size()==4) {
            int value = unfavouredHand.get(0).getNumber();
            for (CardGame.Card i : unfavouredHand) {
                if (i.getNumber() != value) {
                    return false;
                }
            }
            return true;
        // If both hands have less than four cards it is not possible to win.
        } else {return false;}
    }


}
