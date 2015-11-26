default: all

all: clean input compile train test output

compile: network hindcast forecast

input: 
	python extract-input.py

output:
	python extract-output.py

clean: 
	$(RM) *.data *.class *.train *.test

network:
	javac Network.java

hindcast:
	javac Hindcast.java

forecast:
	javac Forecast.java

train:
	java Network

test:
	java Hindcast

