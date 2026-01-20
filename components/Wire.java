package components;
import java.awt.Point;
import java.util.List;
import java.util.ArrayList;

public class Wire {
    private int id = 0;
    private Pin startPin;
    private Pin endPin;
    private List<Point> midPoints = new ArrayList<>(); // grid nodes between start and end

    // Constructor for straight wire (no midpoints)
    public Wire(Pin start, Pin end) {
        this(start, end, new ArrayList<>());
    }

    // Constructor for wire with midpoints
    public Wire(Pin start, Pin end, List<Point> midPoints) {
        this.id = CircuitDataStore.getWires().length;
        this.startPin = start;
        this.endPin = end;
        if (midPoints != null) this.midPoints = new ArrayList<>(midPoints);
        CircuitDataStore.setWires(java.util.Arrays.copyOf(CircuitDataStore.getWires(), CircuitDataStore.getWires().length + 1));
        CircuitDataStore.getWires()[CircuitDataStore.getWires().length - 1] = this;
    }

    public int getId() {
        return this.id;
    }
    public Pin getStartPin() {
        return this.startPin;
    }
    public Pin getEndPin() {
        return this.endPin;
    }
    public List<Point> getMidPoints() {
        return this.midPoints;
    }
}