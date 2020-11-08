package cardgame;

import java.io.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

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
    private Thread[] playerThreads;

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

    private void inputFromUserPackPath() {
        Scanner scanner = new Scanner(System.in);
        String filePath;
        while(true) {
            System.out.print("Please enter the location of pack to load: ");
            filePath = scanner.nextLine();
            File packFile = new File(filePath);
            if (packFile.isFile()) {
                boolean isValid;
                try {
                    isValid = validPackFile(filePath);
                } catch (IOException e) {
                    System.out.println("An error occurred while reading the file.");
                    continue;
                }
                if (isValid) {
                    break;
                } else {
                    System.out.println("The provided file is not valid; either it doesn't have enough values, or" +
                                       " one of the lines is not a positive integer.");
                }
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

        //set pack file and populate pack attribute with cards
        inputFromUserPackPath();

        //initialise decks
        decks = new CardDeck[numberOfPlayers];
        for (int i = 0; i < numberOfPlayers; i++) decks[i] = new CardDeck(i+1);



        //initialise players
        players = new Player[numberOfPlayers];
        for(int i = 0; i < numberOfPlayers; i++) {
            //i is index, actual player number/deck number is i+1
            CardDeck leftDeck = decks[i];
            CardDeck rightDeck = decks[((i+1) % numberOfPlayers)];
            Player player = new Player(i+1,this,leftDeck,rightDeck);
            players[i] = player;
        }

        //split pack into two equal halves; one half for players, other half for decks
        Card[] playerCards = new Card[numberOfPlayers * 4];
        Card[] deckCards   = new Card[numberOfPlayers * 4];

        for(int i = 0; i < numberOfPlayers * 4; i++) {
            playerCards[i] = pack[i];
            deckCards[i]   = pack[i + (numberOfPlayers*4)];
        }

        //deal the cards
        roundRobinDeal(players, playerCards);
        roundRobinDeal(decks, deckCards);

        //make player threads
        playerThreads = new Thread[numberOfPlayers];
        for (int i = 0; i < numberOfPlayers; i++) playerThreads[i] = new Thread(players[i]);


        //start the threads
        for (Thread thread : playerThreads) thread.start();


        //wait on player threads to finish
        for (Thread thread: playerThreads) {
            try {
                thread.join();
            } catch (InterruptedException e) { }
        }

        //make output files for each deck
        for (CardDeck deck : decks) deck.createOutputFile();

        //DEBUG: output winning player number
        System.out.println(winner.get());

    }

    /**
     * Read the pack from the file.
     * If the file isn't valid, return false. Otherwise set the pack attribute to the contents of the file.
     * @return pack of cards
     */
    private boolean validPackFile(String filename) throws IOException {
        pack = new Card[8*numberOfPlayers];
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(filename));
        } catch (FileNotFoundException e) {
            //this can only happen if the the file was deleted
            System.exit(-1);
        }
        int index = 0;
        try {

            while (index < (8 * numberOfPlayers)) {
                String line = reader.readLine();
                if (line == null) {
                    //end of file reached
                    break;
                } else {
                    int value;
                    try {
                        value = Integer.parseInt(line);
                    } catch (NumberFormatException e) {
                        //value is not a number; pack is invalid
                        try {
                            reader.close();
                        } catch (IOException ioe) { }
                        return false;
                    }
                    if (value > 0) {
                        pack[index] = new CardGame.Card(value);
                    } else {
                        //negative/zero value integer; pack is invalid
                        try {
                            reader.close();
                        } catch (IOException ioe) { }
                        return false;
                    }
                }
                index++;
            }

        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                //ignore exception on close
            }
        }
        //if index is not 8n then the pack file had too few values
        return index == (8 * numberOfPlayers);
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
    private static void roundRobinDeal(CardReceiver[] cardReceivers, Card[] pack) {
        int index = 0;
        for (int i=0; i<4; i++){
            for(CardReceiver receiver : cardReceivers){
                receiver.appendCard(pack[index]);
                index++;
            }
        }
    }

    /**
     * Notify all waiting players
     */
    private void notifyAllPlayers() {

    }
}
