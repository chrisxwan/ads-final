import csv
import scipy.stats

def rsquared(x, y):
    """ Return R^2 where x and y are array-like."""

    slope, intercept, r_value, p_value, std_err = scipy.stats.linregress(x, y)
    return r_value**2

trainingFile = 'training.data'
testingFile = 'testing.data'
datesTrainFile = 'dates.train'
datesTestFile = 'dates.test'
salinityTrainFile = 'salinity.train'
salinityTestFile = 'salinity.test'

training = []
testing = []
datesTrain = []
datesTest = []
salinityTrain = []
salinityTest = []

training = [line.strip('\n') for line in open(trainingFile)]
testing = [line.strip('\n') for line in open(testingFile)]
datesTrain = [line.strip('\n') for line in open(datesTrainFile)]
datesTest = [line.strip('\n') for line in open(datesTestFile)]
salinityTrain = [line.strip('\n') for line in open(salinityTrainFile)]
salinityTest = [line.strip('\n') for line in open(salinityTestFile)]


counter = 0
trainingCSV = []
testingCSV = []

for entry in training:
	csvEntry = []
	csvEntry.append(datesTrain[counter])
	csvEntry.append(float(entry))
	csvEntry.append(salinityTrain[counter])
	trainingCSV.append(csvEntry)
	counter += 1

counter = 0

for entry in testing:
	csvEntry = []
	csvEntry.append(datesTest[counter])
	csvEntry.append(float(entry))
	csvEntry.append(salinityTest[counter])
	testingCSV.append(csvEntry)
	counter += 1

with open('training-results.csv', 'w') as out:
	csv_out = csv.writer(out)
	csv_out.writerow(['Date', 'Predicted', 'Observed'])
	for csvEntry in trainingCSV:
		csv_out.writerow(csvEntry)

with open('testing-results.csv', 'w') as out:
	csv_out = csv.writer(out)
	csv_out.writerow(['Date', 'Predicted', 'Observed'])
	for csvEntry in testingCSV:
		csv_out.writerow(csvEntry)


r2train = rsquared([float(elt) for elt in training], [float(elt) for elt in salinityTrain])
r2test = rsquared([float(elt) for elt in testing], [float(elt) for elt in salinityTest])
print "Training results stored in training-results.csv"
print "Training R^2 is " + str(r2train) + "\n"
print "Testing results stored in testing-results.csv"
print "Testing R^2 is " + str(r2test) + "\n"


