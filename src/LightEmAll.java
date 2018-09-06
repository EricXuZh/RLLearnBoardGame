import java.util.*;
import tester.*;
import javalib.impworld.*;
import java.awt.Color;
import javalib.worldimages.*;

class EdgeComparator implements Comparator<Edge> {
  public int compare(Edge e1, Edge e2) {
    return e1.weight - e2.weight;
  }
}

class LightEmAll extends World {
  // a list of columns of GamePieces,
  // i.e., represents the board in column-major order
  ArrayList<ArrayList<GamePiece>> board;
  // a list of all nodes
  ArrayList<GamePiece> nodes;
  // a list of edges of the minimum spanning tree
  ArrayList<Edge> mst;
  // the width and height of the board
  ArrayList<Color> colorPieces = new ArrayList<Color>();
  int width;
  int height;
  // the current location of the power station,
  // as well as its effective radius
  int powerRow;
  int powerCol;
  int radius;
  int trueSideLength = 200;
  // Width of the window for fractal generation that will contain
  // the fractals
  int maximumWidth;
  // Height of the window for fractal generation that will contain
  // the fractals
  int maximumHeight;
  // The smallest side length that is allowed to draw the board
  int smallestEdgeSize = 50;
  
  LightEmAll(int width, int height, int gameMode) {
    if (height <= 0 || width <= 0) {
      throw new IllegalArgumentException("Illegal Board Size");
    }
    
    this.width = width;
    this.height = height;
    this.board = new ArrayList<ArrayList<GamePiece>>();
    this.nodes = new ArrayList<GamePiece>();
    this.mst = new ArrayList<Edge>();
    
    if (gameMode == 0) {
      this.powerRow = this.width / 2;
      this.powerCol = this.height / 2;
      this.hardCodeFormat();
    }
    else if (gameMode == 1) {
      this.maximumHeight = this.height * this.trueSideLength;
      this.maximumWidth = this.width * this.trueSideLength;
      this.board = this.baseFractalDesign();
      this.fractalGeneration();
    }
    else {
      this.instantiateBoardEdges();
      this.board.get(powerRow).get(powerCol).updatePowerStation();
    }
    
    this.radius = this.board.get(powerRow).get(powerCol).findRadius(board, true) + 1;
    // this.randomizeRotations();
    this.board.get(powerRow).get(powerCol).lightUp(this.nodes, this.colorPieces,
        this.board, this.radius, Color.YELLOW);
  }
  
  LightEmAll(int gameMode) {
    this(3, 3, gameMode);
  }
  
  // EFFECTS: Displays the world to be seen
  public void bigBang() {
    super.bigBang(this.width * this.trueSideLength, this.height * this.trueSideLength, 1);
    //super.bigBang(800, 800, 1);
  }
  
  // Returns the scene used to draw the world
  public WorldScene makeScene() {
    WorldScene empty = getEmptyScene();
    empty.placeImageXY(this.draw(), this.width * this.trueSideLength / 2,
        this.height * this.trueSideLength / 2);
    //empty.placeImageXY(this.draw(), 400, 400);
    return empty;
  }
  
