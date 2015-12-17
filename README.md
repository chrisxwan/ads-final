# Automated Decision Systems Final Project

### Christopher Wan

The St. Lucie River in South Florida is a naturally brackish river that sustains a very unique ecosystem. Data were obtained from the South Florida Water Management District on salinity levels in the St. Lucie River at a location known as the US-1 bridge. At this location, salinity levels below 10 Practical Salinity Units (psu) results in the death of organisms that are intolerant to freshwater. When the salinity levels do become too low, the South Florida Water Management District is able to decrease the amount of freshwater inflow coming in to the St. Lucie River from Lake Okeechobee and thus prevent the salinity levels from going any lower. It is important to note, though, that Lake Okeechobee does need to be drained periodically, especially during the "wet season," because high water levels in the lake are dangerous.

The objective of this study is to develop Artificial Neural Networks (ANNs) to predict estuarine salinity given freshwater inflows, rainfall, and tide using the St. Lucie River as a case study for the evaluation. ANNs is a programming logic model using gradient descent, multivariable calculus and an algorithmic “learning” process to simulate various functions related with information processing by imitating how neurons in the brain work. Input neurons (inputs), hidden neurons, and output neurons (outputs) are all connected by a series of weights which manipulate the input until arriving at an output value. The output is compared to an expected output, and based on the difference, the corresponding weights in the network are adjusted using "error backpropagation". More hidden neurons enable greater processing power and system flexibility. This additional flexibility comes at the cost of additional complexity in the training algorithm. Having too many hidden neurons makes the system over-specified and incapable of generalization. Having too few hidden neurons, conversely, can prevent the system from properly fitting the input data, and reduces the robustness of the system. The learning rate is a parameter that affects the speed at which the ANN arrives at the optimal weights by controlling the size of the weight change at each step of error backpropagation. If the learning rate is too high, the system will either oscillate about the true solution, or it will diverge completely. If the learning rate is too low, the system will take a long time to converge on the final solution. There is a momentum parameter that is used to prevent the system from converging to a local minimum or "saddle point" instead of finding the global minimum. The momentum parameter does this by adding a fraction m of the previous weight update to the current one. A high momentum parameter can also help to increase the speed of convergence of the system. However, setting the momentum parameter too high can create a risk of overshooting the minimum, which can cause the system to become unstable. A momentum coefficient that is too low cannot reliably avoid local minima, and also can slow the training of the system. The network continues to train its weights until a maximum number of runs OR a minimum error level is achieved.

####NOTE: Number of hidden neurons and the values of the learning rate and momentum depend dynamically on the process it is modeling.

I have included code (Connection.java and Neuron.java) to simulate the network. I have included comments in the code to describe functionality.

In the St. Lucie River, higher levels of freshwater inflow and higher levels of rainfall will lead to decreased salinity while higher levels of tide will result in increased salinity; however, the exact levels of salinity change are not always predictable, which makes ANNs an ideal tool for modeling and decision making. The neural network that I developed has 3 input neurons (10-day moving average flow (in cubic feet per second), 5-day moving average rainfall level (in millimeters), and tide level (in cubic feet)) and 1 output neuron (salinity level at US-1 bridge (in psu)). Based on various testing and comparisons, the optimum number of hidden neurons is 5, the optimum learning rate is 0.3, and the optimum momentum is 0.1.

I have included the data file ("training-data.csv") along with a Python script ("extract-input.py") to grab all of the training and testing data and to write them to files usable by the neural network. I obtained a master file of data from the South Florida Water Management District. The data contains measurements of flow, rainfall, tide, and measured salinity from 1/1/00 - 1/13/14. I split the data into two halves -- the second half contains the data to train the neural network, and the first half contains the data to test the performance of the trained neural network.

I have included the Java program for the neural network ("Network.java"), which **TRAINS** the networks with the specified inputs, outputs, learning rate, and momentum, and it writes the weights of the network to the file "weights.data". It then writes the predicted salinity results from training to the file "salinity.train"

I have included the Java program to **TEST** the network ("Validation.java"), which takes the weights in "weights.data" and the test inputs. It then writes the predicted salinity results from testing to the file "salinity.test"

I have included the Python script ("extract-output.py"). I have also included a nearly identical Python script ("extract-output-scipy.py") that does R^2 calculations. **However, this script requires SciPy. If SciPy is not on the machine, there will be an error. I have included an image of running this script in "scipy.png"** The script evaluates the accuracy of the training and testing. Although every run of the neural network will be different, I obtained a training R^2 value of 0.881 and a testing R^2 value of 0.827. I have included the figures "training-figures.png" and "testing-figures.png" that show how well the predicted salinity values match the observed salinity values. These values are encouraging, as they suggest that the neural network was able to learn the salinity process in the St. Lucie River Basin with success. The Python script also creates the files "training-results.csv" and "testing-results.csv" with data comparing the observed salinity and predicted salinity by the network for any given date within the training or testing periods.

Finally, I have included the Java program ("Forecast.java") to forecast salinity given the inputs of flow, rainfall, and tide. It outputs the predicted salinity in psu and gives a decision on whether to increase or decrease freshwater flows into the river. Although it is unable to explain why it made its decision with 100% certainty (since it is a "black box solution"), it will try to give at least some sort of explanation for why salinity values are too low (or too high). 

I have included the Makefile for this project. To run everything from beginning to end, simply run `make` and everything should be good! Be patient, it takes about 2 minutes for make to complete.

After running make, you can start forecasting salinity and making decisions by running `java Forecast`

####NOTE:  I had previous done work with Artificial Neural Networks in modeling salinity in the Loxahatchee River, a freshwater river in South Florida. This project is different because I made the entire process of training and testing the network more automated by using Python to extract the input and output data accordingly. Although I used ANNs to model to measure a similar process (salinity), I applied them to an entirely different location, the St. Lucie River. I used entirely new data (inputs and outputs), and I also modified and optimized the ANN Java code to be more succinct.

The link to the GitHub repository is [here](https://github.com/chrisxwan/ads-final)
