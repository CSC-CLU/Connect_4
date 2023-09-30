import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

import java.lang.reflect.InvocationTargetException;
import java.util.Random;

/**
 * This class is the main game engine. It handles all game logic and controls the graphics.
 *
 * @author Eric Heinke
 * @version 2023-9-29 (Sep 29, 2023)
 */
public class Connect4 extends World {
    private static final int FIRST_COLUMN_X = 176; // to place checkers in the correct location
    private static final int FIRST_COLUMN_Y = 72; // to place checkers in the correct location
    private static final int SPACE_BETWEEN_COLUMNS = 41; // to place checkers in the correct location
    private static final int DELAY_ON_WIN = 250; // delay after player wins
    private static final boolean COUNT_TIES = true; // Do ties get included in statistics
    /** Used to allow a piece to fall completely before the next piece is played. */
    private int ACT_COUNTER = 0;
    private static final char EMPTY_SPACE = ' ';
    private static final char BLACK_PIECE = 'b';
    private static final char RED_PIECE = 'r';

    /** Connect 4 game board */
    private final char[][] gameBoard;

    /** Current players turn (Black = BLACK_PIECE, Red = RED_PIECE) */
    private int playerTurn;
    private int firstPlayerTurn;

    /** Black player */
    private final Player playerBlack;

    /** Black player */
    private final Player playerRed;

    private int playerBlackWinTotal;
    private int playerRedWinTotal;
    private int tieCounter;
    private static final Random random = new Random();;

    /**
     * Constructs board and initializes players to predefined players
     *
     */
    public Connect4() throws ClassNotFoundException {
        this(Class.forName("PlayerEricHS"), Class.forName("PlayerRandom"));
    }

    /**
     * Constructs board and initializes players with provided players
     *
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public Connect4(Class<?> black, Class<?> red) {
        // Create a new world with 600x400 cells with a cell size of 1x1 pixels.
        super(600, 400, 1);
        gameBoard = new char[6][7];
        firstPlayerTurn = random.nextInt(2)+1;   //randomize who starts the game

        try {
            playerBlack = (Player) black.getConstructor(char.class, char.class, char.class)
                    .newInstance(BLACK_PIECE, RED_PIECE, EMPTY_SPACE);
            playerRed = (Player) red.getConstructor(char.class, char.class, char.class)
                    .newInstance(RED_PIECE, BLACK_PIECE, EMPTY_SPACE);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }

        showText(playerBlack.getName(),70,100);
        showText(playerRed.getName(),getWidth()-70,100);

        playerBlackWinTotal = 0;
        playerRedWinTotal = 0;
        tieCounter = 0;

        addObject(new Board(),300,200); //adds game board

        newGame();
    }

    /**
     * Perform the next action. This function is called by GreenFoot.
     */
    public void act() {
        if (ACT_COUNTER < 1) {
            char winner = checkForWinner();
            if (winner != EMPTY_SPACE || tie()) {
                showPlayerWin(winner);
                Greenfoot.delay(DELAY_ON_WIN); //allows the highlighted winner more time to be seen
                newGame();
            } else {
                play();
            }
        }
        ACT_COUNTER--;
    }

    /**
     * Start a new game. This includes clearing the board and resetting the players
     */
    private void newGame() {
        showText("Games played : " + (playerBlackWinTotal + playerRedWinTotal + tieCounter),
                getWidth()/2,15); //shows number of games

        //clears all checkers and rings
        removeObjects(getObjects(Checker.class));
        removeObjects(getObjects(Ring.class));
        removeObjects(getObjects(BigRing.class));

        // swap who plays first
        firstPlayerTurn = firstPlayerTurn == BLACK_PIECE ? RED_PIECE : BLACK_PIECE;
        playerTurn = firstPlayerTurn;

        // clear the game board
        for(int row = 0; row < gameBoard.length; row++){
            for(int col = 0; col < gameBoard[0].length; col++){
                gameBoard[row][col] = EMPTY_SPACE;
            }
        }

        // Let players know that it is a new game
        playerBlack.newGame(firstPlayerTurn == BLACK_PIECE);
        playerRed.newGame(firstPlayerTurn == RED_PIECE);
    }

    /**
     * Have the next player make their move
     * @TODO make it so that there is a timeout on how long a player can take for their turn
     */
    private void play() {
        GreenfootImage image;
        int column = -1;
        char color = (char)playerTurn;
        if (playerTurn == BLACK_PIECE) {
            playerTurn = RED_PIECE;
            image = new GreenfootImage("black.png"); //sets image to black checker
            try {
                column = playerBlack.play(duplicateBoard()); // Get player's move
            } catch (Exception ignored) {} // Exceptions are treated as a forfeited turn
        } else {
            playerTurn = BLACK_PIECE;
            image = new GreenfootImage("red.png");  //sets image to red checker
            try {
                column = playerRed.play(duplicateBoard()); // Get player's move
            } catch (Exception ignored) {} // Exceptions are treated as a forfeited turn
        }
        column = dropPiece(column, color);
        addObject(new Checker(column, image, gameBoard, EMPTY_SPACE),
                FIRST_COLUMN_X + (column * SPACE_BETWEEN_COLUMNS), FIRST_COLUMN_Y);
    }

