package cardgame;

import java.io.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Starts the game and aids in running and completing the game.
 *
 * Asks the user for to enter the number of players and card pack file.
 * Organises the player threads and decks.
 *
 * @version 1.1
 */
public class CardGame {

    private int numberOfPlayers;
    private Card[] pack;
    private Player[] players;
    private CardDeck[] decks;
    // Once a player wins, contains the number belonging to the player who has won
    public AtomicInteger winner =  new AtomicInteger(0);
    private Thread[] playerThreads;

    /**
     * Executable main method
     *
     * Creates an instance of a game and executes the non static run method.
     * @param args command line arguments
     */
    public static void main(String[] args) {
        CardGame gameInstance = new CardGame();
        gameInstance.run();
    }

    /**
     * Gets the number of players from the user.
     *
     * Validates user input, keeps asking For input until  valid input is returned.
     * @return number of players
     */
    private static int inputFromUserNumberOfPlayers() {

        // Takes input form the user.
        Scanner scanner = new Scanner(System.in);
        while(true) {
            System.out.print("Please enter the number of players: ");
            try {

                int number = scanner.nextInt();

                //Checks that the user has entered a large enough number of players
                if (number > 1) {
                    return number;
                } else {
                    System.out.println("Number of players must be greater than 1.");
                }
            } catch (InputMismatchException e) {
                //Error thrown if the user enters an invalid type.
                scanner.nextLine();
                System.out.println("Number of players must be an integer.");
            }
        }
    }

    /**
     * Gets the file location of the pack from the user.
     *
     * Validates user input, keeps asking For input until  valid input is returned.
     * Uses the validPackFile method to validate file path.
     *
     */
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
     *
     * Asks user for number of players and location of pack using inputFromUser and inputFromUserPackPath methods.
     * Populates card deck and player arrays with objects and deals cards to them using roundRobinDeal method.
     * Starts player threads.
     * Triggers decks to create files when a player wins and prints the winner to the console.
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

        System.out.println("player " + winner.get() + " wins!");

    }

    /**
     * Reads the pack from the file. Sets pack attribute to file contents.
     * If file isn't valid returns false. Otherwise returns true.
     *
     * @return true if the file was valid.
     */
    private boolean validPackFile(String filename) throws IOException {
        //sets the pack array size to the number of needed cards
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
            // While loop condition turns false when needed number of cards have been input.
            //so that too many cards are never added the game's deck.
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
        //if index is not 8n then the pack file had too few values and the pack is not valid.
        return index == (8 * numberOfPlayers);
    }

    /**
     * Represents a card
     */
    static class Card {
        //The card's Number
        private int number;

        /**
         * Constructs card object
         * @param num card's number
         */
        public Card(int num) {
            this.number = num;
        }

        /**
         * Gets the card's value
         * @return the card's number
         */
        public int getNumber() {
            return this.number;
        }

        /**
         * Tests equality with another object
         * @param o Object to compare against
         * @return true if the object is equal, false otherwise
         */
        @Override
        public boolean equals(Object o) {
            //checks that the object is not null
            if (o == null) {
                return false;
            }
            //Checks that the object is the same class
            if (this.getClass() != o.getClass()) {
                return false;
            }
            Card other = (Card) o;

            //checks that numbers match
            return Objects.equals(this.number, other.getNumber());
        }
    }

    /**
     * Deals cards using round-robin style deal
     * @param pack          array of cards to deal
     * @param cardReceivers array of decks or players
     *
     */
    private static void roundRobinDeal(CardReceiver[] cardReceivers, Card[] pack) {
        //index used to traverse cards in pack
        int index = 0;
        for (int i=0; i<4; i++){
            //Below one card is dealt to each card receiver
            for(CardReceiver receiver : cardReceivers){
                receiver.appendCard(pack[index]);
                index++;
            }
        }
    }

    /**
     * Stops players waiting to pick up continuing to run when the game is finished.
     * Is called by winning player.
     */
    public void notifyAllPlayers() {
        //notifies all decks
        for (CardDeck deck : decks) {
            synchronized (deck) {
                deck.notify();
            }
        }
    }
}