  // EFFECTS: Assigns random edge weights to all GamePieces and their direct neighbors
  void instantiateBoardEdges() {
    Random rand = new Random(4);
    HashMap<GamePiece, GamePiece> hm = new HashMap<GamePiece, GamePiece>();
    
    for (int x = 0; x < this.width; x++) {
      this.board.add(new ArrayList<GamePiece>());
      for (int y = 0; y < this.height; y++) {
        int randInt = rand.nextInt(51);
        this.board.get(x).add(new GamePiece(x, y, false, false, false, false, false));
        hm.put(this.board.get(x).get(y), this.board.get(x).get(y));
        if (x == 0 && y > 0) {
          Edge e1 = new Edge(this.board.get(x).get(y), this.board.get(x).get(y - 1), randInt);
          Edge e2 = new Edge(this.board.get(x).get(y - 1), this.board.get(x).get(y), randInt);
          this.mst.add(e1);
          this.mst.add(e2);
        }
        else if (x > 0 && y == 0) {
          Edge e1 = new Edge(this.board.get(x).get(y), this.board.get(x - 1).get(y), randInt);
          Edge e2 = new Edge(this.board.get(x - 1).get(y), this.board.get(x).get(y), randInt);
          this.mst.add(e1);
          this.mst.add(e2);
        }
        else if (x != 0 && y != 0) {
          Edge e1 = new Edge(this.board.get(x).get(y), this.board.get(x - 1).get(y), randInt);
          Edge e2 = new Edge(this.board.get(x - 1).get(y), this.board.get(x).get(y), randInt);
          this.mst.add(e1);
          this.mst.add(e2);
          randInt = rand.nextInt(51);
          e1 = new Edge(this.board.get(x).get(y), this.board.get(x).get(y - 1), randInt);
          e2 = new Edge(this.board.get(x).get(y - 1), this.board.get(x).get(y), randInt);
          this.mst.add(e1);
          this.mst.add(e2);
        }
      }
    }
    
    HeapSort hs = new HeapSort();
    hs.sort(this.mst, new EdgeComparator());

    for (int i = 0; i < this.mst.size(); i++) {
      this.mst.get(i).updateMST(hm);
    }
  }
  
  // EFFECTS: Creates the fractals to be displayed on the board
  void fractalGeneration() {
    this.width = this.board.size();
    this.height = this.board.get(0).size();
    
    if (this.maximumWidth / this.board.size() < this.smallestEdgeSize
        || this.maximumHeight / this.board.get(0).size() < this.smallestEdgeSize) {
      this.trueSideLength = this.maximumWidth / this.board.size();
      this.powerRow = this.board.size() / 2;
      this.powerCol = this.board.get(0).size() / 2;
      this.board.get(powerRow).get(powerCol).updatePowerStation();
      return;
    }
    
    for (int x = this.width; x < this.width * 2; x++) {
      this.board.add(new ArrayList<GamePiece>());
      for (int y = 0; y < this.height; y++) {
        GamePiece toBeAdded = this.board.get(x - this.width).get(y).replicate(x, y);
        this.board.get(x).add(toBeAdded);
      }
    }
    
    for (int x = 0; x < this.board.size(); x++) {
      for (int y = 0; y < this.height; y++) {
        GamePiece toBeAdded = this.board.get(x).get(y).replicate(x, y + this.height);
        this.board.get(x).add(toBeAdded);
      }
    }
    
    this.board.get(this.width - 1).get(0).connectLeftAndRight(this.board.get(this.width).get(0));
    this.board.get(0).get(this.height - 1).connectTopAndBottom(this.board.get(0).get(this.height));
    this.board.get(this.width * 2 - 1).get(this.height -
        1).connectTopAndBottom(this.board.get(this.width * 2 - 1).get(this.height));
    
    this.fractalGeneration();
  }
  
  // Returns the base fractal design to be replicated in the fractal generation of the board
  ArrayList<ArrayList<GamePiece>> baseFractalDesign() {
    ArrayList<ArrayList<GamePiece>> base = new ArrayList<ArrayList<GamePiece>>();
    if (this.height == 1) {
      base.add(new ArrayList<GamePiece>());
      for (int x = 0; x < this.width; x++) {
        if (x == 0) {
          base.get(x).add(new GamePiece(x, 0, false, true, false, false, false));
        }
        else {
          base.get(x).add(new GamePiece(x, 0, true, true, false, false, false));
        }
      }
    }
    else {
      for (int x = 0; x < this.width; x++) {
        base.add(new ArrayList<GamePiece>());
        for (int y = 0; y < this.height; y++) {
          if (y == 0 && x == 0) {
            base.get(x).add(new GamePiece(x, y, false, true, false, true, false));
          }
          else if (y == 0 && x == this.width - 1) {
            base.get(x).add(new GamePiece(x, y, true, false, false, true, false));
          }
          else if (y == 0) {
            base.get(x).add(new GamePiece(x, y, true, true, false, true, false));
          }
          else if (y == this.height - 1) {
            base.get(x).add(new GamePiece(x, y, false, false, true, false, false));
          }
          else {
            base.get(x).add(new GamePiece(x, y, false, false, true, true, false));
          }
        }
      }
    }
    
    return base;
  }
  
