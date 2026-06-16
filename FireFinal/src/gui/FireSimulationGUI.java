package gui;

import model.FireWorld;
import cells.Cell;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;

/**
 * CS 142 - Fire Spread Simulation
 * Group: Fire Starters - Khoa Cao, Duy Nguyen, Khoi Nguyen
 * Authors: Khoi Nguyen (grid rendering, animation timer, stats panel)
 *          Khoa Cao (control panel — sliders, wind buttons, start/reset)
 *
 * The view/controller class.
 * Draws the simulation grid and animates it using a Swing Timer.
 * Contains a control panel for user input and a stats panel on the side.
 *
 * Layout:
 *   NORTH  → control panel (sliders, buttons, wind selector)
 *   CENTER → grid animation panel
 *   EAST   → live stats panel
 */
public class FireSimulationGUI extends JPanel {

    private static final int CELL_SIZE  = 18;
    private static final int GRID_ROWS  = 28;
    private static final int GRID_COLS  = 48;
    private static final int LEGEND_H   = 28;

    private FireWorld world;
    private Timer     timer;
    private int       tickCount;

    // User-controlled parameters
    private double  humidity      = 0.2;
    private String  windDirection = "E";
    private int     timerDelay    = 250;
    private boolean running       = false;

    // Control panel components (need references to read/update)
    private JButton startBtn;
    private JButton resetBtn;
    private JLabel  humidityLabel;
    private JLabel  speedLabel;

    // Stats panel labels (updated every tick)
    private JLabel treeLbl, vegLbl, fireLbl, ashLbl, burnedLbl, tickLbl, statusLbl;

    // ── GRID, ANIMATION & STATS LOGIC (Khoi Nguyen) ─────────────────────────

    public FireSimulationGUI() {
        setPreferredSize(new Dimension(
            GRID_COLS * CELL_SIZE,
            GRID_ROWS * CELL_SIZE + LEGEND_H
        ));
        setBackground(new Color(20, 20, 20));
        buildWorld();
    }

    // Create a fresh world and timer using current parameters
    private void buildWorld() {
        world     = new FireWorld(GRID_ROWS, GRID_COLS, humidity, windDirection);
        tickCount = 0;
        if (timer != null) timer.stop();
        timer = new Timer(timerDelay, e -> tick());
    }

    // Called every timer tick: advance sim, update stats, repaint
    private void tick() {
        world.update();
        tickCount++;
        updateStats();
        repaint();

        // Auto-stop when all fire is out
        if (!world.isFireActive()) {
            timer.stop();
            running = false;
            statusLbl.setText("Fire out!");
            statusLbl.setForeground(new Color(100, 200, 100));
        }
    }

    // Ignite center and start animation
    public void startSim() {
        world.ignite(GRID_ROWS / 2, GRID_COLS / 2);
        timer.start();
        running = true;
        startBtn.setEnabled(false);
        statusLbl.setText("Burning...");
        statusLbl.setForeground(new Color(255, 120, 0));
    }

    // Reset everything back to fresh state
    public void resetSim() {
        timer.stop();
        running = false;
        buildWorld();
        updateStats();
        repaint();
        startBtn.setEnabled(true);
        statusLbl.setText("Ready");
        statusLbl.setForeground(new Color(180, 180, 180));
    }

