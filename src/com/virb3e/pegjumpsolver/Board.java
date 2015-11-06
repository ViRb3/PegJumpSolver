package com.virb3e.pegjumpsolver;

import java.util.ArrayList;
import java.util.List;

public class Board
{
    public int[][] Table; // row major in Java for ease of use
    public int BestRun = -1;
    private boolean _debug = false;
    private List<int[][]> _steps = new ArrayList<>();

    public Board(int rows, Coordinates holeCoordinates, boolean debug)
    {
        _debug = debug;

        int columns = rows * 2;
        Table = new int[rows][columns];

        int currentRowSize = columns;

        for (int row = 0; row < rows; row++)
        {
            int value = 1;
            for (int i = columns - currentRowSize; i < currentRowSize; i++)
            {
                Table[row][i] = value;
                value = value == 0 ? 1 : 0;
            }

            currentRowSize--;
        }

        FillBoard();
        MakeHole(holeCoordinates);
    }

    public void FillBoard()
    {
        for (int row = 0; row < Table.length; row++)
        {
            for (int column = 0; column < Table[row].length; column++)
            {
                int current = Table[row][column];

                if (current != 1) // must be empty
                    continue;

                Table[row][column] = 2;
            }
        }
    }

    public void MakeHole(Coordinates hole)
    {
        Table[hole.Y][hole.X] = 1;
    }

    public boolean Solve()
    {
        _steps.add(Table);
        return DoSolve();
    }

    private boolean DoSolve()
    {
        Moves moves = new Moves();

        for (int row = 0; row < Table.length; row++)
        {
            for (int column = 0; column < Table[row].length; column++)
            {
                if (Table[row][column] != 2) // must be peg
                    continue;

                Coordinates pegCoordinates = new Coordinates(column, row);
                List<Move> availableMoves = moves.AvailableMoves(pegCoordinates);

                if (availableMoves.size() < 1)
                    continue;

                for (Move move : availableMoves)
                {
                    moves.DoMove(pegCoordinates, move);
                    int[][] stepTable = Main.Clone2DArray(Table);

                    if (_debug)
                    {
                        Main.PrintNewLine();
                        System.out.print(move.toString());
                        Main.PrintBoard(this);
                    }

                    if (DoSolve())
                    {
                        _steps.add(stepTable);
                        return true;
                    } else
                    {
                        int pegs = 0;
                        for (int[] row2 : Table)
                        {
                            for (int item : row2)
                            {
                                if (item == 2)
                                    pegs++;
                            }
                        }

                        if (BestRun == -1 || BestRun > pegs)
                            BestRun = pegs;

                        if (pegs < 2)
                        {
                            _steps.add(stepTable);
                            return true;
                        }

                        moves.RevertMove(pegCoordinates, move);

                        if (_debug)
                        {
                            Main.PrintNewLine();
                            Main.PrintNewLine("DEAD MOVE! REVERTING.");
                            Main.PrintNewLine();
                        }
                    }
                }

            }
        }

        return false;
    }

    public List<int[][]> GetSteps()
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

            if (destination.X < 0 || destination.X >= Table[0].length) // out of bounds on X axis
                return false;

            if (destination.Y < 0 || destination.Y >= Table.length) // out of bounds on Y axis
                return false;

            if (Table[destination.Y][destination.X] != 1) // not empty destination
                return false;

            if (Table[middlePoint.Y][middlePoint.X] != 2) // cannot jump over empty points
                return false;

