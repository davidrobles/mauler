//package dr.games.tron;//package dr.games.tron;
//
//import AbstractGame;
//import Game;
//import Outcome;
//
//import java.io.BufferedReader;
//import java.io.FileReader;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Random;
//
//public class Tron extends AbstractGame implements Game<Tron>
//{
//    private int plyCount = 0;
//    private boolean[][] walls;
////    private List<TronMove> p1Moves = new ArrayList<TronMove>();
////    private List<TronMove> p2Moves = new ArrayList<TronMove>();
//    private static final Random rand = new Random();
//
//    @Override
//    public Tron copy()
//    {
//        Tron board = new Tron();
//        board.walls = new boolean[walls.length][walls[0].length];
//        for (int row = 0; row < board.walls.length; row++)
//            System.arraycopy(walls[row], 0, board.walls[row], 0, board.walls[0].length);
//        board.plyCount = plyCount;
//
//        for (TronMove m : p1Moves) {
//            board.p1Moves.add(m.copy());
//        }
//
//        for (TronMove m : p2Moves) {
//            board.p2Moves.add(m.copy());
//        }
//
//        return board;
//    }
//
//    @Override
//    public int getCurPlayer()
//    {
//        return plyCount % 2;
//    }
//
//    @Override
//    public String[] getMoves()
//    {
////        if (getCurPlayer() == 0)
////            return p1Moves;
////        return p2Moves;
//        return new String[0];
//    }
//
//    @Override
//    public int getNumMoves()
//    {
//        return 0;
//    }
//
//    @Override
//    public String getName()
//    {
//        return "Tron";
//    }
//
//    @Override
//    public int getNumPlayers()
//    {
//        return 2;
//    }
//
//    @Override
//    public Outcome[] getOutcome()
//    {
//        return new Outcome[0];
//    }
//
//    @Override
//    public boolean isOver()
//    {
//        return false;
//    }
//
//    @Override
//    public void makeMove(int move)
//    {
//
//    }
//
//    @Override
//    public Tron newInstance()
//    {
//        return null;
//    }
//
//    @Override
//    public void reset()
//    {
//
//    }
//
//    // FACTORY METHODS
//
//    public static Tron gameWithRandomMap()
//    {
//        Tron tron = new Tron();
//        int rows = rand.nextInt(10) + 10;
//        int cols = rand.nextInt(10) + 10;
//        boolean[][] walls = new boolean[rows][cols];
//        tron.setWalls(walls);
//
//        // Put border walls
//        for (int row = 0; row < rows; row++)
//            for (int col = 0; col < cols; col++)
//                if (row == 0 || row == rows - 1 || col == 0 || col == cols - 1)
//                    tron.setWall(col, row);
//
//        // Put some random Walls
//        for (int row = 0; row < rows; row++)
//            for (int col = 0; col < cols; col++)
//                if (rand.nextDouble() < 0.10)
//                    tron.setWall(col, row);
//
//        // Place player 1
//        while (tron.players[0] == null) {
//            int randCol = rand.nextInt(cols);
//            int randRow = rand.nextInt(rows);
//            if (!tron.isWall(randCol, randRow)) {
//                tron.players[0] = new TronPlayer(agent1);
//                tron.players[0].setX(randCol);
//                tron.players[0].setY(randRow);
//                break;
//            }
//        }
//
//        // Place player 2
//        while ((tron.players[1] == null)
//                || (tron.players[0].getX() == tron.players[1].getX()
//                && tron.players[0].getY() == tron.players[1].getY())) {
//            int randCol = rand.nextInt(cols);
//            int randRow = rand.nextInt(rows);
//            if (!tron.isWall(randCol, randRow)) {
//                tron.players[1] = new TronPlayer(agent2);
//                tron.players[1].setX(randCol);
//                tron.players[1].setY(randRow);
//                break;
//            }
//        }
//
//        tron.calcMoves();
//
//        return tron;
//    }
//
//    public static Tron createGameWithMap(String mapFilename)
//    {
//        System.out.println("loading map: " + mapFilename);
//        Tron tron = new Tron();
//
//        try {
//            BufferedReader in = new BufferedReader(new FileReader(mapFilename));
//            List<String> lines = new ArrayList<String>();
//            String str;
//
//            while ((str = in.readLine()) != null)
//                lines.add(str);
//
//            int rows = lines.size();
//            int cols = lines.get(0).length();
//            tron.setWalls(new boolean[rows][cols]);
//
//            for (int row = 0; row < rows; row++) {
//                for (int col = 0; col < cols; col++) {
//                    char c = lines.get(row).charAt(col);
//                    if (c == '#') {
//                        tron.setWall(col, row);
//                    } else if (c == '1') {
//                        tron.players[0] = new TronPlayer(agent1);
//                        tron.players[0].setX(col);
//                        tron.players[0].setY(row);
//                    } else if (c == '2') {
//                        tron.players[1] = new TronPlayer(agent2);
//                        tron.players[1].setX(col);
//                        tron.players[1].setY(row);
//                    }
//                }
//            }
//
//            in.close();
//
//        } catch (IOException e) {
//            System.out.println("It did not find the map.");
//        }
//
//        tron.calcMoves();
//
//        return tron;
//    }
//
//    public boolean isWall(int x, int y) {
//        return x < 0 || x >= colCount() || y < 0 || y >= rowCount() || walls[y][x];
//    }
//
//    public void setWall(int x, int y) {
//        walls[y][x] = true;
//    }
//
//    public void setWalls(boolean[][] walls) {
//        this.walls = walls;
//    }
//
//    public int colCount() {
//        return walls[0].length;
//    }
//
//    public int rowCount() {
//        return walls.length;
//    }
//
//    public char cellToChar(int x, int y) {
//        if (players[0].getX() == x && players[0].getY() == y
//                && players[1].getX() == x && players[1].getY() == y)
//            return 'X';
//            // prints player 1
//        else if (players[0].getX() == x && players[0].getY() == y)
//            return '1';
//            // prints player 2
//        else if (players[1].getX() == x && players[1].getY() == y)
//            return '2';
//        // print a wall
//        if (isWall(x, y))
//            return '#';
//        return ' ';
//    }
//
//    public int getPlyCount() {
//        return plyCount;
//    }
//
//    // This method must be called before playing the game. After that,
//    // it will be called automatically when making a move.
//    private void calcMoves()
//    {
//        // This method has to be called only when the plyCount % 2 == 0
//        // i. e. when all the players made their move
//        assert plyCount % players.length == 0;
//
//        // calculate the possible moves for each player
//        p1Moves = getMovesHelper(0);
//        p2Moves = getMovesHelper(1);
//
//        // todo: fix this, it only works for 2 players
//        // both players died
//        if (p1Moves.size() == 0 && p2Moves.size() == 0) {
//            players[0].crash();
//            players[1].crash();
//        }
//        // no possible moves for player 1
//        else if (p1Moves.size() == 0) {
//            players[0].crash();
//        }
//        // no possible moves for player 2
//        else if (p2Moves.size() == 0) {
//            players[1].crash();
//        }
//
//        // if both players have one move, and that move is the same for both, remove the moves from
//        // their lists and the game will be over when testing with isGameOver
//        if (p1Moves.size() == 1 && p2Moves.size() == 1 &&
//                ((((TronMove)p1Moves.get(0)).getFromX() == ((TronMove)p2Moves.get(0)).getFromX()) &&
//                        (((TronMove)p1Moves.get(0)).getToX() == ((TronMove)p2Moves.get(0)).getToX())))
//        {
//            p1Moves.clear();
//            p2Moves.clear();
//        }
//    }
//
//    // this is a helper method getIndex be used ONLY by calcMoves()
//    // if you use the program will crash probably
//    private List<TronMove> getMovesHelper(int pix)
//    {
//        int oppix = pix == 0 ? 1 : 0;
//
//        List<TronMove> moves = new ArrayList<TronMove>();
//
//        // NORTH
//        if (!walls[players[pix].getY() - 1][players[pix].getX()]
//                && (players[oppix].getY() != (players[pix].getY() - 1)
//                || players[pix].getX() != players[oppix].getX())) {
//            moves.add(new TronMove(players[pix].getX(), players[pix].getY(),
//                    players[pix].getX(), players[pix].getY() - 1));
//        }
//        // WEST
//        if (!walls[players[pix].getY()][players[pix].getX() - 1]
//                && (players[pix].getY() != players[oppix].getY()
//                || (players[pix].getX() - 1) != players[oppix].getX())) {
//            moves.add(new TronMove(players[pix].getX(), players[pix].getY(),
//                    players[pix].getX() - 1, players[pix].getY()));
//        }
//        // SOUTH
//        if (!walls[players[pix].getY() + 1][players[pix].getX()]
//                && ((players[pix].getY() + 1) != players[oppix].getY()
//                || players[pix].getX() != players[oppix].getX())) {
//            moves.add(new TronMove(players[pix].getX(), players[pix].getY(),
//                    players[pix].getX(), players[pix].getY() + 1));
//        }
//        // EAST
//        if (!walls[players[pix].getY()][players[pix].getX() + 1]
//                && (players[pix].getY() != players[oppix].getY()
//                || (players[pix].getX() + 1) != players[oppix].getX())) {
//            moves.add(new TronMove(players[pix].getX(), players[pix].getY(),
//                    players[pix].getX() + 1, players[pix].getY()));
//        }
//        return moves;
//    }
//
//    // IBOARD INTERFACE IMPLEMENTATION
//
//    @Override
//    public BoardGameOutcome getOutcome(int player) {
//        return null;
//    }
//
//    @Override
//    public void makeMove(TronMove move)
//    {
//        if (isGameOver()) {
//            System.out.println("The game is already over. You can't make any more moves!");
//            return;
//        }
//
//        int playerIndex = currentPlayer();
//        List<TronMove> moves;
//
//        if (playerIndex == 0)
//            moves = p1Moves;
//        else
//            moves = p2Moves;
//
//        // if the move is illegal
//        if (move == null || !moves.contains(move)) {
//            players[playerIndex].crash();
//            changeTurn();
//            return;
//        }
//
//        // moves the player getIndex the next position
//        int toX = move.getToX();
//        int toY = move.getToY();
//        int fromX = move.getFromX();
//        int fromY = move.getFromY();
//        players[playerIndex].setX(toX);
//        players[playerIndex].setY(toY);
//
//        // put a wall in the current position
//        setWall(fromX, fromY);
//
//        changeTurn();
//    }
//
////    @Override
////    public double evaluate(int playerIndex){
////        int oppix = playerIndex == 0 ? 1 : 0;
////        if (isGameOver()) {
////            if (players[playerIndex].crashed() && players[oppix].crashed())
////                return 0;
////            if (players[oppix].crashed())
////                return 100;
////            if (players[playerIndex].crashed())
////                return -100;
////        }
////        return 0;
////    }
//
//
//
//    @Override
//    public boolean isGameOver() {
//        if (plyCount % 2 == 0)
//            for (TronPlayer p : players)
//                if (p.crashed())
//                    return true;
//        return false;
//    }
//
//    // Must be called after making a move (one player's move)
//    public void changeTurn() {
//        plyCount++;
//        if (!isGameOver() && plyCount % 2 == 0) {
//            notifyObservers();
//            // if both players are in the same position, clear moves
//            if (players[0].getX() == players[1].getX() && players[0].getY() == players[1].getY()) {
//                p1Moves.clear();
//                p2Moves.clear();
//            } else {
//                calcMoves();
//            }
//        }
//    }
//
//    // OBJECT OVERRIDES
//
//    @Override
//    public String toString() {
//        StringBuilder builder = new StringBuilder();
//        for (int row = 0; row < walls.length; row++) {
//            for (int col = 0; col < walls[0].length; col++)
//                System.out.print(cellToChar(col, row));
//            System.out.println("");
//        }
//        return builder.toString();
//    }
//
//
//
//
//
////    private static void playGame() {
////
////        // Maps
//////        TronBoard tronBoard = createGameWithMap("/Users/drobles/Desktop/tron/map1.txt");
////
////        // Random game
////        TronBoard tronBoard = gameWithRandomMap(new MonteCarloAgent(), new RandomAgent());
////
////
////        // Text dr.mauler.tetris.view
////        new TronTextView(tronBoard);
////
////        // Graphical dr.mauler.tetris.view
////        TronGraphicalView tronGraphicalView = new TronGraphicalView(tronBoard);
////        JFrame frame = new JFrame();
////        frame.add(tronGraphicalView);
////        frame.setVisible(true);
////        frame.setSize(700, 700);
////
////        // Plays the game
////        while(!tronBoard.isGameOver())
//////        while (true)
////        {
////            TronPlayer player = (TronPlayer) tronBoard.currentPlayerIndex();
////            TronMove move = (TronMove) player.getAgent().move(tronBoard);
////            tronBoard.makeMove(move);
////            try { Thread.sleep(20); } catch (InterruptedException ignored) { }
////        }
////
////        // Evaluates the players
////        for (TronPlayer p : tronBoard.getPlayers()) {
////            System.out.println("Player " + p + ": " + tronBoard.evaluate(p));
////        }
////
////    }
//
////    public static void main(String[] args) {
////        playGame();
////    }
////
//}
//
//
