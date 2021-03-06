package Internal;

import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import com.blacktowhite.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Stack;

/**
 * Justin Harjanto and Daniel Fang
 * Created 6/21/2014
 * Black To White Grid
 */
public class Grid {
    private Panel[][] grid;
    public static final int GRID_SIZE = 3;
    public static final double MARGIN_PERCENT = .03; // how much margin is in between the tiles
    private static final int MIN_BLACK_TILES = 2;
    private static final int MIN_WHITE_TILES = 2;
    private Stack<Integer> moves;
    private MediaPlayer m;
    private int numberOfMoves;
    private boolean isRunning;
    private int stuckLength; // preliminary cycle detection variable
    private Runnable runSolveAlgorithm;
    private Handler handleRunnable;
    private int SOLVE_DELAY = 100;
    private int MAX_DELAY = 600;

    private static double conversionWeight = 1;
    private static double edgeWeight = 0.05;

    public Grid(){
        grid = new Panel[GRID_SIZE][GRID_SIZE];
        moves = new Stack<Integer>();

        handleRunnable = new Handler();
        runSolveAlgorithm = new Runnable() {
            @Override
            public void run() {
                solveIter();
                if (!isSolved() && isRunning) {
                    handleRunnable.postDelayed(this, SOLVE_DELAY);
                } else if(!isRunning){
                    Log.d("stopped", "stopped running the algorithm");
                } else {
                    Log.d("solved", numberOfMoves + " moves made.");
                    numberOfMoves = 0;
                    moves.clear();
                }
            }
        };
    }

    /**
     * Get method for retrieving the grid.
     * @return Panel[][] that represents the grid.
     */
    public Panel[][] getGrid(){
        return grid;
    }

    /**
     * Identifies the panel number that was pressed based on the view passed in.
     *
     * @param v View passed in.
     * @return An integer represents the panel in the grid that was pressed with 0 based indexing
     * with 0 being the top left corner of the grid, -1 if the corresponding view was not found.
     */
    private int identifyPanel(View v){
        for (Panel[] row : grid) {
            for (Panel p : row) {
                if (p.getPanelView() == v) {
                    return p.getPanelId();
                }
            }
        }
        return -1;
    }

    /**
     * Responds to a button press and changes the according panels
     * @param v View passed in representing the Button pressed.
     */
    public void changePanels(View v){
        int panelPressed = identifyPanel(v);
        changePanels(panelPressed, true);
        if(isSolved()){ // Check and generate a new board if it's solved.
            m = MediaPlayer.create(v.getContext(), R.raw.shinyding);
            m.start();
            generateBoard();
        }
    }

    /**
     * Changes the current panel from white to black or black to white along with the tiles
     * adjacent to the panel.
     * @param panelPressed desired panel to change
     */
    private void changePanels(int panelPressed, boolean record) {
        int totalGridDimensions = GRID_SIZE * GRID_SIZE;
        int panelAbove = panelPressed - GRID_SIZE;
        int panelBelow = panelPressed + GRID_SIZE;
        int panelRight = panelPressed + 1;
        int panelLeft = panelPressed - 1;
        if (panelAbove >= 0) {
            changeSinglePanel(panelAbove);
        }
        if (panelBelow < totalGridDimensions) {
            changeSinglePanel(panelBelow);
        }
        if (panelRight % GRID_SIZE != 0 && panelRight < totalGridDimensions){
            changeSinglePanel(panelRight);
        }
        if (panelPressed % GRID_SIZE != 0 && panelLeft >= 0){
            changeSinglePanel(panelLeft);
        }
        changeSinglePanel(panelPressed);
        if (record) {
            moves.push(panelPressed);
        }
    }


    /**
     * Runs the @solveIter() method until the board is solved
     */
    public void solve(boolean delay) {
        isRunning = true;
        if (delay) {
            handleRunnable.postDelayed(runSolveAlgorithm, SOLVE_DELAY);
        } else {
            while (!isSolved()) {
                solveIter();
            }
            ArrayList<Integer> moveList = new ArrayList<Integer>();
            while (!moves.empty()) {
                moveList.add(0, moves.pop());
            }
            Log.d("moves", Arrays.toString(moveList.toArray()));
        }
    }

    /**
     * @return the count of black tiles
     */
    private int getBlackTiles() {
        int blackTiles = 0;
        for (int i = 0; i < GRID_SIZE * GRID_SIZE; i++) {
            if (isBlack(i) == 1) {
                blackTiles++;
            }
        }
        return blackTiles;
    }

