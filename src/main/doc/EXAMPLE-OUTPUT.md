## Reference model
![alt text](https://raw.githubusercontent.com/BandedHawk/system-simulator/master/src/main/doc/images/complex-system.png "System Model for Simulation")
##Command line
``` 
java -jar system-simulator-1.0-SNAPSHOT.jar -cp commons-math3-3.6.1.jar:commons-cli-1.4.jar -g 100000 -s 500 -e 95500 -f balancer.example.txt
``` 
## Example output from a simulation
``` R
Statistics for events that occurred between 500.0 and 95500.0
Event information
  Events completed processing: 94979
  Throughput: 0.9997953410273326
  Ratio of processing in lifetime: 0.9032769471649128
  Event lifetime
    Mean:3.0287315641631465
    Standard Deviation:0.4453376267104064
    Median:2.95505804970162
    Maximum time in system: 6.287263360936777
    Minimum time in system: 2.2722765441139927
  Event in execution
    Mean:2.7357834010592983
    Standard Deviation:0.2527832600980176
    Median:2.694395079872458
    Maximum time processing: 4.351946478182848
    Minimum time processing: 2.272276544109408
Component: database
  function: [default := Skewed - 0.5:1.5:0.8:-3.0]
  Events processed: 94982
  Utilization: 0.5816866870115821
  Throughput: 0.9998232935762267
  Queued events
    Mean:0.2923594021967434
    Standard deviation:0.4549587747631549
    Median:0.0
    Maximum: 2.0
    Minimum: 0.0
  Wait time
    Mean:0.08770134744817903
    Standard deviation:0.16932104998670036
    Median:0.0
    Maximum: 1.2887391694021062
    Minimum: 0.0
  Process time
    Mean:0.5817900243856047
    Standard Deviation:0.09710057918824969
    Median:0.5471394497963047
    Maximum: 1.4036559206870152
    Minimum: 0.5002015061654674
  Visit time
    Mean:0.6694913718337833
    Standard Deviation:0.19455333424387222
    Median:0.5831614137350698
    Maximum: 1.9783919601686648
    Minimum: 0.5002015061654674
  Arrival characteristics
    Mean:1.000182317803858
    Standard Deviation:0.5818372516181879
    Median:1.0004842284433835
    Maximum: 2.7521986709616613
    Minimum: 3.8475678593385965E-5
Component: network 2
  function: [default := Skewed - 0.25:0.75:0.8:-2.0]
  Events processed: 47485
  Utilization: 0.16818020405363537
  Throughput: 0.49986223845582867
  Queued events
    Mean:0.0
    Standard deviation:0.0
    Median:0.0
    Maximum: 0.0
    Minimum: 0.0
  Wait time
    Mean:0.0
    Standard deviation:0.0
    Median:0.0
    Maximum: 0.0
    Minimum: 0.0
  Process time
    Mean:0.3364575180858601
    Standard Deviation:0.08064582897823652
    Median:0.3092634405911667
    Maximum: 0.7358652763359714
    Minimum: 0.2505083263677079
  Visit time
    Mean:0.3364575180858601
    Standard Deviation:0.08064582897823652
    Median:0.3092634405911667
    Maximum: 0.7358652763359714
    Minimum: 0.2505083263677079
  Arrival characteristics
    Mean:2.000593273482101
    Standard Deviation:0.2884925880544451
    Median:2.0016376443527406
    Maximum: 2.4999984701062203
    Minimum: 1.5000358111719834
Component: source 1
  function: [Uniform - 1.5:2.5]
  Events generated: 47496
  Generation rate: 0.4973519873616136
  Generation characteristics
    Mean:2.0001628204740842
    Standard Deviation:0.2883628633468391
    Median:2.000844913927722
    Maximum: 2.499949937555357
    Minimum: 1.5000052739560488
Component: source 2
  function: [Uniform - 1.5:2.5]
  Events generated: 47486
  Generation rate: 0.4972367636214853
  Generation characteristics
    Mean:2.000592916627615
    Standard Deviation:0.28848956079572724
    Median:2.0016181250466616
    Maximum: 2.4999984701062203
    Minimum: 1.5000358111719834
Component: network 1
  function: [default := Skewed - 0.25:0.75:0.8:-2.0]
  Events processed: 47496
  Utilization: 0.16828683441795222
  Throughput: 0.4999692713671791
  Queued events
    Mean:0.0
    Standard deviation:0.0
    Median:0.0
    Maximum: 0.0
    Minimum: 0.0
  Wait time
    Mean:0.0
    Standard deviation:0.0
    Median:0.0
    Maximum: 0.0
    Minimum: 0.0
  Process time
    Mean:0.3365958834915302
    Standard Deviation:0.07987747854798638
    Median:0.3099020206864225
    Maximum: 0.7388336049189093
    Minimum: 0.2505981831927784
  Visit time
    Mean:0.3365958834915302
    Standard Deviation:0.07987747854798638
    Median:0.3099020206864225
    Maximum: 0.7388336049189093
    Minimum: 0.2505981831927784
  Arrival characteristics
    Mean:2.0001628204740842
    Standard Deviation:0.2883628633468391
    Median:2.000844913927722
    Maximum: 2.499949937555357
    Minimum: 1.5000052739560488
Component: web server 2
  function: [default := Skewed - 1.5:2.5:0.8:-1.0]
  Events processed: 47485
  Utilization: 0.908086331150549
  Throughput: 0.49985156714716067
  Queued events
    Mean:0.4890134080448274
    Standard deviation:0.505537159136978
    Median:0.0
    Maximum: 2.0
    Minimum: 0.0
  Wait time
    Mean:0.20366143098223205
    Standard deviation:0.3346023223001282
    Median:0.0
    Maximum: 3.485141505501815
    Minimum: 0.0
  Process time
    Mean:1.816712497508781
    Standard Deviation:0.2185719627059375
    Median:1.768045624707156
    Maximum: 2.485109673187253
    Minimum: 1.502112194450092
  Visit time
    Mean:2.0203739284910176
    Standard Deviation:0.3995144474244025
    Median:1.93489722612685
    Maximum: 5.298913332590018
    Minimum: 1.502112194450092
  Arrival characteristics
    Mean:2.0005892641629637
    Standard Deviation:0.3102732517640913
    Median:2.001706788687443
    Maximum: 2.889524055928632
    Minimum: 1.1117710806283867
Component: web server 1
  function: [default := Skewed - 1.5:2.5:0.8:-1.0]
  Events processed: 47495
  Utilization: 0.9090481890951257
  Throughput: 0.4999640611745867
  Queued events
    Mean:0.49095927674213924
    Standard deviation:0.5054144994392091
    Median:0.0
    Maximum: 2.0
    Minimum: 0.0
  Wait time
    Mean:0.20682200547273707
    Standard deviation:0.33414535183237837
    Median:0.0
    Maximum: 2.815072668643552
    Minimum: 0.0
  Process time
    Mean:1.8182243808924134
    Standard Deviation:0.21909212615158793
    Median:1.7704558694558727
    Maximum: 2.4836513218488108
    Minimum: 1.5026519332895987
  Visit time
    Mean:2.0250463863651516
    Standard Deviation:0.39924194400508606
    Median:1.940124999571708
    Maximum: 4.659421515585564
    Minimum: 1.5026519332895987
  Arrival characteristics
    Mean:2.0001601184949993
    Standard Deviation:0.30933465271317234
    Median:2.002244491588499
    Maximum: 2.8741337098326767
    Minimum: 1.1095585202638176
```
