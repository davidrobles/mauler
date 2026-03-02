package net.davidrobles.mauler.players.minimax;

public class MoveScore
{
    private int move;
    private double score;

    public MoveScore(double score, int move)
    {
        this.move = move;
        this.score = score;
    }

    public int getMove()
    {
        return move;
    }

    public double getScore()
    {
        return score;
    }
}
