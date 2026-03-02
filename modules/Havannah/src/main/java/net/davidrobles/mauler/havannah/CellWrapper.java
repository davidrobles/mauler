package net.davidrobles.mauler.havannah;

public class CellWrapper
{
    private HCell cell;
    public Cell type;

    public CellWrapper(HCell cell, Cell type) {
        this.cell = cell;
        this.type = type;
    }
    
    public int getRow() {
        return cell.row;
    }
    
    public int getCol() {
        return cell.col;
    }
    
    public int getIndex() {
        return cell.index;
    }

    public Cell getType() {
        return type;
    }

    public CellWrapper copy() {
        return new CellWrapper(cell, type);
    }
}