  // EFFECTS: Randomizes the rotations for the board
  void randomizeRotations() {
    Random rand = new Random(4);
    for (int x = 0; x < this.width; x++) {
      for (int y = 0; y < this.height; y++) {
        int tempInt = rand.nextInt(5);
        for (int i = 0; i < tempInt; i++) {
          this.board.get(x).get(y).rotate();
        }
      }
    }
  }
  
  // EFFECTS: Fills in the board with a set pattern and formation of game pieces
  void hardCodeFormat() {
    for (int x = 0; x < width; x++) {
      this.board.add(new ArrayList<GamePiece>());
      for (int y = 0; y < height; y++) {
        if (y == this.height / 2) {
          if (x == 0) {
            this.board.get(x).add(new GamePiece(x, y, false, true, true, true, false));
          }
          else if (x == this.width - 1) {
            boolean light = false;
            if (this.width == 2) {
              light = true;
            } 
            this.board.get(x).add(new GamePiece(x, y, true, false, true, true, light));
          }
          else if (x == width / 2) {
            this.board.get(x).add(new GamePiece(x, y, true, true, true, true, true));
          }
          else {
            this.board.get(x).add(new GamePiece(x, y, true, true, true, true, false));
          }
        }
        else {
          this.board.get(x).add(new GamePiece(x, y, false, false, true, true, false));
        }
      }
    }
  }
  
  // Changes the position of the power station based on the button pressed
  public void onKeyEvent(String buttonPressed) {
    this.board.get(powerRow).get(powerCol).updatePowerStation();
    GamePiece initialPowerStation = this.board.get(powerRow).get(powerCol);
    boolean changed = false;
    if (buttonPressed.equals("up")) {
      if (this.powerCol - 1 >= 0 
          && initialPowerStation.isConnectedGP(this.board.get(powerRow).get(powerCol - 1), 4)) {
        this.powerCol -= 1;
        changed = true;
      }
    }
    else if (buttonPressed.equals("down")) {
      if (this.powerCol + 1 < this.height
          && initialPowerStation.isConnectedGP(this.board.get(powerRow).get(powerCol + 1), 3)) {
        this.powerCol += 1;
        changed = true;
      }
    }
    else if (buttonPressed.equals("left")) {
      if (this.powerRow - 1 >= 0
          && initialPowerStation.isConnectedGP(this.board.get(powerRow - 1).get(powerCol), 2)) {
        this.powerRow -= 1;
        changed = true;
      }
    }
    else if (buttonPressed.equals("right")) {
      if (this.powerRow + 1 < this.width
          && initialPowerStation.isConnectedGP(this.board.get(powerRow + 1).get(powerCol), 1)) {
        this.powerRow += 1;
        changed = true;
      }
    }
    
    if (changed) {
      this.board.get(powerRow).get(powerCol).updatePowerStation();
      this.nodes = new ArrayList<>();
      this.colorPieces = new ArrayList<>();
      this.board.get(powerRow).get(powerCol).lightUp(this.nodes, this.colorPieces,
          this.board, this.radius, Color.YELLOW);
    }
    else {
      this.board.get(powerRow).get(powerCol).updatePowerStation();
    }
  }
  
