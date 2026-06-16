# FireSpreadSimulationFinalProject
This is the final project for CS 142

To run the text-only console version, open src/main/FireSimulation.java and click Run.

Once the GUI window opens, the fire does not start automatically. Optionally adjust the Humidity slider and Wind direction first, then click Start to ignite the center of the grid. The stats panel on the right updates live as the fire spreads, and the simulation stops on its own once the fire burns out. Click Reset at any time to generate a new map and try again.

Note: since the map is randomly generated each time, the center cell occasionally lands on Empty ground or Water instead of a Tree or Vegetation cell. Since fire can only ignite a flammable cell, pressing Start in this case does nothing and the simulation immediately shows as burned out. Simply click Reset to generate a new map and press Start again.