    /**
     * Runs through one iteration of the solving algorithm, which
     * combines minimax pruning and resorts to randomization in
     * infinite-loop situations
     */
    public void solveIter() { // attempting heuristic evaluation

        double maxScore = -1000;
        int index = -1;

        for (int i = 0; i < GRID_SIZE * GRID_SIZE; i++) {
            double score = analyze(i);
            if (score > maxScore && !moves.isEmpty() && i != moves.peek()) {
                index = i;
                maxScore = score;
            }
        }

        // counts the number of black tiles on the board
        int blackTiles = 0;
        for (int i = 0; i < GRID_SIZE * GRID_SIZE; i++) {
            if (isBlack(i) == 1) {
                blackTiles++;
            }
        }

        // if there are two black tiles on the board, the board might be stuck in
        // an infinite loop
        if (getBlackTiles() == 2) {
            stuckLength++;
        } else {
            stuckLength = 0;
        }

        // if a loop is detected (stuck for > 3 cycles), breaks the cycle
        if (stuckLength >= 3) {
            changePanels(4, true);
            changePanels(8, true);
            stuckLength = 0;
        } else if (index == -1) { // if no suitable index is found, chooses a random tile
            changePanels((int) Math.random() * (GRID_SIZE * GRID_SIZE - 1), true);
        } else {                  // otherwise, switches the minimax-determined tile
            changePanels(index, true);
        }
        numberOfMoves++;
    }

    /**
     * Checks the history of moves and undoes the most recent move
     * (if any have been made)
     */
    public void undo() {
        if (!moves.isEmpty()) {
            int move = moves.pop();
            changePanels(move, false);
        }
    }

    /**
     * Changes the panel from white -> black or black -> white depending on
     * its initial state.
     *
     * @param panelNumber Panel number in the grid.
     */
    private void changeSinglePanel(int panelNumber) {
        int row = panelNumber / grid.length;
        int col = panelNumber % grid.length;
        if(row >= grid.length || row < 0){
            Log.d("row", row + "");
        }
        if(col >= grid.length || col < 0){
            Log.d("col", col + "");
        }
        grid[row][col].changeColor();
        if (grid[row][col].getColor()) {
            grid[row][col].getPanelView().setBackgroundColor(Color.WHITE);
        } else {
            grid[row][col].getPanelView().setBackgroundColor(Color.BLACK);
        }
    }

    /**
     * Generates a board to ensure that the Board isn't already solved.
     * pre : All Panels in grid are initialized.
     * post: All Panels in grid are not all white.
     */
    public void generateBoard() {
        randomizePanels();
        calculateEdgeWeights();
    }