    /**
     * Duplicates the game board to prevent cheating
     * @return Duplicate game board
     */
    private char[][] duplicateBoard() {
        return duplicateBoard(this.gameBoard);
    }

    public static char[][] duplicateBoard(char[][] matrix) {
        return java.util.Arrays.stream(matrix).map(char[]::clone).toArray($ -> matrix.clone());
    }

    /**
     * Drop piece onto the game board
     * @param column Column to place the piece
     * @param color Color of the piece being placed
     * @return Column the piece was dropped in
     */
    private int dropPiece(int column, char color) {
        // If column is invalid or is already full, determine new column
        if (column < 0 || column > gameBoard[0].length || gameBoard[0][column] != EMPTY_SPACE) {
            for (int i = 0; i < 10; i++) {
                column = random.nextInt(7);
                if (gameBoard[0][column] == EMPTY_SPACE) {
                    break;
                }
            }
        }
        // If random placement fails, use the first available column
        if (gameBoard[0][column] != EMPTY_SPACE) {
            for (int i = 0; i < gameBoard[0].length; i++) {
                if (gameBoard[0][column] == EMPTY_SPACE) {
                    column = i;
                    break;
                }
            }
        }

        gameBoard[0][column] = color;

        //drops new piece down the column to bottom
        ACT_COUNTER = 2;
        for(int col = 0; col < gameBoard[0].length; col++)
        {
            for(int row = 0; row < gameBoard.length - 1; row++)
            {
                if(gameBoard[row][col] != EMPTY_SPACE && gameBoard[row + 1][col] == EMPTY_SPACE)
                {
                    gameBoard[row+1][col] = gameBoard[row][col];
                    gameBoard[row][col] = EMPTY_SPACE;
                    ACT_COUNTER++;
                }
                else if(gameBoard[row][col] != EMPTY_SPACE && gameBoard[row + 1][col] != EMPTY_SPACE)
                {
                    break;
                }
            }
        }
        return column;
    }

    /**
     * Check to see if there is a winner.
     * @return Winner's piece color or EMPTY_SPACE for no win
     */
    private char checkForWinner() {
        return checkForWinner(this.gameBoard);
    }

    /**
     * Check to see if there is a winner.
     * @param gameBoard Game board to check
     * @return Winner's piece color or EMPTY_SPACE for no win
     */
    public static char checkForWinner(char[][] gameBoard) {
        // check each row for a winner, starting at bottom
        for (int row = gameBoard.length - 1; row >= 0; row--) {
            char winner = gameBoard[row][3];
            if (winner == EMPTY_SPACE) {
                continue;
            }
            for (int col = 0; col < 4; col++) {
                if (gameBoard[row][col] == winner && gameBoard[row][col+1] == winner &&
                        gameBoard[row][col+2] == winner && gameBoard[row][col+3] == winner) {
                    return winner;
                }
            }
        }

        // check each column for a winner
        for (int col = 0; col < gameBoard[0].length; col++) {
            char winner = gameBoard[2][col];
            if (winner == EMPTY_SPACE) {
                continue;
            }
            for (int row = 2; row >=0; row--) {
                if (gameBoard[row][col] == winner && gameBoard[row+1][col] == winner &&
                        gameBoard[row+2][col] == winner && gameBoard[row+3][col] == winner) {
                    return winner;
                }
            }
        }

        // check for diagonal winner
        for (int row = 2; row >= 0; row--) {
            for (int col = 0; col < 4; col++) {
                char winner = gameBoard[row][col];
                if (winner == EMPTY_SPACE) {
                    continue;
                }
                if (gameBoard[row+1][col+1] == winner && gameBoard[row+2][col+2] == winner && gameBoard[row+3][col+3] == winner) {
                    return winner;
                }
            }
        }
        for (int row = 2; row >= 0; row--) {
            for (int col = gameBoard[0].length - 1; col >= gameBoard[0].length - 4; col--) {
                char winner = gameBoard[row][col];
                if (winner == EMPTY_SPACE) {
                    continue;
                }
                if (gameBoard[row+1][col-1] == winner && gameBoard[row+2][col-2] == winner && gameBoard[row+3][col-3] == winner) {
                    return winner;
                }
            }
        }

        // No winner found
        return EMPTY_SPACE;
    }

    /**
     * Check if there is a tie.
     * @return if there is a tie
     */
    private boolean tie() {
        return tie(this.gameBoard);
    }

