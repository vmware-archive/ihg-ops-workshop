# Configuring a WAN Gateway

In this lab, we'll set a one-way WAN connection between two clusters.

## Lab Instructions

### on cluster 2 (gem2111)

1. First we will need to set up SSL for the second cluster as well. 
	Create a `gfsecurity.properties` file in `/runtime/gem_cluster_2` with the 
	following contents. 
	
	```
	ssl-enabled-components=cluster,jmx,locator,server,gateway
	ssl-keystore=/runtime/gem_cluster_2/trusted.keystore
	ssl-keystore-password=password
	ssl-truststore=/runtime/gem_cluster_2/trusted.keystore
	ssl-truststore-password=password
	ssl-truststore-type=JKS
	ssl-keystore-type=JKS
	ssl-ciphers=any
	ssl-protocols=any
	ssl-default-alias=self
	```
	
	Also copy the keystore from the other cluster.
	
	```
	scp 192.168.1.101:/runtime/gem_cluster_1/trusted.keystore .
	```

	Add `"security-properties-file" : "/runtime/gem_cluster_2/gfsecurity.properties"` to the global section of cluster.json.
	
	Use pscp to push out all the files.
	
	```
	pscp -h ~/cluster2_hosts.txt gfsecurity.properties /runtime/gem_cluster_2
	pscp -h ~/cluster2_hosts.txt cluster.json /runtime/gem_cluster_2
	pscp -h ~/cluster2_hosts.txt trusted.keystore /runtime/gem_cluster_2
	```


2. Bring the second cluster up. 
	
	```
	# on gem2111
	cd /runtime/gem_cluster_2
	python gf.py start 
	```
	
3. Create a `person` region.  Just to show it can be done, make the person 
	region REPLICATE on this cluster.  __Note: the locator for cluster 2 
	is `192.168.2.101` and NOT `192.168.1.101`.__
	
	```
	gfsh>connect --locator=192.168.2.101[10000]
	...
	gfsh>create region --name=person --type=REPLICATE
	```
	
4. Now create a gateway receiver on cluster 2:

	```
	gfsh>create gateway-receiver --start-port=10901 --end-port=10901
	```
	
	That completes the required setup for cluster 2. 
	
### on cluster 1 (gem1111)

1. Shut down the cluster using the `shutdown --include-locators=true` gfsh 
	command.
2. Edit `cluster.json` add 
	`"remote-locators" : "192.168.2.101[10000],192.168.2.102[10000]"' in the 
	global section at the top and then use pscp to push it out. Then start the 	cluster again.
	
	```
	# edit cluster.json
	pscp -h ~/cluster1_hosts.txt cluster.json /runtime/gem_cluster_1
	python gf.py start
	```
	
3. Connect to the running cluster with gfsh.  Create a gateway sender called 
	`person-sender` and a disk store to hold any overflow from the
	`person-sender` event queue.
	
	```
	gfsh>create disk-store --name=person-sender-disk-store --dir=/data/person-sender --auto-compact=true --allow-force-compaction=true --max-oplog-size=10
	gfsh>create gateway-sender --id=person-sender --remote-distributed-system-id=2 --parallel=true --disk-store-name=person-sender-disk-store
	```
	
4. Now we just need to connect the `person` region to the person sender. _It 
	is possible to add a sender to a region that already exists but this will 
	not propagate any data that is already in the region._  
	
	We will drop and re-create the person region instead.
	
	```
	gfsh>destroy region --name=person
	gfsh>create region --name=person --type=PARTITION_REDUNDANT --gateway-sender-id=person-sender
	```
	
	That concludes the setup for one region in one direction.  Note that 
	a cluster has only one receiver but multiple senders.   To set the gateway 
	up in the opposite direction you would just need to create a receiver 
	in cluster 1 and a sender in cluster 2.
	
### Verification (Both Clusters)

1. On cluster 1 (`gem1111`) load some data using the people-loader project.

	```
	cd /runtime/people-loader
	python peopleloader.py --locator=192.168.1.101[10000] --region=person --count=1000 --username=app --password=password
	```
	
2. On both clusters, retrieve an entry from the person region.

	```
	gfsh>get --region=person --key=1 --key-class=java.lang.Integer 
	```

3. On both clusters, run the `list gateways` gfsh command.
4. On both clusters, run `describe region --name=person` and compare the entry count.