    /**
     * Generates a board based on the given board state
     * @param id String representation of the board state
     */
    public void generateBoard(String id) {
        if (id.length() != GRID_SIZE * GRID_SIZE) {
            generateBoard();
        } else {
            for (int i = 0; i < GRID_SIZE * GRID_SIZE; i++) {
                int row = i / GRID_SIZE;
                int col = i % GRID_SIZE;
                boolean isWhite = id.charAt(i) == '0';
                grid[row][col].changeColor(isWhite);
            }
        }
    }
    /**
     * Says whether or not if the board is already solved.
     * @return boolean representing if the board is already solved (all white), false if not
     */
    private boolean isSolved(){
        for(Panel[] row : grid){
            for(Panel p : row){
                if(!p.getColor()){ // if Panel is black...
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Randomizes all the panels in a given grid of Panels.
     */
    private void randomizePanels(){
        int numBlackPanels = 0;
        int numWhitePanels = 0;
        while(numBlackPanels < MIN_BLACK_TILES || numWhitePanels < MIN_WHITE_TILES){
            for(Panel[] row : grid){
                for(Panel p : row){
                    p.generatePanel();
                }
            }
            numBlackPanels = 0;
            numWhitePanels = 0;
            for(Panel[] row : grid){
                for(Panel p : row){
                    if(!p.getColor()){ // If the panel is black...
                        numBlackPanels++;
                    } else {
                        numWhitePanels++;
                    }
                }
            }
        }
    }

    private void calculateEdgeWeights() {
        int maxWeight = (GRID_SIZE % 2 == 1) ? GRID_SIZE: GRID_SIZE - 1;
        int currentWeight = maxWeight;
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                grid[row][col].setEdgeWeight(currentWeight);
                if (col != GRID_SIZE - 1) {
                    if (GRID_SIZE % 2 == 1) { // odd
                        currentWeight = col < GRID_SIZE / 2 ? currentWeight - 1 : currentWeight + 1;
                    } else { // even
                        if (col < GRID_SIZE / 2 - 1) {
                            currentWeight--;
                        } else if (col > GRID_SIZE / 2 - 1) {
                            currentWeight++;
                        }
                    }
                }
            }
            if (GRID_SIZE % 2 == 1) { // odd
                currentWeight = row < GRID_SIZE / 2 ? currentWeight - 1: currentWeight + 1;
            } else { // even
                if (row < GRID_SIZE / 2 - 1) {
                    currentWeight--;
                } else if (row > GRID_SIZE / 2 - 1) {
                    currentWeight++;
                }
            }
        }
    }

    /**
     * Calculates the net gain of selecting the given panel
     * @param panelNumber the desired panel
     * @return (#black panels - #white panels), net gain
     */
    private double analyze(int panelNumber) {
        double score = 0;
        int totalGridDimensions = GRID_SIZE * GRID_SIZE;
        int panelAbove = panelNumber - GRID_SIZE;
        int panelBelow = panelNumber + GRID_SIZE;
        int panelRight = panelNumber + 1;
        int panelLeft = panelNumber - 1;

        //heuristic 1: conversion score
        int conversionScore = isBlack(panelNumber);

        //heuristic 2: edge score
        int edgeScore = getPanelEdgeScore(panelNumber);

        if (panelAbove >= 0) {
            conversionScore += isBlack(panelAbove);
            edgeScore += getPanelEdgeScore(panelAbove);
        }
        if (panelBelow < totalGridDimensions) {
            conversionScore += isBlack(panelBelow);
            edgeScore += getPanelEdgeScore(panelBelow);

        }
        if (panelRight % GRID_SIZE != 0 && panelRight < totalGridDimensions){
            conversionScore += isBlack(panelRight);
            edgeScore += getPanelEdgeScore(panelRight);
        }
        if (panelNumber % GRID_SIZE != 0 && panelLeft >= 0){
            conversionScore += isBlack(panelLeft);
            edgeScore += getPanelEdgeScore(panelLeft);
        }
        int row = panelNumber / GRID_SIZE;
        int col = panelNumber % GRID_SIZE;

        score = (conversionWeight * conversionScore) + (edgeWeight * edgeScore);
        grid[row][col].setText("T: " + score + " C: " + conversionWeight * conversionScore + " E: " + edgeWeight * edgeScore);
        return score;
    }

    /**
     * Used to describe the net gain of a tile
     * @param panelNumber
     * @return 1 for a black panel, -1 for a white panel
     */
    private int isBlack(int panelNumber) {
        int row = panelNumber / GRID_SIZE;
        int col = panelNumber % GRID_SIZE;
        if(grid[row][col].getColor()){
            return -1;
        } else {
            return 1;
        }
    }

    /**
     * @param panelNumber the desired panel
     * @return the edge weight of the given panelNumber
     */
    private int getPanelEdgeScore(int panelNumber) {
        int row = panelNumber / GRID_SIZE;
        int col = panelNumber % GRID_SIZE;
        return grid[row][col].getEdgeWeight();
    }

    /**
     * Stops running the algorithm.
     */
    public void stopSolving(){
        isRunning = false;
    }

    /**
     * Stringifies the board into a series of 0s and 1s for white and black, respectively
     * @return the String id for the current board
     */
    public String toString() {
        String id = "";
        for(Panel[] row : grid){
            for(Panel p : row){
                if(p.getColor()){ // If the panel is white...
                    id+= "0";
                } else {
                    id+= "1";
                }
            }
        }
        return id;
    }

    /**
     * Sets solve delay.
     * @param delay How much of a delay there will be between iterations.
     */
    public void setSolveDelay(int delay){
        SOLVE_DELAY = MAX_DELAY - delay * 8;
    }

    /**
     * Used for debugging, sets the entire grid back to black.
     */
    public void resetToBlack(){
        for(Panel[] row : grid){
            for(Panel p : row){
                p.changeColor(false);
            }
        }
    }

    /**
     * Used for debugging, performs n amount of tile presses
     * @param times The amount of random tile presses the Grid will do.
     */
    public void randomTilePresses(int times){
        for(int i = 0; i < times; i++){
            changePanels((int) (Math.random() * Panel.panelNumber), false);
        }
    }

}
