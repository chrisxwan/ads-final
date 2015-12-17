/** 
  * Network.java
  * @author Christopher Wan
  *
  * Artificial neural network that learns the salinity
  * process for the St. Lucie River at the US-1 bridge.
  * 
  * 
  */
import java.text.*;
import java.util.*;
import java.io.*;

public class Network {
    final DecimalFormat df;
    final Random rand = new Random();
    final ArrayList<Neuron> inputLayer = new ArrayList<Neuron>();
    final ArrayList<Neuron> hiddenLayer = new ArrayList<Neuron>();
    final ArrayList<Neuron> outputLayer = new ArrayList<Neuron>();
    final Neuron bias = new Neuron();
    final int[] layers;
    final int randomWeightMultiplier = 1;

    final double epsilon = 0.000000001;
	
    final double learningRate = 0.3f; // optimum learning rate for process
    final double momentum = 0.8f;	  // optimum momentum for process

    // Inputs 
    final double inputs[][];

    // Corresponding outputs
    final double expectedOutputs[][];
    double resultOutputs[][]; 
    double output[];
    
	// weight the inputs and output with the equation:
	// (input - min) / (max - min)
    double flowMax, rainfallMax, tideMax, salinityMax;
    double flowMin, rainfallMin, tideMin, salinityMin;

    // for weight update all
    final HashMap<String, Double> weightUpdate = new HashMap<String, Double>();

    public static void main(String[] args) throws IOException {
        Network nn = new Network(3, 5, 1, "flowMA.train", "rainfall.train", "tide.train", "salinity.train");
        int maxRuns = 50000; // max # of runs for the neural network to train
        double minErrorCondition = 0.001; // stop training if this error is achieved
        nn.run(maxRuns, minErrorCondition);
    }

	/**
	  * Object to train the neural network.
	  * @param input: # of input neurons
	  * @param hidden: # of hidden neurons
	  * @param output: # of output neurons
	  * @param i1: String for name of file to read first input (flow)
	  * @param i2: String for name of file to read second input (rainfall)
	  * @param i3: String for name of file to read third input (tide)
	  * @param o1: String for name of file to read training output (salinity)
	  */