            return true;
        }

        public boolean CanMoveUpRight(Coordinates pegCoordinates)
        {
            Coordinates destination = new Coordinates(pegCoordinates.X + 2, pegCoordinates.Y - 2);
            Coordinates middlePoint = new Coordinates(pegCoordinates.X + 1, pegCoordinates.Y - 1);

            if (destination.X < 0 || destination.X >= Table[0].length) // out of bounds on X axis
                return false;

            if (destination.Y < 0 || destination.Y >= Table.length) // out of bounds on Y axis
                return false;

            if (Table[destination.Y][destination.X] != 1) // not empty destination
                return false;

            if (Table[middlePoint.Y][middlePoint.X] != 2) // cannot jump over empty points
                return false;

            return true;
        }

        public boolean CanMoveLeft(Coordinates pegCoordinates)
        {
            Coordinates destination = new Coordinates(pegCoordinates.X - 4, pegCoordinates.Y);
            Coordinates middlePoint = new Coordinates(pegCoordinates.X - 2, pegCoordinates.Y);

            if (destination.X < 0 || destination.X >= Table[0].length) // out of bounds on X axis
                return false;

            if (destination.Y < 0 || destination.Y >= Table.length) // out of bounds on Y axis
                return false;

            if (Table[destination.Y][destination.X] != 1) // not empty destination
                return false;

            if (Table[middlePoint.Y][middlePoint.X] != 2) // cannot jump over empty points
                return false;

            return true;
        }

        public boolean CanMoveRight(Coordinates pegCoordinates)
        {
            Coordinates destination = new Coordinates(pegCoordinates.X + 4, pegCoordinates.Y);
            Coordinates middlePoint = new Coordinates(pegCoordinates.X + 2, pegCoordinates.Y);

            if (destination.X < 0 || destination.X >= Table[0].length) // out of bounds on X axis
                return false;

            if (destination.Y < 0 || destination.Y >= Table.length) // out of bounds on Y axis
                return false;

            if (Table[destination.Y][destination.X] != 1) // not empty destination
                return false;

            if (Table[middlePoint.Y][middlePoint.X] != 2) // cannot jump over empty points
                return false;

            return true;
        }

        public boolean CanMoveDownLeft(Coordinates pegCoordinates)
        {
            Coordinates destination = new Coordinates(pegCoordinates.X - 2, pegCoordinates.Y + 2);
            Coordinates middlePoint = new Coordinates(pegCoordinates.X - 1, pegCoordinates.Y + 1);

            if (destination.X < 0 || destination.X >= Table[0].length) // out of bounds on X axis
                return false;

            if (destination.Y < 0 || destination.Y >= Table.length) // out of bounds on Y axis
                return false;

            if (Table[destination.Y][destination.X] != 1) // not empty destination
                return false;

            if (Table[middlePoint.Y][middlePoint.X] != 2) // cannot jump over empty points
                return false;

            return true;
        }

        public boolean CanMoveDownRight(Coordinates pegCoordinates)
        {
            Coordinates destination = new Coordinates(pegCoordinates.X + 2, pegCoordinates.Y + 2);
            Coordinates middlePoint = new Coordinates(pegCoordinates.X + 1, pegCoordinates.Y + 1);

            if (destination.X < 0 || destination.X >= Table[0].length) // out of bounds on X axis
                return false;

            if (destination.Y < 0 || destination.Y >= Table.length) // out of bounds on Y axis
                return false;

            if (Table[destination.Y][destination.X] != 1) // not empty destination
                return false;

            if (Table[middlePoint.Y][middlePoint.X] != 2) // cannot jump over empty points
                return false;

            return true;
        }

        public void DoMove(Coordinates pegCoordinates, Move move)
        {
            if (move == Move.UPLEFT)
            {
                Table[pegCoordinates.Y][pegCoordinates.X] = 1;
                Table[pegCoordinates.Y - 1][pegCoordinates.X - 1] = 1;
                Table[pegCoordinates.Y - 2][pegCoordinates.X - 2] = 2;
                return;
            }

            if (move == Move.UPRIGHT)
            {
                Table[pegCoordinates.Y][pegCoordinates.X] = 1;
                Table[pegCoordinates.Y - 1][pegCoordinates.X + 1] = 1;
                Table[pegCoordinates.Y - 2][pegCoordinates.X + 2] = 2;
                return;
            }

            if (move == Move.LEFT)
            {
                Table[pegCoordinates.Y][pegCoordinates.X] = 1;
                Table[pegCoordinates.Y][pegCoordinates.X - 2] = 1;
                Table[pegCoordinates.Y][pegCoordinates.X - 4] = 2;
                return;
            }

            if (move == Move.RIGHT)
            {
                Table[pegCoordinates.Y][pegCoordinates.X] = 1;
                Table[pegCoordinates.Y][pegCoordinates.X + 2] = 1;
                Table[pegCoordinates.Y][pegCoordinates.X + 4] = 2;
                return;
            }

            if (move == Move.DOWNLEFT)
            {
                Table[pegCoordinates.Y][pegCoordinates.X] = 1;
                Table[pegCoordinates.Y + 1][pegCoordinates.X - 1] = 1;
                Table[pegCoordinates.Y + 2][pegCoordinates.X - 2] = 2;
                return;
            }

            if (move == Move.DOWNRIGHT)
            {
                Table[pegCoordinates.Y][pegCoordinates.X] = 1;
                Table[pegCoordinates.Y + 1][pegCoordinates.X + 1] = 1;
                Table[pegCoordinates.Y + 2][pegCoordinates.X + 2] = 2;
            }
        }

        public void RevertMove(Coordinates pegCoordinates, Move move)
        {
            /* UpLeftJump

            B 0 x 0 x
            0 x 0 x 0
            0 0 A 0 0

             */

            /* UpLeftJumpResult

            A 0 x 0 x
            0 1 0 x 0
            0 0 1 0 0

             */

            if (move == Move.UPLEFT)
            {
                Table[pegCoordinates.Y][pegCoordinates.X] = 2;
                Table[pegCoordinates.Y - 1][pegCoordinates.X - 1] = 2;
                Table[pegCoordinates.Y - 2][pegCoordinates.X - 2] = 1;
                return;
            }

            if (move == Move.UPRIGHT)
            {
                Table[pegCoordinates.Y][pegCoordinates.X] = 2;
                Table[pegCoordinates.Y - 1][pegCoordinates.X + 1] = 2;
                Table[pegCoordinates.Y - 2][pegCoordinates.X + 2] = 1;
                return;
            }

            if (move == Move.LEFT)
            {
                Table[pegCoordinates.Y][pegCoordinates.X] = 2;
                Table[pegCoordinates.Y][pegCoordinates.X - 2] = 2;
                Table[pegCoordinates.Y][pegCoordinates.X - 4] = 1;
                return;
            }

            if (move == Move.RIGHT)
            {
                Table[pegCoordinates.Y][pegCoordinates.X] = 2;
                Table[pegCoordinates.Y][pegCoordinates.X + 2] = 2;
                Table[pegCoordinates.Y][pegCoordinates.X + 4] = 1;
                return;
            }

            if (move == Move.DOWNLEFT)
            {
                Table[pegCoordinates.Y][pegCoordinates.X] = 2;
                Table[pegCoordinates.Y + 1][pegCoordinates.X - 1] = 2;
                Table[pegCoordinates.Y + 2][pegCoordinates.X - 2] = 1;
                return;
            }

            if (move == Move.DOWNRIGHT)
            {
                Table[pegCoordinates.Y][pegCoordinates.X] = 2;
                Table[pegCoordinates.Y + 1][pegCoordinates.X + 1] = 2;
                Table[pegCoordinates.Y + 2][pegCoordinates.X + 2] = 1;
            }
        }
    }
}
