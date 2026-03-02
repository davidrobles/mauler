//package dr.shogun.tictactoe;
//
//import MatchController;
//import dr.mauler.core.players.Player;
//import dr.mauler.core.players.RandPlayer;
//import BoardApp;
//import MatchControllerButtonsView;
//import MatchControllerSliderView;
//import MatchControllerTableView;
//import TicTacToe;
//import TicTacToeTableModel;
//import TicTacToeView;
//
//import javax.swing.table.TableModel;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Random;
//
//public class TicGUI
//{
//    private static final Random rng = new Random();
//
//    public static void runTest()
//    {
//        TicTacToe tic = new TicTacToe();
//        List<Player<TicTacToe>> players = new ArrayList<Player<TicTacToe>>()
//        {{
//            add(new RandPlayer<TicTacToe>(rng));
//            add(new RandPlayer<TicTacToe>(rng));
//        }};
//
//        TicTacToeView panel = new TicTacToeView(tic);
//        MatchController<TicTacToe> mc = new MatchController<TicTacToe>(tic, players, 50);
//        MatchControllerButtonsView<TicTacToe> buttonsView = new MatchControllerButtonsView<TicTacToe>(mc);
//        MatchControllerSliderView<TicTacToe> sliderView = new MatchControllerSliderView<TicTacToe>(mc);
//        TableModel model = new TicTacToeTableModel(mc);
//        MatchControllerTableView<TicTacToe> mcTableView = new MatchControllerTableView<TicTacToe>(model, mc);
//        mc.registerObserver(mcTableView);
//        mc.registerObserver(buttonsView);
//        mc.registerObserver(sliderView);
//        mc.registerObserver(panel);
//        new BoardApp<TicTacToe>(panel, mc, buttonsView, sliderView, mcTableView);
//    }
//
//    public static void main(String[] args)
//    {
//        runTest();
//    }
//}
