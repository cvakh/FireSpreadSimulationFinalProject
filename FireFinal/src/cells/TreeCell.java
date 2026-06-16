package cells;

import java.awt.Color;
import model.FireWorld;

/**
 * CS 142 - Fire Spread Simulation
 * Group: Fire Starters - Khoa Cao, Duy Nguyen, Khoi Nguyen
 * Author: Duy Nguyen
 *
 * A dense tree cell. High fuel (10) — burns long and hot.
 * Fire spreads TO this cell from neighboring FireCells.
 */
public class TreeCell extends FuelCell {

    private static final int FUEL = 10;

    public TreeCell(int row, int col) {
        super(row, col, FUEL);
    }

    @Override
    public void update(FireWorld world)  { } // ignited by FireCell neighbors

    @Override
    public Color getColor()              { return new Color(34, 100, 34); }

    @Override
    public char getSymbol()              { return 'T'; }

    @Override
    public String getTypeName()          { return "Tree"; }
}
