import javax.swing.*;
import java.awt.*;
import drawing.GridDrawingUtil;
import drawing.WireDrawingUtil;
import drawing.ElementDrawingUtil;
import components.LogicGate;
import components.InputSwitch;
import components.OutputLight;
import components.Pin;
import components.Wire;
import components.GateType;


public class GridCanvas extends JPanel {
    private Runnable selectionClearCallback = null;
    // Provider to get the current selected gate type from the sidebar
    public interface SelectedGateTypeProvider { String getSelectedGateType(); }
    private SelectedGateTypeProvider selectedGateTypeProvider = null;

    public void setSelectionClearCallback(Runnable callback) {
        this.selectionClearCallback = callback;
    }

    // Set the provider for selected gate type
    public void setSelectedGateTypeProvider(SelectedGateTypeProvider provider) {
        this.selectedGateTypeProvider = provider;
    }

    private int gridSize = 25;
    private int offsetX = 0;
    private int offsetY = 0;
    private int lastDragX = 0;
    private int lastDragY = 0;

    // No longer store selectedGateType here; always use sidebar as source of truth
    private int mouseGridX = -1;
    private int mouseGridY = -1;
    private java.util.List<Object> placedElements = new java.util.ArrayList<>(); // LogicGate, InputSwitch, OutputLight
    private java.util.List<Wire> wires = new java.util.ArrayList<>();
    private Pin wireStartPin = null;
    private Pin hoveredPin = null;
    private boolean previewVisible = false;
    private boolean simulationMode = false;
    private Point lastMousePosition = null;

    // Selection state
    private java.util.Set<Object> selectedElements = new java.util.HashSet<>();


    public void setSimulationMode(boolean sim) {
        this.simulationMode = sim;
        repaint();
    }

    // Called by sidebar to update preview state
    public void setSelectedGateType(String type) {
        previewVisible = false;
        repaint();
    }

    // New: clear preview state (called from callback)
    public void clearPlacementPreview() {
        previewVisible = false;
        repaint();
    }

