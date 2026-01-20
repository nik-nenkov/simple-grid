package drawing;
import java.awt.*;

import components.InputSwitch;
import components.LogicGate;
import components.OutputLight;
import components.Pin;
import components.ClockTicker;

public class ElementDrawingUtil {
        // Draw ClockTicker with pin highlight and selection glow
    public static void drawClockTickerWithPinHighlightAndSelection(Graphics gIn, ClockTicker ct, boolean preview, boolean highlightPin, int gridSize, int offsetX, int offsetY, boolean simulationMode, boolean selected) {
        RectCenter rc = new RectCenter(ct.getCoordinates(), ct.getDimensions(), gridSize, offsetX, offsetY);
        Color old = gIn.getColor();
        // Draw yellow glow if selected
        if (selected) {
            gIn.setColor(new Color(255, 255, 100, 180));
            gIn.fillRect(rc.left - 6, rc.top - 6, (rc.right - rc.left) + 12, (rc.bottom - rc.top) + 12);
        }
        drawPin(gIn, ct.getOutput(), gridSize, offsetX, offsetY, highlightPin);
        Color bgColor = (preview || !ct.getState() || !simulationMode) ? Colors.ELEMENT_BG_OFF : Colors.ELEMENT_BG_ON;
        drawComponentBody(gIn, rc, bgColor);
        Font symbolFont = gridSize >= 40 ? Fonts.SYMBOL_LARGE : (gridSize >= 28 ? Fonts.SYMBOL_MEDIUM : Fonts.SYMBOL_SMALL);
        drawCenteredSymbol(gIn, "\u23F1", symbolFont, rc.centerX, rc.centerY, Color.BLUE); // ⏱
        gIn.setColor(old);
    }

    // Draw ClockTicker (preview or normal)
    public static void drawClockTicker(Graphics gIn, ClockTicker ct, boolean preview, int gridSize, int offsetX, int offsetY, boolean simulationMode) {
        RectCenter rc = new RectCenter(ct.getCoordinates(), ct.getDimensions(), gridSize, offsetX, offsetY);
        Color old = gIn.getColor();
        drawPin(gIn, ct.getOutput(), gridSize, offsetX, offsetY, false);
        Color bgColor = (preview || !ct.getState() || !simulationMode) ? Colors.ELEMENT_BG_OFF : Colors.ELEMENT_BG_ON;
        drawComponentBody(gIn, rc, bgColor);
        Font symbolFont = gridSize >= 40 ? Fonts.SYMBOL_LARGE : (gridSize >= 28 ? Fonts.SYMBOL_MEDIUM : Fonts.SYMBOL_SMALL);
        drawCenteredSymbol(gIn, "\u23F1", symbolFont, rc.centerX, rc.centerY, Color.BLUE); // ⏱
        gIn.setColor(old);
    }
    // Utility to draw a pin (with optional highlight)
    private static void drawPin(Graphics g, Pin pin, int gridSize, int offsetX, int offsetY, boolean highlight) {
        int[] coords = pin.getCoordinates();
        int px = coords[0] * gridSize + offsetX;
        int py = coords[1] * gridSize + offsetY;
        int pinWidth = Math.max(6, gridSize / 7);
        int pinHeight = Math.max(12, gridSize / 3);
        Color old = g.getColor();
        g.setColor(highlight ? Colors.PIN_HIGHLIGHT : Colors.PIN);
        Shape oldClip = g.getClip();
        g.setClip(null);
        g.fillRect(px - pinWidth / 2, py - pinHeight / 2, pinWidth, pinHeight);
        g.setClip(oldClip);
        g.setColor(old);
    }

    // Utility to calculate rectangle and center from coordinates/dimensions
    private static class RectCenter {
        int left, right, top, bottom, centerX, centerY;
        RectCenter(int[] coords, int[] dims, int gridSize, int offsetX, int offsetY) {
            centerX = coords[0] * gridSize + offsetX;
            centerY = coords[1] * gridSize + offsetY;
            left = centerX - dims[0] * gridSize;
            right = centerX + dims[0] * gridSize;
            top = centerY - dims[1] * gridSize;
            bottom = centerY + dims[1] * gridSize;
        }
    }

