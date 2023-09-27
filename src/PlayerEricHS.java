import java.util.ArrayList;

/**
 * @author Eric Heinke
 * @version 1.5 (enhanced 2.0) 2023-9-19 (Sep 19, 2023)
 * A copy of the Connect 4 algorithm I designed and programed in high school.
 * The code has been modified from its original form, but only when necessary to
 * allow for working in the improved game engine or where code could be modified
 * to be cleaner / easier to read without altering function.
 * <br>
 * The features of this algorithm are checking to see if there is an immediate
 * horizontal / vertical winning move or an immediate horizontal / vertical
 * loosing move and to play a piece accordingly. Diagonal move checking is
 * not performed due to my high school self not being able to figure out how
 * to properly check diagonals.
 * <br>
 * Diagonal checking and an additional function that were originally planned but never
 * properly implemented have also been completed and can be enabled or disabled
 * with ENHANCED. With enhancements on, you can experience the full glory of my
 * algorithms design.
 * <br>
 * Against a random player, this algorithm achieves an ~83.7% win rate. With enhancements,
 * it is able to achieve an ~96.1% win rate.
 */
public class PlayerEricHS extends Player {
    /**
     * Enable enhanced features. These were planned but never implemented in the original code.
     */
    private static final boolean ENHANCED = true;
    public PlayerEricHS(char myColor, char opponentColor, char emptySpace) {
        super(myColor, opponentColor, emptySpace);
        this.name = "Eric HS";
    }

    /**
     * The original game engine did not offer any signaling of a new game, so
     * the algorithm was designed without internal state.
     */
    @Override
    public void newGame(boolean firstMove) {

    }

    ArrayList<Integer> clear_columns = new ArrayList<>();
    ArrayList<Integer> win_plays = new ArrayList<>();
    ArrayList<Integer> block_plays = new ArrayList<>();
    ArrayList<Integer> avoid_plays = new ArrayList<>();
    ArrayList<String> my_grid = new ArrayList<>();
    ArrayList<String> tmp_grid = new ArrayList<>();
    int row;
    char[][] grid = null;
    @Override
    public int play(char[][] board) {
        grid = deepCopy(board);
        //Strategy version 1.5
        reset_grid();
        tmp_grid_to_grid();
        check_full_columns();
        check_win();
        check_loss1();
        check_win2();
        check_loss2();
        int colToPlace;
        if(!win_plays.isEmpty()){
            colToPlace = win_plays.get(0);
        }else if(!block_plays.isEmpty()){
            colToPlace = block_plays.get(0);
        }else{
            colToPlace = clear_columns.get(((int)(Math.random()*clear_columns.size())));
        }

        return colToPlace;
    }

    /**
     * Resets my_grid to the same state as grid
     */
    private void reset_grid(){
        my_grid.clear();
        for(int i = 0; i < 6; i++){
            StringBuilder temp = new StringBuilder();
            for(int a = 0; a < 7; a ++){
                if(grid[i][a] == emptySpace){
                    temp.append("0");
                }
                else if(grid[i][a] == this.myColor){
                    temp.append("1");
                }
                else{
                    temp.append("2");
                }
            }
            my_grid.add(temp.toString());
        }
    }

    private void tmp_grid_to_grid(){
        tmp_grid.clear();
        reset_grid();
        tmp_grid = my_grid;
    }

    /**
     * Checks which columns are not yet full
     */
    public void check_full_columns(){
        clear_columns.clear();
        for(int i = 0; i < 7; i++){
            if(my_grid.get(0).charAt(i) == '0'){
                clear_columns.add(i);
            }
        }
    }

    /**
     * Check to see if there is an immediate winning move
     */
    public void check_win(){
        win_plays.clear();
        for(int i = 0; i < 7; i++){
            simulate_placement(i, "1");
            winner(i);
        }
    }

    /**
     * I am not sure what I intended this function to be.
     */
    public void check_win2(){
        //checks winning in two turns
    }

    /**
     * Simulate the placement of a piece
     * @param column Column to place the piece
     * @param color Color of the piece being placed
     */
    public void simulate_placement(int column, String color){
        tmp_grid_to_grid();
        for(int i = 5; i > -1; i--){
            if(tmp_grid.get(i).charAt(column) == '0'){
                StringBuilder temp = new StringBuilder();
                row = i;
                for(int w = 0; w < 7; w++){
                    if(w == column){
                        temp.append(color);
                    }else{
                        temp.append(my_grid.get(i).charAt(w));
                    }
                }
                tmp_grid.set(i, temp.toString());
                return;
            }
        }
    }