    public GridCanvas() {
        java.awt.event.MouseAdapter mouseAdapter = new java.awt.event.MouseAdapter() {
            private boolean dragging = false;

            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                requestFocusInWindow();
                lastDragX = e.getX();
                lastDragY = e.getY();
                // Enable dragging for left mouse button
                dragging = e.getButton() == java.awt.event.MouseEvent.BUTTON1;
                // Right click selection clearing is now handled globally in Main.java
                // (No-op here)
                // Toggle InputSwitch on mouse press in simulation mode
                if (simulationMode && e.getButton() == java.awt.event.MouseEvent.BUTTON1) {
                    for (Object obj : placedElements) {
                        if (obj instanceof InputSwitch) {
                            InputSwitch sw = (InputSwitch) obj;
                            int[] coords = sw.getCoordinates();
                            int[] dims = sw.getDimensions();
                            int centerX = coords[0] * gridSize + offsetX;
                            int centerY = coords[1] * gridSize + offsetY;
                            int left = centerX - dims[0] * gridSize;
                            int right = centerX + dims[0] * gridSize;
                            int top = centerY - dims[1] * gridSize;
                            int bottom = centerY + dims[1] * gridSize;
                            if (e.getX() >= left && e.getX() <= right && e.getY() >= top && e.getY() <= bottom) {
                                sw.toggleState();
                                repaint();
                                break;
                            }
                        }
                    }
                }
                // --- Moved from mouseClicked: selection, placement, and wire logic ---
                if (!simulationMode && e.getButton() == java.awt.event.MouseEvent.BUTTON1) {
                    String selectedGateType = selectedGateTypeProvider != null ? selectedGateTypeProvider.getSelectedGateType() : null;
                    if (selectedGateType == null) {
                        // --- Selection logic ---
                        Object clickedElement = findElementAt(e.getX(), e.getY());
                        boolean ctrlDown = (e.getModifiersEx() & java.awt.event.InputEvent.CTRL_DOWN_MASK) != 0;
                        if (clickedElement != null) {
                            if (ctrlDown) {
                                if (selectedElements.contains(clickedElement)) {
                                    selectedElements.remove(clickedElement);
                                } else {
                                    selectedElements.add(clickedElement);
                                }
                            } else {
                                selectedElements.clear();
                                selectedElements.add(clickedElement);
                            }
                            repaint();
                        } else if (!ctrlDown) {
                            selectedElements.clear();
                            repaint();
                        }
                        // --- Wire drawing mode: click output pin, then input pin ---
                        Pin clickedPin = hoveredPin != null ? hoveredPin : findClosestPin(e.getX(), e.getY(), 10);
                        if (clickedPin != null) {
                            if (wireStartPin == null && isOutputPin(clickedPin)) {
                                wireStartPin = clickedPin;
                            } else if (wireStartPin != null && isInputPin(clickedPin) && wireStartPin != clickedPin) {
                                wires.add(new Wire(wireStartPin, clickedPin));
                                wireStartPin = null;
                                repaint();
                            } else {
                                wireStartPin = null;
                            }
                        } else {
                            wireStartPin = null;
                        }
                    } else if (selectedGateType != null && previewVisible) {
                        int x = mouseGridX;
                        int y = mouseGridY;
                        if (selectedGateType.equals("INPUT_SWITCH")) {
                            placedElements.add(new InputSwitch(x, y));
                        } else if (selectedGateType.equals("OUTPUT_LIGHT")) {
                            placedElements.add(new OutputLight(x, y));
                        } else {
                            GateType gateTypeEnum;
                            try {
                                gateTypeEnum = GateType.valueOf(selectedGateType);
                            } catch (Exception ex) {
                                gateTypeEnum = GateType.AND;
                            }
                            placedElements.add(new LogicGate(x, y, gateTypeEnum));
                        }
                        repaint();
                    }
                }
            }

            @Override
            public void mouseDragged(java.awt.event.MouseEvent e) {
                int snappedX = ((e.getX() - offsetX + gridSize / 2) / gridSize) * gridSize + offsetX;
                int snappedY = ((e.getY() - offsetY + gridSize / 2) / gridSize) * gridSize + offsetY;
                lastMousePosition = new Point(snappedX, snappedY);
                repaint();
                if (dragging) {
                    int dx = e.getX() - lastDragX;
                    int dy = e.getY() - lastDragY;
                    offsetX += dx;
                    offsetY += dy;
                    lastDragX = e.getX();
                    lastDragY = e.getY();
                    repaint();
                }
            }

            @Override
            public void mouseReleased(java.awt.event.MouseEvent e) {
                dragging = false;
            }

            @Override
            public void mouseMoved(java.awt.event.MouseEvent e) {
                // Snap to grid for preview wire
                int snappedX = ((e.getX() - offsetX + gridSize / 2) / gridSize) * gridSize + offsetX;
                int snappedY = ((e.getY() - offsetY + gridSize / 2) / gridSize) * gridSize + offsetY;
                lastMousePosition = new Point(snappedX, snappedY);
                repaint();
                boolean pointerSet = false;
                if (simulationMode) {
                    // Pointer cursor if hovering over clickable input switch
                    for (Object obj : placedElements) {
                        if (obj instanceof InputSwitch) {
                            InputSwitch sw = (InputSwitch) obj;
                            int[] coords = sw.getCoordinates();
                            int[] dims = sw.getDimensions();
                            int centerX = coords[0] * gridSize + offsetX;
                            int centerY = coords[1] * gridSize + offsetY;
                            int left = centerX - dims[0] * gridSize;
                            int right = centerX + dims[0] * gridSize;
                            int top = centerY - dims[1] * gridSize;
                            int bottom = centerY + dims[1] * gridSize;
                            if (e.getX() >= left && e.getX() <= right && e.getY() >= top && e.getY() <= bottom) {
                                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                                pointerSet = true;
                                break;
                            }
                        }
                    }
                }
                if (!pointerSet) {
                    setCursor(Cursor.getDefaultCursor());
                }
                // Always get selectedGateType from sidebar (source of truth)
                String selectedGateType = selectedGateTypeProvider != null ? selectedGateTypeProvider.getSelectedGateType() : null;
                if (selectedGateType != null) {
                    int gx = (e.getX() - offsetX + gridSize / 2) / gridSize;
                    int gy = (e.getY() - offsetY + gridSize / 2) / gridSize;
                    mouseGridX = gx;
                    mouseGridY = gy;
                    previewVisible = true;
                    repaint();
                } else if (selectedGateType == null && !simulationMode) {
                    // Edit mode: highlight pin if mouse is near
                    Pin closest = findClosestPin(e.getX(), e.getY(), 10);
                    if (closest != hoveredPin) {
                        hoveredPin = closest;
                        repaint();
                    }
                }
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                previewVisible = false;
                hoveredPin = null;
                repaint();
            }

            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                // No-op: all selection, placement, and wire logic handled in mousePressed
            }

