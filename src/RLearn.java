import java.util.*;

public class RLearn {

  HashMap<Integer, int[]> values;
  ArrayList<SuperPiece> linearPerms = new ArrayList<SuperPiece>();
  ArrayList<ActionSet> actions = new ArrayList<ActionSet>();
  HashMap<Integer, Integer> bestMove = new HashMap<Integer, Integer>();
  Utils u = new Utils();
  LightEmAll lea;
  int terminalState;
  
  RLearn(ArrayList<ArrayList<GamePiece>> board, LightEmAll lea) {
    for (int x = 0; x < board.size(); x++) {
      for (int y = 0; y < board.get(0).size(); y++) {
        SuperPiece temp = new SuperPiece(board.get(x).get(y));
        linearPerms.add(temp);
      }
    }
    
    this.lea = lea;
    this.terminalState = this.stateGet();
    this.randomizeRotations();
    
    for (int z = 0; z < this.linearPerms.size(); z++) {
      for (int t = 1; t <= this.linearPerms.get(z).actions; t++) {
        this.actions.add(new ActionSet(this.linearPerms.get(z), t));
      }
    }
    
    this.values = new HashMap<Integer, int[]>();
    
    this.stateConversions(0, "");
  }
  
  void stateConversions(int cState, String quad) {
    Random rand = new Random();
    if (cState == this.linearPerms.size() - 1) {
      for (int i = 0; i < this.linearPerms.get(cState).states; i++) {
        int stateC = this.u.stringTob4(quad + Integer.toString(i));
        this.values.put(stateC, new int[this.actions.size()]);
        this.bestMove.put(stateC, rand.nextInt());
      }
    }
    else {
      for (int z = 0; z < this.linearPerms.get(cState).states; z++) {
        stateConversions(cState + 1, quad + Integer.toString(z));
      }
    }
  }
  
  void randomizeRotations() {
    Random rand = new Random(4);
   for (SuperPiece e : this.linearPerms) {
     e.onClick(rand.nextInt(e.actions) + 1);
   }
  }
  
  void TDlearn(double epi, double discount, int runs, double learningRate, int reward) {
    for (int r = 0; r < runs; r ++) {
      int state = this.stateGet();
      int action;
      int nextState;
      while (state != this.terminalState) {
        action = this.nextAction(this.values.get(state), epi);
        this.actions.get(action).act();
        nextState = this.stateGet();
        
        double highestActionValue = Integer.MIN_VALUE;
        int maxAction = 0;
        for (int a = 0; a < this.actions.size(); a++) {
          if (this.values.get(nextState)[a] > highestActionValue) {
            highestActionValue = this.values.get(nextState)[a];
            maxAction = a;
          }
        }
        
        this.values.get(state)[action] += learningRate * (reward + discount * this.values.get(nextState)[maxAction] - this.values.get(state)[action]);
        state = nextState;
      }
    }
  }
  
  int stateGet() {
    String state = "";
    for (int i = 0; i < this.linearPerms.size(); i++) {
      state = this.linearPerms.get(i).stateAdd(state);
    }
    
    return this.u.stringTob4(state);
  }
  
  int nextAction(int[] values, double epi) {
    double divider = epi/(double)values.length;
    
    int highestValue = 0;
    int currentHighest = Integer.MIN_VALUE;
    for (int i = 0; i < values.length; i++) {
      if (values[i] > currentHighest) {
        highestValue = i;
        currentHighest = values[i];
      }
    }
    
    Random rand = new Random();
    
    if (rand.nextDouble() > (epi - divider)) {
      return highestValue;
    }
    
    int nextPotentialAction = rand.nextInt(this.actions.size());
    
    while (nextPotentialAction == highestValue) {
      nextPotentialAction = rand.nextInt(this.actions.size());
    }
    
    return nextPotentialAction;
  }
}

class Utils {
  public static HashMap<Integer, Integer> stateConversion = new HashMap<Integer, Integer>();
  public static HashMap<Integer, Integer> stateNumbers = new HashMap<Integer, Integer>();
  
  Utils() { 
    stateConversion.put(0, 0);
    stateConversion.put(1, 0);
    stateConversion.put(2, 1);
    stateConversion.put(4, 2);
    stateConversion.put(8, 3);
    stateConversion.put(3, 0);
    stateConversion.put(12, 1);
    stateConversion.put(9, 0);
    stateConversion.put(10, 1);
    stateConversion.put(6, 2);
    stateConversion.put(5, 3);
    stateConversion.put(15, 0);
    stateConversion.put(11, 0);
    stateConversion.put(14, 1);
    stateConversion.put(7, 2);
    stateConversion.put(13, 3);
    
    stateNumbers.put(0, 0);
    stateNumbers.put(1, 4);
    stateNumbers.put(2, 4);
    stateNumbers.put(4, 4);
    stateNumbers.put(8, 4);
    stateNumbers.put(3, 2);
    stateNumbers.put(12, 2);
    stateNumbers.put(9, 4);
    stateNumbers.put(10, 4);
    stateNumbers.put(6, 4);
    stateNumbers.put(5, 4);
    stateNumbers.put(15, 1);
    stateNumbers.put(11, 4);
    stateNumbers.put(14, 4);
    stateNumbers.put(7, 4);
    stateNumbers.put(13, 4);
  }
  
  int stringTob4(String n) {
    int b10 = 0;
    for (int i = n.length() - 1; i >= 0; i--) {
      b10 += Character.getNumericValue(n.charAt(i)) * (int)Math.pow(4, i);
    }
    return b10;
  }
  
  int getState(int n) {
    return stateConversion.get(n);
  }
  
  int getStateN(int n) {
    return stateNumbers.get(n);
  }
}

class ActionSet {
  SuperPiece piece;
  int action;
  
  ActionSet(SuperPiece piece, int action) {
    this.piece = piece;
    this.action = action;
  }
  
  void act() {
    this.piece.onClick(this.action);
  }
}

class SuperPiece {
  GamePiece parent;
  int currentState;
  int states;
  int actions;
  Utils u = new Utils();
  
  SuperPiece(GamePiece parent) {
    
    this.parent = parent;
    this.currentState = parent.computeState();
    this.states = parent.stateNumber();
    this.actions = this.states - 1;
  }
  
  void onClick(int n) {
    for (int i = 0; i < n; i++) {
      this.parent.rotate();
    }
    this.currentState = this.parent.computeState();
  }
  
  String stateAdd(String s) {
    return s+= Integer.toString(this.currentState);
  }
}