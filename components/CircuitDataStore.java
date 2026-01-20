package components;

public class CircuitDataStore {
    private static Pin[] pins = new Pin[0];
    private static Wire[] wires = new Wire[0];
    private static LogicGate[] gates = new LogicGate[0];

    public static Pin[] getPins() {
        return pins;
    }
    public static void setPins(Pin[] pinsArr) {
        pins = pinsArr;
    }
    public static Wire[] getWires() {
        return wires;
    }
    public static void setWires(Wire[] wiresArr) {
        wires = wiresArr;
    }
    public static LogicGate[] getGates() {
        return gates;
    }
    public static void setGates(LogicGate[] gatesArr) {
        gates = gatesArr;
    }

    public static int getNextPinId() {
        return pins.length;
    }
    public static int getNextWireId() {
        return wires.length;
    }
    public static int getNextGateId() {
        return gates.length;
    }
}
