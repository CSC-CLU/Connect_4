/**
 * Connect 4 player class. This is used to allow multiple child classes to be created
 * and used without having to modify the game engine to switch players.
 * <br><h2>Directions for use:</h2><br>
 * The constructors should only be used to set the name of the player or
 * to initialize any variables that you add to your player class.
 * <br>
 * The new game function is used to single that a new game is beginning. Use this to set up your
 * player for a new game. The firstMove parameter is used to indicate if you have the first or
 * second move.
 * <br>
 * The play function is called to get your player's next move. It provides a copy of the
 * current state of the game board (it is a copy so don't worry about modifying it) and
 * expects your players move for the return value (0-6 for the seven columns of the board).
 * <br>
 * There are four functions (duplicateBoard, dropPiece, checkForWinner, checkForTie) that are
 * included in the parent player class to aid in player creation. There is no need to overwrite
 * these functions. They are present to help eliminate code that will be common to most players.
 *
 * @author Eric Heinke
 * @version 2023-9-19 (Sep 19, 2023)
 */
public abstract class Player {
    /** Name of the player */
    protected String name;
    /** Color of the players piece */
    protected final char myColor;
    /** Color of the opponents piece */
    protected final char opponentColor;
    /** Character used for an empty space */
    protected final char emptySpace;

    public Player(char myColor, char opponentColor, char emptySpace) {
        this.name = "Unknown Player";
        this.myColor = myColor;
        this.opponentColor = opponentColor;
        this.emptySpace = emptySpace;
    }

    public Player(char myColor, char opponentColor, char emptySpace, String name) {
        this.myColor = myColor;
        this.opponentColor = opponentColor;
        this.emptySpace = emptySpace;
        this.name = name;
    }

    /**
     * Set up player for a new game
     */
    public abstract void newGame(boolean firstMove);

    /**
     * Determine the next move to be played
     * @param board Current state of the board
     * @return position to place piece
     */
    public abstract int play(char[][] board);

    /**
     * Get the name of the player.
     * @return Player's name
     */
    public String getName() {
        return this.name;
    }

    @Override
    public String toString() {
        return "Player: " + this.name;
    }

    /**
     * Create an exact copy of a game board
     * @param gameBoard Board to copy
     * @return Copy of the board
     */
    protected char[][] duplicateBoard(char[][] gameBoard) {
        return Connect4.duplicateBoard(gameBoard);
    }

    /**
     * Drop a piece onto a simulated board
     * @param gameBoard Game board
     * @param col Column to drop piece
     * @param color Color of piece being dropped
     * @return Whether the piece was able to be dropped
     */
    protected boolean dropPiece(char[][] gameBoard, int col, char color) {
        for (int row = gameBoard.length - 1; row >= 0; row--) {
            if (gameBoard[row][col] == emptySpace) {
                gameBoard[row][col] = color;
                return true;
            }
        }
        return false;
    }

    /**
     * Check to see if there is a winner.
     * @param gameBoard Game board to check
     * @return Winner's piece color or EMPTY_SPACE for no win
     */
    protected char checkForWinner(char[][] gameBoard) {
        return Connect4.checkForWinner(gameBoard);
    }

    /**
     * Check if there is a tie
     * @param gameBoard Game board to check
     * @return if there is a tie
     */
    protected boolean checkForTie(char[][] gameBoard) {
        return Connect4.tie(gameBoard);
    }
}

/**
 * A player that places a piece randomly.
 *
 * @author Eric Heinke
 * @version 2023-9-17 (Sep 17, 2023)
 */
class PlayerRandom extends Player {
    public PlayerRandom(char myColor, char opponentColor, char emptySpace) {
        super(myColor, opponentColor, emptySpace);
        this.name = "Random";
    }

    @Override
    public void newGame(boolean firstMove) { }

    @Override
    public int play(char[][] board) {
        return (int)(Math.random()*7);
    }
}
