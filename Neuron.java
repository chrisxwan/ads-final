/**
 * Neuron represents and contains all of the functionalities of
 * an arbitrary neuron in the artificial neural network
 *
 * @author Christopher Wan
 */
import java.util.*;

public class Neuron {	
	static int counter = 0;
	final public int id;  // auto increment, starts at 0
	Connection biasConnection;
	final double bias = -1;
	double output;

	ArrayList<Connection> Inconnections = new ArrayList<Connection>(); // all connections going into this neuron
	HashMap<Integer,Connection> connectionLookup = new HashMap<Integer,Connection>(); // hashmap of all (neuron #, connection) pairs
	
	public Neuron(){		
		id = counter;
		counter++;
	}
	
	/**
	 * Compute Sj = Wij*Aij + w0j*bias
	 */
	public void calculateOutput(){
		double s = 0;
		for(Connection con : Inconnections){
			Neuron leftNeuron = con.getFromNeuron();
			double weight = con.getWeight();
			double a = leftNeuron.getOutput(); //output from previous layer
			
			s = s + (weight*a);
		}
		s = s + (biasConnection.getWeight()*bias);
		
		output = g(s);
	}
	
	
	double g(double x) {
		return sigmoid(x);
	}

	double sigmoid(double x) {
		return 1.0 / (1.0 +  (Math.exp(-x)));
	}
	
	/** Make new connections and add them to all connections
	  *
	  * @param inNeurons: ArrayList of neurons coming in to this current neuron
	  */
	public void addInConnectionsS(ArrayList<Neuron> inNeurons){
		for(Neuron n: inNeurons){
			Connection con = new Connection(n,this);
			Inconnections.add(con);
			connectionLookup.put(n.id, con);
		}
	}
	
	/** Return Connection for some neuron
	  *
	  * @param neuronIndex: Index of neuron in network to return connection
	  */
	public Connection getConnection(int neuronIndex){
		return connectionLookup.get(neuronIndex);
	}

	public void addInConnection(Connection con){
		Inconnections.add(con);
	}

	public void addBiasConnection(Neuron n){
		Connection con = new Connection(n,this);
		biasConnection = con;
		Inconnections.add(con);
	}

	public ArrayList<Connection> getAllInConnections(){
		return Inconnections;
	}
	
	public double getBias() {
		return bias;
	}
	public double getOutput() {
		return output;
	}
	public void setOutput(double o){
		output = o;
	}
}