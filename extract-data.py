import csv

data = []
firstLine = 0
with open('training-data.csv') as f:
	cr = csv.reader(f.read().splitlines())
	for line in cr:
		if(firstLine == 1):
			data.append(line)
		firstLine = 1

tide = open('tide', 'w')
rainfall = open('rainfall', 'w')
salinity = open('salinity', 'w')
flow = open('flow', 'w')
flowMA = open('flowMA', 'w')
flowQueue = []
flowSum = 0
movingAverage = 5

for line in data:
	currMa = float(line[3])
	flowQueue = [currMa] + flowQueue
	flowSum += currMa
	if(len(flowQueue) > movingAverage):
		extract = flowQueue[-1:]
		flowQueue = flowQueue[:-1]
		flowSum -= extract[0]
		currMa = float(flowSum) / movingAverage
	tide.write(line[1] + '\n')
	rainfall.write(line[2] + '\n')
	flow.write(line[3] + '\n')
	flowMA.write(str(currMa) + '\n')
	salinity.write(line[5] + '\n')






