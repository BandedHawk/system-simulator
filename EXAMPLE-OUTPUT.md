## Reference model
![alt text](https://raw.githubusercontent.com/BandedHawk/system-simulator/master/complex-system.png "System Model for Simulation")

## Example output from a simulation
```
Statistics for events that occurred between 500.0 and 95500.0
Event information
  Events completed processing: 94941
  Throughput: 0.9993955847663716
  Ratio of processing in lifetime: 0.9028836730647731
  Event lifetime
    Mean:3.0286837262286035
    Standard Deviation:0.4480269854084294
    Maximum time in system: 7.10068038218742
    Minimum time in system: 2.2724561553040985
  Event in execution
    Mean:2.7345490872887854
    Standard Deviation:0.25144443456499505
    Maximum time processing: 4.241311850158183
    Minimum time processing: 2.2709584868226544
Component: database
  function: [default := Skewed - 0.5:1.5:0.8:-3.0]
  Events processed: 94944
  Utilization: 0.5813601395222406
  Throughput: 0.9994164098446349
  Queued events
    Mean:0.292505979005932
    Standard deviation:0.45506966212553634
    Maximum: 2.0
    Minimum: 0.0
  Wait time
    Mean:0.08737532303285561
    Standard deviation:0.16894798787263135
    Maximum: 1.2659998135786736
    Minimum: 0.0
  Process time
    Mean:0.5817013786328241
    Standard Deviation:0.0969216730763705
    Maximum: 1.433346263896965
    Minimum: 0.5001910920254886
  Visit time
    Mean:0.6690767016656798
    Standard Deviation:0.19472910407784783
    Maximum: 2.3675356975581963
    Minimum: 0.5001910920254886
  Arrival characteristics
    Mean:1.0005875432120819
    Standard Deviation:0.5823362228279145
    Maximum: 2.8886232896165893
    Minimum: 1.5967329090926796E-5
Component: network 2
  function: [default := Skewed - 0.25:0.75:0.8:-2.0]
  Events processed: 47501
  Utilization: 0.1679736205943148
  Throughput: 0.5000298389881427
  Queued events
    Mean:0.0
    Standard deviation:0.0
    Maximum: 0.0
    Minimum: 0.0
  Wait time
    Mean:0.0
    Standard deviation:0.0
    Maximum: 0.0
    Minimum: 0.0
  Process time
    Mean:0.3359310904798441
    Standard Deviation:0.0796181589657758
    Maximum: 0.736373901803745
    Minimum: 0.25026897151838057
  Visit time
    Mean:0.3359310904798441
    Standard Deviation:0.0796181589657758
    Maximum: 0.736373901803745
    Minimum: 0.25026897151838057
  Arrival characteristics
    Mean:1.9999224495729122
    Standard Deviation:0.2883707892495052
    Maximum: 2.4999987047376635
    Minimum: 1.500011856725905
Component: source 1
  function: [Uniform - 1.5:2.5]
  Events generated: 47443
  Generation rate: 0.496787084795525
  Generation characteristics
    Mean:2.002400135843377
    Standard Deviation:0.2884612072713134
    Maximum: 2.4999974335696606
    Minimum: 1.5000871437659953
Component: source 2
  function: [Uniform - 1.5:2.5]
  Events generated: 47501
  Generation rate: 0.4974018801118338
  Generation characteristics
    Mean:1.9999224495729122
    Standard Deviation:0.2883707892495052
    Maximum: 2.4999987047376635
    Minimum: 1.500011856725905
Component: network 1
  function: [default := Skewed - 0.25:0.75:0.8:-2.0]
  Events processed: 47443
  Utilization: 0.1678837017514001
  Throughput: 0.4994078333814277
  Queued events
    Mean:0.0
    Standard deviation:0.0
    Maximum: 0.0
    Minimum: 0.0
  Wait time
    Mean:0.0
    Standard deviation:0.0
    Maximum: 0.0
    Minimum: 0.0
  Process time
    Mean:0.33616915945539644
    Standard Deviation:0.07976390572039105
    Maximum: 0.7351911787918652
    Minimum: 0.2503836346877506
  Visit time
    Mean:0.33616915945539644
    Standard Deviation:0.07976390572039105
    Maximum: 0.7351911787918652
    Minimum: 0.2503836346877506
  Arrival characteristics
    Mean:2.002400135843377
    Standard Deviation:0.2884612072713134
    Maximum: 2.4999974335696606
    Minimum: 1.5000871437659953
Component: web server 2
  function: [default := Skewed - 1.5:2.5:0.8:-1.0]
  Events processed: 47501
  Utilization: 0.9079956082355366
  Throughput: 0.5000152711919857
  Queued events
    Mean:0.4871876937848846
    Standard deviation:0.5065196805599521
    Maximum: 3.0
    Minimum: 0.0
  Wait time
    Mean:0.20732616486229163
    Standard deviation:0.34070352899357326
    Maximum: 3.9430402605030395
    Minimum: 0.0
  Process time
    Mean:1.8159307188786245
    Standard Deviation:0.21753194030839118
    Maximum: 2.484186896304891
    Minimum: 1.502528144423195
  Visit time
    Mean:2.0232568837409266
    Standard Deviation:0.40407182471632763
    Maximum: 5.837244720307353
    Minimum: 1.5028648757943301
  Arrival characteristics
    Mean:1.9999261751317265
    Standard Deviation:0.3093986345546052
    Maximum: 2.872336729782546
    Minimum: 1.1122520999888366
Component: web server 1
  function: [default := Skewed - 1.5:2.5:0.8:-1.0]
  Events processed: 47442
  Utilization: 0.9077415222013033
  Throughput: 0.4994014558828612
  Queued events
    Mean:0.48524288174282254
    Standard deviation:0.5053254372757671
    Maximum: 2.0
    Minimum: 0.0
  Wait time
    Mean:0.20618979828110745
    Standard deviation:0.3387867984154244
    Maximum: 3.471011410027131
    Minimum: 0.0
  Process time
    Mean:1.8176602914331101
    Standard Deviation:0.2182476765197346
    Maximum: 2.485031346053802
    Minimum: 1.5025266495649703
  Visit time
    Mean:2.0238500897142195
    Standard Deviation:0.40249172125528226
    Maximum: 5.148945891951371
    Minimum: 1.5028010353635182
  Arrival characteristics
    Mean:2.002409282875349
    Standard Deviation:0.30971374431240234
    Maximum: 2.9242112672291114
    Minimum: 1.0558629192091757
```