    /**
     * Check to see if game has been won.
     * If game was won, add last play to win_plays
     * @param column Column the last piece was played
     */
    public void winner(int column){
        //adds plays that result in a win to the win list
        int in_a_row = 0;
        for(int w = 0; w < 6; w++){ //Checks for win in column
            if(tmp_grid.get(w).charAt(column) == '1'){
                in_a_row ++;
            }else{
                in_a_row = 0;
            }
        }
        if(in_a_row > 3){
            win_plays.add(column);
        }
        in_a_row = 0;
        for(int i = 0; i < 7; i++){
            if(tmp_grid.get(row).charAt(i) == '1'){
                in_a_row ++;
            }else{
                if(!(in_a_row >= 4)){
                    in_a_row = 0;
                }
            }
        }
        if(in_a_row > 3){
            win_plays.add(column);
        }
        //work here
        if (ENHANCED) { // This code was not originally present, but is part of the enhanced features
            char[][] tempGrid = new char[6][7];
            for (int i = 0; i < tempGrid.length; i++) {
                for (int j = 0; j < tempGrid[0].length; j++) {
                    char piece = tmp_grid.get(i).charAt(j);
                    if (piece == '1') {
                        tempGrid[i][j] = myColor;
                    } else if (piece == '2') {
                        tempGrid[i][j] = opponentColor;
                    } else {
                        tempGrid[i][j] = emptySpace;
                    }
                }
            }
            char winner = checkForWinner(tempGrid);
            if (winner == myColor) {
                win_plays.add(column);
            }
        }
    }

    /**
     * Check to see if game has been lost.
     * If last game was lost, add last play to block_plays
     * @param column Column the last piece was played
     */
    public void blocker(int column){
        //adds plays that result in a win to the win list
        int in_a_row = 0;
        for(int i = 0; i < 6; i++){ //Checks for win in column
            if(tmp_grid.get(i).charAt(column) == '2'){
                in_a_row ++;
            }else{
                in_a_row = 0;
            }
        }
        if(in_a_row > 3){
            block_plays.add(column);
        }
        in_a_row = 0;
        for(int i = 0; i < 7; i++){
            if(tmp_grid.get(row).charAt(i) == '2'){
                in_a_row ++;
            }else{
                if(!(in_a_row >= 4)){
                    in_a_row = 0;
                }
            }
        }
        if(in_a_row > 3){
            block_plays.add(column);
        }
        //work here
        if (ENHANCED) {
            char[][] tempGrid = new char[6][7];
            for (int i = 0; i < tempGrid.length; i++) {
                for (int j = 0; j < tempGrid[0].length; j++) {
                    char piece = tmp_grid.get(i).charAt(j);
                    if (piece == '1') {
                        tempGrid[i][j] = myColor;
                    } else if (piece == '2') {
                        tempGrid[i][j] = opponentColor;
                    } else {
                        tempGrid[i][j] = emptySpace;
                    }
                }
            }
            char winner = checkForWinner(tempGrid);
            if (winner == opponentColor) {
                block_plays.add(column);
            }
        }
    }

    /**
     * Checks to see if the opponent has an immediate winning move.
     */
    public void check_loss1(){
        block_plays.clear();
        for(int i = 0; i < 7; i++){
            simulate_placement(i, "2");
            blocker(i);
        }
    }
    /**
     * Checks to see if any of the two turn wins could result in a next turn loss.
     * This function was originally never implemented, but has since been implemented.
     */
    public void check_loss2(){
        //checks for opponent immediate win off my play
        avoid_plays.clear();
        for (int i = 0; i < 7; i++) {
            char[][] tempGrid = deepCopy(grid);
            // Place my piece
            for (int j = 5; j >= 0; j--) {
                if (tempGrid[j][i] == emptySpace) {
                    tempGrid[j][i] = myColor;
                    break;
                }
            }
            char[][] tempTempGrid = deepCopy(tempGrid);
            for (int j = 0; j < 7; j++) {
                // Place opponent piece
                for (int k = 5; k >= 0; k--) {
                    if (tempGrid[k][j] == emptySpace) {
                        tempGrid[k][j] = opponentColor;
                        break;
                    }
                }
                char winner = checkForWinner(tempTempGrid);
                if (winner == opponentColor) {
                    avoid_plays.add(i);
                }
            }
        }
        for (Integer avoidPlay : avoid_plays) {
            clear_columns.remove(avoidPlay);
        }
    }

    /**
     * Utility function to deep copy a 2d character array. This was added for the enhanced features.
     * @param matrix Array to copy
     * @return Copied array
     */
    char[][] deepCopy(char[][] matrix) {
        return java.util.Arrays.stream(matrix).map(char[]::clone).toArray($ -> matrix.clone());
    }
}
