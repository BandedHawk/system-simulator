## Exercising the system for capability
Simulation systems are always difficult to validate so you need to think of some difficult problems to solve for which you know the answers. The challenge I attempted was to simulate a system that randomly distributes an incoming event to 1 of 3 processing blocks. One of those blocks will terminate the event after processing. The other blocks will return the event back to the distributor for another attempt.

###  Reference model
The model for this system is shown here, with the assumption that the processors have infinite capacity so that events are not waiting to be processed.
![alt text](https://github.com/BandedHawk/system-simulator/blob/master/src/main/doc/images/fun-throughput-simulation.png "Example model")

### Theoretical throughput characteristics
For the implementation of this problem as a simulation model, we will make this more complex by having three event generators in parallel, and using the processor source operation to differentiate between termination and return to the start. To reduce the possibility of waiting queues, we will implement each block as a set of parallel processors with a smart balancer in the front. Each processor will take a constant 1 tick processing time - this equates then to the number of runs before the event leaves the system, making our comparison to theoreticals easy. The simulation model is described in the file [fun.example.txt](https://github.com/BandedHawk/system-simulator/blob/master/src/test/data/fun.example.txt).

In terms of the problem itself, the theoretical calculations are straightfoward. On the first loop, an event has 1 in 3 chance of exiting. It will have 2 in 3 chance of not exiting on the first pass and then 1 in 3 chance of exiting on the second. The following shows the supporting probability model.

![alt text](https://github.com/BandedHawk/system-simulator/blob/master/src/main/doc/images/probability-simulation.png "Probability calculations")

The probability of exiting after 1 run is 1/3 or 33%. The cumulative probability for exiting in 1 run and 2 runs is 5/9 or approximately 56%. Based on this, we can state that the median number of runs will be 2. The probability distribution is skewed-right since we have a long-tail. In theory, an event might never exit the system although the probability of an event still being in the system after 60 trials is extremely small. The simulation will give an idea of the practical limit.

Since this is a geometric distribution, the expected number of runs is also straightforward. Given E(X) is the expected number of runs before success and this is memoryless, we can say that the expectation after 1 trial is the probability of success and the probability of failure multiplied by the expectation plus 1 trial as below, and solving:

![alt text](https://github.com/BandedHawk/system-simulator/blob/master/src/main/doc/images/expectation-probability.png "Expectation calculation")

The average lifetime for an event should then tend to 3 runs for our simulation, given that p is 1/3.
### Simulation results
With the constructed model, the simulation gives us this:
```
Statistics for events that occurred between 500.0 and 299500.0
General event information
  Events completed processing: 179399
  Throughput: 0.6000066890750682 events per tick
          Or: 1.666648086109733 ticks between events
  Ratio of processing in lifetime: 99.99944331561233 %
  Event lifetime
    Mean: 3.0039465102926983 ticks
    Standard Deviation: 2.451049443244448
    Median: 2.0 ticks
    Maximum time in system: 30.0 ticks
    Minimum time in system: 1.0 ticks
  Event in execution
    Mean: 3.0039297877914612 ticks
    Standard Deviation: 2.450991477104942
    Median: 2.0 ticks
    Maximum time processing: 30.0 ticks
    Minimum time processing: 1.0 ticks
    'source 3' event information
      Events completed processing: 59800
      Throughput: 0.20000334453753407 events per tick
              Or: 4.999916387959866 ticks between events
      Ratio of processing in lifetime: 99.9994434116638 %
      Event lifetime
        Mean: 3.0044481605351225 ticks
        Standard Deviation: 2.4394156310551662
        Median: 2.0 ticks
        Maximum time in system: 24.0 ticks
        Minimum time in system: 1.0 ticks
      Event in execution
        Mean: 3.004431438127094 ticks
        Standard Deviation: 2.439350536200217
        Median: 2.0 ticks
        Maximum time processing: 24.0 ticks
        Minimum time processing: 1.0 ticks
    'source 1' event information
      Events completed processing: 59800
      Throughput: 0.20000334453753407 events per tick
              Or: 4.999916387959866 ticks between events
      Ratio of processing in lifetime: 100.0 %
      Event lifetime
        Mean: 3.0057190635451536 ticks
        Standard Deviation: 2.4526623976151396
        Median: 2.0 ticks
        Maximum time in system: 27.0 ticks
        Minimum time in system: 1.0 ticks
      Event in execution
        Mean: 3.0057190635451536 ticks
        Standard Deviation: 2.4526623976151396
        Median: 2.0 ticks
        Maximum time processing: 27.0 ticks
        Minimum time processing: 1.0 ticks
    'source 2' event information
      Events completed processing: 59799
      Throughput: 0.20000334459346467 events per tick
              Or: 4.999916386561648 ticks between events
      Ratio of processing in lifetime: 99.9988857752498 %
      Event lifetime
        Mean: 3.0016722687670345 ticks
        Standard Deviation: 2.461061122044318
        Median: 2.0 ticks
        Maximum time in system: 30.0 ticks
        Minimum time in system: 1.0 ticks
      Event in execution
        Mean: 3.001638823391696 ticks
        Standard Deviation: 2.460952421772226
        Median: 2.0 ticks
        Maximum time processing: 30.0 ticks
        Minimum time processing: 1.0 ticks

```
The outcomes are in line with our theoreticals. Median is at 2 runs for all our sources, and therefore overall. The average lifetime of events is tending towards 3, which is equavalent to 3 runs. Additionally, the simulation shows that for practical purposes the maximum number of runs before exit would be around 25 to 30 - this value could be higher but it is difficult to achieve in simulation with limited events generated.

With all this, we validate our simulation system. We've also shown that the balancers are working as expected, particularly as there is very little wait time involved in the event lifetimes as observed by the processing ratio.