            // Find closest pin at screen coordinates within a given radius
            private Pin findClosestPin(int sx, int sy, int radius) {
                Pin closest = null;
                double minDist = Double.MAX_VALUE;
                for (Object obj : placedElements) {
                    if (obj instanceof LogicGate) {
                        LogicGate g = (LogicGate) obj;
                        Pin[] pins = { g.getInput1(), g.getInput2(), g.getOutput() };
                        for (Pin p : pins) {
                            if (p == null)
                                continue;
                            int[] c = p.getCoordinates();
                            int px = c[0] * gridSize + offsetX;
                            int py = c[1] * gridSize + offsetY;
                            double dist = Math.hypot(sx - px, sy - py);
                            if (dist < radius && dist < minDist) {
                                minDist = dist;
                                closest = p;
                            }
                        }
                    } else if (obj instanceof InputSwitch) {
                        Pin p = ((InputSwitch) obj).getOutput();
                        int[] c = p.getCoordinates();
                        int px = c[0] * gridSize + offsetX;
                        int py = c[1] * gridSize + offsetY;
                        double dist = Math.hypot(sx - px, sy - py);
                        if (dist < radius && dist < minDist) {
                            minDist = dist;
                            closest = p;
                        }
                    } else if (obj instanceof OutputLight) {
                        Pin p = ((OutputLight) obj).getInput();
                        int[] c = p.getCoordinates();
                        int px = c[0] * gridSize + offsetX;
                        int py = c[1] * gridSize + offsetY;
                        double dist = Math.hypot(sx - px, sy - py);
                        if (dist < radius && dist < minDist) {
                            minDist = dist;
                            closest = p;
                        }
                    }
                }
                return closest;
            }

            // Check if pin is output
            private boolean isOutputPin(Pin p) {
                for (Object obj : placedElements) {
                    if (obj instanceof LogicGate && ((LogicGate) obj).getOutput() == p)
                        return true;
                    if (obj instanceof InputSwitch && ((InputSwitch) obj).getOutput() == p)
                        return true;
                }
                return false;
            }

