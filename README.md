# ICDE20-SRFN

Code release of "Swapping Repair for Misplaced Attribute Values" (ICDE 20).

Parameters
----------
The input and output of **SRFN** algorithm is:

Method

```
setParams(K, certainIndexes, t0RowIndexes);
```

Input:

```
int K;  // number of considered nearest neighbors 
int[] certainIndexes;  // reliable or certain attributes that should not be modified in repairing
int[] t0RowIndexes;  // tuples considered for repairing
```

Output

```
ArrayList<ErrorTuple> errorTuples // identified misplaced tuples with modifed results
```

Citation
----------
If you use this code for your research, please consider citing:

```
@inproceedings{DBLP:conf/icde/SunSW020,
  author    = {Yu Sun and
               Shaoxu Song and
               Chen Wang and
               Jianmin Wang},
  title     = {Swapping Repair for Misplaced Attribute Values},
  booktitle = {{ICDE}},
  pages     = {721--732},
  publisher = {{IEEE}},
  year      = {2020},
}
```