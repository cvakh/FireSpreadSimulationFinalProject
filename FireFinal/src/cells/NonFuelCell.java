package cells;

import model.FireWorld;

/**
 * CS 142 - Fire Spread Simulation
 * Group: Fire Starters - Khoa Cao, Duy Nguyen, Khoi Nguyen
 * Author: Khoa Cao
 *
 * Level 2 abstract class for all non-burnable cells.
 * These cells block fire and do nothing each tick.
 */
public abstract class NonFuelCell extends Cell {

    public NonFuelCell(int row, int col) {
        super(row, col);
    }

    @Override
    public void update(FireWorld world)  { } // nothing to do

    @Override
    public boolean isFlammable()         { return false; }
}
