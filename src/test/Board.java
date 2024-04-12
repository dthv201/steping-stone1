package test;
import java.awt.*;
import java.util.*;
import java.util.List;



public class Board {
    static int counter = 0;
    private Tile[][] tiles;
    private final int rows;
    private final int cols;
    private boolean isFirstWordPlaced;
    private Map<Position, BonusType> bonusTiles;
    private static Board instance = null;
    private HashSet<String> uniqueWords; // Keep track of unique words placed on the board

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
        this.uniqueWords = new HashSet<>();
    }

    // No-argument constructor defaults to 15x15
    public Board() {
        this(15, 15); // Calls the constructor with specified dimensions
    }

    // Initialize Double Letter (DL) score tiles in a pattern
    public void initializeDoubleLetterPositions() {
        int[][] dlPositions = {
                {0, 3}, {0, 11},
                {2, 6}, {2, 8},
                {3, 0}, {3, 7}, {3, 14},
                {6, 2}, {6, 6}, {6, 8}, {6, 12},
                {7, 3}, {7, 11},
                {8, 2}, {8, 6}, {8, 8}, {8, 12},
                {11, 0}, {11, 7}, {11, 14},
                {12, 6}, {12, 8},
                {14, 3}, {14, 11}
        };

        // Apply Double Letter bonus to the specified positions
        for (int[] pos : dlPositions) {
            bonusTiles.put(new Position(pos[0], pos[1]), BonusType.DOUBLE_LETTER);
        }
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

    boolean dictionaryLegal(Word word) {
        return true;
    }

    private boolean isInBorders(Word w) {
        //if the first tile isn't in the borders
        int row = w.getRow();
        int col = w.getCol();
        if ((row < 0) || (row > 14) || (col < 0) || (col > 14)) {
            return false;
        }
        //checks is the last tile in border
        if (w.isVertical()) {
            //meaning i need to check the vartical componenet
            if (row + w.getTiles().length - 1 <= 14) {
                int finalRow =row + w.getTiles().length - 1;
//                System.out.println(" The row is"+finalRow+"The col is: "+col);
                return true;
            } else {
                int finalRow = row + w.getTiles().length - 1;
//                System.out.println(" The row is "+finalRow+"The col is: "+col+" return false");
                return false;
            }
        } else {
            if (col + w.getTiles().length - 1 <= 14) {
                int finalCol = col + w.getTiles().length - 1;
//                System.out.println(" The row is"+finalCol+"The col is: "+col+ " return false");
                return true;
            } else {
                return false;
            }
        }
    }

    private boolean IsFirstWordTouchCenter(Word w) {
        // The center position is (7, 7) on a 15x15 board
        int centerRow = 7;
        int centerCol = 7;

        if (!isFirstWordPlaced) {
            // Calculate the ending row and column indices of the word
            int endRow = w.getRow() + (w.isVertical() ? w.getTiles().length - 1 : 0);
            int endCol = w.getCol() + (w.isVertical() ? 0 : w.getTiles().length - 1);

            // Check if the word crosses or touches the center tile
            boolean touchesCenterRow = (w.getRow() <= centerRow) && (endRow >= centerRow);
            boolean touchesCenterCol = w.getCol() <= centerCol && endCol >= centerCol;

            // The word must span the center tile in either the row or column, depending on its orientation
            return touchesCenterRow && touchesCenterCol;
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
        //  starting postion  if vartical i want to end in the last els i stay in same row
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
            for (int i = 0; i < w.getTiles().length; i++)
            {
                //i look where do i stand?
                int curr_row = w.getRow() + (w.isVertical() ? i : 0);
                int curr_col = w.getCol() + (w.isVertical() ? 0 : i);
                // i check if it is empty in all 4 directions: (if null returns true meaning if all null around i'll not return yet
                //The problem is fuking right here!!
//                System.out.println(tiles[curr_row][curr_col]);
                if (!isEmpty(curr_row, curr_col) || !isEmpty(curr_row + 1, curr_col) || !isEmpty(curr_row - 1, curr_col) || !isEmpty(curr_row, curr_col + 1) || !isEmpty(curr_row, curr_col - 1))
                {
                    counter +=1;
//                    System.out.println("isBaseOnOtherTile returns true on "+counter);

                    return true;
                }

            }
        }
        //all nulls around me
        return false;
    }
    // In Board class
    public String getCompleteWordString(Word word) {
        StringBuilder fullWord = new StringBuilder();
        int rowStart = word.getRow();
        int colStart = word.getCol();

        // Iterate through the word's length
        for (int i = 0; i < word.getTiles().length; i++) {
            int row = rowStart + (word.isVertical() ? i : 0);
            int col = colStart + (word.isVertical() ? 0 : i);

            // Check if the tile at the current position is a placeholder or null
            if (word.getTiles()[i] == null || "_".equals(word.getTiles()[i].letter))
            {
                // If it's a placeholder, use the letter from the board
                if (tiles[row][col] != null) {
                    fullWord.append(tiles[row][col].letter);
                }
            }
            else {
                // Otherwise, use the letter from the word itself
                fullWord.append(word.getTiles()[i].letter);
            }
        }

        return fullWord.toString();
    }
    private boolean isValidPosition(int row, int col) {
        if ((row < 0) || (row > 14) || (col < 0) || (col > 14)) {
            return false;
        }
        return true;

    }

    //returns list arry of all the new words we created on board
    public ArrayList<Word> getWords(Word placedWord)
    {
        //First i want to add the word itself
        ArrayList<Word> newWords = new ArrayList<Word>();
        newWords.add(placedWord);
        String strPlacedWord = getCompleteWordString(placedWord);
        uniqueWords.add(strPlacedWord);

        int row = placedWord.getRow();
        int col = placedWord.getCol();

        //for each tile in the word i want to check if i created new word
        for(int i = 0; i<placedWord.getTiles().length; i++)
        {
            //search for new word horizotly
            if(placedWord.isVertical()) {
                Word horizontalWord = findNewWord(row+i, col, false);
                if ((horizontalWord != null) && (!uniqueWords.contains(horizontalWord.toString())) && dictionaryLegal(horizontalWord) ) {
                    newWords.add(horizontalWord);
                    System.out.println(getCompleteWordString(horizontalWord));
                    uniqueWords.add(getCompleteWordString(horizontalWord));
                }
            }
            //search varticly
            else {
                Word varticalWord = findNewWord(row, col+i, true);
                if ((varticalWord != null) && (!uniqueWords.contains(varticalWord.toString())) && (dictionaryLegal(varticalWord))) {
                    newWords.add(varticalWord);
                    System.out.println(getCompleteWordString(varticalWord));
                    uniqueWords.add(getCompleteWordString(varticalWord));
                }
            }
        }
        return newWords;

    }
    private Word findNewWord(int row, int col, boolean isVertical) {
        // Directions are inverted because we're looking perpendicularly to the word's orientation
        int[] directions = isVertical ? new int[]{1, 0} : new int[]{0, 1};
        List<Tile> tilesForWord = new ArrayList<>();
        int startRow = row;
        int startCol = col;

        // First check the tile at the current position if it's not part of the main word being placed
        if (tiles[row][col] != null && !"_".equals(tiles[row][col].letter)) {
            tilesForWord.add(tiles[row][col]);
        }

        // Move "backwards" from the starting tile and collect tiles, excluding the start if it's the main word tile
        int r = row - directions[0];
        int c = col - directions[1];
        while (isValidPosition(r, c) && !isEmpty(r, c)) {
            tilesForWord.addFirst(tiles[r][c]); // Add at the beginning of the list
            r -= directions[0];
            c -= directions[1];
        }
        // Update start position to the beginning of the new word
        if (!tilesForWord.isEmpty()) {
            startRow = r + directions[0];
            startCol = c + directions[1];
        }

        // Reset to the starting position and move "forwards", excluding the initial position if it's part of the main word
        r = row + directions[0];
        c = col + directions[1];
        while (isValidPosition(r, c) && !isEmpty(r, c)) {
            tilesForWord.add(tiles[r][c]); // Add at the end of the list
            r += directions[0];
            c += directions[1];
        }

        // Create a word if there's more than one tile collected
        if (tilesForWord.size() > 1) {
            return new Word(tilesForWord.toArray(new Tile[0]), startRow, startCol, isVertical);
        }

        return null; // Return null if no valid word is formed
    }


    //    private Word findNewWord(int row, int col, boolean isVertical) {
//        // Directions are inverted because we're looking perpendicularly to the word's orientation
//        int[] directions = isVertical ? new int[]{1, 0} : new int[]{0, 1};
//        List<Tile> tilesForWord = new ArrayList<>();
//        int startRow = row;
//        int startCol = col;
//
//
//        while (isValidPosition(row - directions[0], col - directions[1]) && !isEmpty(row - directions[0], col - directions[1])) {
//            row -= directions[0];
//            col -= directions[1];
//            tilesForWord.addFirst(tiles[row][col]); // Add at the beginning of the list
//        }
//
//
//        startRow = row;
//        startCol = col;
//
//        tilesForWord.add(tiles[startRow][startCol]);
//
//
//        //setting for moving from middle to end
//        row = startRow + directions[0];
//        col = startCol + directions[1];
//
//        // Move "forwards" from the starting tile and collect tiles
//        while (isValidPosition(row, col) && !isEmpty(row, col)) {
//            tilesForWord.add(tiles[row][col]);
//            row += directions[0];
//            col += directions[1];
//        }
//
//        // Check if the collected tiles form a valid word (more than just a single tile)
//        if (tilesForWord.size() > 1 ) {
//            // Create a new Word from the collected tiles, using the correct starting position and orientation
//            return new Word(tilesForWord.toArray(new Tile[0]), startRow, startCol, !isVertical);
//        }
//
//
//        return null; // Return null if no valid word is formed
//    }
  public boolean boardLegal(Word word)
  {
      //check if the word is in the board borders
    return ( isInBorders(word) && isBaseOnOtherTile(word));

  }
    private int getScore(Word word)
    {
        // i want to canculate the srore meaning that i need to go tile by tile breackdown:
        //I want to check if there is a bonous of word
        int score = 0;
        int wordMulti = 1;
        int[] directions = word.isVertical() ? new int[]{1, 0} : new int[]{0, 1};
        int r = word.getRow() ;
        int c = word.getCol() ;
        BonusType bonus = null;
        Position tilePos;

        //now we gp tile by tile
        for(Tile tile: word.getTiles())
        {
            int letterScore = 0;

            if (tile != null)
            {
                letterScore = tile.score;
                tilePos = new Position(r,c);
                bonus = bonusTiles.get(tilePos);
                if (bonus != null) {
                    switch (bonus) {
                        case DOUBLE_LETTER:
                            letterScore *= 2;
                            break;
                        case TRIPLE_LETTER:
                            letterScore *= 3;
                            break;
                        case DOUBLE_WORD:
                            wordMulti *= 2;
                            break;
                        case TRIPLE_WORD:
                            wordMulti *= 3;
                            break;
                    }
                }
            }
            else
            {
                letterScore = tiles[r][c].score;
            }
            //now i want to see if it is on spesial place:

            r +=  directions[0];
            c +=  directions[1];

            score += letterScore;
        }
        score *= wordMulti;
        return score;


    }
    public void printBoardStatus() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (tiles[i][j] == null) {
                    System.out.print(". "); // Print a dot for empty positions
                } else {
                    System.out.print(tiles[i][j].letter + " "); // Print the letter of the tile
                }
            }
            System.out.println(); // New line after each row
        }
        System.out.println(); // Extra line for readability after the board is printed
    }
    private void addWordToBoard(Word word) {
        int row = word.getRow();
        int col = word.getCol();
        //the tiles of the word itself not on board yet
        for (Tile tile : word.getTiles()) {
            // Determine the actual tile to place at this position (handle placeholders)
            Tile actualTile = tile;
            //if in the word i got there is a null or place holder in the "tile" i check now
            if (tile == null || "_".equals(String.valueOf(tile.letter))) { // Assuming your Tile class has a 'letter' field
                // For a placeholder, use the existing tile on the board
                actualTile = tiles[row][col];
            }

            // Update the board only if there's a tile to place
            if (actualTile != null) {
                tiles[row][col] = actualTile;
            }

            // Move to the next tile position based on word orientation
            if (word.isVertical()) {
                row++;
            } else {
                col++;
            }
        }
    }

    public int tryPlaceWord(Word word)
    {
        if (!boardLegal(word) || !dictionaryLegal(word))
        {
            return 0;
        }

        addWordToBoard(word);
        printBoardStatus();

        int score = 0;
        ArrayList<Word> newWords = new ArrayList<Word>();
        newWords = getWords(word);
        if(uniqueWords.size() == 1)
        {
            isFirstWordPlaced = true;
        }
        for(Word w: newWords)
        {
            score += getScore(w);
        }
        return score;

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

