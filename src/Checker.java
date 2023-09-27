import greenfoot.*;

public class Checker extends Actor
{
    private int col;
    private int numMoves;
    private char[][] gameBoard;
    private char emptySpace;

    public Checker(int col, GreenfootImage image, char[][] gameBoard, char emptySpace){
        //scales image
        image.scale(41, 41);
        setImage(image);
        
        //turns checker so it moves down
        turn(270);

        this.col = col;
        this.numMoves=-1;
        this.gameBoard = gameBoard;
        this.emptySpace = emptySpace;
    }
    
    public void act() 
    {
        // determines how far to drop
        if(numMoves==-1){
            this.numMoves+=2;
            for(int r = 0; r < gameBoard.length; r++){
                if(gameBoard[r][col] == emptySpace)
                    this.numMoves++;
            }
        }
        
        if(numMoves > 0){
            //drops the checker the required amount
             move(-38);
             numMoves--;
        }
    }    
}
