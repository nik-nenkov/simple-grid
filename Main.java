import javax.swing.*;
import java.awt.*;
import drawing.Colors;
import drawing.Fonts;

public class Main {

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> createAndShowGUI());
	}

	private static void createAndShowGUI() {
		JFrame frame = new JFrame("Simple Logic Grid");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(900, 600);

		// Right canvas panel (declare before menu for use in callbacks)
		GridCanvas canvasPanel = new GridCanvas();
		canvasPanel.setPreferredSize(new Dimension(750, 600));

		// Modern left menu panel
		LeftMenuPanel menuPanel = new LeftMenuPanel(canvasPanel);
		menuPanel.setPreferredSize(new Dimension(180, 600));
		menuPanel.setBackground(Colors.BACKGROUND);
		menuPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Colors.SIDEBAR_BORDER));

		// Register panels with SelectionManager singleton
		SelectionManager.registerPanels(menuPanel, canvasPanel);
		// Setup selection clear callback and provider immediately after construction
		menuPanel.setSelectionClearCallback(() -> {
			SelectionManager.clearSelection();
		});
		canvasPanel.setSelectedGateTypeProvider(menuPanel::getSelectedGateType);

		// Pass selection to canvas
		canvasPanel.setSelectedGateType(menuPanel.getSelectedGateType());

		// Add mode toggle button at the bottom
		JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
		bottomPanel.setBackground(Colors.BACKGROUND);
		JButton modeButton = new JButton("Switch to Simulation Mode");
		modeButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		modeButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 54));
		modeButton.setPreferredSize(new Dimension(menuPanel.getPreferredSize().width - 32, 54));
		modeButton.setFont(Fonts.UI_BOLD);
		modeButton.setFocusPainted(false);
		modeButton.setForeground(Colors.MODE_BUTTON_FG);
		modeButton.setBackground(Colors.MODE_BUTTON_BG);
		modeButton.setBorder(BorderFactory.createEmptyBorder(12, 18, 12, 18));
		modeButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		bottomPanel.add(Box.createVerticalGlue());
		bottomPanel.add(modeButton);
		menuPanel.add(Box.createVerticalGlue());
		menuPanel.add(bottomPanel);

		// Track mode
		final boolean[] simulationMode = {false};
		modeButton.addActionListener(e -> {
			simulationMode[0] = !simulationMode[0];
			if (simulationMode[0]) {
				modeButton.setText("Simulation Mode");
				modeButton.setBackground(Colors.MODE_BUTTON_BG_ACTIVE);
				modeButton.setForeground(Colors.MODE_BUTTON_FG_ACTIVE);
			} else {
				modeButton.setText("Edit Mode");
				modeButton.setBackground(Colors.MODE_BUTTON_BG);
				modeButton.setForeground(Colors.MODE_BUTTON_FG);
			}
			canvasPanel.setSimulationMode(simulationMode[0]);
			menuPanel.setSimulationMode(simulationMode[0]);
		});
		// Ensure menu starts in edit mode
		menuPanel.setSimulationMode(false);
		canvasPanel.setSimulationMode(false);

		// Layout
		frame.setLayout(new BorderLayout());
		frame.add(menuPanel, BorderLayout.WEST);
		frame.add(canvasPanel, BorderLayout.CENTER);

		// GLOBAL: Escape key clears selection anywhere
		java.awt.KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(e -> {
			if (e.getID() == java.awt.event.KeyEvent.KEY_PRESSED && e.getKeyCode() == java.awt.event.KeyEvent.VK_ESCAPE) {
				SelectionManager.clearSelection();
				return true; // consume event
			}
			return false;
		});

		// Add right-click handler to GridCanvas
		canvasPanel.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mousePressed(java.awt.event.MouseEvent e) {
				if (javax.swing.SwingUtilities.isRightMouseButton(e)) {
					SelectionManager.clearSelection();
				}
			}
		});
		// Add right-click handler to LeftMenuPanel
		menuPanel.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mousePressed(java.awt.event.MouseEvent e) {
				if (javax.swing.SwingUtilities.isRightMouseButton(e)) {
					SelectionManager.clearSelection();
				}
			}
		});

		frame.setVisible(true);
	}
}