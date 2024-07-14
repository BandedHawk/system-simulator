# system-simulator
This is code that allows modeling a system as a connection of blocks characterised by delay functions. The current modeler can represent reasonably complex systems by allowing parallel connection of blocks, and variation of delay characteristics based on the event source. This allows a better understanding of throughput and performance in a system.

Typically, you would use the modeler to understand and identify theoretical inflection points in the real system when variation in the rates of incoming requests cause a rapid deterioration in the responsiveness of the system. This is preferred to generating sythetic loads on the system, particularly when it is difficult to achieve the required levels. The software is simpler than SciLab and Matlab to set up a model and run through what-if scenarios, and the modeling language is reasonably straight-forward.
## Building the software
The source is Java and there is a standard Maven build configuration for the code. I developed the software with NetBeans but should be able to be built using any platform that uses Maven.
## Running the software
You will need the Apache CLI 1.4 and Apache Math3 3.6.1 libraries to run the jar.
```
java -jar system-simulator-1.0-SNAPSHOT.jar -cp commons-math3-3.6.1.jar:commons-cli-1.4.jar [parameters]
```
Help on parameters will be generated if no parameters are supplied.

```
-g <integer>    timespan to generate samples in ticks
-s <integer>    time to start taking statistical sample in ticks
-e <integer>    time to stop taking statistical sample in ticks
-f <filename>   location of the file that contains the definition of the system model
```
## To do
* Capability to dump the raw simulation data for external analysis
* Graphical modeling and output?

## Specification language
Examples of the specification language for system components can be found in [src/test/data](https://github.com/BandedHawk/system-simulator/blob/master/src/test/data "Examples"). More documentation can be found in [src/main/doc](https://github.com/BandedHawk/system-simulator/blob/master/src/main/doc "Language Introduction")