    // Utility to draw the component body rectangle
    private static void drawComponentBody(Graphics g, RectCenter rc, Color fill) {
        Color old = g.getColor();
        g.setColor(fill);
        g.fillRect(rc.left, rc.top, rc.right - rc.left, rc.bottom - rc.top);
        g.setColor(Color.BLACK);
        g.drawRect(rc.left, rc.top, rc.right - rc.left, rc.bottom - rc.top);
        g.setColor(old);
    }

    // Utility to draw a centered symbol
    private static void drawCenteredSymbol(Graphics g, String symbol, Font font, int centerX, int centerY, Color color) {
        Color old = g.getColor();
        g.setColor(color);
        g.setFont(font);
        FontMetrics fm = g.getFontMetrics();
        int width = fm.stringWidth(symbol);
        int height = fm.getAscent();
        int y = centerY + height / 2 - 2;
        g.drawString(symbol, centerX - width / 2, y);
        g.setColor(old);
    }

    // Draw InputSwitch with pin highlight and selection glow
    public static void drawInputSwitchWithPinHighlightAndSelection(Graphics gIn, InputSwitch sw, boolean preview, boolean highlightPin, int gridSize, int offsetX, int offsetY, boolean simulationMode, boolean selected) {
        RectCenter rc = new RectCenter(sw.getCoordinates(), sw.getDimensions(), gridSize, offsetX, offsetY);
        Color old = gIn.getColor();
        // Draw yellow glow if selected
        if (selected) {
            gIn.setColor(new Color(255, 255, 100, 180));
            gIn.fillRect(rc.left - 6, rc.top - 6, (rc.right - rc.left) + 12, (rc.bottom - rc.top) + 12);
        }
        drawPin(gIn, sw.getOutput(), gridSize, offsetX, offsetY, highlightPin);
        Color bgColor = (preview || !sw.getState() || !simulationMode) ? Colors.ELEMENT_BG_OFF : Colors.ELEMENT_BG_ON;
        drawComponentBody(gIn, rc, bgColor);
        Font symbolFont = gridSize >= 40 ? Fonts.SYMBOL_LARGE : (gridSize >= 28 ? Fonts.SYMBOL_MEDIUM : Fonts.SYMBOL_SMALL);
        drawCenteredSymbol(gIn, "\u23FB", symbolFont, rc.centerX, rc.centerY, Colors.POWER_SYMBOL);
        gIn.setColor(old);
    }

    // Draw OutputLight with pin highlight and selection glow
    public static void drawOutputLightWithPinHighlightAndSelection(Graphics gIn, OutputLight ol, boolean preview, boolean highlightPin, int gridSize, int offsetX, int offsetY, boolean simulationMode, boolean selected) {
        RectCenter rc = new RectCenter(ol.getCoordinates(), ol.getDimensions(), gridSize, offsetX, offsetY);
        Color old = gIn.getColor();
        if (selected) {
            gIn.setColor(new Color(255, 255, 100, 180));
            gIn.fillRect(rc.left - 6, rc.top - 6, (rc.right - rc.left) + 12, (rc.bottom - rc.top) + 12);
        }
        drawPin(gIn, ol.getInput(), gridSize, offsetX, offsetY, highlightPin);
        Color bgColor = (preview || !ol.getState() || !simulationMode) ? Colors.ELEMENT_BG_OFF : Colors.ELEMENT_BG_ON;
        drawComponentBody(gIn, rc, bgColor);
        Font bulbFont = gridSize >= 40 ? Fonts.SYMBOL_LARGE : (gridSize >= 28 ? Fonts.SYMBOL_MEDIUM : Fonts.SYMBOL_SMALL);
        drawCenteredSymbol(gIn, "\uD83D\uDCA1", bulbFont, rc.centerX, rc.centerY, Colors.OUTPUT_SYMBOL);
        gIn.setColor(old);
    }

