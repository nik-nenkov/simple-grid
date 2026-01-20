package components;
public class Wire {
    private int id = 0;
    private Pin startPin;
    private Pin endPin;
    public Wire(Pin start, Pin end) {
        this.id = CircuitDataStore.getWires().length;
        this.startPin = start;
        this.endPin = end;
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
}