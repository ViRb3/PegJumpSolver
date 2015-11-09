package com.virb3e.pegjumpsolver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class Main
{
    public static void main(String[] args)
    {
        System.out.println();
        System.out.println("-----====================================-----");
        System.out.println("Peg Jump Solver v1.11   ~ViR");
        System.out.println("-----====================================-----");
        System.out.println();
        System.out.println("Size of board:");

        int boardSize = ReadConsoleNumber();

        if (boardSize == -1)
            return;

        if (boardSize < 3)
        {
            System.err.println("Invalid input! Board size cannot be less than 3!");
            return;
        }

        System.out.println("X coordinate of initial hole:");

        int x = ReadConsoleNumber();
        if (x < 0)
        {
            System.err.println("Invalid input! X cannot be less than 0!");
            return;
        }

        System.out.println("Y coordinate of initial hole:");

        int y = ReadConsoleNumber();
        if (y < 0)
        {
            System.err.println("Invalid input! Y cannot be less than 0!");
            return;
        }

        System.out.println("Do you want to enable verbose printing?");

        String verboseString = ReadConsole();
        if (verboseString == null)
            return;

        boolean verbose = false;

        if (verboseString.toLowerCase().equals("y") || verboseString.toLowerCase().equals("yes"))
            verbose = true;

        System.out.println();
        System.out.println();

        Run(boardSize, new Coordinates(x, y), verbose);
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

    private static void Run(int boardSize, Coordinates initialHole, boolean verbose)
    {
        Board board = new Board(boardSize, initialHole, verbose); // zero-based coordinates

        System.out.print("ORIGINAL LAYOUT");
        PrintBoard(board);

        if (board.Solve())
        {
            System.out.println();
            System.out.print("SOLUTION FOUND!");

            System.out.println();
            System.out.println();
            System.out.println();
            System.out.println("-------------------------------");
            System.out.println("STEPS:");
            System.out.println("-------------------------------");

            PrintSteps(board);

            System.out.println();
        }

        System.out.println();
        System.out.println("BEST RUN: " + board.BestRun);
    }

    public static void PrintSteps(Board board)
    {
        List<Step> steps = board.GetSteps();

        for (int i = steps.size() - 1; i >= 0; i--)
        {
            Step step = steps.get(i);
            System.out.println();
            System.out.print(step.GetMove());
            PrintTable(step.GetTable());
        }
    }

    public static void PrintBoard(Board board)
    {
        PrintTable(board.Table);
    }

    public static void PrintTable(boolean[][] table)
    {
        System.out.println();

        int spaces = table[0].length - 1;

        for (int i = 0; i < table.length; i++)
        {
            boolean[] row = table[i];

            for (int u = 0; u < spaces; u++) // initial spacing
                System.out.print(" ");

            for (int column = 0; column < row.length; column++)
            {
                String value = " ";
                System.out.print(value);

                if (column <= i)
                {
                    if (row[column] == false)
                        value = "O";

                    if (row[column] == true)
                        value = "*";
                }

                System.out.print(value);
            }

            System.out.println();
            spaces--;
        }
    }

    public static boolean[][] Clone2DArray(boolean[][] array)
    {
        boolean[][] newArray = new boolean[array.length][];

        for (int i = 0; i < array.length; i++)
        {
            boolean[] aMatrix = array[i];
            int aLength = aMatrix.length;
            newArray[i] = new boolean[aLength];

            System.arraycopy(aMatrix, 0, newArray[i], 0, aLength);
        }

        return newArray;
    }
}
