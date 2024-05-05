package csci446.project1;

import csci446.project1.GraphSystem.Graph;

import java.io.*;

public class Main {

    public static final boolean verbose = false;

    public static void main(String[] args) throws InterruptedException {

        // Basic Settings IF LOADING THESE MUSSSSSSSSSSSSSSSSSSSST MATCH THE GENERATED DATA.
        int startingColors = 3; //k
        int endingColors = 4;
        int numberOfTries = 5;
        int startingGraphSize = 10;
        int graphIncrementSize = 10;
        int graphIncrementCount = 1;

        boolean[] skipTestSets = new boolean[graphIncrementCount + 1];
        //SET ANY TESTS YOU WANT TO SKIP TO TRUE
        //There is no test 0.
        //skipTestSets[1] = true;
        //skipTestSets[2] = true;
        //skipTestSets[3] = true;
        //skipTestSets[4] = true;
        //skipTestSets[5] = true;
        //skipTestSets[6] = true;
        //skipTestSets[7] = true;
        //skipTestSets[8] = true;
        //skipTestSets[9] = true;
        //skipTestSets[10] = true;

        boolean multithreaded = false;
        boolean loadGraphs = false;
        boolean writeGraphs = false;

        Graph[] graphs = new Graph[graphIncrementCount * numberOfTries];

        if (writeGraphs) {
            for (int testSet = 1; testSet <= graphIncrementCount; testSet++) {
                for (int tryNum = 1; tryNum <= numberOfTries; tryNum++) {
                    graphs[testSet * tryNum - 1] = new Graph(startingGraphSize + ((testSet - 1) * graphIncrementSize));
                    try {
                        FileOutputStream fileOut = new FileOutputStream("tests/" + (testSet * tryNum - 1) + ".ser");
                        ObjectOutputStream out = new ObjectOutputStream(fileOut);
                        out.writeObject(graphs[testSet * tryNum - 1]);
                        out.close();
                        fileOut.close();
                        System.out.println("Serialized data for graph " + (testSet * tryNum - 1) + " is saved.");
                    } catch (IOException i) {
                        i.printStackTrace();
                    }

                }
            }

            return;
        }

        if (loadGraphs) {
            for (int colors = startingColors; colors <= endingColors; colors++) {
                for (int testSet = 1; testSet <= graphIncrementCount; testSet++) {
                    for (int tryNum = 1; tryNum <= numberOfTries; tryNum++) {
                        try {
                            FileInputStream fileIn = new FileInputStream("tests/" + (testSet * tryNum - 1) + ".ser");
                            ObjectInputStream in = new ObjectInputStream(fileIn);
                            graphs[testSet * tryNum - 1] = (Graph) in.readObject();
                            in.close();
                            fileIn.close();
                        } catch (IOException i) {
                            i.printStackTrace();
                            return;
                        } catch (ClassNotFoundException c) {
                            System.out.println("Graph class not found");
                            c.printStackTrace();
                            return;
                        }
                    }
                }
            }
        }

        MinConflict[] minConflictSolutions = new MinConflict[graphIncrementCount * numberOfTries * (endingColors - startingColors + 1)];
        SimpleBacktracking[] simpleBacktrackingSolutions = new SimpleBacktracking[graphIncrementCount * numberOfTries * (endingColors - startingColors + 1)];
        BacktrackingWithForwardChecking[] backtrackingWithForwardCheckingSolutions = new BacktrackingWithForwardChecking[graphIncrementCount * numberOfTries * (endingColors - startingColors + 1)];
        BacktrackingMAC[] backtrackingMACSolutions = new BacktrackingMAC[graphIncrementCount * numberOfTries * (endingColors - startingColors + 1)];
        Genetic[] geneticSolutions = new Genetic[graphIncrementCount * numberOfTries * (endingColors - startingColors + 1)];

        long runTimeMinConflict[] = new long[graphIncrementCount * numberOfTries * (endingColors - startingColors + 1)];
        long runTimeSimpleBacktracking[] = new long[graphIncrementCount * numberOfTries * (endingColors - startingColors + 1)];
        long runTimeBacktrackingWithForwardChecking[] = new long[graphIncrementCount * numberOfTries * (endingColors - startingColors + 1)];
        long runTimeBacktrackingMAC[] = new long[graphIncrementCount * numberOfTries * (endingColors - startingColors + 1)];
        long runTimeGenetic[] = new long[graphIncrementCount * numberOfTries * (endingColors - startingColors + 1)];

        // Run all the tests, storing their graphs and results.

        for (int colors = startingColors; colors <= endingColors; colors++) {
            for (int testSet = 1; testSet <= graphIncrementCount; testSet++) {
                if (!skipTestSets[testSet]) {
                    System.out.println("Testing " + (startingGraphSize + (testSet - 1) * graphIncrementSize) + " points:");
                    for (int tryNum = 1; tryNum <= numberOfTries; tryNum++) {

                        if (!loadGraphs && colors == startingColors) {
                            graphs[testSet * tryNum - 1] = new Graph(startingGraphSize + ((testSet - 1) * graphIncrementSize));
                        }
                        if (multithreaded) {
                            MinConflictThread minConflictThread = new MinConflictThread(testSet, colors, startingColors, tryNum, graphs);
                            minConflictThread.run();
                            SimpleBacktrackingThread simpleBacktrackingThread = new SimpleBacktrackingThread(testSet, colors, startingColors, tryNum, graphs);
                            simpleBacktrackingThread.run();
                            BacktrackingWithForwardCheckingThread backtrackingWithForwardCheckingThread = new BacktrackingWithForwardCheckingThread(testSet, colors, startingColors, tryNum, graphs);
                            backtrackingWithForwardCheckingThread.run();
                            BacktrackingMACThread backtrackingMACThread = new BacktrackingMACThread(testSet, colors, startingColors, tryNum, graphs);
                            backtrackingMACThread.run();
                            GeneticThread geneticThread = new GeneticThread(testSet, colors, startingColors, tryNum, graphs);
                            geneticThread.run();

                            minConflictThread.join();
                            minConflictSolutions[testSet * (colors - startingColors + 1) * tryNum - 1] = minConflictThread.getInstance();

                            simpleBacktrackingThread.join();
                            simpleBacktrackingSolutions[testSet * (colors - startingColors + 1) * tryNum - 1] = simpleBacktrackingThread.getInstance();

                            backtrackingWithForwardCheckingThread.join();
                            backtrackingWithForwardCheckingSolutions[testSet * (colors - startingColors + 1) * tryNum - 1] = backtrackingWithForwardCheckingThread.getInstance();

                            backtrackingMACThread.join();
                            backtrackingMACSolutions[testSet * (colors - startingColors + 1) * tryNum - 1] = backtrackingMACThread.getInstance();

                            geneticThread.join();
                            geneticSolutions[testSet * (colors - startingColors + 1) * tryNum - 1] = geneticThread.getInstance();
                        } else {
                            long start = System.nanoTime();
                            minConflictSolutions[testSet * (colors - startingColors + 1) * tryNum - 1] = new MinConflict(graphs[testSet * tryNum - 1].points.length, colors, graphs[testSet * tryNum - 1].points, graphs[testSet * tryNum - 1].connections);
                            runTimeMinConflict[testSet * (colors - startingColors + 1) * tryNum - 1] = System.nanoTime() - start;
                            start = System.nanoTime();
                            simpleBacktrackingSolutions[testSet * (colors - startingColors + 1) * tryNum - 1] = new SimpleBacktracking(colors, graphs[testSet * tryNum - 1]);
                            runTimeSimpleBacktracking[testSet * (colors - startingColors + 1) * tryNum - 1] = System.nanoTime() - start;
                            start = System.nanoTime();
                            backtrackingWithForwardCheckingSolutions[testSet * (colors - startingColors + 1) * tryNum - 1] = new BacktrackingWithForwardChecking(colors, graphs[testSet * tryNum - 1]);
                            runTimeBacktrackingWithForwardChecking[testSet * (colors - startingColors + 1) * tryNum - 1] = System.nanoTime() - start;
                            start = System.nanoTime();
                            backtrackingMACSolutions[testSet * (colors - startingColors + 1) * tryNum - 1] = new BacktrackingMAC(graphs[testSet * tryNum - 1], colors);
                            runTimeBacktrackingMAC[testSet * (colors - startingColors + 1) * tryNum - 1] = System.nanoTime() - start;
                            start = System.nanoTime();
                            geneticSolutions[testSet * (colors - startingColors + 1) * tryNum - 1] = new Genetic(graphs[testSet * tryNum - 1], colors);
                            runTimeGenetic[testSet * (colors - startingColors + 1) * tryNum - 1] = System.nanoTime() - start;
                        }
                    }
                }
            }
        }
        //Print Solutions
        System.out.println("\nFinal Results\n");
        for (int colors = startingColors; colors <= endingColors; colors++) {
            System.out.println("Test Graphs With " + colors + " Colors:");
            for (int testSet = 1; testSet <= graphIncrementCount; testSet++) {
                if (!skipTestSets[testSet]) {
                    System.out.println("\t" + (startingGraphSize + (testSet - 1) * graphIncrementSize) + " points:");
                    for (int tryNum = 1; tryNum <= numberOfTries; tryNum++) {
                        System.out.println("\t\tTry " + tryNum + ":");
                        if (minConflictSolutions[testSet * (colors - startingColors + 1) * tryNum - 1].success) {
                            System.out.println("\t\t\tMinConflict: Success, Iterations: " + minConflictSolutions[testSet * (colors - startingColors + 1) * tryNum - 1].timesColored + ", Time: " + runTimeMinConflict[testSet * (colors - startingColors + 1) * tryNum - 1]);
                        } else {
                            System.out.println("\t\t\tMinConflict: Failure, Iterations: " + minConflictSolutions[testSet * (colors - startingColors + 1) * tryNum - 1].timesColored + ", Time: " + runTimeMinConflict[testSet * (colors - startingColors + 1) * tryNum - 1]);
                        }
                        if (simpleBacktrackingSolutions[testSet * (colors - startingColors + 1) * tryNum - 1].success) {
                            System.out.println("\t\t\tSimpleBacktracking: Success, Iterations: " + simpleBacktrackingSolutions[testSet * (colors - startingColors + 1) * tryNum - 1].iterations + ", Time: " + runTimeSimpleBacktracking[testSet * (colors - startingColors + 1) * tryNum - 1]);
                        } else {
                            System.out.println("\t\t\tSimpleBacktracking: Failure, Iterations: " + simpleBacktrackingSolutions[testSet * (colors - startingColors + 1) * tryNum - 1].iterations + ", Time: " + runTimeSimpleBacktracking[testSet * (colors - startingColors + 1) * tryNum - 1]);
                        }
                        if (backtrackingWithForwardCheckingSolutions[testSet * (colors - startingColors + 1) * tryNum - 1].success) {
                            System.out.println("\t\t\tBacktrackingWithForwardChecking: Success, Iterations: " + backtrackingWithForwardCheckingSolutions[testSet * (colors - startingColors + 1) * tryNum - 1].iterations + ", Time: " + runTimeBacktrackingWithForwardChecking[testSet * (colors - startingColors + 1) * tryNum - 1]);
                        } else {
                            System.out.println("\t\t\tBacktrackingWithForwardChecking: Failure, Iterations: " + backtrackingWithForwardCheckingSolutions[testSet * (colors - startingColors + 1) * tryNum - 1].iterations + ", Time: " + runTimeBacktrackingWithForwardChecking[testSet * (colors - startingColors + 1) * tryNum - 1]);
                        }
                        if (backtrackingMACSolutions[testSet * (colors - startingColors + 1) * tryNum - 1].success) {
                            System.out.println("\t\t\tBacktracking MAC: Success, Iterations: " + backtrackingMACSolutions[testSet * (colors - startingColors + 1) * tryNum - 1].iterations + ", Time: " + runTimeBacktrackingMAC[testSet * (colors - startingColors + 1) * tryNum - 1]);
                        } else {
                            System.out.println("\t\t\tBacktracking MAC:: Failure, Iterations: " + backtrackingMACSolutions[testSet * (colors - startingColors + 1) * tryNum - 1].iterations + ", Time: " + runTimeBacktrackingMAC[testSet * (colors - startingColors + 1) * tryNum - 1]);
                        }
                        if (geneticSolutions[testSet * (colors - startingColors + 1) * tryNum - 1].success) {
                            System.out.println("\t\t\tGenetic Algorithm: Success, Iterations: " + geneticSolutions[testSet * (colors - startingColors + 1) * tryNum - 1].iterations + ", Time: " + runTimeGenetic[testSet * (colors - startingColors + 1) * tryNum - 1]);
                        } else {
                            System.out.println("\t\t\tGenetic Algorithm: Failure, Iterations: " + geneticSolutions[testSet * (colors - startingColors + 1) * tryNum - 1].iterations + ", Time: " + runTimeGenetic[testSet * (colors - startingColors + 1) * tryNum - 1]);
                        }
                    }
                }
            }
        }
    }
}