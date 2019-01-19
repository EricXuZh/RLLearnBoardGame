# RLLightEmAll

Wire connecting board game with random board generation. 

![lightemallgame](https://user-images.githubusercontent.com/33673296/51421193-98771300-1b68-11e9-851d-99ed9b191ab6.PNG)

The goal of the game is to connect all the board pieces together. Furthermore, the power station must be able to reach all pieces in order to "power" (denoted by the orange coloring of the piece's wire) the piece up. Only then does the player win.

I also integrated a naive reinforcement learning model onto the board game in order to have an AI solve the board connection aspect of the game independently. I currently use a simple temporal difference Q-learning reinforcement model (code can be found in the RLlearn.java file). The reward system is as follows; there is a penalty of -1 for each move taken, with no penalty for any move that results in the winning state of the board, which in this case it would be any move that results in the board being completely connected. As a result, the learning algorithm seeks to find the optimal move set (in this context optimal being the shortest necessary amount of moves) from any possible board position to reach the fully connected board state. 

For the state representation, each state for the learning agent is stored as a digit in base 4, with the length of the digit being the number of pieces on the board. Since each piece containing wires in the game can be rotated 90 degrees, the four different orientations of each piece can be mapped to a number between 0-3. Appending all these numbers together into a digit creates a numerical representation of the board state from which actions can be made. In brief, the total state size is O(n^4). 


# TODO / FUTURE AMBITIONS:

Admittedly, the above learning method is extremely naive; when a board is given, the learning model is only capable of discovering a move set that is capable of solving the currently given board. Any other board, the learning model is at square one and must begin the entire process over again of learning the values of each state for a new board. A better idea would be to find a way to generalize the learning model such that it doesn't solve boards in their entirety any more, but rather attempts to solve common "paradigms" of the board and extrapolates these patterns to larger boards. For example, currently if I give a 7x7 board to the learning model I have right now, it will only be capable of solving this particular instance of this 7x7 board with no further extrapolation on how to solve different boards. 

Imagine, however, if instead I train the learning model on a multitude of different 3x3 board sizes and have the learning model find the solution set for a multitude of different 3x3 board sizes. For example, the learning agent, given a 3x3 board size with a certain set of pieces, finds the solution set to fully connect this 3x3 board. From there, we can generate a 9x9 board and have the learning model solve 3x3 portions of this board and see how close it gets to actually solving the 9x9 board. In this way, the learning model becomes more generalized and is capabel of solving boards it has never seen before based upon the 3x3 boards it does know how to solve.