    public Network(int input, int hidden, int output, String i1, String i2, String i3, String o1) throws IOException {
        PrintWriter outFile = new PrintWriter(new FileWriter("maxmin.data"));
        
        ArrayList<Double> flow = new ArrayList<Double>();
        ArrayList<Double> rainfall = new ArrayList<Double>();
        ArrayList<Double> tide = new ArrayList<Double>();
        ArrayList<Double> salinity = new ArrayList<Double>();
        
		// Initialize Scanners to read in from appropriate files
        Scanner flowFile = new Scanner(new File(i1));
        Scanner rainfallFile = new Scanner(new File(i2));
        Scanner tideFile = new Scanner(new File(i3));
        Scanner salinityFile = new Scanner(new File(o1));
        
        int counter = 0;
		// Initialize all max and min values
        flowMax = Integer.MIN_VALUE;
        rainfallMax = Integer.MIN_VALUE;
        tideMax = Integer.MIN_VALUE;
        salinityMax = Integer.MIN_VALUE;
        flowMin = Integer.MAX_VALUE;
        rainfallMin = Integer.MAX_VALUE;
        tideMin = Integer.MAX_VALUE;
        salinityMin = Integer.MAX_VALUE;

		// Find all max and min of data
        while(flowFile.hasNext())
            flow.add(flowFile.nextDouble());
        while(rainfallFile.hasNext())
            rainfall.add(rainfallFile.nextDouble());
        while(tideFile.hasNext())
            tide.add(tideFile.nextDouble());
        while(salinityFile.hasNext())
        {
            salinity.add(salinityFile.nextDouble());        
            counter++;
        }
        for(int x = 0; x < flow.size(); x++)
        {
            if(flow.get(x) > flowMax)
                flowMax = flow.get(x);

            if(rainfall.get(x) > rainfallMax)
                rainfallMax = rainfall.get(x);

            if(tide.get(x) > tideMax)
                tideMax = tide.get(x);

            if(salinity.get(x) > salinityMax)
                salinityMax = salinity.get(x);

            if(flow.get(x) < flowMin)
                flowMin = flow.get(x);

            if(rainfall.get(x) < rainfallMin)
                rainfallMin = rainfall.get(x);

            if(tide.get(x) < tideMin)
                tideMin = tide.get(x);

            if(salinity.get(x) < salinityMin)
                salinityMin = salinity.get(x);
        }

        inputs = new double [counter][3];
        expectedOutputs = new double[counter][1];
        resultOutputs = new double[counter][1];
        
        
        // Weight the data using the corresponding max and min
        for(int x = 0; x < counter; x++)
        {
            inputs[x][0] = (flow.get(x) - flowMin) / (flowMax - flowMin);
            inputs[x][1] = (rainfall.get(x) - rainfallMin) / (rainfallMax - rainfallMin);
            inputs[x][2] = (tide.get(x) - tideMin) / (tideMax - tideMin);
            expectedOutputs[x][0] = (salinity.get(x) - salinityMin) / (salinityMax - salinityMin);
            resultOutputs[x][0] = -1.0;
        }
                     
        this.layers = new int[] { input, hidden, output };
        df = new DecimalFormat("#.0#");

        /**
         * Create all neurons and connections.
		 * Connections are created in the neuron class.
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

        // initialize random weights
        for (Neuron neuron : hiddenLayer) {
            ArrayList<Connection> connections = neuron.getAllInConnections();
            for (Connection conn : connections) {
                double newWeight = getRandom();
                conn.setWeight(newWeight);
            }
        }
        for (Neuron neuron : outputLayer) {
            ArrayList<Connection> connections = neuron.getAllInConnections();
            for (Connection conn : connections) {
                double newWeight = getRandom();
                conn.setWeight(newWeight);
            }
        }
        

		/** Write all of the max and min to a separate file so they can be used by
		  * Validation.java and Forecast.java
		  */

        outFile.println(flowMax);
        outFile.println(rainfallMax);
        outFile.println(tideMax);
        outFile.println(salinityMax);
        outFile.println(flowMin);
        outFile.println(rainfallMin);
        outFile.println(tideMin);
        outFile.println(salinityMin);
        outFile.close();
        
        // reset id counters
        Neuron.counter = 0;
        Connection.counter = 0;

    }

    // random
    double getRandom() {
        return randomWeightMultiplier * (rand.nextDouble() * 2 - 1);
    }

    /** Set all the inputs to the neural network
     * 
     * @param inputs: array of inputs for a particular neuron
     */
    public void setInput(double inputs[]) {
        for (int i = 0; i < inputLayer.size(); i++) {
            inputLayer.get(i).setOutput(inputs[i]);
        }
    }

	/** Get the outputs for a run of the neural network */
    public double[] getOutput() {
        double[] outputs = new double[outputLayer.size()];
        for (int i = 0; i < outputLayer.size(); i++)
            outputs[i] = outputLayer.get(i).getOutput();
        return outputs;
    }

    /**
     * Calculate the output of the neural network based on the input
     */
    public void activate() {
        for (Neuron n : hiddenLayer)
            n.calculateOutput();
        for (Neuron n : outputLayer)
            n.calculateOutput();
    }

