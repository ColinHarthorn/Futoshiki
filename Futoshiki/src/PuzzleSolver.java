import java.util.ArrayList;
import java.util.concurrent.RecursiveAction;
/**
 * PuzzleSolver is a class that extends RecursiveAction in order to solve Futoshiki puzzles optimally. 
 * PuzzleSolver uses Fork/Join in order to solve quickly with a Divide and Conquer strategy.
 * compute() protected void method implemented to extend RecursiveAction that solves an incomplete Puzzle Object
 * getAnswer() public getter method for the private answer Puzzle
 * 
 * @author Colin Harthorn
 *
 */
public class PuzzleSolver extends RecursiveAction {
    // defines the problem
    /** An incomplete Puzzle object that is set by the constructor */
    Puzzle inputPuzzle;
    /** An index to keep track of the current unfilled square of the puzzle being computed */
    int solvingIndex;
    // answer the problem
    /** A complete Puzzle object with no empty spaces. Set to null by default, and will remain null if there is no solution*/
    Puzzle answer = null;
    
    /**
     * public constructor to create a new PuzzleSolver object by setting the instance variables that define the problem
     * 
     * @param puzzleToSolve incomplete Puzzle object to be solved
     * @param index int to track the current square being solved on the puzzle
     */
    public PuzzleSolver(Puzzle puzzleToSolve, int index) {
        inputPuzzle = puzzleToSolve;
        solvingIndex = index;
    }
    
    /**
     * compute is a method that uses Fork/Join to recursively solve the Puzzle with optimal CPU usage and 
     * set the private answer variable to a solved puzzle that can be accessed with the getter
     * 
     */
    @Override
    protected void compute() {
        // size of puzzle is set to int size with the Puzzle getSize() method
        int size = inputPuzzle.getSize();
        // number of unfilled squares is found by subtracting the current solvingIndex from the total number of squares.
        int squaresRemaining = (size*size)-solvingIndex;
        // when there are less than 20 remaining squares the answer is set using the Puzzle method solve
        if (squaresRemaining < 20) {
            answer = Puzzle.solve(inputPuzzle, solvingIndex);
        }
        else {
            // An ArrayList of PuzzleSolvers is created to enable easy divide and conquer using for loops and Fork/Join
            ArrayList<PuzzleSolver> solverList = new ArrayList<PuzzleSolver>();
            // the solvingIndex is divided by size to determine the current row and column variables
            int currentRow = solvingIndex/size;
            int currentColumn = solvingIndex%size;
            if (inputPuzzle.getValue(currentRow, currentColumn)==Puzzle.EMPTY) {
                // empty spot, can try out possibilities
                for (int i = 1; i <= size; i++) {
                    // a new Puzzle object is created in order to test each empty spot with all possibilities without changing the original puzzle
                    Puzzle next = new Puzzle(inputPuzzle);
                    next.insertValue(currentRow, currentColumn, i);
                    // if the tested number is valid a new PuzzleSolver is made and added to the ArrayList
                    if (next.isValid()==true) {
                        solverList.add(new PuzzleSolver(next, solvingIndex+1));
                    }
                }
                // Once all PuzzleSolver objects are created and in the solverList, three for loops are used to Divide and Conquer
                // Loop 1 calls fork() on each solver in the ArrayList
                for (int i = 0; i < solverList.size(); i++) {
                    solverList.get(i).fork();
                }
                // Loop 2 completes the Fork/Join by calling join() on each solver in the ArrayList
                for (int i = 0; i < solverList.size(); i++) {
                    solverList.get(i).join();
                }
                // The last loop checks the answer from each PuzzleSolver. 
                for (int i = 0; i < solverList.size(); i++) {
                    // If a PuzzleSolver object in the ArrayList has found an answer, it will no longer be equal to null which will set the answer variable
                    if (solverList.get(i).getAnswer() != null) {
                        answer = solverList.get(i).getAnswer();
                    }
                }
                
            }
            // if the spot in the puzzle at the current solvingIndex is not empty, 
            // a new PuzzleSolver is made by incrementing solvingIndex without filling the space at the solvingIndex 
            else {
                PuzzleSolver puzzleOverThreshold = new PuzzleSolver(inputPuzzle, solvingIndex+1);
                puzzleOverThreshold.compute();
                // answer variable is set to the answer that is obtained recursively through the compute() method
                answer = puzzleOverThreshold.getAnswer();
            }
        }
        
    }
    /**
     * public getter method for the private Puzzle answer.
     * 
     * @return answer, Puzzle object that has no empty spaces with all inequalities being fulfilled or null
     */
    public Puzzle getAnswer() {
        return answer;
    }
}
