package com.virb3e.pegjumpsolver;

import java.util.ArrayList;
import java.util.List;

public class Board
{
    /*
        o
        * *
        * * *
        * * * *
        * * * * *
     */

    public boolean[][] Table; // row major in Java for ease of use
    public int BestRun = -1;
    private boolean _verbose = false;
    private List<Step> _steps = new ArrayList<>();

    public Board(int size, Coordinates holeCoordinates, boolean verbose)
    {
        _verbose = verbose;

        Table = new boolean[size][size];

        int currentRowSize = 1;

        for (int row = 0; row < size; row++)
        {
            for (int i = 0; i < currentRowSize; i++)
            {
                Table[row][i] = true;
            }

            currentRowSize++;
        }

        MakeHole(holeCoordinates);
    }

    public void MakeHole(Coordinates hole)
    {
        Table[hole.Y][hole.X] = false;
    }

    public boolean Solve()
    {
        _steps.add(new Step(Table, Move.INITIAL));
        return DoSolve();
    }

    private boolean DoSolve()
    {
        Moves moves = new Moves();

        for (int row = 0; row < Table.length; row++)
        {
            for (int column = 0; column < Table[row].length; column++)
            {
                if (Table[row][column] != true) // must be peg
                    continue;

                Coordinates pegCoordinates = new Coordinates(column, row);
                List<Move> availableMoves = moves.AvailableMoves(pegCoordinates);

                if (availableMoves.size() < 1)
                    continue;

                for (Move move : availableMoves)
                {
                    moves.DoMove(pegCoordinates, move);
                    boolean[][] stepTable = Main.Clone2DArray(Table);

                    if (_verbose)
                    {
                        System.out.println();
                        System.out.print(move.toString());
                        Main.PrintBoard(this);
                    }

                    if (DoSolve())
                    {
                        _steps.add(new Step(stepTable, move));
                        return true;
                    } else
                    {
                        int pegs = 0;
                        for (boolean[] row2 : Table)
                        {
                            for (boolean item : row2)
                            {
                                if (item == true) // is peg
                                    pegs++;
                            }
                        }

                        if (BestRun == -1 || BestRun > pegs)
                            BestRun = pegs;

                        if (pegs < 2)
                        {
                            _steps.add(new Step(stepTable, move));
                            return true;
                        }

                        moves.RevertMove(pegCoordinates, move);

                        if (_verbose)
                        {
                            System.out.println();
                            System.out.println("DEAD MOVE! REVERTING.");
                            System.out.println();
                        }
                    }
                }

            }
        }

        return false;
    }

    public List<Step> GetSteps()
    {
        return _steps;
    }

    class Moves
    {
        public List<Move> AvailableMoves(Coordinates pegCoordinate)
        {
            List<Move> availableMoves = new ArrayList<>();

            if (CanMoveUpLeft(pegCoordinate))
                availableMoves.add(Move.UPLEFT);

            if (CanMoveUpRight(pegCoordinate))
                availableMoves.add(Move.UPRIGHT);

            if (CanMoveLeft(pegCoordinate))
                availableMoves.add(Move.LEFT);

            if (CanMoveRight(pegCoordinate))
                availableMoves.add(Move.RIGHT);

            if (CanMoveDownLeft(pegCoordinate))
                availableMoves.add(Move.DOWNLEFT);

            if (CanMoveDownRight(pegCoordinate))
                availableMoves.add(Move.DOWNRIGHT);

            return availableMoves;
        }

        /* UpLeftJump

        B 0 x 0 x
        0 x 0 x 0
        0 0 A 0 0

         */

