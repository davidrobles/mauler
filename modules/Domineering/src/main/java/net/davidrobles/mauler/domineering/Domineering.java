package net.davidrobles.mauler.domineering;

import net.davidrobles.mauler.core.Game;
import net.davidrobles.mauler.core.Outcome;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Domineering implements Game<Domineering>
{
    private long vBoard, hBoard;
    private int turn = 0;

    public static int[] vertMoves = new int[56];
    public static long[] vertBitMoves = new long[56];

    public static int[] horMoves = new int[56];
    public static long[] horBitMoves = new long[56];

    // TODO move

    private List<Long> bitMoves;

    static
    {
        initVertBitMoves();
        initHorBitMoves();
    }

    public Domineering()
    {
        reset();
    }

    private static void initVertBitMoves()
    {
        for (int i = 0; i < 56; i++)
        {
            vertMoves[i] = i;
            vertBitMoves[i] = (1L << i) | (1L << (i + 8));
        }
    }

    private static void initHorBitMoves()
    {
        for (int i = 0; i < 56; i++)
        {
            int index = (i / 7 * 8) + (i % 7);
            horMoves[i] = index;
            horBitMoves[i] = (1L << index) | (1L << (index + 1));
        }
    }

    public List<Long> bitMoves()
    {
        if (bitMoves != null)
            return bitMoves;

        long mergedBitBoard = vBoard | hBoard;
        long[] moves = getCurPlayer() == 0 ? vertBitMoves : horBitMoves;
        return bitMoves(mergedBitBoard, moves);
    }

    private List<Long> bitMoves(long bitBoard, long[] cachedMoves)
    {
        bitMoves = new ArrayList<Long>();

        for (int i = 0; i < cachedMoves.length; i++)
            if (((bitBoard & cachedMoves[i]) == 0))
                bitMoves.add(cachedMoves[i]);

        return bitMoves;
    }

    private void makeMove(long bitMove)
    {
        if (getCurPlayer() == 0)
            vBoard |= bitMove;
        else
            hBoard |= bitMove;

        bitMoves = null; // TODO move somewhere else?
    }

    public static void printBitBoard(long bitBoard)
    {
        for (long i = 0; i < 64; i++)
        {
            if ((bitBoard & (1L << i)) != 0)
                System.out.print("X");
            else
                System.out.print("-");

            if (i % 8 == 7)
                System.out.println();
        }

        System.out.println("\n");
    }

    public void setBoard(String player, String[] board)
    {
        turn = player.equals("v") ? 0 : 1;
        vBoard = hBoard = 0L;

        for (int row = 0; row < board.length; row++)
        {
            char[] charArray = board[row].toCharArray();

            for (int col = 0; col < charArray.length; col++)
            {
                char c = charArray[col];
                int index = (row * 8) + col;

                if (c == 'v')
                    vBoard |= (1L << index);
                else if (c == 'h')
                    hBoard |= (1L << index);
            }
        }
    }

    //////////
    // Game //
    //////////

    @Override
    public Domineering copy()
    {
        Domineering dom = new Domineering();
        dom.vBoard = vBoard;
        dom.hBoard = hBoard;
        dom.turn = turn;
        return dom;
    }

    @Override
    public int getCurPlayer()
    {
        return turn % 2;
    }

    @Override
    public String[] getMoves()
    {
        List<Long> bitMoves = bitMoves();
        String[] moves = new String[bitMoves.size()];

        for (int i = 0; i < bitMoves.size(); i++)
        {
            long bitMove = bitMoves.get(i);
            int test = Long.numberOfTrailingZeros(bitMove);
            moves[i] = "" + (test / 8) + " " + (test % 8);
        }

        return moves;
    }

    @Override
    public int getNumMoves()
    {
        return bitMoves().size();
    }

    @Override
    public String getName()
    {
        return "net.davidrobles.mauler.domineering.Domineering";
    }

    @Override
    public int getNumPlayers()
    {
        return 2;
    }

    @Override
    public Outcome[] getOutcome()
    {
        if (!isOver())
           return new Outcome[] { Outcome.NA, Outcome.NA };

        if (getCurPlayer() == 0)
            return new Outcome[] { Outcome.LOSS, Outcome.WIN };

        return new Outcome[] { Outcome.WIN, Outcome.LOSS };
    }

    @Override
    public boolean isOver()
    {
        return getNumMoves() == 0;
    }

    @Override
    public void makeMove(int move)
    {
        List<Long> bitMoves = bitMoves();
        long bitMove = bitMoves.get(move);
        makeMove(bitMove);
        turn++;
        bitMoves();
    }

    @Override
    public Domineering newInstance()
    {
        return new Domineering();
    }

    @Override
    public void reset()
    {
        vBoard = hBoard = 0L;
        turn = 0;
        bitMoves = null;
    }

    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("Cur. player: " + getCurPlayer() + "\n");
        builder.append("Num. moves: " + getNumMoves() + "\n");
        builder.append("Moves: " + Arrays.toString(getMoves()) + "\n\n");

        for (int i = 0; i < 64; i++)
        {
            if ((vBoard & (1L << i)) > 0)
                builder.append("v");
            else if (((hBoard & (1L << i)) != 0))
                builder.append("h");
//            else if (((legal & (1L << i)) != 0))
//                builder.append("X");
            else
                builder.append("-");

            if (i % 8 == 7)
                builder.append("\n");
        }

        return builder.toString();
    }

