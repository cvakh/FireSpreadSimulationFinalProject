package model;

import cells.*;
import java.util.ArrayList;
import java.util.Random;

/**
 * CS 142 - Fire Spread Simulation
 * Group: Fire Starters - Khoa Cao, Duy Nguyen, Khoi Nguyen
 * Author: Duy Nguyen (with help from Khoa Cao on the spread/queue logic in update())
 *
 * The model class — stores the grid and drives the simulation.
 * The GUI calls update() on a timer to advance one tick at a time.
 */
public class FireWorld {

    private Cell[][] grid;
    private int      rows, cols;
    private double   humidity;
    private String   windDirection;
    private boolean  fireActive;

    // Changes are queued here and applied AFTER all cells update each tick
    // This prevents cells from seeing each other's changes mid-tick
    private ArrayList<int[]> toIgnite = new ArrayList<>();
    private ArrayList<int[]> toAsh    = new ArrayList<>();

    private Random rand = new Random();

    public FireWorld(int rows, int cols, double humidity, String windDirection) {
        this.rows          = rows;
        this.cols          = cols;
        this.humidity      = humidity;
        this.windDirection = windDirection;
        this.grid          = new Cell[rows][cols];
        this.fireActive    = false;
        populateGrid();
    }

    /**
     * Fill the grid randomly:
     *   45% Tree, 25% Vegetation, 15% Empty, 10% Water, 5% river strips
     */
    private void populateGrid() {
        // Step 1: Fill base terrain
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                double roll = rand.nextDouble();
                if      (roll < 0.45) grid[r][c] = new TreeCell(r, c);
                else if (roll < 0.70) grid[r][c] = new VegetationCell(r, c);
                else if (roll < 0.85) grid[r][c] = new EmptyCell(r, c);
                else                  grid[r][c] = new WaterCell(r, c);
            }
        }

        // Step 2: Add 1-2 random river strips (vertical water lines)
        int numRivers = 1 + rand.nextInt(2);
        for (int i = 0; i < numRivers; i++) {
            int riverCol = 5 + rand.nextInt(cols - 10);
            for (int r = 0; r < rows; r++) {
                if (rand.nextDouble() < 0.85) { // slight gaps make it look natural
                    grid[r][riverCol] = new WaterCell(r, riverCol);
                }
            }
        }
    }

    /**
     * Advance the simulation by one tick:
     *   Step 1 - Clear queues from last tick
     *   Step 2 - Call update() on every cell
     *   Step 3 - Apply all queued ignitions and burnouts
     */
    public void update() {
        // Step 1: Reset change queues
        toIgnite.clear();
        toAsh.clear();

        // Step 2: Each cell decides what it wants to do
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                grid[r][c].update(this);
            }
        }

        // Step 3: Apply ignitions
        for (int[] pos : toIgnite) {
            if (inBounds(pos[0], pos[1]) && grid[pos[0]][pos[1]].isFlammable()) {
                grid[pos[0]][pos[1]] = new FireCell(pos[0], pos[1]);
            }
        }

        // Step 3b: Apply burnouts
        for (int[] pos : toAsh) {
            grid[pos[0]][pos[1]] = new AshCell(pos[0], pos[1]);
        }

        // Check if any fire cells remain
        fireActive = countCellType("Fire") > 0;
    }

    // Immediately ignite a cell — used to start the first fire
    public void ignite(int row, int col) {
        if (inBounds(row, col) && grid[row][col].isFlammable()) {
            grid[row][col] = new FireCell(row, col);
            fireActive = true;
        }
    }

    // Called by FireCell to queue a neighbor ignition
    public void scheduleIgnite(int row, int col) {
        toIgnite.add(new int[]{row, col});
    }

    // Called by FireCell when its burn timer expires
    public void scheduleAsh(int row, int col) {
        toAsh.add(new int[]{row, col});
    }

    // Count how many cells match a given type name (for stats panel)
    public int countCellType(String typeName) {
        int count = 0;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (grid[r][c].getTypeName().equals(typeName)) count++;
            }
        }
        return count;
    }

    // Returns the percentage of burnable cells that have burned
    public int getBurnedPercent() {
        int ash   = countCellType("Ash");
        int fire  = countCellType("Fire");
        int tree  = countCellType("Tree");
        int veg   = countCellType("Vegetation");
        int total = ash + fire + tree + veg;
        if (total == 0) return 0;
        return (int)(((double)(ash + fire) / total) * 100);
    }

    public Cell    getCell(int row, int col)  { return grid[row][col]; }
    public boolean inBounds(int r, int c)     { return r >= 0 && r < rows && c >= 0 && c < cols; }
    public int     getRows()                  { return rows; }
    public int     getCols()                  { return cols; }
    public double  getHumidity()              { return humidity; }
    public String  getWindDirection()         { return windDirection; }
    public boolean isFireActive()             { return fireActive; }

    // Text version of the grid for console output / testing
    public String toText() {
        StringBuilder sb = new StringBuilder();
        sb.append("Wind: ").append(windDirection)
          .append("  Humidity: ").append(humidity).append("\n");
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                sb.append(grid[r][c].getSymbol()).append(" ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}
