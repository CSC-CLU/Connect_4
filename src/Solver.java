package src;

import java.util.HashMap;

public class Solver {

    private static final int[] EXPLORE_ORDER = { 3, 2, 4, 1, 5, 0, 6 };
    private static HashMap<Long, Integer> posMap = new HashMap<>();

    public static int tableSize() {
        return posMap.size();
    }

    public static int negamax(BitBoard bb, int alpha, int beta) {
        if (bb.isDraw()) {
            return 0;
        }

        int max = (41 - bb.getTurnCount()) / 2;
        Integer val = posMap.get(bb.generateKey());
        if (val != null) {
            max = val - 19;
        }
        if (beta > max) {
            beta = max;
            if (alpha >= beta) {
                return beta;
            }
        }

        for (int i = 0; i < bb.listMoves().length; i++) {
            if (bb.listMoves()[EXPLORE_ORDER[i]] != -1) {
                BitBoard b2 = new BitBoard(bb);
                b2.makeMove(EXPLORE_ORDER[i]);
                int score = -negamax(b2, -beta, -alpha);
                if (score >= beta)
                    return score;
                if (score > alpha) {
                    alpha = score;
                }
            }
        }

        posMap.put(bb.generateKey(), alpha + 19);
        return alpha;
    }

    public static int[] customSolve(BitBoard bb) {
        int[] output = new int[] { 0, 0, 0, 0, 0, 0, 0 };
        if (bb.getTurnCount() < 4) { // Early in the game, the center is always best.
            output[3] = 18;
            return output;
        }
        for (int i = 0; i < bb.listMoves().length; i++) { // Make the winning move if it exists.
            if (bb.listMoves()[i] != -1 && bb.isWinningMove(i)) {
                output[i] = 18;
                return output;
            }
        }
        // Loop through all moves to avoid making the losing move.
        for (int i = 0; i < bb.listMoves().length; i++) {
            if (bb.listMoves()[i] != -1) { // If move is possible
                bb.makeMove(i);
                for (int j = 0; j < bb.listMoves().length; j++) {
                    if (bb.listMoves()[j] != -1) {
                        if (bb.isWinningMove(j)) { // Check for opponent winning move, avoid.
                            output[j] = -18;
                        }
                    }
                }
                if (bb.isDraw()) { // If this move is a draw neutral scoring.
                    output[i] = 0;
                }
                bb.undoMove();
            }
        }
        int WEIGHT_SCORE = 13;
        for (int i : EXPLORE_ORDER) {
            if (bb.listMoves()[i] != -1) {
                output[i] = WEIGHT_SCORE--;
            }
        }
        if (bb.getTurnCount() > 13) {
            for (int i = 0; i < output.length; i++) {
                if (output[i] == -18) {
                    continue;
                }
                if (bb.listMoves()[i] != -1) {
                    bb.makeMove(i);
                    output[i] = -negamax(bb, -18, 18);
                    bb.undoMove();
                }
            }
        }

        return output;
    }

    public static int solve(BitBoard bb, int d) {
        if (bb.getTurnCount() == 0) {
            return 1;
        }
        int min = -(42 - bb.getTurnCount()) / 2;
        int max = (43 - bb.getTurnCount()) / 2;

        while (min < max) {
            int med = min + (max - min) / 2;
            if (med <= 0 && min / 2 < med) {
                med = min / 2;
            } else if (max >= 0 && max / 2 > med) {
                med = max / 2;
            }
            int r = negamax(bb, med, med + 1);
            if (r <= med) {
                max = r;
            } else {
                min = r;
            }
        }
        return min;
    }
}
