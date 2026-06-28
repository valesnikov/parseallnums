.PHONY: clean jar run

all: 
	mvn compile                                                            

clean:
	mvn clean

jar:
	mvn package

run:
	mvn exec:java -Dexec.mainClass="com.github.valesnikov.calculans.Main"
