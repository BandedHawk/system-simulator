# system-simulator
Code that allows simulation of a real system to determine flow of events and operational statistics on each component in the system. The current modeler can handle simple linear connections although there are plans to add a load dispersion component (load balancer). There are still some rough edges to be knocked out in the interpretation of elements in the specification language but currently it is good enough to model real systems with sensible values.

Typically, you would use the modeler to understand and detect theoretical inflection points in the real system when variation in the rates of incoming requests cause a rapid deterioration in the responsiveness of the system.
## Building the software
The source is Java and there is a standard maven build configuration for the code. A better explanation will be forthcoming.
## Running the software
You will need the Apache CLI 1.4 and Apache Math3 3.6.1 libraries to run the jar.
```Java
java -jar system-simulator-1.0-SNAPSHOT.jar -cp commons-math3-3.6.1.jar:commons-cli-1.4.jar
```
Help on parameters will be generated if no parameters are supplied.
## To do
* Solution for reporting on statistics for multiple sources
* Tracking component where event terminated

## Specification language
Examples of the specification language for system components can be found in [src/test/data](https://github.com/BandedHawk/system-simulator/blob/master/src/test/data "Examples"). More documentation can be found in [src/main/doc](https://github.com/BandedHawk/system-simulator/blob/master/src/main/doc "Language Introduction")
