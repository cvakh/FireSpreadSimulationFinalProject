package cells;

import java.awt.Color;

/**
 * CS 142 - Fire Spread Simulation
 * Group: Fire Starters - Khoa Cao, Duy Nguyen, Khoi Nguyen
 * Author: Duy Nguyen
 *
 * Burned-out ground. Created when a FireCell's timer hits 0.
 * Cannot reignite — acts as a permanent firebreak.
 */
public class AshCell extends NonFuelCell {

    public AshCell(int row, int col) { super(row, col); }

    @Override
    public Color getColor()              { return new Color(70, 70, 70); }

    @Override
    public char getSymbol()              { return 'A'; }

    @Override
    public String getTypeName()          { return "Ash"; }
}
