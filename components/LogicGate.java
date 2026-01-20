package components;
public class LogicGate {
	private int id = 0;
	private int[] coordinates = {0, 0};
	private int[] dimensions = {1, 2};
	private Pin input1;
	private Pin input2;
	private Pin output;
	private GateType type;
	public void calculateOutput() {
		// Logic to calculate output based on gate type and input states
		this.output.setState(switch (this.type) {
			case AND -> this.input1.getState() && this.input2.getState();
			case OR -> this.input1.getState() || this.input2.getState();
			case NAND -> !(this.input1.getState() && this.input2.getState());
			case NOR -> !(this.input1.getState() || this.input2.getState());
			case XOR -> this.input1.getState() ^ this.input2.getState();
			case XNOR -> !(this.input1.getState() ^ this.input2.getState());
		});
	}
	public LogicGate(int x, int y, GateType type) {
		this.id = CircuitDataStore.getGates().length;
		this.coordinates[0] = x;
		this.coordinates[1] = y;
		this.type = type;
		this.input1 = new Pin(x-dimensions[0], y-dimensions[1]/2);
		this.input2 = new Pin(x-dimensions[0], y+dimensions[1]/2);
		this.output = new Pin(x+dimensions[0], y);
		CircuitDataStore.setGates(java.util.Arrays.copyOf(CircuitDataStore.getGates(), CircuitDataStore.getGates().length + 1));
		CircuitDataStore.getGates()[CircuitDataStore.getGates().length - 1] = this;
	}
	public int getId() {
		return this.id;
	}
	public int[] getCoordinates() {
		return this.coordinates;
	}
	public int[] getDimensions() {
		return this.dimensions;
	}
	public Pin getInput1() {
		return this.input1;
	}
	public Pin getInput2() {
		return this.input2;
	}
	public Pin getOutput() {
		return this.output;
	}
	public GateType getType() {
		return this.type;
	}
	
}