  // EFFECTS: Rotates a game piece 90 degrees in the clockwise direction
  public void onMouseClicked(Posn pos, String buttonName) {
    int x = pos.x / this.trueSideLength;
    int y = pos.y / this.trueSideLength;
    
    this.board.get(x).get(y).rotate();

    this.nodes = new ArrayList<>();
    this.colorPieces = new ArrayList<>();
    this.board.get(powerRow).get(powerCol).lightUp(this.nodes, this.colorPieces,
        this.board, this.radius, Color.YELLOW);
  }
  
  // Draws the board
  WorldImage draw() {
    WorldImage base = new EmptyImage();
    for (int x = 0; x < this.width; x++) {
      WorldImage temp = new EmptyImage();
      for (int y = 0; y < this.height; y++) {
        int iLight = this.nodes.indexOf(this.board.get(x).get(y));
        if (iLight != -1) {
          temp = new AboveImage(temp, 
              this.board.get(x).get(y).draw(this.trueSideLength, 
                  this.trueSideLength, colorPieces.get(iLight)));
        }
        else {
          temp = new AboveImage(temp, 
              this.board.get(x).get(y).draw(this.trueSideLength,
                  this.trueSideLength, Color.LIGHT_GRAY));
        }
      }
      base = new BesideImage(base, temp);
    }
    
    return base;
  }
  
  // Displays the end of the world if the game is beaten
  public WorldEnd worldEnds() {
    if (this.nodes.size() == this.width * this.height) {
      return new WorldEnd(true, this.makeFinalScreen());
    }
    else {
      return new WorldEnd(false, this.makeScene());
    }
  }
  
  // Draws the final Scene to be Displayed
  WorldScene makeFinalScreen() {
    WorldImage img = this.draw();
    WorldImage winTxt = new TextImage("You Win", 48, Color.GREEN);
    winTxt = new OverlayImage(winTxt, img);
    
    WorldScene endScene = getEmptyScene();
    
    endScene.placeImageXY(winTxt,
        this.width * this.trueSideLength / 2, 
        this.height * this.trueSideLength / 2);
    return endScene;
  }
}

class GamePiece {
  // in logical coordinates, with the origin
  // at the top-left corner of the screen
  int row;
  int col;
  // whether this GamePiece is connected to the
  // adjacent left, right, top, or bottom pieces
  boolean left;
  boolean right;
  boolean top;
  boolean bottom;
  // whether the power station is on this piece
  boolean powerStation;
  Utils util = new Utils();
  
  GamePiece(int row, int col, boolean left, boolean right,
      boolean top, boolean bottom, boolean powerStation) {
    this.row = row;
    this.col = col;
    this.left = left;
    this.right = right;
    this.top = top;
    this.bottom = bottom;
    this.powerStation = powerStation;
  }
  
  int stateNumber() {
    int state = 0;
    if (this.top) {
      state += Math.pow(2, 3);
    }
    if (this.bottom) {
      state += Math.pow(2, 2);
    }
    if (this.left) {
      state += Math.pow(2, 1);
    }
    if (this.right) {
      state += 1;
    }
    return this.util.getStateN(state);
  }
  
  // Determines if the given Game Piece's directions is connected to this game PIece
  boolean isConnected(boolean left, boolean right, 
      boolean top, boolean bottom, int from) {
    if (from == 1) {
      return right && this.left;
    }
    else if (from == 2) {
      return left && this.right;
    }
    else if (from == 3) {
      return bottom && this.top;
    }
    else {
      return top && this.bottom;
    }
  }
  
