package cardgame;

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
    private Boolean hasWon = false;


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
                String fh = handToString(favouredHand);
                String ufh = handToString(unfavouredHand);
                CardGame.Card drawnCard = leftDeck.takeCard();
                appendCard(drawnCard);
                int index = rand.nextInt(unfavouredHand.size());
                CardGame.Card discardCard = unfavouredHand.remove(index);
                rightDeck.addCard(discardCard);
                markAction(drawnCard, discardCard, fh, ufh);
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

    private void markAction(CardGame.Card drawn, CardGame.Card discarded, String fHand, String ufHand) {
        //temp debug
        System.out.println("player " + playerNumber + "\n\thand favoured: "+fHand+"\n\thand unfavoured: "+ufHand+
                           "\n\tdraws a " + drawn.getNumber() +
                           " from deck " + leftDeck.getDeckNumber() + "\n\t" +
                           "discards a " + discarded.getNumber() + " to deck " + rightDeck.getDeckNumber() + "\n\t");

    }

    private static String handToString(ArrayList<CardGame.Card> hand) {
        String handString = "";
        for (CardGame.Card card : hand) {
            handString += card.getNumber() + " ";
        }
        return handString;
    }

    private void winAndExit() {
        hasWon = true;
        gameOver = true;

    }

    private void loseAndExit() {
        gameOver = true;
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
