all: data compile test

compile: network hindcast forecast

data: 
	python extract-data.py

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