  // Returns all the game pieces that are connected to this piece
  ArrayList<GamePiece> connectedGPs(ArrayList<ArrayList<GamePiece>> board,
      int width, int height) {
    ArrayList<Integer> xOffset = new ArrayList<Integer>();
    xOffset.add(0);
    xOffset.add(0);
    xOffset.add(1);
    xOffset.add(-1);
    
    ArrayList<Integer> yOffset = new ArrayList<Integer>();
    yOffset.add(1);
    yOffset.add(-1);
    yOffset.add(0);
    yOffset.add(0);
    
    ArrayList<GamePiece> connectedPieces = new ArrayList<>();
    for (int i = 0; i < xOffset.size(); i++) {
      int xShift = this.row + xOffset.get(i);
      int yShift = this.col + yOffset.get(i);
      
      int from = 0;
      if (yOffset.get(i) == -1) {
        from = 4;
      }
      else if (yOffset.get(i) == 1) {
        from = 3;
      }
      else if (xOffset.get(i) == -1) {
        from = 2;
      }
      else {
        from = 1;
      }
      
      if (xShift < width && xShift >= 0 && yShift < height && yShift >= 0) {
        if (board.get(xShift).get(yShift).isConnected(this.left,
            this.right, this.top, this.bottom, from)) {
          connectedPieces.add(board.get(xShift).get(yShift));
        }
      }
    }
    
    return connectedPieces;
  }
  
  // Returns an identical GamePiece to this one
  GamePiece replicate(int x, int y) {
    return new GamePiece(x, y, this.left, this.right, this.top, this.bottom, false);
  }
  
  // EFFECTS: Connects this GamePiece with the given GamePiece
  void connect(GamePiece gp) {
    int deltaX = this.row - gp.row;
    int deltaY = this.col - gp.col;
    
    if (deltaX == -1) {
      this.connectLeftAndRight(gp);
    }
    else if (deltaX == 1) {
      gp.connectLeftAndRight(this);
    }
    else if (deltaY == -1) {
      this.connectTopAndBottom(gp);
    }
    else {
      gp.connectTopAndBottom(this);
    }
  }
  
  // EFFECTS: Updates the power station for this GamePiece
  void updatePowerStation() {
    this.powerStation = !this.powerStation;
  }
  
  // Calculates the shortest path from two of the farthest nodes within the given board
  int findRadius(ArrayList<ArrayList<GamePiece>> board, boolean fromPowerCell) {
    Deque<GamePiece> worklist = new Deque<>();
    ArrayList<GamePiece> visited = new ArrayList<GamePiece>();
    HashMap<GamePiece, Integer> trace = new HashMap<GamePiece, Integer>();
    
    worklist.addAtHead(board.get(this.row).get(this.col));
    trace.put(board.get(this.row).get(this.col), 1);
    
    GamePiece lastPiece = null;
    while (worklist.size() != 0) {
      GamePiece next = worklist.removeFromHead();
      
      visited.add(next);
      lastPiece = next;
      
      ArrayList<GamePiece> connectedPieces 
          = next.connectedGPs(board, board.size(), board.get(0).size());
      for (int i = 0; i < connectedPieces.size(); i++) {
        if (!visited.contains(connectedPieces.get(i))) {
          visited.add(connectedPieces.get(i));
          worklist.addAtTail(connectedPieces.get(i));
          trace.put(connectedPieces.get(i), trace.get(next) + 1);
        }
      }
    }
    
    if (fromPowerCell) {
      return lastPiece.findRadius(board, false);
    }
    else {
      return trace.get(lastPiece) / 2;
    }
  }
  
