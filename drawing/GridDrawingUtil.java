package drawing;
import java.awt.*;

public class GridDrawingUtil {
    public static void drawGrid(Graphics g, int width, int height, int gridSize, int offsetX, int offsetY) {
        g.setColor(Color.GRAY);
        for (int x = offsetX % gridSize; x < width; x += gridSize) {
            g.drawLine(x, 0, x, height);
        }
        for (int y = offsetY % gridSize; y < height; y += gridSize) {
            g.drawLine(0, y, width, y);
        }
    }
}
