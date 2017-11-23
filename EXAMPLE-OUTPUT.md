## Reference model
![System Model for Reference](https://raw.githubusercontent.com/BandedHawk/system-simulator/master/complex-system.png System Model for Simulation)

## Example output from a simulation
```
Statistics for events that occurred between 500.0 and 95500.0
Event information
  Events completed processing: 95027
  Throughput: 1.0002883485688838
  Ratio of processing in lifetime: 0.9019072023713387
  Event lifetime
    Mean:3.032834744758948
    Standard Deviation:0.45003929284698607
    Maximum time in system: 6.028273356923819
    Minimum time in system: 2.2654519753632485
  Event in execution
    Mean:2.735335499900136
    Standard Deviation:0.2520750558404329
    Maximum time processing: 4.097095694791079
    Minimum time processing: 2.2654519753646394
Component: database
  function: Skewed - 0.5:1.5:0.8:-3.0
  Events processed: 95029
  Utilization: 0.5816102824605603
  Throughput: 1.000315745051886
  Queued events
    Mean:0.2916929106305535
    Standard deviation:0.4547418856383724
    Maximum: 2.0
    Minimum: 0.0
  Wait time
    Mean:0.08638379351552564
    Standard deviation:0.1679898608486262
    Maximum: 1.216351238792413
    Minimum: 0.0
  Process time
    Mean:0.5814280847626211
    Standard Deviation:0.09658534678756221
    Maximum: 1.3906841953394178
    Minimum: 0.5001633096762816
  Visit time
    Mean:0.6678118782781473
    Standard Deviation:0.19405332146788315
    Maximum: 2.165735519345617
    Minimum: 0.5002518266410334
  Arrival characteristics
    Mean:0.9996954195340712
    Standard Deviation:0.579576271099223
    Maximum: 2.866908715752288
    Minimum: 4.8942965804599226E-6
Component: network 2
  function: Skewed - 0.25:0.75:0.8:-2.0
  Events processed: 47529
  Utilization: 0.16828892986759553
  Throughput: 0.5003148983522873
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
    Mean:0.3363669049639202
    Standard Deviation:0.08027448457353359
    Maximum: 0.7432940221979152
    Minimum: 0.2503263014386903
  Visit time
    Mean:0.3363669049639202
    Standard Deviation:0.08027448457353359
    Maximum: 0.7432940221979152
    Minimum: 0.2503263014386903
  Arrival characteristics
    Mean:1.9987716834508915
    Standard Deviation:0.28846675374425523
    Maximum: 2.4999933236977085
    Minimum: 1.5000845960057632
Component: source 1
  function: Uniform - 1.5:1.0
  Events generated: 47500
  Generation rate: 0.49738767248938653
  Generation characteristics
    Mean:2.000009060127785
    Standard Deviation:0.28941787836623806
    Maximum: 2.499984596091963
    Minimum: 1.5000268436851911
Component: source 2
  function: Uniform - 1.5:1.0
  Events generated: 47529
  Generation rate: 0.49769560961974296
  Generation characteristics
    Mean:1.9987716834508915
    Standard Deviation:0.28846675374425523
    Maximum: 2.4999933236977085
    Minimum: 1.5000845960057632
Component: network 1
  function: Skewed - 0.25:0.75:0.8:-2.0
  Events processed: 47500
  Utilization: 0.1680465893490428
  Throughput: 0.5000045477851811
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
    Mean:0.3360904391470344
    Standard Deviation:0.07986156887580259
    Maximum: 0.7223344180019922
    Minimum: 0.25032805404043756
  Visit time
    Mean:0.3360904391470344
    Standard Deviation:0.07986156887580259
    Maximum: 0.7223344180019922
    Minimum: 0.25032805404043756
  Arrival characteristics
    Mean:2.000009060127785
    Standard Deviation:0.28941787836623806
    Maximum: 2.499984596091963
    Minimum: 1.5000268436851911
Component: web server 2
  function: Skewed - 1.5:2.5:0.8:-1.0
  Events processed: 47528
  Utilization: 0.909245635852429
  Throughput: 0.5003050812054474
  Queued events
    Mean:0.4926441192931968
    Standard deviation:0.5064655434980359
    Maximum: 2.0
    Minimum: 0.0
  Wait time
    Mean:0.21101041214979277
    Standard deviation:0.34113126054735265
    Maximum: 2.8988093737243616
    Minimum: 0.0
  Process time
    Mean:1.8173768144138884
    Standard Deviation:0.21858750978505537
    Maximum: 2.484400257395464
    Minimum: 1.5025668179951026
  Visit time
    Mean:2.0283872265636753
    Standard Deviation:0.40412064892942184
    Maximum: 4.849931536386066
    Minimum: 1.5025668179951026
  Arrival characteristics
    Mean:1.9987614555228508
    Standard Deviation:0.3096412905831853
    Maximum: 2.9096184731533867
    Minimum: 1.0750999381598376
Component: web server 1
  function: Skewed - 1.5:2.5:0.8:-1.0
  Events processed: 47499
  Utilization: 0.9089877431376266
  Throughput: 0.4999952885679924
  Queued events
    Mean:0.48998899889989056
    Standard deviation:0.5053963206554042
    Maximum: 2.0
    Minimum: 0.0
  Wait time
    Mean:0.21122669099256422
    Standard deviation:0.34246435863189384
    Maximum: 3.0645156835598755
    Minimum: 0.0
  Process time
    Mean:1.8179800914918078
    Standard Deviation:0.21929232591329403
    Maximum: 2.4906086566043086
    Minimum: 1.5026214236277156
  Visit time
    Mean:2.029206782484369
    Standard Deviation:0.4066409122719644
    Maximum: 4.705273791070795
    Minimum: 1.5034692328772508
  Arrival characteristics
    Mean:2.0000087617479836
    Standard Deviation:0.3101329153658553
    Maximum: 2.8887798777068383
    Minimum: 1.1306507724075345
```