  // Draws this game piece with the appropriate colors if lit
  WorldImage draw(int width, int height, Color wireColor) {
    WorldImage background = new RectangleImage(width, 
        height, OutlineMode.SOLID, Color.DARK_GRAY);
    
    if (this.right) {
      WorldImage line = new LineImage(new Posn(width / 2, 0),
          wireColor).movePinhole(- width / 4.0, 0);
      background = new OverlayImage(line, background);
    }
    if (this.left) {
      WorldImage line = new LineImage(new Posn(width / 2, 0),
          wireColor).movePinhole(width / 4.0, 0);
      background = new OverlayImage(line, background);
    }
    if (this.top) {
      WorldImage line = new LineImage(new Posn(0, height / 2),
          wireColor).movePinhole(0, height / 4.0);
      background = new OverlayImage(line, background);
    }
    if (this.bottom) {
      WorldImage line = new LineImage(new Posn(0, height / 2),
          wireColor).movePinhole(0, - height / 4.0);
      background = new OverlayImage(line, background);
    }
    
    if (this.powerStation) {
      WorldImage triangle1 = new TriangleImage(new Posn(0, 0), new Posn(0, height),
          new Posn(width * 3 / 10, height), OutlineMode.SOLID, Color.CYAN);
      WorldImage triangle2 = new TriangleImage(new Posn(0, 0), new Posn(0, height),
          new Posn(- width * 3 / 10, height), OutlineMode.SOLID, Color.CYAN);
      
      WorldImage otriangle1 = new TriangleImage(new Posn(0, 0), new Posn(0, height),
          new Posn(width * 3 / 10, height), OutlineMode.OUTLINE, Color.YELLOW);
      WorldImage otriangle2 = new TriangleImage(new Posn(0, 0), new Posn(0, height),
          new Posn(- width * 3 / 10, height), OutlineMode.OUTLINE, Color.YELLOW);
        
      WorldImage mainTriangle = new BesideImage(triangle2,
          triangle1).movePinholeTo(new Posn(0, 20));
      WorldImage oTriangle = new BesideImage(otriangle2,
          otriangle1).movePinholeTo(new Posn(0, 20));
      mainTriangle = new OverlayImage(oTriangle, mainTriangle);
      int angle = 51;
      WorldImage ps = new EmptyImage();
      for (int i = 0; i < 7; i++) {
        ps = new OverlayImage(ps, new RotateImage(mainTriangle, angle * i));
      }
      ps = new ScaleImage(ps, 0.1);
      background = new OverlayImage(ps, background);
    }
    
    return background;
  }
  
  // EFFECTS: Rotates the game piece
  void rotate() {
    boolean tempTop = this.top;
    this.top = this.left;
    this.left = this.bottom;
    this.bottom = this.right;
    this.right = tempTop;
  }
  
  // EFFECTS: Adds to the given array list the pieces that are to be lit up
  void lightUp(ArrayList<GamePiece> gpa, ArrayList<Color> colorPieces,
      ArrayList<ArrayList<GamePiece>> board, int radius, Color color) {
    ArrayList<GamePiece> connectedPieces = this.connectedGPs(board,
        board.size(), board.get(0).size());
    
    Color thisColor = new Color(color.getRed(), color.getGreen(), color.getBlue());
    gpa.add(this);
    colorPieces.add(thisColor);
    
    if (thisColor.getGreen() > 180) {
      thisColor = new Color(color.getRed(), color.getGreen() - 5, color.getBlue());
    }
    else {
      if (color.getRed() >= 10 && color.getGreen() >= 10) {
        thisColor = new Color(color.getRed() - 5, color.getGreen() - 5, color.getBlue());
      }
    }
    
    if (radius != 0) {
      for (int i = 0; i < connectedPieces.size(); i++) {
        if (!gpa.contains(connectedPieces.get(i))) {
          connectedPieces.get(i).lightUp(gpa, colorPieces, board, radius - 1, thisColor);
        }
      }
    }
  }
  
  // Returns if this GamePiece is connected to the given GamePiece
  boolean isConnectedGP(GamePiece gp, int from) {
    return gp.isConnected(this.left, this.right, this.top, this.bottom, from);
  }
  
  // EFFECTS: Connects this top GamePiece to the given bottom GamePiece
  void connectTopAndBottom(GamePiece bottom) {
    this.bottom = true;
    bottom.top = true;
  }
  
  // EFFECTS: Connects the right GamePiece to the right GamePiece
  void connectLeftAndRight(GamePiece left) {
    this.right = true;
    left.left = true;
  }
  
