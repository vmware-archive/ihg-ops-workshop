# Shrinking

Shrinking a cluster must be done carefully.  

**When a data node goes down or is stopped , the default behavior is not to restore redundancy.** This means that you can easily lose data if you shrink too fast.

The procedure for shrinking a cluster is:

- stop a data node
- use gfsh to rebalance 
- repeat for each data node you wish to remove

# Growing

When growing a cluster, it's important to understand that redundancy recovery 
happens automatically, but beyond that, data will not automatically spread 
out onto new data nodes.  The procedure for grow a cluster is:

- start all new data nodes (one at a time or in parallel is OK)
- use the rebalance command to spread the data out

# Lab Instructions

In this lab we will shrink the cluster down to a single node, then grow it back 
to 3.  We will be using pulse to observe the behavior.

## Shrink the Cluster

1. On any member run gfsh.  Connect to the cluster and stop datanode2

	```
	gfsh>connect --locator=192.168.1.101[10000]
	...
	gfsh>stop server --name=datanode3
	...
	```
	
2. Now check the redundancy.  Observe that there are buckets without redundancy.
   _If you stop another server at this point you would lose data._ Use the 
   rebalance command to restore redundancy.
   
   ```
   gfsh>show metrics --region=person --categories=partition
   ...
   gfsh>rebalance
   ...
   gfsh>show metrics --region=person --categories=partition
   ```

3. stop datanode2
	Use Pulse or the `describe region` command to verify that the region 
	still contains the correct number of entries. However, note that this time 
	you will not be able to restore redundancy by rebalancing.  *Why not?*

## Grow the Cluster 

### On gem1102

Start datanode2.  

```
$GEMFIRE/bin/gfsh start server --name=datanode2 --dir=datanode2 --locators=192.168.1.101[10000]
```

Use Pulse to verify that datanode2 now contains some data. Also, check
 `NumBucketsWithoutRedundancy` and verify that redundancy has been recovered.
 
By default, redundancy is recovered automatically when a new server is 
added to the cluster _but this can be overridden at the region level._ 

### On gem1103

1. Start datanode3. 

	Use Pulse to check the data distribution. You will see different behavior
	this time. Unless the cluster needs to restore missing redundant copies,
	*data does not automatically spread out to new servers.*

2. Use the rebalance command to spread the data out.