# Cluster Setup

In this lab we will continue where we left off.  We'll finish setting 
up a new cluster and load some data.  

## Initial Cluster Configuration 

On `gem1101` where the locator is running.  Unzip `gemfire-starter.zip` and 
have a look at `cluster/cluster.xml`.  It will look like this:

```xml
<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<cache xmlns="http://geode.apache.org/schema/cache" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" copy-on-read="false" is-server="false" lock-lease="120" lock-timeout="60" search-timeout="300" version="1.0" xsi:schemaLocation="http://geode.apache.org/schema/cache http://geode.apache.org/schema/cache/cache-1.0.xsd">
<disk-store allow-force-compaction="true" max-oplog-size="10" name="pdx-disk-store">
    <disk-dirs>
      <disk-dir>pdx</disk-dir>
    </disk-dirs>
  </disk-store>
<pdx disk-store-name="pdx-disk-store" persistent="true" read-serialized="true"/>
</cache>
```

This file specifies that we want `read-serialized` pdx behavior and 
it creates a disk store for pdx metadata.  This is a typical starter file.

We can use a zip file like this to perform one time setup of a new cluster.

__After the initial setup, additional changes to the cluster configuration 
should be made through gfsh.__

Follow these steps to import the cluster configuration. This needs to be done 
before any data nodes are started.

### On gem1101 

1. start gfsh: `$GEMFIRE/bin/gfsh`
2. connect to the locator: 

	`gfsh>connect --locator=192.168.1.101[10000]`
	
3. import `gemfire-starter.zip`

	```
	gfsh>import cluster-configuration --zip-file-name=gemfire-starter.zip
	```

	_From this point on, any data nodes that are connected to this locator
	will receive the initial configuration from `gemfire-starter.zip`.  Note 
	that the zip file is no longer needed.  The configuration has been imported 
	and now resides in a disk store under the locator working directory._
	
4. Start a data node on this machine.

	```
	gfsh>start server --name=datanode1 --dir=datanode1 --locators=192.168.1.101[10000]
	```
	
5. Verify that the new data node has received the new cluster configuration. 
	
	```
	gfsh>describe config --member=datanode1
	```
	
	Look for evidence that `pdx-read-serialized` is `true`.  You can also 
	view the logs files of any process in the cluster using gfsh.  Give 
	it a try: 
	
	`gfsh> show log --member=datanode1 --lines=500`
	
	
### On gem1102 and gem1103

Start a data node on gem1102 and another on gem1103.  The start command 
would be the same as for the data node on gem1101 except that the `--name` 
argument must be different since each member of the distributed system must 
have a unique name. 

You do not need to be in gfsh or connected to a locator, you can simply 
issue the `gfsh start` command.  An example is below.

```bash
$GEMFIRE/bin/gfsh start server --dir=datanode2 --name=datanode2 --locators=192.168.1.101[10000]
```

Verify in Pulse that you can see all 3 data nodes and 1 locator.

## Loading Data

### On gem 1111

This server has a GemFire client program called `people-loader`.  We 
will use it to load some data but first we need to create a region 
to hold the data.


Connect to the cluster using gfsh:

```
$GEMFIRE/bin/gfsh
gfsh>connect --locator=192.168.1.101[10000]
```

We are going to create a persistent region, which means that the data in the 
region will survive across a cluster restart.  For that we need to first 
create a disk store and then create the region using the disk store.

```
gfsh>create disk-store --name=person-disk-store --dir=/data/person --auto-compact=true --allow-force-compaction=true --max-oplog-size=10
...
gfsh>create region --name=person --type=PARTITION_REDUNDANT_PERSISTENT --disk-store=person-disk-store
```

You can now exit gfsh.  Use the following command to load some data.

```bash
cd /runtime/people-loader
python peopleloader.py --locator=192.168.1.101[10000] --region=person --count=100000 --threads=4
```

Use Pulse to verify that there are 100,000 entries in the person region.  

In Pulse, go to the "Data Browser" tab and execute the following query:

`select * from /person where address.zip='30306'`

If you don't get any results, try a different zip.  

Note that you can double-click on the row to launch an "object explorer" view.

