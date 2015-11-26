import csv

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
	for csvEntry in trainingCSV:
		csv_out.writerow(csvEntry)

with open('testing-results.csv', 'w') as out:
	csv_out = csv.writer(out)
	for csvEntry in testingCSV:
		csv_out.writerow(csvEntry)


