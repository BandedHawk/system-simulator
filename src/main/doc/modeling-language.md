### Introduction to the modeling language
A system model can be built using connected combinations of 3 types of components. These components are:
* the source
* the processor
* the balancer

Events flow through the system, interacting with processors and balancers. A source generates events.

Sources and processors can be described by functions called generators. Sources create events separated in time described by the characteristics of the generator. Similarly, processors delay events as described by the generator.

The balancer provides a mechanism to switch an event to a downstream component from a list of downstream components. The selection of the downstream component is based on the strategy of the function defined for the balancer. A balancer has no delay component in its interaction with an event.

System connectivity is defined within functions, and not by the components. This allows a certain amount of flexibility as we will see later.
#### General language syntax
Given the above, the language has a consistent form for definition - a component, within which is nested a function or functions that describe the component and connectivity to downstream components. The language is case-sensitive. All values are defined in name-value pairs. The name-value pairs do not have to be given in any particular order. Components must always define a <i>name</i> and a <i>type</i>. Everything else is optional. Functions must always define a <i>type</i>. The type of function will dictate legal and mandatory values. Connectivity is defined by the <i>next</i> name-value pair. Double forward slashes define the start of a comment that is ignored by the model compiler. An example of the form is:
```javascript
// Start of a component definition
component
{
    type: source
    name: source
    function
    {
        type: constant
        period: 0.8
        next: network
    }
    monitor: Yes
}
```
### Generator functions
#### Constant
As the functional definition for a source, this will generate events a constant interval apart, as set by the value of <i>period</i>. Otherwise, for a processor, this represents a fixed delay of <i>period</i>. The definition is:
```javascript
function
{
    type: constant
    period: 0.8
    next: network
}
```
Everything except <i>next</i> is mandatory.
#### Uniform
This produces a uniformally random distribution of values between <i>minimum</i> and <i>maximum</i>. For a source, events are separated by the generated value. For processors, the delay is the generated value.
```javascript
function
{
    type: uniform
    minimum: 5
    maximum: 6
    next: processor 2
}
```
#### Gaussian
The Gaussian function generates values that are normally distributed between <i>minimum</i> and <i>maximum</i>. For sources, this represents the event separation function and for processors, this is the delay caused by the component.
```javascript
function
{
    type: gaussian
    minimum: 5
    maximum: 6
    next: processor 1
}
```
Everything except <i>next</i> is mandatory.
#### Skewed
This generates values within a certain range but skewed to the left or right, depending on the <i>bias</i> value. This is useful in depicting real-world systems where the response or delay is normally at the lower end, but may occasionally go higher - a right-skewed generator.

Mathematically, the function is:
$$
\frac{maximum + minimum}{2}
$$

Additional information on the skewing calculation can be found <a href="https://stackoverflow.com/questions/5853187/skewing-java-random-number-generation-toward-a-certain-number">here</a>.
```javascript
function
{
    type: skewed
    minimum: 5.0
    maximum: 6.0
    skew: 0.8
    bias: -3
    next: database
}
```
Everything except <i>next</i> is mandatory.
#### Special cases
For processor functions, there are additional features