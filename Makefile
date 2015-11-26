all: data compile test

compile: network hindcast forecast

input: 
	python extract-input.py

output:
	python extract-output.py

clean: 
	$(RM) *.data *.class

network:
	javac Network.java

hindcast:
	javac Hindcast.java

forecast:
	javac Forecast.java

test:
	java Network