        public boolean CanMoveUpLeft(Coordinates pegCoordinates)
        {
            Coordinates destination = new Coordinates(pegCoordinates.X - 2, pegCoordinates.Y - 2);
            Coordinates middlePoint = new Coordinates(pegCoordinates.X - 1, pegCoordinates.Y - 1);

            if (destination.X < 0 || destination.X > destination.Y) // out of bounds on X axis
                return false;

            if (destination.Y < 0 || destination.Y >= Table.length) // out of bounds on Y axis
                return false;

            if (Table[destination.Y][destination.X] != false) // not empty destination
                return false;

            if (Table[middlePoint.Y][middlePoint.X] != true) // cannot jump over empty points
                return false;

            return true;
        }

        public boolean CanMoveUpRight(Coordinates pegCoordinates)
        {
            Coordinates destination = new Coordinates(pegCoordinates.X, pegCoordinates.Y - 2);
            Coordinates middlePoint = new Coordinates(pegCoordinates.X, pegCoordinates.Y - 1);

            if (destination.X < 0 || destination.X > destination.Y) // out of bounds on X axis
                return false;

            if (destination.Y < 0 || destination.Y >= Table.length) // out of bounds on Y axis
                return false;

            if (Table[destination.Y][destination.X] != false) // not empty destination
                return false;

            if (Table[middlePoint.Y][middlePoint.X] != true) // cannot jump over empty points
                return false;

            return true;
        }

        public boolean CanMoveLeft(Coordinates pegCoordinates)
        {
            Coordinates destination = new Coordinates(pegCoordinates.X - 2, pegCoordinates.Y);
            Coordinates middlePoint = new Coordinates(pegCoordinates.X - 1, pegCoordinates.Y);

            if (destination.X < 0 || destination.X > destination.Y) // out of bounds on X axis
                return false;

            if (destination.Y < 0 || destination.Y >= Table.length) // out of bounds on Y axis
                return false;

            if (Table[destination.Y][destination.X] != false) // not empty destination
                return false;

            if (Table[middlePoint.Y][middlePoint.X] != true) // cannot jump over empty points
                return false;

            return true;
        }

        public boolean CanMoveRight(Coordinates pegCoordinates)
        {
            Coordinates destination = new Coordinates(pegCoordinates.X + 2, pegCoordinates.Y);
            Coordinates middlePoint = new Coordinates(pegCoordinates.X + 1, pegCoordinates.Y);

            if (destination.X < 0 || destination.X > destination.Y) // out of bounds on X axis
                return false;

            if (destination.Y < 0 || destination.Y >= Table.length) // out of bounds on Y axis
                return false;

            if (Table[destination.Y][destination.X] != false) // not empty destination
                return false;

            if (Table[middlePoint.Y][middlePoint.X] != true) // cannot jump over empty points
                return false;

            return true;
        }

        public boolean CanMoveDownLeft(Coordinates pegCoordinates)
        {
            Coordinates destination = new Coordinates(pegCoordinates.X, pegCoordinates.Y + 2);
            Coordinates middlePoint = new Coordinates(pegCoordinates.X, pegCoordinates.Y + 1);

            if (destination.X < 0 || destination.X > destination.Y) // out of bounds on X axis
                return false;

            if (destination.Y < 0 || destination.Y >= Table.length) // out of bounds on Y axis
                return false;

            if (Table[destination.Y][destination.X] != false) // not empty destination
                return false;

            if (Table[middlePoint.Y][middlePoint.X] != true) // cannot jump over empty points
                return false;

            return true;
        }

        public boolean CanMoveDownRight(Coordinates pegCoordinates)
        {
            Coordinates destination = new Coordinates(pegCoordinates.X + 2, pegCoordinates.Y + 2);
            Coordinates middlePoint = new Coordinates(pegCoordinates.X + 1, pegCoordinates.Y + 1);

            if (destination.X < 0 || destination.X > destination.Y) // out of bounds on X axis
                return false;

            if (destination.Y < 0 || destination.Y >= Table.length) // out of bounds on Y axis
                return false;

            if (Table[destination.Y][destination.X] != false) // not empty destination
                return false;

            if (Table[middlePoint.Y][middlePoint.X] != true) // cannot jump over empty points
                return false;

            return true;
        }