//    public static void main(String[] args)
//    {
////        long test = (1L << 4);
////        System.out.println(Long.numberOfLeadingZeros(test));
//
////        testSet();
//
////        Random rnd = new Random();
////        printBitBoard(dom.legal);
//
////        System.out.println(dom);
////        printBitBoard(dom.vBoard);
////        printBitBoard(dom.hBoard);
//
////        dom.makeMove(rnd.nextInt(dom.getNumMoves()));
////        System.out.println(dom);
////        dom.makeMove(rnd.nextInt(dom.getNumMoves()));
////        System.out.println(dom);
//
////        GamesUtil.playRandomGame(new net.davidrobles.mauler.domineering.Domineering());
////        MonteCarlo<net.davidrobles.mauler.domineering.Domineering> mc = new MonteCarlo<net.davidrobles.mauler.domineering.Domineering>(500);
//        UCT<net.davidrobles.mauler.domineering.Domineering> uct = new UCT<net.davidrobles.mauler.domineering.Domineering>(0.5, 2000);
//        AlphaBeta<net.davidrobles.mauler.domineering.Domineering> ab = new AlphaBeta<net.davidrobles.mauler.domineering.Domineering>(new UtilFunc());
//        RandPlayer<net.davidrobles.mauler.domineering.Domineering> randPlayer = new RandPlayer<net.davidrobles.mauler.domineering.Domineering>();
//        List<Player<net.davidrobles.mauler.domineering.Domineering>> players = new ArrayList<Player<net.davidrobles.mauler.domineering.Domineering>>();
//        players.add(ab);
//        players.add(randPlayer);
//        net.davidrobles.mauler.domineering.Domineering dom = new net.davidrobles.mauler.domineering.Domineering();
//
//        while (dom.getNumMoves() > 16)
//        {
//            dom.makeMove(randPlayer.move(dom));
//            System.out.println(dom);
//        }
//
//
////        GamesUtil.playGame(dom, players, false, true, true, 0);
//
//        while (!dom.isOver())
//        {
//            dom.makeMove(players.get(dom.getCurPlayer()).move(dom));
//            System.out.println(dom);
//
//        }
//
////        play();
//    }
//
//    static void nextMove(String player, String[] board)
//    {
//        net.davidrobles.mauler.domineering.Domineering domineering = new net.davidrobles.mauler.domineering.Domineering();
//        domineering.setBoard(player, board);
//
//        int move;
//
//        if (domineering.getNumMoves() > 16)
//        {
//            UCT<net.davidrobles.mauler.domineering.Domineering> uct = new UCT<net.davidrobles.mauler.domineering.Domineering>(0.5, 2000);
//            move = uct.move(domineering);
//        }
//        else
//        {
//            AlphaBeta<net.davidrobles.mauler.domineering.Domineering> ab = new AlphaBeta<net.davidrobles.mauler.domineering.Domineering>(new UtilFunc());
//            move = ab.move(domineering);
//        }
//
//        String strMove = domineering.getMoves()[move];
//        System.out.println(strMove);
//    }
//
//    public static void play()
//    {
//        net.davidrobles.mauler.domineering.Domineering domineering = new net.davidrobles.mauler.domineering.Domineering();
////        MonteCarlo<net.davidrobles.mauler.domineering.Domineering> mc = new MonteCarlo<net.davidrobles.mauler.domineering.Domineering>(000);
//        UCT<net.davidrobles.mauler.domineering.Domineering> uct = new UCT<net.davidrobles.mauler.domineering.Domineering>(0.5, 5000);
//        AlphaBeta<net.davidrobles.mauler.domineering.Domineering> ab = new AlphaBeta<net.davidrobles.mauler.domineering.Domineering>(new UtilFunc());
//        RandPlayer<net.davidrobles.mauler.domineering.Domineering> randPlayer = new RandPlayer<net.davidrobles.mauler.domineering.Domineering>();
//        List<Player<net.davidrobles.mauler.domineering.Domineering>> players = new ArrayList<Player<net.davidrobles.mauler.domineering.Domineering>>();
//        players.add(uct);
//        players.add(randPlayer);
//
//
//        for (int i = 0; i < 10; i++)
//        {
//            domineering.reset();
//
//            while (!domineering.isOver())
//                domineering.makeMove(players.get(domineering.getCurPlayer()).move(domineering));
//
//            System.out.println(Arrays.toString(domineering.getOutcome()));
//        }
//    }
}
