import javax.swing.*;
import java.awt.*;
import drawing.Colors;
import drawing.Fonts;
import components.GateType;

public class LeftMenuPanel extends JPanel {
    private String selectedGateType = null;
    private JButton[] gateButtons;
    private boolean simulationMode = false;
    private Runnable selectionClearCallback;

    public LeftMenuPanel(GridCanvas canvasPanel) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setPreferredSize(new Dimension(180, 600));
        setBackground(Colors.BACKGROUND);
        setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Colors.SIDEBAR_BORDER));

        JLabel titleLabel = new JLabel("LOGIC GATES");
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setFont(Fonts.UI_LARGE);
        titleLabel.setForeground(Colors.TITLE_FG);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(24, 0, 24, 0));
        add(titleLabel);

        String[] gateTypes = java.util.Arrays.stream(GateType.values()).map(Enum::name).toArray(String[]::new);
        String[] elementTypes = {"INPUT_SWITCH", "CLOCK_TICKER", "OUTPUT_LIGHT"};
        String[] allTypes = new String[gateTypes.length + elementTypes.length];
        System.arraycopy(gateTypes, 0, allTypes, 0, gateTypes.length);
        System.arraycopy(elementTypes, 0, allTypes, gateTypes.length, elementTypes.length);
        gateButtons = new JButton[allTypes.length];

        for (int i = 0; i < allTypes.length; i++) {
            String type = allTypes[i];
            String label;
            if (type.equals("INPUT_SWITCH")) label = "Input Switch";
            else if (type.equals("CLOCK_TICKER")) label = "Clock Ticker";
            else if (type.equals("OUTPUT_LIGHT")) label = "Output Light";
            else label = type;
            JButton btn = new JButton(label);
            gateButtons[i] = btn;
            btn.setAlignmentX(Component.CENTER_ALIGNMENT);
            btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
            btn.setPreferredSize(new Dimension(getPreferredSize().width - 32, 38));
            btn.setFont(Fonts.UI_REGULAR);
            btn.setFocusPainted(false);
            btn.setForeground(Colors.SIDEBAR_BUTTON_FG);
            btn.setBackground(Colors.SIDEBAR_BUTTON_BG);
            btn.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btn.setBorder(BorderFactory.createLineBorder(Colors.SIDEBAR_BORDER, 1, true));
            btn.addActionListener(e -> {
                selectedGateType = type;
                for (JButton b : gateButtons) {
                    b.setBackground(Colors.SIDEBAR_BUTTON_BG);
                    b.setForeground(Colors.SIDEBAR_BUTTON_FG);
                }
                btn.setBackground(Colors.SIDEBAR_BUTTON_BG_SELECTED);
                btn.setForeground(Colors.SIDEBAR_BUTTON_FG_SELECTED);
                canvasPanel.setSelectedGateType(selectedGateType);
            });
            btn.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseEntered(java.awt.event.MouseEvent e) {
                    if (!btn.getBackground().equals(Colors.SIDEBAR_BUTTON_BG_SELECTED))
                        btn.setBackground(Colors.SIDEBAR_BUTTON_BG_HOVER);
                }
                @Override
                public void mouseExited(java.awt.event.MouseEvent e) {
                    if (!btn.getBackground().equals(Colors.SIDEBAR_BUTTON_BG_SELECTED))
                        btn.setBackground(Colors.SIDEBAR_BUTTON_BG);
                }
            });
            add(btn);
            add(Box.createRigidArea(new Dimension(0, 6)));
        }
    }

    public void setSelectionClearCallback(Runnable callback) {
        this.selectionClearCallback = callback;
    }

    public void clearSelection() {
        selectedGateType = null;
        // Visually deselect all buttons
        for (JButton b : gateButtons) {
            b.setBackground(Colors.SIDEBAR_BUTTON_BG);
            b.setForeground(Colors.SIDEBAR_BUTTON_FG);
        }
        repaint();
        if (selectionClearCallback != null) selectionClearCallback.run();
    }
    // Show/hide add-item buttons based on simulation mode
    public void setSimulationMode(boolean sim) {
        this.simulationMode = sim;
        for (JButton b : gateButtons) {
            b.setVisible(!sim);
        }
        // Also clear selection when entering simulation mode
        if (sim) {
            clearSelection();
        }
        revalidate();
        repaint();
    }

    public void setSelectedGateType(String type) {
        this.selectedGateType = type;
        // Always update button visuals
        for (int i = 0; i < gateButtons.length; i++) {
            JButton btn = gateButtons[i];
            String btnType = btn.getText().equals("Input Switch") ? "INPUT_SWITCH" : btn.getText().equals("Output Light") ? "OUTPUT_LIGHT" : btn.getText();
            if (type != null && btnType.equals(type)) {
                btn.setBackground(Colors.SIDEBAR_BUTTON_BG_SELECTED);
                btn.setForeground(Colors.SIDEBAR_BUTTON_FG_SELECTED);
            } else {
                btn.setBackground(Colors.SIDEBAR_BUTTON_BG);
                btn.setForeground(Colors.SIDEBAR_BUTTON_FG);
            }
        }
        repaint();
    }

    public String getSelectedGateType() {
        return selectedGateType;
    }

    public boolean isSimulationMode() {
        return simulationMode;
    }
}
