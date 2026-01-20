package drawing;
import java.awt.*;

import components.Pin;
import components.Wire;
import java.util.List;

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
        List<java.awt.Point> midPoints = w.getMidPoints();
        g2d.setStroke(new BasicStroke(3));
        if (simulationMode && start.getState()) {
            g2d.setColor(Colors.OUTPUT_ON);
        } else {
            g2d.setColor(Colors.PIN);
        }
        int lastX = x1, lastY = y1;
        if (midPoints != null && !midPoints.isEmpty()) {
            for (java.awt.Point pt : midPoints) {
                int mx = pt.x * gridSize + offsetX;
                int my = pt.y * gridSize + offsetY;
                g2d.drawLine(lastX, lastY, mx, my);
                lastX = mx;
                lastY = my;
            }
        }
        g2d.drawLine(lastX, lastY, x2, y2);
    }

    public static void drawPreviewWire(Graphics2D g2d, Pin wireStartPin, int gridSize, int offsetX, int offsetY, java.util.List<Point> previewMidPoints, Point mouse, String selectedGateType) {
        if (wireStartPin != null && selectedGateType == null) {
            int[] c1 = wireStartPin.getCoordinates();
            int x1 = c1[0] * gridSize + offsetX;
            int y1 = c1[1] * gridSize + offsetY;
            g2d.setStroke(new BasicStroke(3));
            g2d.setColor(Color.ORANGE);
            int lastX = x1, lastY = y1;
            if (previewMidPoints != null && !previewMidPoints.isEmpty()) {
                for (Point pt : previewMidPoints) {
                    int mx = pt.x * gridSize + offsetX;
                    int my = pt.y * gridSize + offsetY;
                    g2d.drawLine(lastX, lastY, mx, my);
                    lastX = mx;
                    lastY = my;
                }
            }
            if (mouse != null) {
                g2d.drawLine(lastX, lastY, mouse.x, mouse.y);
            }
        }
        g2d.setStroke(new BasicStroke(1));
    }
}
