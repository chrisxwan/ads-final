import csv

trainingFile = 'training.data'
datesFile = 'dates.data'
testingFile = 'testing.data'
salinityFile = 'salinity.data'

training = []
dates = []
testing = []
salinity = []
with open(trainingFile) as f:
	training = f.readlines()

with open(dates) as f:
	dates = dates.readlines()

with open(testingFile) as f:
	training = f.readlines()

with open(salinityFile) as f:
	training = f.readlines()

training = [line.strip('\n') for line in training]
dates = [line.strip('\n') for line in dates]


