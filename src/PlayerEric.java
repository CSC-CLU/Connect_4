import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author Eric Heinke
 * @version 2.0 2023-10-22 (Oct 22, 2023)
 */
public class PlayerEric extends Player {
    int[] moves = new int[7];
    boolean newGame;
    boolean firstMove;

    public PlayerEric(char myColor, char opponentColor, char emptySpace) {
        super(myColor, opponentColor, emptySpace);
        this.name = "Eric Heinke";
    }

    @Override
    public void newGame(boolean firstMove) {
        this.newGame = true;
        this.firstMove = firstMove;
    }

    @Override
    public int play(char[][] board) {
        calculateMoveScores(board);
        int bestMove = (int)(Math.random()*7);
        for (int col = 0; col < moves.length; col++) {
            if (moves[col] > moves[bestMove] && board[0][col] == emptySpace) {
                bestMove = col;
            }
        }
        ArrayList<Integer> bestMoves = new ArrayList<>(7);
        for (int col = 0; col < moves.length; col++) {
            if (moves[bestMove] == moves[col]) {
                bestMoves.add(col);
            }
        }
        bestMove = bestMoves.get((int) (Math.random()*bestMoves.size()));
//        System.out.println(bestMoves);
//        System.out.println(Arrays.toString(moves));
        return bestMove;
    }

    private void calculateMoveScores(char[][] originalBoard) {
        Arrays.fill(moves, 0);

        for (int col = 0; col < moves.length; col++) {
            char[][] board1 = duplicateBoard(originalBoard);
            dropPiece(board1, col, myColor);
            if (checkForWinner(board1) == myColor) {
                moves[col] += 10_000_000;
            }
            char[][] board1b = duplicateBoard(originalBoard);
            dropPiece(board1b, col, opponentColor);
            if (checkForWinner(board1b) == opponentColor) {
                moves[col] += 1_000_000;
            }
            for (int a = 0; a < moves.length; a++) {
                char[][] board2 = duplicateBoard(board1);
                dropPiece(board2, a, opponentColor);
                if (checkForWinner(board2) == opponentColor) {
                    moves[col] -= 10_000;
                }
                for (int b = 0; b < moves.length; b++) {
                    char[][] board3 = duplicateBoard(board2);
                    dropPiece(board3, b, myColor);
                    if (checkForWinner(board3) == myColor) {
                        moves[col] += 100;
                    }
                    for (int c = 0; c < moves.length; c++) {
                        char[][] board4 = duplicateBoard(board3);
                        dropPiece(board4, c, opponentColor);
                        if (checkForWinner(board4) == opponentColor) {
                            moves[col] -= 10;
                        }
                        for (int d = 0; d < moves.length; d++) {
                            char[][] board5 = duplicateBoard(board4);
                            dropPiece(board5, d, myColor);
                            if (checkForWinner(board5) == myColor) {
                                moves[col] += 1;
                            }
                        }
                    }
                }
            }
        }
    }
}
