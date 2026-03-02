package net.davidrobles.mauler.othello;

import net.davidrobles.mauler.core.MatchController;
import net.davidrobles.mauler.players.Player;
import net.davidrobles.mauler.players.RandPlayer;
import net.davidrobles.mauler.gui.BoardApp;
import net.davidrobles.mauler.gui.MatchControllerButtonsView;
import net.davidrobles.mauler.gui.MatchControllerSliderView;
import net.davidrobles.mauler.gui.MatchControllerTableView;
import net.davidrobles.util.DRUtil;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class OthelloGUI
{
    private static final Random rng = new Random();

    public static void runTest()
    {
        Othello othello = new Othello();
//        long blackBB = -1089623802071814462L;
//        long whiteBB = 1089623802054971449L;
//        othello.setBoard(blackBB, whiteBB);
        List<Player<Othello>> players = new ArrayList<Player<Othello>>()
        {{
            add(new RandPlayer<Othello>(rng));
            add(new RandPlayer<Othello>(rng));
        }};

        OthelloView panel = new OthelloView(othello);
        MatchController<Othello> mc = new MatchController<Othello>(othello, players, 50);
        MatchControllerButtonsView<Othello> buttonsView = new MatchControllerButtonsView<Othello>(mc);
        MatchControllerSliderView<Othello> sliderView = new MatchControllerSliderView<Othello>(mc);
        TableModel model = new OthelloTableModel(mc);
        MatchControllerTableView<Othello> mcTableView = new MatchControllerTableView<Othello>(model, mc);
        mc.registerObserver(mcTableView);
        mc.registerObserver(buttonsView);
        mc.registerObserver(sliderView);
        mc.registerObserver(panel);
        BoardApp<Othello> boardApp = new BoardApp<Othello>(panel, mc, buttonsView, sliderView, mcTableView);
        DRUtil.centerJFrame(boardApp);
    }

    public static void runBasic()
    {
        Othello othello = new Othello();

        List<Player<Othello>> players = new ArrayList<Player<Othello>>()
        {{
            add(new RandPlayer<Othello>(rng));
            add(new RandPlayer<Othello>(rng));
        }};

        OthelloView panel = new OthelloView(othello);
        MatchController<Othello> match = new MatchController<Othello>(othello, players, 50);
        match.registerObserver(panel);
        JFrame frame = new JFrame();
        frame.add(panel);
        frame.pack();
        frame.setVisible(true);

        while (!othello.isOver())
        {
            match.next();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    public static void main(String[] args)
    {
        runTest();
//        runBasic();
    }
}