    // Draw LogicGate with pin highlight and selection glow
    public static void drawGateWithPinHighlightAndSelection(Graphics gIn, LogicGate gate, boolean preview, boolean highlightIn1, boolean highlightIn2, boolean highlightOut, int gridSize, int offsetX, int offsetY, boolean selected) {
        RectCenter rc = new RectCenter(gate.getCoordinates(), gate.getDimensions(), gridSize, offsetX, offsetY);
        Color old = gIn.getColor();
        if (selected) {
            gIn.setColor(new Color(255, 255, 100, 180));
            gIn.fillRect(rc.left - 6, rc.top - 6, (rc.right - rc.left) + 12, (rc.bottom - rc.top) + 12);
        }
        if (gate.getInput1() != null) drawPin(gIn, gate.getInput1(), gridSize, offsetX, offsetY, highlightIn1);
        if (gate.getInput2() != null) drawPin(gIn, gate.getInput2(), gridSize, offsetX, offsetY, highlightIn2);
        if (gate.getOutput() != null) drawPin(gIn, gate.getOutput(), gridSize, offsetX, offsetY, highlightOut);
        drawComponentBody(gIn, rc, preview ? Colors.GATE_PREVIEW : Colors.GATE_SIM);
        Font labelFont = gridSize >= 40 ? Fonts.UI_LARGE : (gridSize >= 28 ? Fonts.UI_BOLD : Fonts.UI_REGULAR);
        drawCenteredSymbol(gIn, gate.getType() != null ? gate.getType().toString() : "?", labelFont, rc.centerX, rc.centerY, Color.BLACK);
        gIn.setColor(old);
    }

    public static void drawInputSwitchWithPinHighlight(Graphics gIn, InputSwitch sw, boolean preview, boolean highlightPin, int gridSize, int offsetX, int offsetY, boolean simulationMode) {
        RectCenter rc = new RectCenter(sw.getCoordinates(), sw.getDimensions(), gridSize, offsetX, offsetY);
        Color old = gIn.getColor();
        // Draw output pin first, so it appears below the component
        drawPin(gIn, sw.getOutput(), gridSize, offsetX, offsetY, highlightPin);
        // Draw component body: grey if edit mode or no signal, light green if signal
        Color bgColor = (preview || !sw.getState() || !simulationMode) ? Colors.ELEMENT_BG_OFF : Colors.ELEMENT_BG_ON;
        drawComponentBody(gIn, rc, bgColor);
        // Draw power button symbol centered and thick
        Font symbolFont = gridSize >= 40 ? Fonts.SYMBOL_LARGE : (gridSize >= 28 ? Fonts.SYMBOL_MEDIUM : Fonts.SYMBOL_SMALL);
        drawCenteredSymbol(gIn, "\u23FB", symbolFont, rc.centerX, rc.centerY, Colors.POWER_SYMBOL);
        gIn.setColor(old);
    }

    public static void drawInputSwitch(Graphics gIn, InputSwitch sw, boolean preview, int gridSize, int offsetX, int offsetY, boolean simulationMode) {
        RectCenter rc = new RectCenter(sw.getCoordinates(), sw.getDimensions(), gridSize, offsetX, offsetY);
        Color old = gIn.getColor();
        // Draw output pin first
        drawPin(gIn, sw.getOutput(), gridSize, offsetX, offsetY, false);
        // Draw component body: grey if edit mode or no signal, light green if signal
        Color bgColor = (preview || !sw.getState() || !simulationMode) ? Colors.ELEMENT_BG_OFF : Colors.ELEMENT_BG_ON;
        drawComponentBody(gIn, rc, bgColor);
        // Draw power button symbol centered and thick
        Font symbolFont = gridSize >= 40 ? Fonts.SYMBOL_LARGE : (gridSize >= 28 ? Fonts.SYMBOL_MEDIUM : Fonts.SYMBOL_SMALL);
        drawCenteredSymbol(gIn, "\u23FB", symbolFont, rc.centerX, rc.centerY, Colors.POWER_SYMBOL);
        gIn.setColor(old);
    }

    public static void drawOutputLightWithPinHighlight(Graphics gIn, OutputLight ol, boolean preview, boolean highlightPin, int gridSize, int offsetX, int offsetY, boolean simulationMode) {
        RectCenter rc = new RectCenter(ol.getCoordinates(), ol.getDimensions(), gridSize, offsetX, offsetY);
        Color old = gIn.getColor();
        // Draw input pin first
        drawPin(gIn, ol.getInput(), gridSize, offsetX, offsetY, highlightPin);
        // Draw component body: grey if edit mode or no signal, light green if signal
        Color bgColor = (preview || !ol.getState() || !simulationMode) ? Colors.ELEMENT_BG_OFF : Colors.ELEMENT_BG_ON;
        drawComponentBody(gIn, rc, bgColor);
        // Draw lightbulb symbol centered in square
        Font bulbFont = gridSize >= 40 ? Fonts.SYMBOL_LARGE : (gridSize >= 28 ? Fonts.SYMBOL_MEDIUM : Fonts.SYMBOL_SMALL);
        drawCenteredSymbol(gIn, "\uD83D\uDCA1", bulbFont, rc.centerX, rc.centerY, Colors.OUTPUT_SYMBOL);
        gIn.setColor(old);
    }