    /**
     * all output propagate back
     * 
     * @param expectedOutput: first calculate the partial derivative of the error with
     * respect to each of the weight leading into the output neurons bias is also updated here
     */
    public void applyBackpropagation(double expectedOutput[]) {

        // error check, normalize value ]0;1[
        for (int i = 0; i < expectedOutput.length; i++) {
            double d = expectedOutput[i];
            if (d < 0 || d > 1) {
                if (d < 0)
                    expectedOutput[i] = 0 + epsilon;
                else
                    expectedOutput[i] = 1 - epsilon;
            }
        }

        int i = 0;
        for (Neuron n : outputLayer) {
            ArrayList<Connection> connections = n.getAllInConnections();
            for (Connection con : connections) {
                double ak = n.getOutput();
                double ai = con.leftNeuron.getOutput();
                double desiredOutput = expectedOutput[i];

                double partialDerivative = -ak * (1 - ak) * ai
                        * (desiredOutput - ak);
                double deltaWeight = -learningRate * partialDerivative;
                double newWeight = con.getWeight() + deltaWeight;
                con.setDeltaWeight(deltaWeight);
                con.setWeight(newWeight + momentum * con.getPrevDeltaWeight());
            }
            i++;
        }

        // update weights for the hidden layer
        for (Neuron n : hiddenLayer) {
            ArrayList<Connection> connections = n.getAllInConnections();
            for (Connection con : connections) {
                double aj = n.getOutput();
                double ai = con.leftNeuron.getOutput();
                double sumKoutputs = 0;
                int j = 0;
                for (Neuron out_neu : outputLayer) {
                    double wjk = out_neu.getConnection(n.id).getWeight();
                    double desiredOutput = (double) expectedOutput[j];
                    double ak = out_neu.getOutput();
                    j++;
                    sumKoutputs = sumKoutputs
                            + (-(desiredOutput - ak) * ak * (1 - ak) * wjk);
                }

                double partialDerivative = aj * (1 - aj) * ai * sumKoutputs;
                double deltaWeight = -learningRate * partialDerivative;
                double newWeight = con.getWeight() + deltaWeight;
                con.setDeltaWeight(deltaWeight);
                con.setWeight(newWeight + momentum * con.getPrevDeltaWeight());
            }
        }
    }

	/** Train the neural network and write the results to files.
	  * @param maxSteps: max number of trials for the neural network to run
	  * @param inError: minimum error such that once achieved, the network stops running
	  */
    void run(int maxSteps, double minError) throws IOException {
        int i;
        int counter = 0;
        // Train neural network until minError reached or maxSteps exceeded
        double error = 1;
        double globalError = 0;
        for (i = 0; i < maxSteps && error > minError; i++) {
            error = 0;
            for (int p = 0; p < inputs.length; p++) {
                setInput(inputs[p]);

                activate();

                output = getOutput();
                resultOutputs[p] = output;
                
                if(i == maxSteps - 1) {
                    for (int j = 0; j < expectedOutputs[p].length; j++) {
                        double err = (Math.pow(output[j] - expectedOutputs[p][j], 2));
                        globalError += err;
                        counter++;
                    }
                }
                for (int j = 0; j < expectedOutputs[p].length; j++) {
                    double err = (Math.pow(output[j] - expectedOutputs[p][j], 2));
                    error += err;
                }

                applyBackpropagation(expectedOutputs[p]);
            }
        }
        
        globalError /= counter;
        globalError = Math.sqrt(globalError);
        printResult("training.data");
        System.out.println();
        printAllWeights("weights.data");
    }


    /** Print the predicted salinity results to a a file
	  * @param fileName: String of file name to write to
	  */
    void printResult(String fileName) throws IOException {
		PrintWriter outFile = new PrintWriter(new FileWriter(fileName));

        System.out.println("Training Results written to file: " + fileName);
        for (int p = 0; p < inputs.length; p++) {
            for (int x = 0; x < layers[2]; x++)
                outFile.print((resultOutputs[p][x] * (salinityMax - salinityMin) + salinityMin));
            outFile.println();
        }
		outFile.close();
    }
    
	/** Print all of the weights from the trained neural network to a file
	  * @param fileName: String of file name to write to
	  */
    public void printAllWeights(String fileName) throws IOException {
		System.out.println("Weights written to file: " + fileName);
        PrintWriter outFile = new PrintWriter(new FileWriter(fileName));        
        // weights for the hidden layer
        for (Neuron n : hiddenLayer) {
            ArrayList<Connection> connections = n.getAllInConnections();
            for (Connection con : connections) {
                double w = con.getWeight();
                outFile.println(w);
            }
        }
        // weights for the output layer
        for (Neuron n : outputLayer) {
            ArrayList<Connection> connections = n.getAllInConnections();
            for (Connection con : connections) {
                double w = con.getWeight();
                outFile.println(w);
            }
        }
        System.out.println();
        outFile.close();
    }
}