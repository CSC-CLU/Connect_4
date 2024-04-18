public class PlayerScott extends Player {

    public PlayerScott(char myColor, char opponentColor, char emptySpace) {
        super(myColor, opponentColor, emptySpace);
        this.name = "Scott";
    }

    @Override
    public void newGame(boolean firstMove) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public int play(char[][] board) {
        BitBoard bb = new BitBoard(board);
        int[] scores = Solver.customSolve(bb);
        int bestPos = -17;
            for (int i = 0; i < scores.length; i++) {
                System.out.print(scores[i] + " ");
                if (bestPos == -17) {
                    if (bb.listMoves()[i] != -1) {
                        bestPos = i;
                    }
                } else {
                    if (bb.listMoves()[i] != -1) {
                        if (scores[bestPos] <= scores[i]) {
                            bestPos = i;
                        }
                    }
                }
            }
    }
    
}