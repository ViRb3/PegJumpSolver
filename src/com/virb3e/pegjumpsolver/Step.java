package com.virb3e.pegjumpsolver;

public class Step
{
    private boolean[][] _table;
    private Move _move;

    public Step(boolean[][] table, Move move)
    {
        _table = table;
        _move = move;
    }

    public boolean[][] GetTable()
    {
        return _table;
    }

    public Move GetMove()
    {
        return _move;
    }
}
