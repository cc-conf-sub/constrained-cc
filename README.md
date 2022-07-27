Data Format:
* Data files are stored in Data/[name of data set]/graph.txt
  * All data sets are either publicly available, provided here, or the code used to generate them is provided
  * We provide data set samples to show the format for the various experiments 
* Delimiter is hard-coded in driver files
* For 0/1 Correlation Clustering: 
  * The first line of the file must contain the total number of nodes (anything that follows it on the line will be ignored)
  * Rest of file lists positive edges as [node1] [node2]

To Compile: javac *.java
To Run: java [DriverName] [data set folder name]
* additional heap space may be needed for some experiments; increase the max heap size with the -Xmx flag

Drivers
-------

Hard coded parameters:
* delimiter for data set
* number of Pivot rounds, number of attributes used, etc. 

KLocalSearch.java
* Input: positive edge list
* Method: Initialize random K clustering and perform local search
* Sample data set: cor_amazon

PTAS.java
* Input: positive edge list
* Method: MaxAgree and MinDisagree PTAS
* Additional command line argument: sample_size
* Sample data set: cor_gym

RunAtLeastKCorrelation.java
* Input: positive edge list
* Method: K Pivot and ConstrainedVote (with early stopping for exactly K clusters)
* Sample data set: cor_amazon 

RunKCorrelation.java
* Input: positive edge list
* Method: "neighborhood oracle" for Pivot algorithms; also runs ConstrainedVote and Blend
* Sample data sets: cor_gym, cor_amazon

Code Files
----------

DNode.java
* Implementations of DeterministicNode

Helper.java
* Helper functions for reading data sets

Pair.java
* Custom data structure used for heap implementation

PKwik.java
* Pivot algorithm implementations

Plots
-----

k_cc_plots.ipynb
-- Jupyter Notebook for data plots
