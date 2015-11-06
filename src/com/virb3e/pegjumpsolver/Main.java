package com.virb3e.pegjumpsolver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class Main
{
    public static void main(String[] args)
    {
        PrintNewLine();
        PrintNewLine("-----====================================-----");
        PrintNewLine("Peg Jump Solver v1.0   ~ViR");
        PrintNewLine("-----====================================-----");
        PrintNewLine();
        PrintNewLine("Size of board:");

        int boardSize = ReadConsoleNumber();

        if (boardSize == -1)
            return;
        if (boardSize < 3)
        {
            System.err.println("Invalid input! Board size cannot be less than 3!");
            return;
        }

        PrintNewLine("X coordinate of initial hole:");

        int x = ReadConsoleNumber();
        if (x < 0)
        {
            System.err.println("Invalid input! X cannot be less than 0!");
            return;
        }

        PrintNewLine("Y coordinate of initial hole:");

        int y = ReadConsoleNumber();
        if (y < 0)
        {
            System.err.println("Invalid input! Y cannot be less than 0!");
            return;
        }

        PrintNewLine("Do you want to enable verbose printing?");

        String debugString = ReadConsole();
        if (debugString == null)
            return;

        boolean debug = false;

        if (debugString.toLowerCase().equals("y") || debugString.toLowerCase().equals("yes"))
            debug = true;

        PrintNewLine();
        PrintNewLine();

        Run(boardSize, new Coordinates(x, y), debug);
    }

    private static int ReadConsoleNumber()
    {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        try
        {
            return Integer.parseInt(reader.readLine());
        } catch (IOException e)
        {
            e.printStackTrace();
            return -1;
        } catch (NumberFormatException e)
        {
            System.err.println("Invalid input!");
            return -1;
        }
    }

    private static String ReadConsole()
    {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        try
        {
            return reader.readLine();
        } catch (IOException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    private static void Run(int boardSize, Coordinates initialHole, boolean debug)
    {
        Board board = new Board(boardSize, initialHole, debug); // zero-based coordinates

        System.out.print("ORIGINAL LAYOUT");
        PrintBoard(board);

        if (board.Solve())
        {
            PrintNewLine();
            System.out.print("SOLUTION FOUND!");

            Main.PrintNewLine();
            Main.PrintNewLine();
            Main.PrintNewLine();
            Main.PrintNewLine("-------------------------------");
            Main.PrintNewLine("STEPS:");
            Main.PrintNewLine("-------------------------------");

            PrintSteps(board);

            Main.PrintNewLine();
        }

        Main.PrintNewLine();
        PrintNewLine("BEST RUN: " + board.BestRun);
    }

    public static void PrintSteps(Board board)
    {
        List<int[][]> getSteps = board.GetSteps();

        for (int i = getSteps.size() - 1; i >= 0; i--)
        {
            int[][] table = getSteps.get(i);
            PrintTable(table);
        }
    }

    public static void PrintBoard(Board board)
    {
        PrintTable(board.Table);
    }

    public static void PrintTable(int[][] table)
    {
        PrintNewLine();

        for (int[] row : table)
        {
            for (int column : row)
            {
                String value = " ";

                if (column == 1)
                    value = "O";

                if (column == 2)
                    value = "*";

                System.out.print(value);
            }

            PrintNewLine();
        }
    }

    public static void PrintNewLine(String text)
    {
        System.out.print(text);
        PrintNewLine();
    }

    public static void PrintNewLine()
    {
        System.out.print(System.lineSeparator());
    }

    public static int[][] Clone2DArray(int[][] array)
    {
        int[][] newArray = new int[array.length][];

        for (int i = 0; i < array.length; i++)
        {
            int[] aMatrix = array[i];
            int aLength = aMatrix.length;
            newArray[i] = new int[aLength];

            System.arraycopy(aMatrix, 0, newArray[i], 0, aLength);
        }

        return newArray;
    }
}
