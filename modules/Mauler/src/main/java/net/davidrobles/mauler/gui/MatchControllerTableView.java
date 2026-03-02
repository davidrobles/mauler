package net.davidrobles.mauler.gui;

import net.davidrobles.mauler.core.Game;
import net.davidrobles.mauler.core.MoveObservable;
import net.davidrobles.mauler.core.MatchController;
import net.davidrobles.mauler.core.MatchControllerObserver;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableModel;
import java.awt.*;

public class MatchControllerTableView<GAME extends Game<GAME> & MoveObservable> extends JPanel
        implements MatchControllerObserver<GAME>
{
    private JTable table;
    private MatchController<GAME> match;

    public MatchControllerTableView(TableModel model, MatchController<GAME> match)
    {
        this.match = match;
        match.registerObserver(this);
        table = new JTable(model);
        table.setDefaultRenderer(String.class, new CustomRenderer());
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane);
    }

    @Override
    public void update(GAME game)
    {
        table.tableChanged(null);

    }

    class CustomRenderer extends DefaultTableCellRenderer
    {
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
        {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            if (row > 0 && column > 0 && match.getCurrentIndex() == ((row - 1) * 2 + column))
                c.setBackground(Color.YELLOW);
            else
                c.setBackground(Color.WHITE);

            return c;
        }
    }
}