  int computeState() {
    int state = 0;
    if (this.top) {
      state += Math.pow(2, 3);
    }
    if (this.bottom) {
      state += Math.pow(2, 2);
    }
    if (this.left) {
      state += Math.pow(2, 1);
    }
    if (this.right) {
      state += 1;
    }
    
    return util.getState((int)state);
  }
}

class Edge {
  GamePiece fromNode;
  GamePiece toNode;
  int weight;
  
  Edge(GamePiece fromNode, GamePiece toNode, int weight) {
    this.fromNode = fromNode;
    this.toNode = toNode;
    this.weight = weight;
  }
  
  // EFFECTS: Updates the hashmap used to create the MST and connects
  // the two game pieces if they do not create a cycle
  void updateMST(HashMap<GamePiece, GamePiece> hm) {
    GamePiece fromNodeRoot = this.rootNode(hm, this.fromNode);
    GamePiece toNodeRoot = this.rootNode(hm, this.toNode);
    
    if (fromNodeRoot != toNodeRoot) {
      hm.put(fromNodeRoot, this.toNode);
      this.fromNode.connect(this.toNode);
    }
  }
  
  // Returns the root gamePiece of the MST
  GamePiece rootNode(HashMap<GamePiece, GamePiece> hm, GamePiece prev) {
    if (hm.get(prev) == prev) {
      return prev;
    }
    
    return this.rootNode(hm, hm.get(prev));
  }
  
  
}

class GPCompare implements Comparator<Edge> {
  // Compares the weight of the first edge to the second edge
  public int compare(Edge e1, Edge e2) {
    return e1.weight - e2.weight;
  }
}

class HeapSort {
  // EFFECTS: Sorts the ArrayList of the given type
  <T> void sort(ArrayList<T> a, Comparator<T> c) {
    this.buildList(a, c);
    this.sortList(a, c);
  }
  
  // EFFECTS: Sorts the ArrayList by taking the largest element off the top of the heap
  <T> void sortList(ArrayList<T> a, Comparator<T> c) {
    for (int i = a.size() - 1; i >= 0; i--) {
      this.swap(a, 0, i);
      this.downHeap(a, c, 0, i - 1);
    }
  }
  
  // EFFECTS: Moves the element at the given index down to its proper position in the heap
  <T> void downHeap(ArrayList<T> a, Comparator<T> c, int i, int heapSize) {
    int leftChild = 2 * i + 1;
    int rightChild = 2 * i + 2;
    if (leftChild > heapSize) {
      return;
    }
    
    if (rightChild > heapSize) {
      if (c.compare(a.get(i), a.get(leftChild)) < 0) {
        this.swap(a, i, leftChild);
      }
      return;
    }
    
    if (c.compare(a.get(i), a.get(leftChild)) < 0 
        && c.compare(a.get(i), a.get(rightChild)) < 0) {
      if (c.compare(a.get(leftChild), a.get(rightChild)) > 0) {
        this.swap(a, i, leftChild);
        this.downHeap(a, c, leftChild, heapSize);
      }
      else {
        this.swap(a, i, rightChild);
        this.downHeap(a, c, rightChild, heapSize);
      }
    }
    else if (c.compare(a.get(i), a.get(leftChild)) < 0) {
      this.swap(a, i, leftChild);
      this.downHeap(a, c, leftChild, heapSize);
    }
    else if (c.compare(a.get(i), a.get(rightChild)) < 0) {
      this.swap(a, i, rightChild);
      this.downHeap(a, c, rightChild, heapSize);
    }
  }
  
  // EFFECTS: Creates the heap from the given ArrayList
  <T> void buildList(ArrayList<T> a, Comparator<T> c) {
    for (int i = (a.size() - 1) / 2; i >= 0; i--) {
      this.downHeap(a, c, i, a.size() - 1);
    }
  }
  
