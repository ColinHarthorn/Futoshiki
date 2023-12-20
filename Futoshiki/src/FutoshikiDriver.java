

import java.util.Scanner;
import java.util.concurrent.ForkJoinPool;



/**
 * A driver class for solving Futoshiki Puzzles after loading the puzzle from a file. The puzzle can either be solved
 * sequentially or with a Fork/Join strategy.
 * 
 * @author Colin Harthorn
 *
 */
public class FutoshikiDriver {

    public static void main(String[] args){
        // startTime and endTime are used to track how long it takes to solve a puzzle.
        long startTime;
        long endTime;
        // Puzzle declared that will contain the selected Puzzle object
        Puzzle puzzleSelected;
        // String to be used for user input
        String userInput;
        // String to be used to represent the filename of a Puzzle file in "xy.txt" format
        String fileName;
        // Scanner object to be used for all user input
        Scanner keyboard = new Scanner(System.in);
        System.out.println("Welcome to the Futoshiki solver");
        System.out.print("Enter a puzzle number: ");
        // User is prompted to enter a number that corresponds to a puzzle filename
        userInput = keyboard.next();
        // user input is stripped of any leading and trailing space
        userInput = userInput.strip();
        // fileName is assigned to the number entered by the user + .txt
        fileName = userInput+".txt";
        // the fileName is used by the Puzzle method fromFile(String) in a try to catch and handle an Exception when reading the file
        try {
            puzzleSelected = Puzzle.fromFile(fileName);
            // if there are no exceptions the puzzle is displayed with toString()
            System.out.print(puzzleSelected.toString());
        } catch (IllegalArgumentException e) {
            // if there is an issue loading the file there is an error message and the program will terminate by returning from main
            System.out.println("File does not exist. Program terminating.");
            return;
        }
        // The user is prompted to enter "1" or "2" to select the method they would like to use to solve the puzzle
        System.out.println(" \nHow would you like to solve the puzzle? \n1. Sequential \n2. Fork/Join");
        String userChoice = keyboard.next(); 
        userChoice = userChoice.strip();
        // If the user enters "1" the puzzle is solved by using the solve(Puzzle, int) method 
        if (userChoice.equals("1")) {
            Puzzle solvedPuzzle;
            // start time is assigned to the current time in milliseconds
            startTime = System.currentTimeMillis();
            // message is printed to inform the user the solving has started
            System.out.println("Solving sequentialy...");
            // puzzle is solved with the solve() method of Puzzle and assigned to the local solvedPuzzle variable
            solvedPuzzle = Puzzle.solve(puzzleSelected, 0);
            
            try {
                // solution is printed to the screen
                System.out.print(solvedPuzzle.toString());
            } catch (NullPointerException e) {
                // if the solvedPuzzle is null a message will be printed and the program will terminate
                System.out.println("No possible solution was found.");
                endTime = System.currentTimeMillis();
                System.out.println("Puzzle was determined impossible to solve in "+(endTime-startTime)/1000.0+" seconds.");
                return;
            }
            
            // endTime is recorded
            endTime = System.currentTimeMillis();
            // startTime is subtracted from endTime and divided by 1000 to display the time to solve in seconds
            System.out.println("\nSolution found in "+(endTime-startTime)/1000.0+" seconds.");
        }
        else if (userChoice.equals("2")) {
            Puzzle solvedPuzzle;
            // start time is assigned to the current time in milliseconds
            startTime = System.currentTimeMillis();
            // message is printed to inform user the solving has started
            System.out.println("Solving with Fork/Join...");
            // A new PuzzleSolver object is created with the puzzleSelected and index of 0
            PuzzleSolver forkJoinSolver = new PuzzleSolver(puzzleSelected, 0);
            // A commonPool is created and the PuzzleSolver is invoked to start the Fork/Join solving process
            ForkJoinPool.commonPool().invoke(forkJoinSolver);
            // the answer found by the compute method is assigned to the solvedPuzzle variable
            solvedPuzzle = forkJoinSolver.getAnswer();
            // if there is no answer found and answer is still null a message will be printed and the program will terminate.
            if (solvedPuzzle == null) {
                System.out.println("No possible solution was found.");
                endTime = System.currentTimeMillis();
                System.out.println("Puzzle was determined impossible to solve in "+(endTime-startTime)/1000.0+" seconds.");
                return;
            } 
            // if the puzzle was solved the solution is printed to the screen as well as the time taken to find the solution
            System.out.print(solvedPuzzle.toString());
            endTime = System.currentTimeMillis();
            System.out.println("\nSolution found in "+(endTime-startTime)/1000.0+" seconds.");
        }
        // if the User does not enter "1" or "2" they will be informed the selection was invalid and the program will terminate 
        else {
            System.out.println("Invalid selection. Please restart and try again.");
            return;
        }
        
        
    }
    
    

}
