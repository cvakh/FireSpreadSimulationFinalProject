package cells;

import java.awt.Color;
import model.FireWorld;
import java.util.Random;

/**
 * CS 142 - Fire Spread Simulation
 * Group: Fire Starters - Khoa Cao, Duy Nguyen, Khoi Nguyen
 * Author: Khoa Cao
 *
 * An actively burning cell. Core of the simulation.
 * Each tick: tries to spread fire to 4 neighbors, then counts down.
 * When burn timer hits 0, becomes AshCell.
 */
public class FireCell extends FuelCell {

    private static final int    BURN_DURATION = 4;
    private static final double BASE_SPREAD   = 0.45;

    private int    burnTimer;
    private Random rand;

    public FireCell(int row, int col) {
        super(row, col, BURN_DURATION);
        this.burnTimer = BURN_DURATION;
        this.rand      = new Random();
    }

    /**
     * Step 1 - Check all 4 neighbors (N, S, W, E)
     * Step 2 - Calculate spread chance: base × (1 - humidity) + wind bonus
     * Step 3 - Roll random number; schedule ignition if roll succeeds
     * Step 4 - Decrement burn timer; schedule ash when it hits 0
     */
    @Override
    public void update(FireWorld world) {

        // Step 1: The 4 adjacent neighbors
        int[][] neighbors = {
            {getRow() - 1, getCol()}, // North
            {getRow() + 1, getCol()}, // South
            {getRow(), getCol() - 1}, // West
            {getRow(), getCol() + 1}  // East
        };

        for (int[] pos : neighbors) {
            int r = pos[0], c = pos[1];
            if (!world.inBounds(r, c)) continue;

            Cell neighbor = world.getCell(r, c);
            if (!neighbor.isFlammable() || neighbor instanceof FireCell) continue;

            // Step 2: Base chance scaled down by humidity
            double chance = BASE_SPREAD * (1.0 - world.getHumidity());

            // Step 2b: Wind direction boosts spread in that direction
            String wind = world.getWindDirection();
            if (wind.equals("N") && r < getRow()) chance += 0.25;
            if (wind.equals("S") && r > getRow()) chance += 0.25;
            if (wind.equals("E") && c > getCol()) chance += 0.25;
            if (wind.equals("W") && c < getCol()) chance += 0.25;

            // Step 3: Spread if random roll is within chance
            if (rand.nextDouble() < chance) {
                world.scheduleIgnite(r, c);
            }
        }

        // Step 4: Burn down; become ash when finished
        burnTimer--;
        if (burnTimer <= 0) {
            world.scheduleAsh(getRow(), getCol());
        }
    }

    // Color shifts from bright orange to deep red as it burns down
    @Override
    public Color getColor() {
        float ratio = (float) burnTimer / BURN_DURATION;
        int r = 255;
        int g = (int)(80 * ratio); // less green as it burns out
        return new Color(r, g, 0);
    }

    @Override
    public char getSymbol()     { return 'F'; }

    @Override
    public String getTypeName() { return "Fire"; }
}
