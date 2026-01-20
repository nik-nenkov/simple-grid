package components;
public class OutputLight {
    private int[] coordinates = {0, 0};
    private int[] dimensions = {1, 1};
    private Pin input;

    public OutputLight(int x, int y) {
        this.coordinates[0] = x;
        this.coordinates[1] = y;
        this.input = new Pin(x - dimensions[0], y);
    }

    public int[] getCoordinates() {
        return coordinates;
    }

    public int[] getDimensions() {
        return dimensions;
    }

    public Pin getInput() {
        return input;
    }

    public boolean getState() {
        return input.getState();
    }
}