            // Check if pin is input
            private boolean isInputPin(Pin p) {
                for (Object obj : placedElements) {
                    if (obj instanceof LogicGate) {
                        if (((LogicGate) obj).getInput1() == p || ((LogicGate) obj).getInput2() == p)
                            return true;
                    }
                    if (obj instanceof OutputLight && ((OutputLight) obj).getInput() == p)
                        return true;
                }
                return false;
            }
        };
        addMouseListener(mouseAdapter);
        addMouseMotionListener(mouseAdapter);
        // Mouse wheel zoom
        addMouseWheelListener(e -> {
            int notches = e.getWheelRotation();
            int oldGridSize = gridSize;
            if (notches < 0) {
                gridSize = Math.min(100, gridSize + 5);
            } else {
                gridSize = Math.max(10, gridSize - 5);
            }
            // Adjust offset to zoom around mouse pointer
            int mx = e.getX();
            int my = e.getY();
            offsetX = mx - (int) (((mx - offsetX) / (double) oldGridSize) * gridSize);
            offsetY = my - (int) (((my - offsetY) / (double) oldGridSize) * gridSize);
            repaint();
        });
        // Listen for ESC key to clear selection, and DEL to delete selected
        setFocusable(true);
        addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ESCAPE) {
                    if (selectionClearCallback != null) {
                        selectionClearCallback.run();
                    }
                    return;
                }
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_DELETE) {
                    if (!selectedElements.isEmpty()) {
                        // Remove selected elements and their wires
                        java.util.Set<Pin> pinsToRemove = new java.util.HashSet<>();
                        for (Object obj : selectedElements) {
                            if (obj instanceof LogicGate) {
                                LogicGate g = (LogicGate) obj;
                                pinsToRemove.add(g.getInput1());
                                pinsToRemove.add(g.getInput2());
                                pinsToRemove.add(g.getOutput());
                            } else if (obj instanceof InputSwitch) {
                                pinsToRemove.add(((InputSwitch) obj).getOutput());
                            } else if (obj instanceof OutputLight) {
                                pinsToRemove.add(((OutputLight) obj).getInput());
                            }
                        }
                        // Remove wires connected to those pins
                        wires.removeIf(w -> pinsToRemove.contains(w.getStartPin()) || pinsToRemove.contains(w.getEndPin()));
                        // Remove elements
                        placedElements.removeAll(selectedElements);
                        selectedElements.clear();
                        repaint();
                    }
                }
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawGrid(g);
        if (simulationMode) {
            propagateSignals();
        }
        drawWires((Graphics2D) g);
        drawPreviewWire((Graphics2D) g);
        drawPlacedElements(g);
        drawPreviewElement(g);
    }

    // Draw the grid lines
    private void drawGrid(Graphics g) {
        GridDrawingUtil.drawGrid(g, getWidth(), getHeight(), gridSize, offsetX, offsetY);
    }

    // Propagate signals for simulation
    private void propagateSignals() {
        clearPinStatesExceptInputSwitches();
        for (int pass = 0; pass < 4; pass++) {
            propagateWireStates();
            calculateGateOutputs();
        }
    }

    private void clearPinStatesExceptInputSwitches() {
        java.util.HashSet<Pin> inputSwitchPins = new java.util.HashSet<>();
        for (Object obj : placedElements) {
            if (obj instanceof InputSwitch) {
                InputSwitch sw = (InputSwitch) obj;
                sw.getOutput().setState(sw.getState());
                inputSwitchPins.add(sw.getOutput());
            }
        }
        for (Object obj : placedElements) {
            if (obj instanceof LogicGate) {
                LogicGate gate = (LogicGate) obj;
                gate.getInput1().setState(false);
                gate.getInput2().setState(false);
                gate.getOutput().setState(false);
            } else if (obj instanceof OutputLight) {
                ((OutputLight) obj).getInput().setState(false);
            }
        }
    }

    private void propagateWireStates() {
        for (Wire w : wires) {
            Pin start = w.getStartPin();
            Pin end = w.getEndPin();
            end.setState(start.getState());
        }
    }

    private void calculateGateOutputs() {
        for (Object obj : placedElements) {
            if (obj instanceof LogicGate) {
                ((LogicGate) obj).calculateOutput();
            }
        }
    }

    // Draw all wires
    private void drawWires(Graphics2D g2d) {
        for (Wire w : wires) {
            WireDrawingUtil.drawWire(g2d, w, gridSize, offsetX, offsetY, simulationMode);
        }
    }

    // Draw preview wire if in wire drawing mode
    private void drawPreviewWire(Graphics2D g2d) {
        String selectedGateType = selectedGateTypeProvider != null ? selectedGateTypeProvider.getSelectedGateType() : null;
        WireDrawingUtil.drawPreviewWire(g2d, wireStartPin, gridSize, offsetX, offsetY, lastMousePosition, selectedGateType);
    }

    // Draw all placed elements (gates, switches, lights)
    private void drawPlacedElements(Graphics g) {
        for (Object obj : placedElements) {
            boolean selected = selectedElements.contains(obj);
            if (obj instanceof LogicGate) {
                LogicGate gate = (LogicGate) obj;
                boolean highlightIn1 = (wireStartPin != null && gate.getInput1() == wireStartPin) || (hoveredPin != null && gate.getInput1() == hoveredPin);
                boolean highlightIn2 = (wireStartPin != null && gate.getInput2() == wireStartPin) || (hoveredPin != null && gate.getInput2() == hoveredPin);
                boolean highlightOut = (wireStartPin != null && gate.getOutput() == wireStartPin) || (hoveredPin != null && gate.getOutput() == hoveredPin);
                ElementDrawingUtil.drawGateWithPinHighlightAndSelection(g, gate, false, highlightIn1, highlightIn2, highlightOut, gridSize, offsetX, offsetY, selected);
            } else if (obj instanceof InputSwitch) {
                InputSwitch sw = (InputSwitch) obj;
                boolean highlight = (wireStartPin != null && sw.getOutput() == wireStartPin) || (hoveredPin != null && sw.getOutput() == hoveredPin);
                ElementDrawingUtil.drawInputSwitchWithPinHighlightAndSelection(g, sw, false, highlight, gridSize, offsetX, offsetY, simulationMode, selected);
            } else if (obj instanceof OutputLight) {
                OutputLight ol = (OutputLight) obj;
                boolean highlight = (wireStartPin != null && ol.getInput() == wireStartPin) || (hoveredPin != null && ol.getInput() == hoveredPin);
                ElementDrawingUtil.drawOutputLightWithPinHighlightAndSelection(g, ol, false, highlight, gridSize, offsetX, offsetY, simulationMode, selected);
            }
        }
    }

    // Find element at screen coordinates (for selection)
    private Object findElementAt(int sx, int sy) {
        for (int i = placedElements.size() - 1; i >= 0; i--) { // Topmost first
            Object obj = placedElements.get(i);
            int[] coords = null, dims = null;
            if (obj instanceof LogicGate) {
                coords = ((LogicGate) obj).getCoordinates();
                dims = ((LogicGate) obj).getDimensions();
            } else if (obj instanceof InputSwitch) {
                coords = ((InputSwitch) obj).getCoordinates();
                dims = ((InputSwitch) obj).getDimensions();
            } else if (obj instanceof OutputLight) {
                coords = ((OutputLight) obj).getCoordinates();
                dims = ((OutputLight) obj).getDimensions();
            }
            if (coords != null && dims != null) {
                int centerX = coords[0] * gridSize + offsetX;
                int centerY = coords[1] * gridSize + offsetY;
                int left = centerX - dims[0] * gridSize;
                int right = centerX + dims[0] * gridSize;
                int top = centerY - dims[1] * gridSize;
                int bottom = centerY + dims[1] * gridSize;
                if (sx >= left && sx <= right && sy >= top && sy <= bottom) {
                    return obj;
                }
            }
        }
        return null;
    }

    // Draw preview element (not yet placed)
    private void drawPreviewElement(Graphics g) {
        String selectedGateType = selectedGateTypeProvider != null ? selectedGateTypeProvider.getSelectedGateType() : null;
        if (selectedGateType != null && previewVisible) {
            int x = mouseGridX;
            int y = mouseGridY;
            if (selectedGateType.equals("INPUT_SWITCH")) {
                ElementDrawingUtil.drawInputSwitch(g, new InputSwitch(x, y), true, gridSize, offsetX, offsetY, simulationMode);
            } else if (selectedGateType.equals("OUTPUT_LIGHT")) {
                ElementDrawingUtil.drawOutputLight(g, new OutputLight(x, y), true, gridSize, offsetX, offsetY, simulationMode);
            } else {
                GateType gateTypeEnum;
                try {
                    gateTypeEnum = GateType.valueOf(selectedGateType);
                } catch (Exception ex) {
                    gateTypeEnum = GateType.AND;
                }
                ElementDrawingUtil.drawGate(g, new LogicGate(x, y, gateTypeEnum), true, gridSize, offsetX, offsetY);
            }
        }
    }


}