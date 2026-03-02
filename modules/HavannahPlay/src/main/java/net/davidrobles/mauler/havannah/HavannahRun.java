package net.davidrobles.mauler.havannah;

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

public class HavannahRun
{
    private static Random rng = new Random();

    public static void runGUI()
    {
        Havannah havannah = new Havannah(8);
        List<Player<Havannah>> players = new ArrayList<Player<Havannah>>() {{
            add(new RandPlayer<Havannah>(rng));
            add(new RandPlayer<Havannah>(rng));
        }};

        HavannahView panel = new HavannahView(havannah);
        MatchController<Havannah> mc = new MatchController<Havannah>(havannah, players, 50);
        MatchControllerButtonsView<Havannah> buttonsView = new MatchControllerButtonsView<Havannah>(mc);
        MatchControllerSliderView<Havannah> sliderView = new MatchControllerSliderView<Havannah>(mc);
        TableModel model = new HavannahTableModel(mc);
        MatchControllerTableView<Havannah> mcTableView = new MatchControllerTableView<Havannah>(model, mc);
        mc.registerObserver(mcTableView);
        mc.registerObserver(buttonsView);
        mc.registerObserver(sliderView);
        mc.registerObserver(panel);
        BoardApp<Havannah> boardApp = new BoardApp<Havannah>(panel, mc, buttonsView, sliderView, mcTableView);
        DRUtil.centerJFrame(boardApp);
    }    

    public static void test()
    {
        Havannah havannah = new Havannah(8);
//        System.out.println(havannah);

        while (!havannah.isOver())
        {
            havannah.makeMove(rng.nextInt(havannah.getNumMoves()));
//            System.out.println(havannah);
        }

        HavannahView view = new HavannahView(havannah);
        JFrame frame = new JFrame();
        frame.add(view);
        frame.setVisible(true);
        frame.pack();
        DRUtil.centerJFrame(frame);
    }

    public static void main(String[] args) {
//        GamesUtil.playRandomGame(new Havannah(4));
//        GamesUtil.playerSpeed(new Havannah(6), 10000);
        runGUI();
//        test();
    }
}
