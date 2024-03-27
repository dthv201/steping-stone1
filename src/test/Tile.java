package test;


import java.util.Arrays;
import java.util.Objects;
import java.util.Random;
import java.util.List;
import java.util.ArrayList;

public class Tile {
    public final char letter;
    public final int score;

    private Tile(char letter, int score) {
        this.letter = letter;
        this.score = score;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tile tile = (Tile) o;
        return letter == tile.letter && score == tile.score;
    }

    @Override
    public int hashCode() {
        return Objects.hash(letter, score);
    }
    public static class Bag{
        private int[] bagQuantities;
        private Tile[] tiles;
        private List<Integer> availablesIndexes;
        private static final int[] MAXED_TILES_VALUES = new int[]{9, 2, 2, 4, 12, 2, 3, 2, 9, 1, 1, 4, 2, 6, 8, 2, 1, 6, 4, 6, 4, 2, 2, 1, 2, 1};
        private static Bag instance = null;

        private Bag() {
            tiles = new Tile[26];
//            bagQuantities = new int[26];
            bagQuantities = new int[]{9, 2, 2, 4, 12, 2, 3, 2, 9, 1, 1, 4, 2, 6, 8, 2, 1, 6, 4, 6, 4, 2, 2, 1, 2, 1};
            availablesIndexes = new ArrayList<>();
            for (char letter = 'A'; letter <= 'Z'; letter++) {
                int index = letter - 'A';
                int score = getScoreForLetter(letter); // Assuming a method that returns the score for a letter
                tiles[letter - 'A'] = new Tile(letter, score);
                availablesIndexes.add(index);
            }
        }
        private int getScoreForLetter(char letter) {
            // Example scoring logic (simplified)
            switch(letter) {
                case 'A': case 'E': case 'I': case 'O': case 'U': case 'L': case 'N': case 'S': case 'T': case 'R':
                    return 1;
                case 'D': case 'G':
                    return 2;
                case 'B': case 'C': case 'M': case 'P':
                    return 3;
                case 'F': case 'Y': case 'W': case 'V':
                    return 4;
                case 'K':
                    return 5;
                case 'J': case 'X':
                    return 8;
                default:
                    return 10;
            }
        }
        //return a random tile from the array
        public Tile getRand()
        {
            if (availablesIndexes.isEmpty()) {
                return null; // The bag is empty
            }
            Random random = new Random();
            // Pick a random index from availablesIndexes
            int randomIndex = availablesIndexes.get(random.nextInt(availablesIndexes.size()));
            // Decrement the quantity of the selected tile
            bagQuantities[randomIndex]--;
            if (bagQuantities[randomIndex] == 0) {
                // If the tile's quantity reaches 0, remove it from available indices
                availablesIndexes.remove(Integer.valueOf(randomIndex));
            }
           Tile randomTile =  tiles[randomIndex];
            return randomTile;

        }
        private Boolean isBagEmpty()
        {
            return availablesIndexes.isEmpty();
        }

        public Tile getTile(char someChar) {

            if ((someChar >='A') && (someChar <= 'Z')) {
                int index = someChar - 'A';
                if (bagQuantities[index] > 0){
                    bagQuantities[index]--;
                    return tiles[index];
                }
                else{
                    return null;
                }
            }
            else
            {
                return null;
            }
        }
        public void put(Tile tile) {
            char charTile = tile.letter;
            int index = charTile - 'A';

            // First, check if adding the tile back is allowed
            if (!isMaxedTile(charTile)) {
                // Increment the tile quantity since it's within legal limits
                bagQuantities[index]++;

                // If this tile type was previously depleted, reintroduce it to the list of available tiles
                if (!availablesIndexes.contains(Integer.valueOf(index))) {
                    availablesIndexes.add(index);
                }
            }

        }

        private boolean isMaxedTile(char c) {
            int index = c - 'A';
            return bagQuantities[index] >= MAXED_TILES_VALUES[index]; // Use >= to include the current quantity in the check
        }

        public int size()
        {
            int numTilesInBag = 0;
            for (char letter = 'A'; letter <= 'Z'; letter++) {
                int index = letter - 'A';
                numTilesInBag += bagQuantities[index];
            }
            return numTilesInBag;
        }
        public int[] getQuantities()
        {
            return Arrays.copyOf(bagQuantities, bagQuantities.length);
        }

        public static Bag getBag() {
            if (instance == null) {
                instance = new Bag(); // Initialize the instance if it doesn't exist
            }
            return instance; // Return the existing or new instance
        }
    }
}
