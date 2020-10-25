package cardgame;

import java.util.Stack;


/**
 * Card game class etc etc
 *
 * @version 1.1
 * @author SN690024245, SN680046138
 */
public class CardGame {
    /**
     * Executable main method
     * @param args command line arguments
     */

    private int n;

    public static void main(String[] args) {
        Stack<Card> pack = new Stack<Card>();

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
