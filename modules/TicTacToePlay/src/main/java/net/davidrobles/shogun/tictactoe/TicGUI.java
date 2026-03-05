package net.davidrobles.shogun.tictactoe;

import java.util.List;
import javax.swing.table.TableModel;
import net.davidrobles.mauler.core.MatchController;
import net.davidrobles.mauler.core.Strategy;
import net.davidrobles.mauler.gui.BoardApp;
import net.davidrobles.mauler.gui.MatchControllerButtonsView;
import net.davidrobles.mauler.gui.MatchControllerSliderView;
import net.davidrobles.mauler.gui.MatchControllerTableView;
import net.davidrobles.mauler.strategies.RandomStrategy;
import net.davidrobles.mauler.tictactoe.TicTacToe;
import net.davidrobles.util.DRUtil;

public class TicGUI {
    public static void main(String[] args) {
        List<Strategy<TicTacToe>> players = List.of(new RandomStrategy<>(), new RandomStrategy<>());

        TicTacToeView panel = new TicTacToeView(new TicTacToe());
        MatchController<TicTacToe> mc = new MatchController<>(TicTacToe::new, players, 50);
        MatchControllerButtonsView<TicTacToe> buttonsView = new MatchControllerButtonsView<>(mc);
        MatchControllerSliderView<TicTacToe> sliderView = new MatchControllerSliderView<>(mc);
        TableModel model = new TicTacToeTableModel(mc);
        MatchControllerTableView<TicTacToe> mcTableView = new MatchControllerTableView<>(model, mc);
        mc.registerObserver(mcTableView);
        mc.registerObserver(buttonsView);
        mc.registerObserver(sliderView);
        mc.registerObserver(panel);
        BoardApp<TicTacToe> boardApp =
                new BoardApp<>(panel, mc, buttonsView, sliderView, mcTableView);
        DRUtil.centerJFrame(boardApp);
    }
}