    public static void drawOutputLight(Graphics gIn, OutputLight ol, boolean preview, int gridSize, int offsetX, int offsetY, boolean simulationMode) {
        RectCenter rc = new RectCenter(ol.getCoordinates(), ol.getDimensions(), gridSize, offsetX, offsetY);
        Color old = gIn.getColor();
        // Draw input pin first
        drawPin(gIn, ol.getInput(), gridSize, offsetX, offsetY, false);
        // Draw component body: grey if edit mode or no signal, light green if signal
        Color bgColor = (preview || !ol.getState() || !simulationMode) ? Colors.ELEMENT_BG_OFF : Colors.ELEMENT_BG_ON;
        drawComponentBody(gIn, rc, bgColor);
        // Draw lightbulb symbol centered in square
        Font bulbFont = gridSize >= 40 ? Fonts.SYMBOL_LARGE : (gridSize >= 28 ? Fonts.SYMBOL_MEDIUM : Fonts.SYMBOL_SMALL);
        drawCenteredSymbol(gIn, "\uD83D\uDCA1", bulbFont, rc.centerX, rc.centerY, Colors.OUTPUT_SYMBOL);
        gIn.setColor(old);
    }

    public static void drawGateWithPinHighlight(Graphics gIn, LogicGate gate, boolean preview, boolean highlightIn1, boolean highlightIn2, boolean highlightOut, int gridSize, int offsetX, int offsetY) {
        RectCenter rc = new RectCenter(gate.getCoordinates(), gate.getDimensions(), gridSize, offsetX, offsetY);
        Color old = gIn.getColor();
        // Draw pins first
        if (gate.getInput1() != null) drawPin(gIn, gate.getInput1(), gridSize, offsetX, offsetY, highlightIn1);
        if (gate.getInput2() != null) drawPin(gIn, gate.getInput2(), gridSize, offsetX, offsetY, highlightIn2);
        if (gate.getOutput() != null) drawPin(gIn, gate.getOutput(), gridSize, offsetX, offsetY, highlightOut);
        // Draw component body
        drawComponentBody(gIn, rc, preview ? Colors.GATE_PREVIEW : Colors.GATE_SIM);
        // Draw gate type
        Font labelFont = gridSize >= 40 ? Fonts.UI_LARGE : (gridSize >= 28 ? Fonts.UI_BOLD : Fonts.UI_REGULAR);
        drawCenteredSymbol(gIn, gate.getType() != null ? gate.getType().toString() : "?", labelFont, rc.centerX, rc.centerY, Color.BLACK);
        gIn.setColor(old);
    }

    public static void drawGate(Graphics gIn, LogicGate gate, boolean preview, int gridSize, int offsetX, int offsetY) {
        RectCenter rc = new RectCenter(gate.getCoordinates(), gate.getDimensions(), gridSize, offsetX, offsetY);
        Color old = gIn.getColor();
        // Draw pins first
        if (gate.getInput1() != null) drawPin(gIn, gate.getInput1(), gridSize, offsetX, offsetY, false);
        if (gate.getInput2() != null) drawPin(gIn, gate.getInput2(), gridSize, offsetX, offsetY, false);
        if (gate.getOutput() != null) drawPin(gIn, gate.getOutput(), gridSize, offsetX, offsetY, false);
        // Draw component body
        drawComponentBody(gIn, rc, preview ? Colors.GATE_PREVIEW : Colors.GATE_SIM);
        // Draw gate type
        Font labelFont = gridSize >= 40 ? Fonts.UI_LARGE : (gridSize >= 28 ? Fonts.UI_BOLD : Fonts.UI_REGULAR);
        drawCenteredSymbol(gIn, gate.getType() != null ? gate.getType().toString() : "?", labelFont, rc.centerX, rc.centerY, Color.BLACK);
        gIn.setColor(old);
    }
}
