import java.util.LinkedList;
import java.util.Queue;

/**
 * This class is a tournament system for Connect 4.
 *
 * @author Eric Heinke
 * @version 2023-10-4 (Oct 4, 2023)
 */
public class Tournament {
    public static void main(String[] args) throws Exception {
        Queue<Class<?>> players = new LinkedList<>();
        players.add(Class.forName("PlayerEricHS"));
        players.add(Class.forName("PlayerRandom"));
        players.add(Class.forName("PlayerEricHS"));
        players.add(Class.forName("PlayerRandom"));
        players.add(Class.forName("PlayerEricHS"));
        players.add(Class.forName("PlayerRandom"));
        players.add(Class.forName("PlayerEricHS"));
        players.add(Class.forName("PlayerRandom"));

        int round = 0;
        while (players.size() > 1) { // Rounds of play
            System.out.print("Round: " + ++round + " (");
            if (players.size() %2 == 0) {
                System.out.println(players.size() + " players, " + (players.size()/2) + " games)");
            } else {
                System.out.println(players.size() + " players, " + (players.size()/2) + " games, 1 passthrough)");
            }
            Queue<Class<?>> temp = new LinkedList<>();
            while (players.size() > 1) { // Plays per round
                Class<?> playerBlackClass = players.remove();
                Class<?> playerRedClass = players.remove();

                // Make games run in parallel
                Match game = new Match(
                        Common.constructPlayer(playerBlackClass),
                        Common.constructPlayer(playerRedClass),
                        1000);
                game.start();

                while (game.isAlive()) {
                    Thread.sleep(100);
                }

                MatchResult matchResult = game.getResults();

                if (matchResult.winner() == Common.BLACK_PIECE) {
                    temp.add(playerBlackClass);
                } else if (matchResult.winner() == Common.RED_PIECE) {
                    temp.add(playerRedClass);
                } else { //@TODO improve this later
                    temp.add(Common.randomPlayer() == Common.BLACK_PIECE ? playerBlackClass : playerRedClass);
                }
                System.out.println(matchResult);
            }
            if (players.size() == 1) {
                temp.add(players.remove());
            }
            players = temp;
        }
    }
}

/**
 * Class for storing the results of a match between two players
 */
@SuppressWarnings("unused")
class MatchResult {
    private final Class<?> playerBlackClass;
    private final String playerBlackName;
    private final Class<?> playerRedClass;
    private final String playerRedName;
    private final int blackWins;
    private final int redWins;
    private final int ties;
    private final int games;

    public MatchResult(Class<?> playerBlack, String playerBlackName, Class<?> playerRed, String playerRedName, int blackWins, int redWins, int ties) {
        this.playerBlackClass = playerBlack;
        this.playerBlackName = playerBlackName;
        this.playerRedClass = playerRed;
        this.playerRedName = playerRedName;
        this.blackWins = blackWins;
        this.redWins = redWins;
        this.ties = ties;
        this.games = blackWins + redWins + ties;
    }

    public Class<?> getPlayerBlackClass() {
        return playerBlackClass;
    }
    public String getPlayerBlackClassName() {
        return playerBlackClass.getName();
    }

    public String getPlayerBlackName() {
        return playerBlackName;
    }

    public Class<?> getPlayerRedClass() {
        return playerRedClass;
    }
    public String getPlayerRedClassName() {
        return playerRedClass.getName();
    }

    public String getPlayerRedName() {
        return playerRedName;
    }

    public int getBlackWins() {
        return blackWins;
    }

    public int getRedWins() {
        return redWins;
    }

    public int getTies() {
        return ties;
    }

    public int getGames() {
        return games;
    }

    //@TODO Improve this later
    public char winner() {
        if (blackWins > redWins) {
            return Common.BLACK_PIECE;
        } else if (redWins > blackWins) {
            return Common.RED_PIECE;
        }
        return Common.EMPTY_SPACE;
    }

    @Override
    public String toString() {
        return String.format("Game: %s vs %s, %5.2f%%, %5.2f%%, %5.2f%%", playerBlackName, playerRedName, blackWins*100.0/games, redWins*100.0/games, ties*100.0/games);
    }
}

/**
 * Play a specified number of games between two players on an independent thread.
 */
class Match extends Thread {
    private final Player playerBlack;
    private final Player playerRed;

    private int blackWins;
    private int redWins;
    private int ties;

    private int gamesLeft;

    private char firstPlayerTurn;

    /** Connect 4 game board */
    private final char[][] gameBoard = new char[6][7];

    public Match(Player playerBlack, Player playerRed, int games) {
        this.playerBlack = playerBlack;
        this.playerRed = playerRed;
        this.gamesLeft = games;

        this.blackWins = 0;
        this.redWins = 0;
        this.ties = 0;

        this.firstPlayerTurn = Common.randomPlayer(); //randomize who starts the game
    }

    public void run() {
        for (; gamesLeft > 0; gamesLeft--) {
            char winner = playGame();
            if (winner == Common.BLACK_PIECE) {
                blackWins++;
            } else if (winner == Common.RED_PIECE) {
                redWins++;
            } else {
                ties++;
            }
        }
    }

    private char playGame() {
        // swap who plays first
        firstPlayerTurn = Common.swapPlayer(firstPlayerTurn);
        int playerTurn = firstPlayerTurn;

        // clear the game board
        Common.clearBoard(gameBoard);

        // Let players know that it is a new game
        playerBlack.newGame(firstPlayerTurn == Common.BLACK_PIECE);
        playerRed.newGame(firstPlayerTurn == Common.RED_PIECE);

        char winner;
        while ((winner = checkForWinner()) == Common.EMPTY_SPACE && !tie()) {
            int column = -1;
            char color = (char) playerTurn;
            if (playerTurn == Common.BLACK_PIECE) {
                playerTurn = Common.RED_PIECE;
                try {
                    column = playerBlack.play(duplicateBoard()); // Get player's move
                } catch (Exception ignored) {} // Exceptions are treated as a forfeited turn
            } else {
                playerTurn = Common.BLACK_PIECE;
                try {
                    column = playerRed.play(duplicateBoard()); // Get player's move
                } catch (Exception ignored) {} // Exceptions are treated as a forfeited turn
            }
            Common.dropPiece(gameBoard, column, color);
        }
        return winner;
    }

    public MatchResult getResults() {
        return new MatchResult(
                playerBlack.getClass(), playerBlack.getName(),
                playerRed.getClass(), playerRed.getName(),
                blackWins, redWins, ties);
    }

    /**
     * Duplicates the game board to prevent cheating
     * @return Duplicate game board
     */
    private char[][] duplicateBoard() {
        return Common.duplicateBoard(gameBoard);
    }

    /**
     * Check to see if there is a winner.
     * @return Winner's piece color or EMPTY_SPACE for no win
     */
    private char checkForWinner() {
        return Common.checkForWinner(gameBoard);
    }

    /**
     * Check if there is a tie.
     * @return if there is a tie
     */
    private boolean tie() {
        return Common.tie(gameBoard);
    }
}
