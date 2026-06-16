package cells;

import java.awt.Color;
import model.FireWorld;

/**
 * CS 142 - Fire Spread Simulation
 * Group: Fire Starters - Khoa Cao, Duy Nguyen, Khoi Nguyen
 * Author: Duy Nguyen
 *
 * Dry grass or brush. Medium fuel (5) — ignites easier than trees.
 * Fire spreads TO this cell from neighboring FireCells.
 */
public class VegetationCell extends FuelCell {

    private static final int FUEL = 5;

    public VegetationCell(int row, int col) {
        super(row, col, FUEL);
    }

    @Override
    public void update(FireWorld world)  { } // ignited by FireCell neighbors

    @Override
    public Color getColor()              { return new Color(154, 205, 50); }

    @Override
    public char getSymbol()              { return 'V'; }

    @Override
    public String getTypeName()          { return "Vegetation"; }
}
