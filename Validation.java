/** 
  * Validation.java
  * @author Christopher Wan
  *
  * This class imports the weights learned from the Network class and builds the
  * Artificial Neural Network corresponding to those weights.
  *
  * The resulting network can be used to test the training results from Network.java
  * The neural network predicts salinity for the St. Lucie River at the US-1 bridge.
  * 
  */
import java.text.*;
import java.util.*;
import java.io.*;
public class Validation {
    final ArrayList<Neuron> inputLayer = new ArrayList<Neuron>();
    final ArrayList<Neuron> hiddenLayer = new ArrayList<Neuron>();
    final ArrayList<Neuron> outputLayer = new ArrayList<Neuron>();
    final Neuron bias = new Neuron();
    final int[] layers;
    final DecimalFormat df;
	
	// inputs
    final double inputs[][];

	// corresponding outputs
    double[][] resultOutputs;
    double[] output;
    
    double flowMax, rainfallMax, tideMax, salinityMax;
    double flowMin, rainfallMin, tideMin, salinityMin;
    
    public static void main(String[] args) throws IOException {
        Validation validation = new Validation(3, 5, 1, "flowMA.test", "rainfall.test", "tide.test", "weights.data", "maxmin.data");
        validation.run();
    }
   
    /** 
	  * Object to test the already-trained neural network.
	  * @param input: # of input neurons
	  * @param hidden: # of hidden neurons
	  * @param output: # of output neurons
	  * @param i1: String for name of file to read first input (flow)
	  * @param i2: String for name of file to read second input (rainfall)
	  * @param i3: String for name of file to read third input (tide)
	  * @param weight: String for name of file to read weights
	  * @param maxminStr: String for name of file to read mins and maxes of data
	  */
    public Validation(int input, int hidden, int output, String i1, String i2, String i3, String weight, String maxminStr) throws IOException {
        int marker = 0;
        
        ArrayList<Double> flow = new ArrayList<Double>();
        ArrayList<Double> rainfall = new ArrayList<Double>();
        ArrayList<Double> tide = new ArrayList<Double>();
        ArrayList<Double> weights = new ArrayList<Double>();
        ArrayList<Double> maxmin = new ArrayList<Double>();

		// Initialize Scanners to read from appropriate files
        Scanner flowFile = new Scanner(new File(i1));
        Scanner rainfallFile = new Scanner(new File(i2));
        Scanner tideFile = new Scanner(new File(i3));
        Scanner weightsFile = new Scanner(new File(weight));
        Scanner maxminFile = new Scanner(new File(maxminStr));
        
        int counter = 0;
		
		// Read from files
        while(flowFile.hasNext()) {
            flow.add(flowFile.nextDouble());
            counter++;
        }
        while(rainfallFile.hasNext())
            rainfall.add(rainfallFile.nextDouble());
        while(tideFile.hasNext())
            tide.add(tideFile.nextDouble());
        while(weightsFile.hasNext())
            weights.add(weightsFile.nextDouble());
        while(maxminFile.hasNext())
            maxmin.add(maxminFile.nextDouble());
        
		// Initialize all max and mins
        flowMax = maxmin.get(0);
        rainfallMax = maxmin.get(1);
        tideMax = maxmin.get(2);
        salinityMax = maxmin.get(3);
        flowMin = maxmin.get(4);
        rainfallMin = maxmin.get(5);
        tideMin = maxmin.get(6);
        salinityMin = maxmin.get(7);
        
        inputs = new double [counter][3];
        resultOutputs = new double[counter][1];
                
        for(int x = 0; x < counter; x++) {
            inputs[x][0] = (flow.get(x) - flowMin) / (flowMax - flowMin);
            inputs[x][1] = (rainfall.get(x) - rainfallMin) / (rainfallMax - rainfallMin);
            inputs[x][2] = (tide.get(x) - tideMin) / (tideMax - tideMin);
        }
        
        
        this.layers = new int[] { input, hidden, output };
        df = new DecimalFormat("#.0#");

        /**
         * Create all neurons and connections Connections are created in the
         * neuron class
         */
        for (int i = 0; i < layers.length; i++) {
            if (i == 0) { // input layer
                for (int j = 0; j < layers[i]; j++) {
                    Neuron neuron = new Neuron();
                    inputLayer.add(neuron);
                }
            } else if (i == 1) { // hidden layer
                for (int j = 0; j < layers[i]; j++) {
                    Neuron neuron = new Neuron();
                    neuron.addInConnectionsS(inputLayer);
                    neuron.addBiasConnection(bias);
                    hiddenLayer.add(neuron);
                }
            }
            else if (i == 2) { // output layer
                for (int j = 0; j < layers[i]; j++) {
                    Neuron neuron = new Neuron();
                    neuron.addInConnectionsS(hiddenLayer);
                    neuron.addBiasConnection(bias);
                    outputLayer.add(neuron);
                }
            } else {
                System.out.println("!Error NeuralNetwork init");
            }
        }

        for(Neuron neuron : hiddenLayer) {
            ArrayList<Connection> connections = neuron.getAllInConnections();
            for(Connection conn : connections) {
                double newWeight = weights.get(marker);
                conn.setWeight(newWeight);
                marker++;
            }            
        }
        
        for (Neuron neuron : outputLayer) {
            ArrayList<Connection> connections = neuron.getAllInConnections();
            for (Connection conn : connections) {
                double newWeight = weights.get(marker);
                conn.setWeight(newWeight);
                marker++;
            }
        }

        // reset id counters
        Neuron.counter = 0;
        Connection.counter = 0;
    
    }

	/** Set all the inputs to the neural network
     * 
     * @param inputs: array of inputs for a particular neuron
     */
    public void setInput(double inputs[]) {
        for(int i = 0; i < inputLayer.size(); i++)
            inputLayer.get(i).setOutput(inputs[i]);
    }
    
	/** 
	  * Get the outputs for a run of the neural network
	  */
    public double[] getOutput() {
        double[] outputs = new double[outputLayer.size()];
        for(int i = 0; i < outputLayer.size(); i++)
            outputs[i] = outputLayer.get(i).getOutput();
        return outputs;
    }
    

	/**
     * Calculate the output of the neural network based on the input
     */
    public void activate() {
        for(Neuron n : hiddenLayer)
            n.calculateOutput();
        for(Neuron n : outputLayer)
            n.calculateOutput();
    }
   
   	/** Run the neural network with the following steps:
	  * set the inputs, activate, get the outputs, print the results to a file
	  */
    public void run() throws IOException {
        for(int p = 0; p < inputs.length; p++) {
            setInput(inputs[p]);
            activate();
            
            output = getOutput();
            resultOutputs[p] = output;
        }
        printResult("testing.data");
    }
    
	/** Print the predicted salinity results to a a file
	  * @param fileName: String of file name to write to
	  */
    public void printResult(String fileName) throws IOException {
		PrintWriter outFile = new PrintWriter(new FileWriter(fileName));
        System.out.println("Testing Results stored in file " + fileName + "\n");
        for(int p = 0; p < inputs.length; p++) {   
            for(int x = 0; x < layers[2]; x++)
                outFile.print((resultOutputs[p][x] * (salinityMax - salinityMin) + salinityMin));
			outFile.println();
        }
		outFile.close();
    }
}
