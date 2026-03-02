package net.davidrobles.mauler.othello.ef.wpc;

public enum WPCType
{
    SYM(10), ASYM(64);

    private int size;

    WPCType(int size)
    {
        this.size = size;
    }

    public int getSize()
    {
        return size;
    }
}