  // EFFECTS: Swaps the the elements at the two indexes
  <T> void swap(ArrayList<T> a, int index1, int index2) {
    T temp = a.get(index1);
    a.set(index1, a.get(index2));
    a.set(index2, temp);
  }
}

class CompareInts implements Comparator<Integer> {
  // Compares two integers
  public int compare(Integer i1, Integer i2) {
    return i1 - i2;
  }
}

class ExamplesGame {
  
  LightEmAll lea;
  
  void init() {
    lea = new LightEmAll(2, 2, 0);
  }
  
  void testSort(Tester t) {
    ArrayList<Integer> tempA = new ArrayList<>();
    ArrayList<Integer> tempB = new ArrayList<>();
    for (int i = 0; i < 100000; i++) {
      tempA.add(i);
      tempB.add(i);
    }
    t.checkExpect(tempA, tempB);
    new HeapSort().sort(tempA, new CompareInts());
    Collections.sort(tempB);
    t.checkExpect(tempA, tempB);
  }
  
  void testWorld(Tester t) {
    LightEmAll lea1 = new LightEmAll(2);
    RLearn rl = new RLearn(lea1.board, lea1);
    rl.TDlearn(0.2, 1, 1, 0.1, -1);
    lea1.bigBang();
  }
  
  void testRotate(Tester t) {
    GamePiece gp = new GamePiece(2, 2, true, true, true, false, true);
    t.checkExpect(gp.bottom, false);
    t.checkExpect(gp.top, true);
    t.checkExpect(gp.left, true);
    t.checkExpect(gp.right, true);
    gp.rotate();
    t.checkExpect(gp.bottom, true);
    t.checkExpect(gp.top, true);
    t.checkExpect(gp.left, false);
    t.checkExpect(gp.right, true);
  }
  
  void testEndGame(Tester t) {
    this.init();
    t.checkExpect(this.lea.worldEnds(), new WorldEnd(true, 
        this.lea.makeFinalScreen()));
    this.lea.nodes.remove(1);
    t.checkExpect(this.lea.worldEnds(),
        new WorldEnd(false, this.lea.makeScene()));
  }
  
  void testConnect(Tester t) {
    GamePiece gp = new GamePiece(0, 1, false, false, false, false, false);
    GamePiece gp1 = new GamePiece(0, 2, false, false, false, false, false);
    
    t.checkExpect(gp.bottom, false);
    t.checkExpect(gp1.top, false);
    gp.connect(gp1);
    t.checkExpect(gp.bottom, true);
    t.checkExpect(gp1.top, true);
  }
  
  void testLeftAndRight(Tester t) {
    GamePiece gp = new GamePiece(0, 1, false, false, false, false, false);
    GamePiece gp1 = new GamePiece(0, 2, false, false, false, false, false);
    
    t.checkExpect(gp.left, false);
    t.checkExpect(gp1.right, false);
    gp.connectLeftAndRight(gp1);
    t.checkExpect(gp.right, true);
    t.checkExpect(gp1.left, true);
  }
  
  void testTopAndBottom(Tester t) {
    GamePiece gp = new GamePiece(0, 1, false, false, false, false, false);
    GamePiece gp1 = new GamePiece(0, 2, false, false, false, false, false);
    
    t.checkExpect(gp.bottom, false);
    t.checkExpect(gp1.top, false);
    gp.connectTopAndBottom(gp1);
    t.checkExpect(gp.bottom, true);
    t.checkExpect(gp1.top, true);
  }
  
  void testUpdatePowerStation(Tester t) {
    GamePiece gp = new GamePiece(0, 1, false, false, false, false, false);
    t.checkExpect(gp.powerStation, false);
    gp.updatePowerStation();
    t.checkExpect(gp.powerStation, true);
  }
  
  void testBreadthFirstSearch(Tester t) {
    this.init();
    t.checkExpect(this.lea.board.get(0).get(0).findRadius(this.lea.board, true), 2);
  }
}