# system-simulator
Code that allows simulation of a real system to determine flow of events and operational statistics on each component in the system. The current modeler can handle simple linear connections although there are plans to add a load dispersion component (load balancer). There are still some rough edges to be knocked out in the interpretation of elements in the specification language but currently it is good enough to model real systems with sensible values.

Examples of the specification language for system components can be found in src/test/data.

Typically, you would use the modeler to understand and detect theoretical inflection points in the real system when variation in the rates of incoming requests cause a rapid deterioration in the responsiveness of the system.

Upcoming changes to modeler:
Addition of a load balancer component to spread events between downstream components
Better handling of numerical values in system specification file
