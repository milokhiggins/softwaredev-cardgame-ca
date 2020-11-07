package cardgame;

import java.io.FileNotFoundException;
import java.util.InputMismatchException;
import java.util.Stack;
import java.util.Objects;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;

/**
 * Card game class etc etc
 *
 * @version 1.1
 * @author SN690024245, SN680046138
 */
public class CardGame {

    private int numberOfPlayers;
    public AtomicInteger winner =  new AtomicInteger(0);
    private Card[] pack;
    private Player[] players;
    private CardDeck[] decks;

    /**
     * Executable main method
     * @param args command line arguments
     */
    public static void main(String[] args) {
        CardGame gameInstance = new CardGame();
        gameInstance.run();
    }

    /**
     * Get the number of players from the user and validate their input
     * Keep asking until they return something valid.
     * @return number of players
     */
    private static int inputFromUserNumberOfPlayers() {
        Scanner scanner = new Scanner(System.in);
        while(true) {
            System.out.print("Please enter the number of players: ");
            try {
                int number = scanner.nextInt();
                if (number > 1) {
                    return number;
                } else {
                    System.out.println("Number of players must be greater than 1.");
                }
            } catch (InputMismatchException e) {
                scanner.nextLine();
                System.out.println("Number of players must be an integer.");
            }
        }
    }

    private static String inputFromUserPackPath() {
        Scanner scanner = new Scanner(System.in);
        String filename;
        while(true) {
            System.out.print("Please enter the location of pack to load: ");
            filename = scanner.nextLine();
            File packFile = new File(filename);
            if (packFile.isFile()) {
                return filename;
            } else {
                System.out.println("Invalid filename.");
            }
        }
    }


    /**
     * Main code of CardGame
     */
    private void run() {
        numberOfPlayers = inputFromUserNumberOfPlayers();

    }

    /**
     * Ask user to input number of players and location of pack file
     * Then read the pack file and return the cards
     * @return pack of cards
     */
    private Card[] getPack(String filename) {
        Card[] pack = new Card[8*numberOfPlayers];
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));
        } catch (FileNotFoundException e) {
            //this can only happen if the the file was deleted
            System.exit(-1);
        }


        return pack;
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

    /**
     * Notify all waiting players
     */
    private void notifyAllPlayers() {

    }
}
