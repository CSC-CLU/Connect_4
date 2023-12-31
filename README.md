# Connect 4

A connect 4 tournament style game for creating and competing different Connect 4 algorithms/AIs

## Directions:

### How to run the program:

GreenFoot is required to run the Connect 4 program.
In GreenFoot, of you open the folder containing the code, the program will load and be ready for use. Click Run to run a game, Pause to pause the game, Act to progress the game by one 'act' (one call to the Act function), or Reset to reset the program.

To compile changes, you will need to go to Tools and Recompile Scenario

### How to make your own player

To make a player, simply create a class that extends the Player class.
Descriptions of the required functions can be found in the player class.

Some guidelines to follow:
- You can add as many variables to your player class as you want and utilize external libraries if desired (including AI and machine learning libraries), but use your best judgement to ensure that you follow the spirit of the game.
- Aside from initializing variables and libraries, try not to include any unnecessary code in the constructor.
- Try to keep your algorithm reasonably fast. Remember that the intent of this program is to be a competition, and games should be completed in a reasonable time frame.
- Players are **NOT** allowed to access each other. A player should never try to access any of the resources of the Connect 4 game or resources of other players code. Reason: There was an incident where two people wrote a player that would find their opponent's code and run simulated games. This both messed with their opponent player's state and caused the program to crash do to their recursive calls.
