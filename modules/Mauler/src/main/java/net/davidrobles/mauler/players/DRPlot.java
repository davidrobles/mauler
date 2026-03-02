package net.davidrobles.mauler.players;

public class DRPlot
{
    private double[][] data;
    private Object[] rowNames, colNames;

    public DRPlot(Object[] rowNames, Object[] colNames)
    {
        this.rowNames = rowNames;
        this.colNames = colNames;
        data = new double[rowNames.length][colNames.length];
    }

    public void setData(int row, int col, double value)
    {
        data[row][col] = value;
    }

    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();

        builder.append(",");

        for (int i = 0; i < colNames.length; i++)
        {
            builder.append(colNames[i]);

            if (i == colNames.length - 1)
                builder.append("\n");
            else
                builder.append(",");
        }

        for (int row = 0; row < rowNames.length; row++)
        {
            for (int col = -1; col < colNames.length; col++)
            {
                if (col == -1)
                    builder.append(rowNames[row] + ",");
                else if (col == colNames.length - 1)
                    builder.append(data[row][col] + "\n");
                else
                    builder.append(data[row][col] + ",");
            }
        }

        return builder.toString();
    }

    public static void main(String[] args)
    {
        String[] colNames = { "David", "Alex", "Pepe", "Raul", "Spyros", "Philipp" };
        String[] rowNames = { "0.1", "0.001", "0.0001" };

        DRPlot plot = new DRPlot(rowNames, colNames);
        System.out.println(plot);
    }
}
