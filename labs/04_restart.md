# Restart a Cluster

Most configuration changes can be done without restarting the cluster 
at all however, there are certain times when you may need to do a rolling 
restart.  The most common reason is when performing a GemFire upgrade. If 
done correctly, this can be done while the cluster is running.

_Restarting a cluster has to be done in a very specific way in order to avoid
losing data. The procedure is as follows._ 

_Repeat For Each Data Node:_

1. For each partitioned region, run `gfsh>show metrics --region=x --categories=partition` and verify that the metric `NumBucketsWithoutRedundancy` 
is 0.  __If NumBucketsWithoutRedundancy is not zero do not proceed!__
2. stop the data node with the `stop server --name=x` gfsh command
3. Make whatever config changes need to be made then start the server again.

_Do Once at the End_

1. execute the `rebalance` gfsh command.

## Lab Instructions

Follow the procedure to restart your cluster without losing data.


