package cells;

import model.FireWorld;

/**
 * CS 142 - Fire Spread Simulation
 * Group: Fire Starters - Khoa Cao, Duy Nguyen, Khoi Nguyen
 * Author: Khoa Cao
 *
 * Level 2 abstract class for all burnable cells.
 * Tracks a fuel level that decreases as the cell burns.
 */
public abstract class FuelCell extends Cell {

    private int fuelLevel;

    public FuelCell(int row, int col, int fuelLevel) {
        super(row, col);
        this.fuelLevel = fuelLevel;
    }

    // Decrease fuel; clamp at 0
    public void reduceFuel(int amount) {
        fuelLevel = Math.max(0, fuelLevel - amount);
    }

    public int getFuelLevel()            { return fuelLevel; }

    @Override
    public boolean isFlammable()         { return true; }
}
