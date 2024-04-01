package test;
import java.util.*;


public class Board {
    private Tile[][] tiles;
    private final int rows;
    private final int cols;
    private boolean isFirstWordPlaced;
    private Map<Position, BonusType> bonusTiles;
    private static Board instance = null;

    public enum BonusType {
        DOUBLE_LETTER,
        TRIPLE_LETTER,
        DOUBLE_WORD,
        TRIPLE_WORD
    }

    public class Position {
        private final int rows;
        private final int cols;

        public Position(int rows, int cols) {
            this.rows = rows;
            this.cols = cols;
        }

        public int getRows() {
            return rows;
        }

        public int getCols() {
            return cols;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Position position = (Position) o;
            return rows == position.rows && cols == position.cols;
        }

        @Override
        public int hashCode() {
            return Objects.hash(rows, cols);
        }


    }

    // Constructor with specified dimensions
    public Board(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.tiles = new Tile[this.rows][this.cols];
        this.bonusTiles = new HashMap<>();
        isFirstWordPlaced = false;
        initializeBonusTiles();
    }

    // No-argument constructor defaults to 15x15
    public Board() {
        this(15, 15); // Calls the constructor with specified dimensions
    }

    // Initialize Double Letter (DL) score tiles in a pattern
    public void initializeDoubleLetterPositions() {
        // A pattern that places DL tiles in a ring around the center
        int[] offsets = {2, 3, 4, 5, 6, 7, 8, 9, 10, 11}; // Offset from the center and corners

        for (int offset : offsets) {
            // Horizontal positions relative to the center
            bonusTiles.put(new Position(7 - offset, 7), BonusType.DOUBLE_LETTER);
            bonusTiles.put(new Position(7 + offset, 7), BonusType.DOUBLE_LETTER);

            // Vertical positions relative to the center
            bonusTiles.put(new Position(7, 7 - offset), BonusType.DOUBLE_LETTER);
            bonusTiles.put(new Position(7, 7 + offset), BonusType.DOUBLE_LETTER);
        }

        // Additional DL tiles at strategic positions
        bonusTiles.put(new Position(0, 3), BonusType.DOUBLE_LETTER);
        bonusTiles.put(new Position(0, 11), BonusType.DOUBLE_LETTER);
        bonusTiles.put(new Position(3, 0), BonusType.DOUBLE_LETTER);
        bonusTiles.put(new Position(3, 14), BonusType.DOUBLE_LETTER);
        bonusTiles.put(new Position(11, 0), BonusType.DOUBLE_LETTER);
        bonusTiles.put(new Position(11, 14), BonusType.DOUBLE_LETTER);
        bonusTiles.put(new Position(14, 3), BonusType.DOUBLE_LETTER);
        bonusTiles.put(new Position(14, 11), BonusType.DOUBLE_LETTER);
    }

    public void initializeTriple_word() {
        // Directly set TW tiles for the corners
        bonusTiles.put(new Position(0, 0), BonusType.TRIPLE_WORD);
        bonusTiles.put(new Position(0, 14), BonusType.TRIPLE_WORD);
        bonusTiles.put(new Position(14, 0), BonusType.TRIPLE_WORD);
        bonusTiles.put(new Position(14, 14), BonusType.TRIPLE_WORD);

        // Set TW tiles for the midpoints of each edge
        bonusTiles.put(new Position(0, 7), BonusType.TRIPLE_WORD);
        bonusTiles.put(new Position(7, 0), BonusType.TRIPLE_WORD);
        bonusTiles.put(new Position(7, 14), BonusType.TRIPLE_WORD);
        bonusTiles.put(new Position(14, 7), BonusType.TRIPLE_WORD);
    }

    public void initializeDoble_word() {

        // Double Word Score
        int[] dwInnerPositions = {1, 2, 3, 4}; // Positions for the inner square
        for (int pos : dwInnerPositions) {
            bonusTiles.put(new Position(pos, pos), BonusType.DOUBLE_WORD);
            bonusTiles.put(new Position(pos, 14 - pos), BonusType.DOUBLE_WORD);
            bonusTiles.put(new Position(14 - pos, pos), BonusType.DOUBLE_WORD);
            bonusTiles.put(new Position(14 - pos, 14 - pos), BonusType.DOUBLE_WORD);
        }

        // Center tile, often starts play or has a star, could be considered a DW
        bonusTiles.put(new Position(7, 7), BonusType.DOUBLE_WORD);
    }

