package drawing;
import java.awt.*;

import components.Pin;
import components.Wire;

public class WireDrawingUtil {
    public static void drawWire(Graphics2D g2d, Wire w, int gridSize, int offsetX, int offsetY, boolean simulationMode) {
        Pin start = w.getStartPin();
        Pin end = w.getEndPin();
        int[] c1 = start.getCoordinates();
        int[] c2 = end.getCoordinates();
        int x1 = c1[0] * gridSize + offsetX;
        int y1 = c1[1] * gridSize + offsetY;
        int x2 = c2[0] * gridSize + offsetX;
        int y2 = c2[1] * gridSize + offsetY;
        g2d.setStroke(new BasicStroke(3));
        if (simulationMode && start.getState()) {
            g2d.setColor(Colors.OUTPUT_ON); // Use the same color as active output
        } else {
            g2d.setColor(Colors.PIN);
        }
        g2d.drawLine(x1, y1, x2, y2);
    }

    public static void drawPreviewWire(Graphics2D g2d, Pin wireStartPin, int gridSize, int offsetX, int offsetY, Point mouse, String selectedGateType) {
        if (wireStartPin != null && selectedGateType == null) {
            int[] c1 = wireStartPin.getCoordinates();
            int x1 = c1[0] * gridSize + offsetX;
            int y1 = c1[1] * gridSize + offsetY;
            if (mouse != null) {
                g2d.setStroke(new BasicStroke(3));
                g2d.setColor(Color.ORANGE);
                g2d.drawLine(x1, y1, mouse.x, mouse.y);
            }
        }
        g2d.setStroke(new BasicStroke(1));
    }
}
