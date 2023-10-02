import java.util.Random;

/**
 * This class is a tournament system for Connect 4.
 *
 * @author Eric Heinke
 * @version 2023-10-1 (Oct 1, 2023)
 */
public class Tournament {
    private static final boolean COUNT_TIES = true; // Do ties get included in statistics
    private static final char EMPTY_SPACE = ' ';
    private static final char BLACK_PIECE = 'b';
    private static final char RED_PIECE = 'r';

    /** Connect 4 game board */
    private static final char[][] gameBoard;

    private static int playerTurn;
    private static int firstPlayerTurn;

    private static final Random random = new Random();

    static {
        gameBoard = new char[6][7];
        firstPlayerTurn = random.nextInt(2)+1;   //randomize who starts the game
    }
    public static void main(String[] args) throws Exception {
        Class<?>[] players = new Class<?>[] {
                Class.forName("PlayerEricHS"),
                Class.forName("PlayerRandom")};

        Class<?> black = players[0];
        Class<?> red = players[1];

        int games = 10000;

        int playerBlackWinTotal = 0;
        int playerRedWinTotal = 0;
        int tieCounter = 0;

        Player playerBlack = (Player) black.getConstructor(char.class, char.class, char.class)
                .newInstance(BLACK_PIECE, RED_PIECE, EMPTY_SPACE);
        Player playerRed = (Player) red.getConstructor(char.class, char.class, char.class)
                .newInstance(RED_PIECE, BLACK_PIECE, EMPTY_SPACE);

        for (int game = 0; game < games; game++) {
            char winner = playGame(playerBlack, playerRed);
            if (winner == BLACK_PIECE) {
                playerBlackWinTotal++;
            } else if (winner == RED_PIECE) {
                playerRedWinTotal++;
            } else {
                tieCounter++;
            }
        }

        System.out.println("Black:\t" + (playerBlackWinTotal * 100 / games));
        System.out.println("Red:\t" + (playerRedWinTotal * 100 / games));
        System.out.println("Tie:\t" + (tieCounter * 100 / games));
    }

    private static char playGame(Player playerBlack, Player playerRed) throws Exception {


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

        char winner;
        while ((winner = checkForWinner()) == EMPTY_SPACE && !tie()) {
            int column = -1;
            char color = (char)playerTurn;
            if (playerTurn == BLACK_PIECE) {
                playerTurn = RED_PIECE;
                try {
                    column = playerBlack.play(duplicateBoard()); // Get player's move
                } catch (Exception ignored) {} // Exceptions are treated as a forfeited turn
            } else {
                playerTurn = BLACK_PIECE;
                try {
                    column = playerRed.play(duplicateBoard()); // Get player's move
                } catch (Exception ignored) {} // Exceptions are treated as a forfeited turn
            }
            dropPiece(column, color);
        }
        return winner;
    }

    /**
     * Duplicates the game board to prevent cheating
     * @return Duplicate game board
     */
    private static char[][] duplicateBoard() {
        return duplicateBoard(gameBoard);
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
    private static int dropPiece(int column, char color) {
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
        for(int col = 0; col < gameBoard[0].length; col++)
        {
            for(int row = 0; row < gameBoard.length - 1; row++)
            {
                if(gameBoard[row][col] != EMPTY_SPACE && gameBoard[row + 1][col] == EMPTY_SPACE)
                {
                    gameBoard[row+1][col] = gameBoard[row][col];
                    gameBoard[row][col] = EMPTY_SPACE;
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
    private static char checkForWinner() {
        return checkForWinner(gameBoard);
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
    private static boolean tie() {
        return tie(gameBoard);
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
}
