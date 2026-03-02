package net.davidrobles.mauler.havannah;

public class HCell
{
    public int row, col, index;
    public Cell type;

    public HCell(int row, int col, Cell type)
    {
        this.row = row;
        this.col = col;
        this.type = type;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HCell hCell = (HCell) o;
        if (col != hCell.col) return false;
        if (row != hCell.row) return false;
        return true;
    }

    @Override
    public int hashCode()
    {
        int result = row;
        result = 31 * result + col;
        return result;
    }
}