        public void DoMove(Coordinates pegCoordinates, Move move)
        {
            if (move == Move.UPLEFT)
            {
                Table[pegCoordinates.Y][pegCoordinates.X] = false;
                Table[pegCoordinates.Y - 1][pegCoordinates.X - 1] = false;
                Table[pegCoordinates.Y - 2][pegCoordinates.X - 2] = true;
                return;
            }

            if (move == Move.UPRIGHT)
            {
                Table[pegCoordinates.Y][pegCoordinates.X] = false;
                Table[pegCoordinates.Y - 1][pegCoordinates.X] = false;
                Table[pegCoordinates.Y - 2][pegCoordinates.X] = true;
                return;
            }

            if (move == Move.LEFT)
            {
                Table[pegCoordinates.Y][pegCoordinates.X] = false;
                Table[pegCoordinates.Y][pegCoordinates.X - 1] = false;
                Table[pegCoordinates.Y][pegCoordinates.X - 2] = true;
                return;
            }

            if (move == Move.RIGHT)
            {
                Table[pegCoordinates.Y][pegCoordinates.X] = false;
                Table[pegCoordinates.Y][pegCoordinates.X + 1] = false;
                Table[pegCoordinates.Y][pegCoordinates.X + 2] = true;
                return;
            }

            if (move == Move.DOWNLEFT)
            {
                Table[pegCoordinates.Y][pegCoordinates.X] = false;
                Table[pegCoordinates.Y + 1][pegCoordinates.X] = false;
                Table[pegCoordinates.Y + 2][pegCoordinates.X] = true;
                return;
            }

            if (move == Move.DOWNRIGHT)
            {
                Table[pegCoordinates.Y][pegCoordinates.X] = false;
                Table[pegCoordinates.Y + 1][pegCoordinates.X + 1] = false;
                Table[pegCoordinates.Y + 2][pegCoordinates.X + 2] = true;
            }
        }

        public void RevertMove(Coordinates pegCoordinates, Move move)
        {
            //TODO: Save redundant method by transforming the main one to a toggle instead

            if (move == Move.UPLEFT)
            {
                Table[pegCoordinates.Y][pegCoordinates.X] = true;
                Table[pegCoordinates.Y - 1][pegCoordinates.X - 1] = true;
                Table[pegCoordinates.Y - 2][pegCoordinates.X - 2] = false;
                return;
            }

            if (move == Move.UPRIGHT)
            {
                Table[pegCoordinates.Y][pegCoordinates.X] = true;
                Table[pegCoordinates.Y - 1][pegCoordinates.X] = true;
                Table[pegCoordinates.Y - 2][pegCoordinates.X] = false;
                return;
            }

            if (move == Move.LEFT)
            {
                Table[pegCoordinates.Y][pegCoordinates.X] = true;
                Table[pegCoordinates.Y][pegCoordinates.X - 1] = true;
                Table[pegCoordinates.Y][pegCoordinates.X - 2] = false;
                return;
            }

            if (move == Move.RIGHT)
            {
                Table[pegCoordinates.Y][pegCoordinates.X] = true;
                Table[pegCoordinates.Y][pegCoordinates.X + 1] = true;
                Table[pegCoordinates.Y][pegCoordinates.X + 2] = false;
                return;
            }

            if (move == Move.DOWNLEFT)
            {
                Table[pegCoordinates.Y][pegCoordinates.X] = true;
                Table[pegCoordinates.Y + 1][pegCoordinates.X] = true;
                Table[pegCoordinates.Y + 2][pegCoordinates.X] = false;
                return;
            }

            if (move == Move.DOWNRIGHT)
            {
                Table[pegCoordinates.Y][pegCoordinates.X] = true;
                Table[pegCoordinates.Y + 1][pegCoordinates.X + 1] = true;
                Table[pegCoordinates.Y + 2][pegCoordinates.X + 2] = false;
            }
        }
    }
}
