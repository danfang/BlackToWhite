package Internal;

import android.graphics.Color;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.View;

import com.blacktowhite.R;

import java.util.Stack;

/**
 * Created by Justin on 6/21/2014.
 */
public class Grid {
    private Panel[][] grid;
    private static final int GRID_SIZE = 3;
    private static final int MIN_BLACK_TILES = 2;
    private static final int MIN_WHITE_TILES = 2;
    private Stack<Integer> moves;

    public Grid(){
        grid = new Panel[GRID_SIZE][GRID_SIZE];
        moves = new Stack<Integer>();
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
            MediaPlayer m = MediaPlayer.create(v.getContext(), R.raw.shinyding);
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
        int totalGridDimensions = grid.length * grid.length;
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
        if (panelRight % grid.length != 0 && panelRight < totalGridDimensions){
            changeSinglePanel(panelRight);
        }
        if (panelPressed % grid.length != 0 && panelLeft >= 0){
            changeSinglePanel(panelLeft);
        }
        changeSinglePanel(panelPressed);
        if (record) {
            moves.push(panelPressed);
        }
    }


    /**
     * Runs through one iteration of the solution algorithm.
     */
    public void solve() {
        changePanels((int) (Math.random() * 8), true);
        if (isSolved()) {
            Log.d("solved", "true");
        }
        int max = 0;
        int index = 0;
        for(int i = 0; i < GRID_SIZE * GRID_SIZE; i++){
            int netTiles = analyze(i);
            if(netTiles > max){
                index = i;
                max = netTiles;
            }
        }
        changePanels(index, true);
    }

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
    public void generateBoard(){
        randomizePanels();
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

    private int analyze(int panelNumber){
        int totalNetGain = 0;
        int totalGridDimensions = grid.length * grid.length;
        int panelAbove = panelNumber - GRID_SIZE;
        int panelBelow = panelNumber + GRID_SIZE;
        int panelRight = panelNumber + 1;
        int panelLeft = panelNumber - 1;
        if (panelAbove >= 0) {
            totalNetGain += isBlack(panelAbove);
        }
        if (panelBelow < totalGridDimensions) {
            totalNetGain += isBlack(panelBelow);
        }
        if (panelRight % grid.length != 0 && panelRight < totalGridDimensions){
            totalNetGain += isBlack(panelRight);
        }
        if (panelNumber % grid.length != 0 && panelLeft >= 0){
            totalNetGain += isBlack(panelLeft);
        }
        totalNetGain += isBlack(panelNumber);
        return totalNetGain;
    }

    private int isBlack(int panelNumber){
        int row = panelNumber / GRID_SIZE;
        int col = panelNumber % GRID_SIZE;
        if(grid[row][col].getColor()){
            return -1;
        } else {
            return 1;
        }
    }

}
