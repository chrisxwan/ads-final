import csv

trainingFile = 'training.data'
datesFile = 'dates.data'
testingFile = 'testing.data'
salinityFile = 'salinity.data'

training = []
dates = []
testing = []
salinity = []
"""
with open(trainingFile) as f:
	training = f.readlines()

with open(datesFile) as f:
	dates = dates.readlines()

with open(testingFile) as f:
	testing = f.readlines()

with open(salinityFile) as f:
	salinity = f.readlines()
"""

training = [line.strip('\n') for line in open(trainingFile)]
dates = [line.strip('\n') for line in open(datesFile)]
testing = [line.strip('\n') for line in open(testingFile)]
salinity = [line.strip('\n') for line in open(salinityFile)]

counter = 0
trainingCSV = []
testingCSV = []

for entry in training:
	csvEntry = []
	csvEntry.append(dates[counter])
	csvEntry.append(float(entry))
	csvEntry.append(salinity[counter])
	trainingCSV.append(csvEntry)
	counter += 1

for entry in testing:
	csvEntry = []
	csvEntry.append(dates[counter])
	csvEntry.append(float(entry))
	csvEntry.append(salinity[counter])
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


