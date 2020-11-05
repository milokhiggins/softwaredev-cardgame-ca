package cardgame;

import java.util.Stack;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Card game class etc etc
 *
 * @version 1.1
 * @author SN690024245, SN680046138
 */
public class CardGame {

    private int n;
    public AtomicInteger winner =  new AtomicInteger(0);

    /**
     * Executable main method
     * @param args command line arguments
     */
    public static void main(String[] args) {
        Stack<Card> pack = new Stack<>();

    }

    /**
     *
     */
    static class Card {
        private int number;
        public Card(int num) {
            this.number = num;
        }

        /**
         * Get the value of the card
         * @return the card's value
         */
        public int getNumber() {
            return this.number;
        }

        /**
         * Test equality with another object
         * @param o Object to compare against
         * @return true if the object is equal, false otherwise
         */
        @Override
        public boolean equals(Object o) {
            if (o == null) {
                return false;
            }
            if (this.getClass() != o.getClass()) {
                return false;
            }
            Card other = (Card) o;

            return Objects.equals(this.number, other.getNumber());
        }
    }

    /**
     * Deal cards using round-robin style deal
     * @param pack          array of cards to deal
     * @param cardReceivers array of decks/players
     *
     */
    private static void roundRobinDeal(CardReceiver[] cardReceivers, Stack<Card> pack) {
        for (int i=0; i<4; i++){
            for(CardReceiver receiver : cardReceivers){
                receiver.appendCard(pack.pop());
            }
        }
    }
}