    // Initialize Triple Letter (TL) score tiles in a pattern
    public void initializeTripleLetterPositions() {
        // Select positions for TL tiles that encourage strategic play across the board
        int[][] tlPositions = {
                {1, 5}, {1, 9}, // Near the top edge
                {5, 1}, {5, 5}, {5, 9}, {5, 13}, // Column 5, varying rows
                {9, 1}, {9, 5}, {9, 9}, {9, 13}, // Column 9, varying rows
                {13, 5}, {13, 9}, // Near the bottom edge
        };

        for (int[] pos : tlPositions) {
            bonusTiles.put(new Position(pos[0], pos[1]), BonusType.TRIPLE_LETTER);
        }

        // Add more TL tiles at symmetric positions to balance the board
        // Assuming symmetry and strategic placement are desired
        int[][] additionalTlPositions = {
                {0, 6}, {0, 8}, // Top edge, slightly inward
                {6, 0}, {6, 14}, // Left edge, middle section
                {8, 0}, {8, 14}, // Right edge, middle section
                {14, 6}, {14, 8}, // Bottom edge, slightly inward
        };

        for (int[] pos : additionalTlPositions) {
            bonusTiles.put(new Position(pos[0], pos[1]), BonusType.TRIPLE_LETTER);
        }
    }

    private void initializeBonusTiles() {

        initializeTriple_word();
        initializeDoble_word();
        initializeDoubleLetterPositions();
        initializeTripleLetterPositions();
    }


    public static Board getBoard() {
        if (instance == null) {
            instance = new Board(); // Initialize the instance if it doesn't exist
        }
        return instance; // Return the existing or new instance
    }

    boolean dictionaryLegal() {
        return true;
    }

    private boolean isInBorders(Word w) {
        //if the first tile isn't in the borders
        if ((w.getRow() < 0) || (w.getRow() > 14) || (w.getCol() < 0) || (w.getCol() > 14)) {
            return false;
        }
        //checks is the last tile in border
        if (w.isVertical()) {
            //meaning i need to check the vartical componenet
            if (w.getRow() + w.getTiles().length - 1 <= 14) {
                return true;
            } else {
                return false;
            }
        } else {
            if (w.getCol() + w.getTiles().length - 1 <= 14) {
                return true;
            } else {
                return false;
            }
        }
    }

    private boolean IsFirstWordTouchCenter(Word w) {
        //I know for sure thet there is no other word on bord i must go in the center
        if (w.isVertical()) {
            //meaning i need to check the vartical componenet
            if (w.getRow() <= 7) {
                if (w.getRow() == 7) {
                    return true;
                } else if (w.getRow() + w.getTiles().length - 1 < 7) {
                    return true;
                }
                return false;
            }
        }
        //meaning horizontal
        else {
            if (w.getCol() == 7) {
                return true;
            } else if (w.getCol() + w.getTiles().length - 1 < 7) {
                return true;
            }

        }
        return false;
    }

    private boolean isEmpty(int row, int col) {
        // Check boundaries first to avoid IndexOutOfBoundsException
        if (row < 0 || row >= rows || col < 0 || col >= cols) {
            return true; // Out of bounds treated as "empty"
        }
        return tiles[row][col] == null; // True if the spot is empty
    }

    private boolean isBaseOnOtherTile(Word w) {

        int wordEndRow = w.getRow() + (w.isVertical() ? w.getTiles().length : 1) - 1;
        int wordEndCol = w.getCol() + (w.isVertical() ? 1 : w.getTiles().length) - 1;

        //checks is it is the first word meaning covering the (7,7) in first try
        if (!this.isFirstWordPlaced) {
//            this.isFirstWordPlaced = true;
            return IsFirstWordTouchCenter(w);
        }
        //checks if based on another tile meaning hofef or zamood
        else {
            // I want to check aroud each letter is there somthing around it on board?
            for (int i = 0; i < w.getTiles().length; i++) {
                //i look where do i stand?
                int curr_row = w.getRow() + (w.isVertical() ? i : 0);
                int curr_col = w.getCol() + (w.isVertical() ? 0 : i);
                // i check if it is empty in all 4 directions: (if null returns true meaning if all null around i'll not return yet
                if (!isEmpty(curr_row, curr_col) || !isEmpty(curr_row + 1, curr_col) || !isEmpty(curr_row - 1, curr_col)
                        || !isEmpty(curr_row, curr_col + 1) || !isEmpty(curr_row, curr_col - 1))
                    return true;
            }
        }
        return false;
    }

  public boolean boardLegal(Word word)
  {
      //check if the word is in the board borders
    return ( isInBorders(word) && isBaseOnOtherTile(word) && isBaseOnOtherTile(word));

  }

    public Tile[][] getTiles()
    {
        Tile[][] copyT = new Tile[rows][cols];
        for(int i=0 ; i < rows;i++)
        {
            //to the copyT copy each row from beging to end
            System.arraycopy(tiles[i],0, copyT[0], 0, tiles[i].length);
        }
        return  copyT;
    }

}

