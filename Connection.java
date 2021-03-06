/**
 * Connection represents every connection between any two given neuron,
 * and can hold and change the weight between them
 *
 * @author Christopher Wan
 */

public class Connection {
	double weight = 0;
	double prevDeltaWeight = 0; // for momentum
	double deltaWeight = 0;

	final Neuron leftNeuron; // from
	final Neuron rightNeuron; // to
	static int counter = 0;
	final public int id; // auto increment, starts at 0

	public Connection(Neuron fromN, Neuron toN) {
		leftNeuron = fromN;
		rightNeuron = toN;
		id = counter;
		counter++;
	}

	public double getWeight() {
		return weight;
	}

	public void setWeight(double w) {
		weight = w;
	}

	public void setDeltaWeight(double w) {
		prevDeltaWeight = deltaWeight;
		deltaWeight = w;
	}

	public double getPrevDeltaWeight() {
		return prevDeltaWeight;
	}

	public Neuron getFromNeuron() {
		return leftNeuron;
	}

	public Neuron getToNeuron() {
		return rightNeuron;
	}
}