# Stopping and Starting a GemFire Cluster

## Stopping a GemFire Cluster

__Do not stop a GemFire cluster by stopping one member at a time__.  This
is especially important if you have persistent data.  Even if you don't have
persistent regions you may still have persistent data in the form of pdx
metadata.  

_Also note that only persistent regions will survive across a full shutdown and
restart._

The correct way to shut down a GemFire cluster is to connect to the cluster
with gfsh and issue a _shutdown_ command.  There is one variant that will shut
down locators and datanodes and another that shuts down only data nodes.  

```
# shut down all of the data nodes
gfsh> shutdown

# shut down data nodes and locators
gfsh> shutdown --include-locators=true
```


## Starting a GemFire Cluster

The correct procedure for starting a GemFire cluster is:

1. start all of the locators in parallel
2. after the locators have started, start all of the data nodes in parallel

_Starting the locators or data nodes sequentially may result in a data node
or locator hanging forever during startup_.  This phenomenon will not be
observed the first time a cluster is started.


## Lab Instructions

### From gem1111

1. Connect to gfsh and run `shutdown`.  You can leave the locator running.
2. Verify using Pulse that all data nodes have left the distributed system.

Now we will intentionally start the data nodes up sequentially to see what
happens.

### On gem1101

Start datanode1 back up.

```
$GEMFIRE/bin/gfsh start server --name=datanode1 --dir=datanode1 --locators=192.168.1.101[10000]
```

You may see something like this:

```
Region /PdxTypes has potentially stale data. It is waiting for another member to recover the latest data.
My persistent id:

  DiskStore ID: 7e017bd6-afc3-4172-8047-12fce8b3a25a
  Name: datanode1
  Location: /192.168.1.101:/home/ec2-user/datanode1/pdx

Members with potentially new data:
[
  DiskStore ID: 4509014c-8e44-44e6-924f-ab23f367d481
  Name: datanode2
  Location: /192.168.1.102:/home/ec2-user/datanode2/pdx
]
```

__Don't panic.  This is normal. Let the gfsh command continue to run.__ 

### In a new window, SSH to gem1102

Start data node 2

```
$GEMFIRE/bin/gfsh start server --name=datanode2 --dir=datanode2 --locators=192.168.1.101[10000]
```

__This one may also hang.  Let it run and start the the 3rd data node in another 
window__.

### On gem1103

```
$GEMFIRE/bin/gfsh start server --name=datanode3 --dir=datanode3 --locators=192.168.1.101[10000]
```

This should break the log jam!

Use Pulse or gfsh to verify that you have 3 data nodes and that there is 
data in the `person` region.