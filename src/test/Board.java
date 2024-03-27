package test;


public class Board {
private Tile[][] tiles;
private final int rows;
private final int cols;
private static Board instance = null;

    public Board() {
        this.rows = 15;
        this.cols = 15;
        this.tiles = new Tile[rows][cols];
    }

    public static Board getBoard() {
        if (instance == null) {
            instance = new Board(); // Initialize the instance if it doesn't exist
        }
        return instance; // Return the existing or new instance
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
    public boolean boardLegal(Word word)
    {

    }
}
