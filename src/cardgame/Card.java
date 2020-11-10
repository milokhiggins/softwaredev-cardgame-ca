package cardgame;

import java.util.Objects;

/**
 * Represents a card
 */
public class Card {
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
