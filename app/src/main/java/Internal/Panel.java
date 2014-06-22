package Internal;

import android.graphics.Color;
import android.view.View;
import android.widget.Button;

import java.util.Random;

/**
 * Justin Harjanto and Daniel Fang
 * Created 6/20/2014
 * BlackToWhite Panel
 */
public class Panel {
    private Button panel;
    private boolean isWhite; // false if black
    private static int panelNumber = 0;
    private int panelId;

    /**
     * Constructs a new Panel. A panel is a single portion of a grid and has two states:
     * White and black.
     *
     * @param panel Button corresponding to the panel itself.
     */
    public Panel(Button panel) {
        if (panel == null) {
            throw new IllegalArgumentException("button must be initialized");
        }
        this.panel = panel;
        this.panelId = panelNumber;
        panelNumber++;
        generatePanel();
    }

    /**
     * Decides whether or not if it's black or white. By default the panel is black.
     */
    protected void generatePanel() {
        Random r = new Random();
        isWhite = r.nextBoolean();
        if (isWhite) {
            panel.setBackgroundColor(Color.WHITE);
        } else {
            panel.setBackgroundColor(Color.BLACK);
        }
    }

    /**
     * Get method to return the Button view that the Panel is attached to. Used to figure out
     * which panel was pressed.
     * @return View that represents the View the Button is associated with.
     */
    public View getPanelView(){
        return panel;
    }

    /**
     * Get method to return the panelId that associated with this panel.
     * @return An integer representing which panel in the Grid was pressed.
     */
    protected int getPanelId(){
        return panelId;
    }

    /**
     * Get method to return whether or not the panel is white.
     * @return Boolean that represents whether the panel is white, true if it is and false if it
     * is not.
     */
    protected boolean getColor(){
        return isWhite;
    }

    /**
     * Set method to change the color of the panel from black to white or white to black
     * depending on the initial state of the panel.
     */
    protected void changeColor(){
        isWhite = !isWhite;
    }

}
