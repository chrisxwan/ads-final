/**
  * This class imports the weights learned from the Network class and builds 
  * the Artificial Neural Network corresponding to those weights. It allows users to
  * forecast the salinity at RM6 given input prompts.
  * 
  * @author Christopher Wan 
  */
import java.text.*;
import java.util.*;
import java.io.*;
public class Forecast
{
    final ArrayList<Neuron> inputLayer = new ArrayList<Neuron>();
    final ArrayList<Neuron> hiddenLayer = new ArrayList<Neuron>();
    final ArrayList<Neuron> outputLayer = new ArrayList<Neuron>();
    final Neuron bias = new Neuron();
    final int[] layers;
    final DecimalFormat df;
    final double inputs[][];
    double[][] resultOutputs;
    double[] output;
    
    double flowMax, rainfallMax, tideMax, salinityMax;
    double flowMin, rainfallMin, tideMin, salinityMin;
    
    public static void main(String[] args) throws IOException {
        Scanner in = new Scanner(System.in);
        System.out.println("What is the 10-day moving average flow in cubic feet per second? ");
        double q = in.nextDouble();
        System.out.println("What is the 5-day moving average rainfall level in millimeters?");
        double r = in.nextDouble();
        System.out.println("What is the tide level in cubic feet?");
        double t = in.nextDouble();
        Forecast forecast = new Forecast(3, 5, 1, q, r, t, "weights.data", "maxmin.data");
        forecast.run();
    }
    
	/** 
	  * Object to forecast salinity given the appropriate inputs
	  * @param input: # of input neurons
	  * @param hidden: # of hidden neurons
	  * @param output: # of output neurons
	  * @param q: 10-day moving average for flow
	  * @param r: 5-day moving average for rainfall
	  * @param t: tide levels
	  * @param weight: String for name of file to read weights
	  * @param maxminStr: String for name of file to read mins and maxes of data
	  */
    public Forecast(int input, int hidden, int output, double q, double r, double t, String weight, String maxminStr) throws IOException {
        int marker = 0;
        
        ArrayList<Double> weights = new ArrayList<Double>();
        ArrayList<Double> maxmin = new ArrayList<Double>();
        
        Scanner weightsFile = new Scanner(new File(weight));
        Scanner maxminFile = new Scanner(new File(maxminStr));
        

        while(weightsFile.hasNext())
            weights.add(weightsFile.nextDouble());
        while(maxminFile.hasNext())
            maxmin.add(maxminFile.nextDouble());
        
        flowMax = maxmin.get(0);
        rainfallMax = maxmin.get(1);
        tideMax = maxmin.get(2);
        salinityMax = maxmin.get(3);
        flowMin = maxmin.get(4);
        rainfallMin = maxmin.get(5);
        tideMin = maxmin.get(6);
        salinityMin = maxmin.get(7);
        
        inputs = new double [1][3];
        resultOutputs = new double[1][1];
		 
		// weight the input data appropriately
        inputs[0][0] = (q - flowMin) / (flowMax - flowMin);
        inputs[0][1] = (r - rainfallMin) / (rainfallMax - rainfallMin);
        inputs[0][2] = (t - tideMin) / (tideMax - tideMin);
        
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

    public void setInput(double inputs[]) {
        for(int i = 0; i < inputLayer.size(); i++)
            inputLayer.get(i).setOutput(inputs[i]);
    }
    
    public double[] getOutput() {
        double[] outputs = new double[outputLayer.size()];
        for(int i = 0; i < outputLayer.size(); i++)
            outputs[i] = outputLayer.get(i).getOutput();
        return outputs;
    }
    
    public void activate() {
        for(Neuron n : hiddenLayer)
            n.calculateOutput();
        for(Neuron n : outputLayer)
            n.calculateOutput();
    }
    
    public void run() {
        for(int p = 0; p < inputs.length; p++) {
            setInput(inputs[p]);
            activate();
            
            output = getOutput();
            resultOutputs[p] = output;
        }
        printResult();
    }
    
	/**
	  * Print the results of the forecast.
	  *
	  * If the salinity is too low (less than 10 psu), provide plausible explanations
	  * If the salinity is too high (higher than 10 psu), provide plausible explanation
	  */
    public void printResult() {
        System.out.println("Predicted Salinity Result:");
		double salinity = 0.0;
        for(int p = 0; p < inputs.length; p++) {               
            for(int x = 0; x < layers[2]; x++) {
				// weight the salinity back to normal
				salinity = resultOutputs[p][x] * (salinityMax-salinityMin) + salinityMin;
                System.out.print(salinity + " psu ");
            }
            System.out.println();
        }
		if(salinity < 10) {
			System.out.println("The salinity is too low, which will result in the death of organisms that are intolerant to freshwater. This may be caused by an excess of freshwater inflows or rainfall, or unusually low tide levels. To increase salinity, less water should be discharged from Lake Okeechobee.");
		} else if(salinity > 10 && salinity < 30) {
			System.out.println("The salinity is at optimum levels to sustain the ecosystem.");
		} else {
			System.out.println("The salinity is too high, which will result in the death of organisms that cannot sustain life in salinity levels that are beyond that of brackish water. This may be caused by a lack of freshwater inflows or rainfall, or unusually high tide levels. To decrease salinity, more water should be discharged from Lake Okeechobee.");
		}
    }
        
}
