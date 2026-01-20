package components;

public class ClockTicker {
    private int[] coordinates = {0, 0};
    private int[] dimensions = {1, 1};
    private Pin output;
    private boolean state = false;
    private boolean running = false;
    private Thread tickerThread;

    public ClockTicker(int x, int y) {
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

    public void startTicker() {
        if (running) return;
        running = true;
        tickerThread = new Thread(() -> {
            while (running) {
                toggleState();
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
        tickerThread.setDaemon(true);
        tickerThread.start();
    }

    public void stopTicker() {
        running = false;
        if (tickerThread != null) {
            tickerThread.interrupt();
        }
    }

    private void toggleState() {
        state = !state;
        output.setState(state);
    }

    public void setState(boolean s) {
        state = s;
        output.setState(state);
    }
}
