package components;
public class Pin {
    private int id = 0;
    private boolean state = false;
    private int[] coordinates = {0, 0};
    public boolean getState() {
        return this.state;
    }
    public void setState(boolean newState) {
        this.state = newState;
    }
    public Pin(int x, int y) {
        this.id = CircuitDataStore.getPins().length;
        this.coordinates[0] = x;
        this.coordinates[1] = y;
        CircuitDataStore.setPins(java.util.Arrays.copyOf(CircuitDataStore.getPins(), CircuitDataStore.getPins().length + 1));
        CircuitDataStore.getPins()[CircuitDataStore.getPins().length - 1] = this;
    }
    public int getId() {
        return this.id;
    }
    public int[] getCoordinates() {
        return this.coordinates;
    }
}