    /**
     * Paint the grid each tick:
     *   Step 1 - Draw each cell as a filled rectangle using its color
     *   Step 2 - Draw the top info bar (tick, wind, humidity)
     *   Step 3 - Draw the bottom color legend
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Step 1: Draw every cell
        for (int r = 0; r < world.getRows(); r++) {
            for (int c = 0; c < world.getCols(); c++) {
                Cell cell = world.getCell(r, c);
                g2.setColor(cell.getColor());
                g2.fillRect(c * CELL_SIZE, r * CELL_SIZE, CELL_SIZE, CELL_SIZE);
            }
        }

        // Step 2: Semi-transparent info bar at top
        g2.setColor(new Color(0, 0, 0, 160));
        g2.fillRect(0, 0, GRID_COLS * CELL_SIZE, 20);
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 11));
        g2.drawString("Tick: " + tickCount
                + "   Wind: " + windDirection
                + "   Humidity: " + String.format("%.1f", humidity)
                + "   Burned: " + world.getBurnedPercent() + "%",
                6, 14);

        // Step 3: Legend bar at bottom
        int ly = GRID_ROWS * CELL_SIZE + 4;
        g2.setColor(new Color(30, 30, 30));
        g2.fillRect(0, GRID_ROWS * CELL_SIZE, GRID_COLS * CELL_SIZE, LEGEND_H);
        g2.setFont(new Font("Arial", Font.PLAIN, 11));
        drawLegend(g2, new Color(34, 100, 34),   "Tree",       8,   ly);
        drawLegend(g2, new Color(154, 205, 50),  "Vegetation", 85,  ly);
        drawLegend(g2, new Color(255, 80, 0),    "Fire",       195, ly);
        drawLegend(g2, new Color(194, 178, 128), "Empty",      255, ly);
        drawLegend(g2, new Color(70, 70, 70),    "Ash",        325, ly);
        drawLegend(g2, new Color(30, 100, 200),  "Water",      375, ly);
    }

    // Draw one colored square + label for the legend
    private void drawLegend(Graphics2D g, Color color, String label, int x, int y) {
        g.setColor(color);
        g.fillRoundRect(x, y, 13, 13, 3, 3);
        g.setColor(Color.WHITE);
        g.drawString(label, x + 16, y + 11);
    }

    // Update all stat labels on the right panel
    private void updateStats() {
        if (treeLbl == null) return;
        treeLbl.setText("Trees:      " + world.countCellType("Tree"));
        vegLbl.setText ("Vegetation: " + world.countCellType("Vegetation"));
        fireLbl.setText("Fire:       " + world.countCellType("Fire"));
        ashLbl.setText ("Ash:        " + world.countCellType("Ash"));
        burnedLbl.setText("Burned: " + world.getBurnedPercent() + "%");
        tickLbl.setText ("Tick:   " + tickCount);
    }

    // ── CONTROL PANEL (Khoa Cao) ────────────────────────────────────────────

    /**
     * Build the top control panel:
     *   - Humidity slider (0.0 to 1.0)
     *   - Wind direction radio buttons (N, S, E, W)
     *   - Speed slider (Slow → Very Fast)
     *   - Start and Reset buttons
     */
    public JPanel buildControlPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 6));
        panel.setBackground(new Color(35, 35, 35));
        panel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(60, 60, 60)));

        // Humidity slider (0–10 internally, displayed as 0.0–1.0)
        humidityLabel = valueLabel("0.2");
        JSlider humSlider = darkSlider(0, 10, 2, 130);
        humSlider.addChangeListener(e -> {
            humidity = humSlider.getValue() / 10.0;
            humidityLabel.setText(String.format("%.1f", humidity));
        });

        // Wind direction radio buttons
        ButtonGroup windGroup = new ButtonGroup();
        JPanel windPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 3, 0));
        windPanel.setBackground(new Color(35, 35, 35));
        for (String dir : new String[]{"N", "S", "E", "W"}) {
            JRadioButton btn = new JRadioButton(dir);
            styleRadio(btn);
            if (dir.equals("E")) btn.setSelected(true);
            btn.addActionListener(e -> windDirection = dir);
            windGroup.add(btn);
            windPanel.add(btn);
        }

        // Speed slider (1=slow 500ms → 5=fast 80ms)
        speedLabel = valueLabel("Normal");
        JSlider speedSlider = darkSlider(1, 5, 3, 110);
        int[]    delays = {500, 350, 250, 150, 80};
        String[] sLabels = {"Slow", "Normal", "Normal", "Fast", "Very Fast"};
        speedSlider.addChangeListener(e -> {
            int lv = speedSlider.getValue() - 1;
            timerDelay = delays[lv];
            speedLabel.setText(sLabels[lv]);
            if (timer != null) timer.setDelay(timerDelay);
        });

        // Start button — orange, ignites center and begins animation
        startBtn = new JButton("▶  Start");
        startBtn.setBackground(new Color(210, 65, 0));
        startBtn.setForeground(Color.WHITE);
        startBtn.setFont(new Font("Arial", Font.BOLD, 13));
        startBtn.setFocusPainted(false);
        startBtn.setBorderPainted(false);
        startBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        startBtn.addActionListener(e -> { if (!running) startSim(); });

        // Reset button — dark gray, rebuilds world with new parameters
        resetBtn = new JButton("↺  Reset");
        resetBtn.setBackground(new Color(60, 60, 60));
        resetBtn.setForeground(Color.WHITE);
        resetBtn.setFont(new Font("Arial", Font.BOLD, 13));
        resetBtn.setFocusPainted(false);
        resetBtn.setBorderPainted(false);
        resetBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        resetBtn.addActionListener(e -> resetSim());

        // Add all components
        panel.add(titleLabel("Humidity:"));
        panel.add(humSlider);
        panel.add(humidityLabel);
        panel.add(sep());
        panel.add(titleLabel("Wind:"));
        panel.add(windPanel);
        panel.add(sep());
        panel.add(titleLabel("Speed:"));
        panel.add(speedSlider);
        panel.add(speedLabel);
        panel.add(sep());
        panel.add(startBtn);
        panel.add(resetBtn);

        return panel;
    }

    // ── STATS PANEL (Khoi Nguyen) ───────────────────────────────────────────

    // Build the right-side live stats panel
    public JPanel buildStatsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(30, 30, 30));
        panel.setBorder(BorderFactory.createEmptyBorder(16, 14, 16, 14));
        panel.setPreferredSize(new Dimension(160, 0));

        JLabel title = new JLabel("LIVE STATS");
        title.setForeground(new Color(255, 120, 0));
        title.setFont(new Font("Arial", Font.BOLD, 13));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        treeLbl   = statLabel("Trees:      0");
        vegLbl    = statLabel("Vegetation: 0");
        fireLbl   = statLabel("Fire:       0");
        ashLbl    = statLabel("Ash:        0");

        JSeparator sep = new JSeparator();
        sep.setMaximumSize(new Dimension(140, 1));
        sep.setForeground(new Color(70, 70, 70));

        burnedLbl = statLabel("Burned: 0%");
        burnedLbl.setForeground(new Color(255, 160, 60));

        tickLbl   = statLabel("Tick:   0");
        tickLbl.setForeground(new Color(150, 200, 255));

        statusLbl = statLabel("Ready");
        statusLbl.setForeground(new Color(180, 180, 180));
        statusLbl.setFont(new Font("Arial", Font.BOLD, 12));

        panel.add(title);
        panel.add(Box.createVerticalStrut(12));
        panel.add(treeLbl);
        panel.add(Box.createVerticalStrut(4));
        panel.add(vegLbl);
        panel.add(Box.createVerticalStrut(4));
        panel.add(fireLbl);
        panel.add(Box.createVerticalStrut(4));
        panel.add(ashLbl);
        panel.add(Box.createVerticalStrut(10));
        panel.add(sep);
        panel.add(Box.createVerticalStrut(10));
        panel.add(burnedLbl);
        panel.add(Box.createVerticalStrut(4));
        panel.add(tickLbl);
        panel.add(Box.createVerticalStrut(10));
        panel.add(statusLbl);

        updateStats();
        return panel;
    }

    // ── HELPERS ──────────────────────────────────────────────────────────────

    private JLabel titleLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setForeground(new Color(200, 200, 200));
        lbl.setFont(new Font("Arial", Font.BOLD, 12));
        return lbl;
    }

    private JLabel valueLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setForeground(new Color(100, 200, 255));
        lbl.setFont(new Font("Arial", Font.BOLD, 12));
        lbl.setPreferredSize(new Dimension(65, 20));
        return lbl;
    }

    private JLabel statLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setForeground(new Color(210, 210, 210));
        lbl.setFont(new Font("Monospaced", Font.PLAIN, 12));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private JSlider darkSlider(int min, int max, int val, int width) {
        JSlider s = new JSlider(min, max, val);
        s.setBackground(new Color(35, 35, 35));
        s.setPreferredSize(new Dimension(width, 28));
        return s;
    }

    private void styleRadio(JRadioButton btn) {
        btn.setForeground(Color.WHITE);
        btn.setBackground(new Color(35, 35, 35));
        btn.setFont(new Font("Arial", Font.PLAIN, 12));
    }

    private Component sep() {
        return Box.createHorizontalStrut(8);
    }

    // ── MAIN ─────────────────────────────────────────────────────────────────

    public static void main(String[] args) {
        FireSimulationGUI gridPanel = new FireSimulationGUI();

        JFrame frame = new JFrame("Fire Spread Simulation — Fire Starters");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.add(gridPanel.buildControlPanel(), BorderLayout.NORTH);
        frame.add(gridPanel,                     BorderLayout.CENTER);
        frame.add(gridPanel.buildStatsPanel(),   BorderLayout.EAST);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setVisible(true);
    }
}
