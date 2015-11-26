import csv

data = []
firstLine = 0
with open('training-data.csv') as f:
	cr = csv.reader(f.read().splitlines())
	for line in cr:
		if(firstLine == 1):
			data.append(line)
		firstLine = 1

dates = open('dates.data', 'w')
salinity = open('salinity.data', 'w')

tideTrain = open('tide.train', 'w')
rainfallTrain = open('rainfall.train', 'w')
flowTrain = open('flow.train', 'w')
flowMATrain = open('flowMA.train', 'w')
salinityTrain = open('salinity.train', 'w')

tideTest = open('tide.test', 'w')
flowTest = open('flow.test', 'w')
rainfallTest = open('rainfall.test', 'w')
flowMATest = open('flowMA.test', 'w')
flowQueue = []
flowSum = 0
movingAverage = 5

dateList = []
flowList = []
rainfallList = []
salinityList = []
tideList = []
allData = []

for line in data:
	currDate = line[0]
	currTide = line[1]
	currRainfall = line[2]
	currFlow = line[3]
	currSalinity = line[7]
	dateList.append(currDate)
	tideList.append(currTide)
	rainfallList.append(currRainfall)
	flowList.append(currFlow)
	salinityList.append(currSalinity)
	allData.append([currDate, currTide, currRainfall, currFlow, currSalinity])

trainingCounter = len(salinityList) / 2
counter = 0

for line in allData:
	dates.write(line[0] + '\n')
	salinity.write(line[4] + '\n')
	currMa = float(line[3])
	flowQueue = [currMa] + flowQueue
	flowSum += currMa
	if(len(flowQueue) > movingAverage):
		extract = flowQueue[-1:]
		flowQueue = flowQueue[:-1]
		flowSum -= extract[0]
		currMa = float(flowSum) / movingAverage
	if(counter < trainingCounter):
		tideTrain.write(line[1] + '\n')
		rainfallTrain.write(line[2] + '\n')
		flowTrain.write(line[3] + '\n')
		flowMATrain.write(str(currMa) + '\n')
		salinityTrain.write(line[4] + '\n')
	else:
		tideTest.write(line[1] + '\n')
		rainfallTest.write(line[2] + '\n')
		flowTest.write(line[3] + '\n')
		flowMATest.write(str(currMa) + '\n')
	counter += 1
