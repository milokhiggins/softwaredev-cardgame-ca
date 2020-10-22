package cardgame;

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
    public static void main(String[] args) {

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
    private static void roundRobinDeal(CardReceiver[] cardReceivers, Card[] pack) {

    }

}
