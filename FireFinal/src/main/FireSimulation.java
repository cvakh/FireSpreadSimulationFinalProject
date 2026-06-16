package main;

import model.FireWorld;

/**
 * CS 142 - Fire Spread Simulation
 * Group: Fire Starters - Khoa Cao, Duy Nguyen, Khoi Nguyen
 * Author: Khoi Nguyen
 *
 * Text-only entry point for testing.
 * Run this to verify grid and fire logic before launching the GUI.
 */
public class FireSimulation {

    public static void main(String[] args) {
        // Step 1: Create world (rows, cols, humidity, wind)
        FireWorld world = new FireWorld(15, 30, 0.2, "E");

        System.out.println("=== Fire Spread Simulation — Text Mode ===");
        System.out.println("T=Tree  V=Vegetation  F=Fire  .=Empty  A=Ash  ~=Water\n");

        // Step 2: Print initial grid
        System.out.println("--- Initial State ---");
        System.out.println(world.toText());

        // Step 3: Ignite center cell
        world.ignite(7, 15);
        System.out.println("--- Fire Ignited ---");
        System.out.println(world.toText());

        // Step 4: Simulate 6 ticks and print each one
        for (int i = 1; i <= 6; i++) {
            world.update();
            System.out.println("--- Step " + i + " | Burned: " + world.getBurnedPercent() + "% ---");
            System.out.println(world.toText());
        }

        System.out.println("Done. Run gui.FireSimulationGUI for the animated version.");
    }
}
