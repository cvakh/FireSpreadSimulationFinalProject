package cells;

import java.awt.Color;

/**
 * CS 142 - Fire Spread Simulation
 * Group: Fire Starters - Khoa Cao, Duy Nguyen, Khoi Nguyen
 * Author: Duy Nguyen
 *
 * Bare ground — no fuel, permanently blocks fire spread.
 * Acts as a natural firebreak.
 */
public class EmptyCell extends NonFuelCell {

    public EmptyCell(int row, int col) { super(row, col); }

    @Override
    public Color getColor()              { return new Color(194, 178, 128); }

    @Override
    public char getSymbol()              { return '.'; }

    @Override
    public String getTypeName()          { return "Empty"; }
}
