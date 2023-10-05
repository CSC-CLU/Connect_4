import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Random;

/**
 * Common code for Connect 4 games
 * @author Eric Heinke
 * @version 2023-10-4 (Oct 4, 2023)
 */
public class Common {
    public static final char EMPTY_SPACE = ' ';
    public static final char BLACK_PIECE = 'b';
    public static final char RED_PIECE = 'r';

    private static final Random RANDOM = new Random();

    protected static char randomPlayer() {
        if (RANDOM.nextInt(2) == 1) {
            return BLACK_PIECE;
        }
        return RED_PIECE;
    }

    protected static char swapPlayer(char player) {
        return player == Common.BLACK_PIECE ? Common.RED_PIECE : Common.BLACK_PIECE;
    }

    protected static Player constructPlayer(Class<?> playerClass) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        return (Player) playerClass
                .getConstructor(char.class, char.class, char.class)
                .newInstance(BLACK_PIECE, RED_PIECE, EMPTY_SPACE);
    }

    /**
     * Duplicates the game board to prevent cheating
     * @param matrix Board to be duplicated
     * @return Duplicated board
     */
    public static char[][] duplicateBoard(char[][] matrix) {
        return java.util.Arrays.stream(matrix).map(char[]::clone).toArray($ -> matrix.clone());
    }

    public static void clearBoard(char[][] board) {
        for (char[] chars : board) {
            Arrays.fill(chars, EMPTY_SPACE);
        }
    }

    /**
     * Drop piece onto the game board
     * @param column Column to place the piece
     * @param color Color of the piece being placed
     * @return Column the piece was dropped in
     */
    protected static int dropPiece(char[][] gameBoard, int column, char color) {
        // If column is invalid or is already full, determine new column
        if (column < 0 || column > gameBoard[0].length || gameBoard[0][column] != EMPTY_SPACE) {
            for (int i = 0; i < 10; i++) {
                column = RANDOM.nextInt(7);
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
