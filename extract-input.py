import csv

data = []
firstLine = 0
with open('training-data.csv') as f:
	cr = csv.reader(f.read().splitlines())
	for line in cr:
		if(firstLine == 1):
			data.append(line)
		firstLine = 1


tideTrain = open('tide.train', 'w')
rainfallTrain = open('rainfall.train', 'w')
flowTrain = open('flow.train', 'w')
flowMATrain = open('flowMA.train', 'w')
rainMATrain = open('rainMA.train', 'w')
salinityTrain = open('salinity.train', 'w')
datesTrain = open('dates.train', 'w')

tideTest = open('tide.test', 'w')
flowTest = open('flow.test', 'w')
rainfallTest = open('rainfall.test', 'w')
flowMATest = open('flowMA.test', 'w')
rainMATest = open('rainMA.test', 'w')
datesTest = open('dates.test', 'w')
salinityTest = open('salinity.test', 'w')
flowQueue = []
rainQueue = []
flowSum = 0
rainSum = 0
flowMovingAverage = 10
rainMovingAverage = 5

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
	currFlowMA = float(line[3])
	currRainMA = float(line[2])
	flowQueue = [currFlowMA] + flowQueue
	flowSum += currFlowMA
	rainQueue = [currRainMA] + rainQueue
	rainSum += currRainMA
	if(len(flowQueue) > flowMovingAverage):
		extract = flowQueue[-1:]
		flowQueue = flowQueue[:-1]
		flowSum -= extract[0]
		currFlowMA = float(flowSum) / flowMovingAverage
	if(len(rainQueue) > rainMovingAverage):
		extract = rainQueue[-1:]
		rainQueue = rainQueue[:-1]
		rainSum -= extract[0]
		currRainMA = float(rainSum) / rainMovingAverage
	if(counter > trainingCounter):
		tideTrain.write(line[1] + '\n')
		rainfallTrain.write(line[2] + '\n')
		flowTrain.write(line[3] + '\n')
		flowMATrain.write(str(currFlowMA) + '\n')
		rainMATrain.write(str(currRainMA) + '\n')
		salinityTrain.write(line[4] + '\n')
		datesTrain.write(line[0] + '\n')
	else:
		datesTest.write(line[0] + '\n')
		tideTest.write(line[1] + '\n')
		rainfallTest.write(line[2] + '\n')
		flowTest.write(line[3] + '\n')
		flowMATest.write(str(currFlowMA) + '\n')
		rainMATrain.write(str(currRainMA) + '\n')
		salinityTest.write(line[4] + '\n')
	counter += 1