    /**
     * Check if there is a tie
     * @param gameBoard Game board to check
     * @return if there is a tie
     */
    public static boolean tie(char[][] gameBoard) {
        //if there is an empty space on the top row of the board, there is not a tie
        for(int i = 0; i< gameBoard[0].length; i++)
        {
            if(gameBoard[0][i] == EMPTY_SPACE)
            {
                return false;
            }
        }
        return true;
    }

    /**
     * Show that a player has won, show the winning four pieces, and update statistics
     * @param player Player that won
     */
    public void showPlayerWin(char player) {
        if (player == BLACK_PIECE) {
            playerBlackWinTotal++;
            addObject(new BigRing(),70,130); //highlights black name
        } else if (player == RED_PIECE) {
            playerRedWinTotal++;
            addObject(new BigRing(),getWidth()-70,130); //highlights black name
        } else {
            tieCounter++;
        }

        int games = playerBlackWinTotal + playerRedWinTotal;
        if (COUNT_TIES) {
            games += tieCounter;
        }

        //calculates and shows all the stats
        showText("Wins: " + playerBlackWinTotal,70,130); //shows the total black wins
        double winPercentageBlack = (Math.round((playerBlackWinTotal *1000f)/games)/10.0);
        showText(winPercentageBlack + "%",70,160); //shows the total black win %

        showText("Wins: " + playerRedWinTotal,getWidth()-70,130); //shows the total red wins
        double winPercentageRed = (Math.round((playerRedWinTotal *1000f)/games)/10.0);
        showText(winPercentageRed + "%",getWidth()-70,160); //shows the total red win %

        if (tieCounter > 0) {
            showText("Ties: " + tieCounter,getWidth()/2,360); //shows number of ties
            if (COUNT_TIES) {
                double tiePercentage = (Math.round((tieCounter *1000f)/games)/10.0);
                showText(tiePercentage + "%",getWidth()/2,380); //shows number of ties
            }
        }

        // check each row for a winner, starting at bottom
        for (int row = gameBoard.length - 1; row >= 0; row--) {
            char winner = gameBoard[row][3];
            if (winner == EMPTY_SPACE) {
                continue;
            }
            for (int col = 0; col < 4; col++) {
                if (gameBoard[row][col] == winner && gameBoard[row][col+1] == winner &&
                        gameBoard[row][col+2] == winner && gameBoard[row][col+3] == winner) {
                    for(int i = 0; i < 4; i++){
                        addObject(new Ring(), FIRST_COLUMN_X +((col+i) * SPACE_BETWEEN_COLUMNS), FIRST_COLUMN_Y +(row+1)*38);
                    }
                    return;
                }
            }
        }

        // check each column for a winner
        for (int col = 0; col < gameBoard[0].length; col++) {
            char winner = gameBoard[2][col];
            if (winner == EMPTY_SPACE) {
                continue;
            }
            for (int row = 2; row >=0; row--) {
                if (gameBoard[row][col] == winner && gameBoard[row+1][col] == winner &&
                        gameBoard[row+2][col] == winner && gameBoard[row+3][col] == winner) {
                    for(int i = 0; i < 4; i++){
                        addObject(new Ring(), FIRST_COLUMN_X +(col * SPACE_BETWEEN_COLUMNS), FIRST_COLUMN_Y +(row+i+1)*38);
                    }
                    return;
                }
            }
        }

        // check for diagonal winner
        for (int row = 2; row >= 0; row--) {
            for (int col = 0; col < 4; col++) {
                char winner = gameBoard[row][col];
                if (winner == EMPTY_SPACE) {
                    continue;
                }
                if (gameBoard[row+1][col+1] == winner && gameBoard[row+2][col+2] == winner && gameBoard[row+3][col+3] == winner) {
                    for (int i = 0; i < 4; i++) {
                        addObject(new Ring(), FIRST_COLUMN_X +((col+i) * SPACE_BETWEEN_COLUMNS), FIRST_COLUMN_Y +(row+i+1)*38);
                    }
                    return;
                }
            }
        }
        for (int row = 2; row >= 0; row--) {
            for (int col = gameBoard[0].length - 1; col >= gameBoard[0].length - 4; col--) {
                char winner = gameBoard[row][col];
                if (winner == EMPTY_SPACE) {
                    continue;
                }
                if (gameBoard[row+1][col-1] == winner && gameBoard[row+2][col-2] == winner && gameBoard[row+3][col-3] == winner) {
                    for (int i = 0; i < 4; i++) {
                        addObject(new Ring(), FIRST_COLUMN_X +((col-i) * SPACE_BETWEEN_COLUMNS), FIRST_COLUMN_Y +(row+i+1)*38);
                    }
                    return;
                }
            }
        }
    }
}
