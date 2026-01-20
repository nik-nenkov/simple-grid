package components;
public class InputSwitch {
    private int[] coordinates = {0, 0};
    private int[] dimensions = {1, 1};
    private Pin output;
    private boolean state = false;

    public InputSwitch(int x, int y) {
        this.coordinates[0] = x;
        this.coordinates[1] = y;
        this.output = new Pin(x + dimensions[0], y);
        this.output.setState(state);
    }

    public int[] getCoordinates() {
        return coordinates;
    }

    public int[] getDimensions() {
        return dimensions;
    }

    public Pin getOutput() {
        return output;
    }

    public boolean getState() {
        return state;
    }

    public void toggleState() {
        state = !state;
        output.setState(state);
    }

    public void setState(boolean s) {
        state = s;
        output.setState(state);
    }
}
