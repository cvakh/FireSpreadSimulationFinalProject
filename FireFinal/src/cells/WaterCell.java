package cells;

import java.awt.Color;

/**
 * CS 142 - Fire Spread Simulation
 * Group: Fire Starters - Khoa Cao, Duy Nguyen, Khoi Nguyen
 * Author: Duy Nguyen
 *
 * A water/lake cell. Completely blocks fire — stronger than empty ground.
 * Adds visual variety to the map and acts as a river/lake firebreak.
 */
public class WaterCell extends NonFuelCell {

    public WaterCell(int row, int col) { super(row, col); }

    @Override
    public Color getColor()              { return new Color(30, 100, 200); }

    @Override
    public char getSymbol()              { return '~'; }

    @Override
    public String getTypeName()          { return "Water"; }
}
