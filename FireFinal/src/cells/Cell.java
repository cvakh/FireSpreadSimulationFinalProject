package cells;

import java.awt.Color;
import model.FireWorld;

/**
 * CS 142 - Fire Spread Simulation
 * Group: Fire Starters - Khoa Cao, Duy Nguyen, Khoi Nguyen
 * Author: Khoa Cao
 *
 * Abstract base class for every cell in the grid.
 * All cell types must extend this and implement the abstract methods.
 */
public abstract class Cell {

    private int row;
    private int col;

    public Cell(int row, int col) {
        this.row = row;
        this.col = col;
    }

    // Called every tick — each subclass defines its own behavior
    public abstract void update(FireWorld world);

    // Returns the color to draw this cell in the GUI
    public abstract Color getColor();

    // Returns whether fire can spread to this cell
    public abstract boolean isFlammable();

    // Returns a single character for console output
    public abstract char getSymbol();

    // Returns the name of this cell type for the stats panel
    public abstract String getTypeName();

    public int getRow() { return row; }
    public int getCol() { return col; }
}
