package net.davidrobles.mauler.loa;

import net.davidrobles.mauler.core.MatchController;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

public class LOATableModel implements TableModel
{
    private static final String[] columnNames = {"Turn", "Black", "White"};
    private MatchController<LOA> controller;

    public LOATableModel(MatchController<LOA> controller)
    {
        this.controller = controller;
    }

    @Override
    public int getRowCount()
    {
        return controller.getSize() / 2 + 1;
    }

    @Override
    public int getColumnCount()
    {
        return 3;
    }

    @Override
    public String getColumnName(int columnIndex)
    {
        return columnNames[columnIndex];
    }

    @Override
    public Class<?> getColumnClass(int columnIndex)
    {
        return String.class;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex)
    {
        return false;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex)
    {
        // first row
        if (rowIndex == 0)
        {
            if (columnIndex == 0)
                return "Start";
            else if (columnIndex == 1)
                return "";
            else if (columnIndex == 2)
                return "";
        }

        // first column
        if (columnIndex == 0)
            return String.valueOf(rowIndex);

        int gameIndex = (rowIndex * 2) - 1 + (columnIndex - 1);
        int moveIndex = gameIndex - 1;

        if (gameIndex < controller.getSize())
            return String.valueOf(controller.getMove(moveIndex));

        return "";
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex)
    {

    }

    @Override
    public void addTableModelListener(TableModelListener l)
    {

    }

    @Override
    public void removeTableModelListener(TableModelListener l)
    {

    }
}

