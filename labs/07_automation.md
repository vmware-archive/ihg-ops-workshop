# Automation

Managing a large GemFire cluster in the way we have done so far is tedious 
and error prone.  Automation is essential to the smooth GemFire operations!

One solution is available here: [gemfire-manager](https://github.com/Pivotal-Data-Engineering/gemfire-manager). Use this or Ansible or something else but 
whatever you do, **please automate your gemfire operations**.

## Lab Instructions

In this lab we're going to switch over to the automated cluster management
system.  It has already been installed so we just need to start using it.

1. Stop your cluster by connecting to gfsh (from any member) running 	the shutdown command with the `--include-locators` option.  

	```
	gfsh>connect --locator=192.168.1.101[10000]
	gfsh>shutdown --include-locators=true
	```
	
2. SSH to gem1111 . Start a new cluster using the following procedure.
 	
 	```
 	cd /runtime/gem_cluster_1
 	python gf.py start 
 	```
 	
 	Configuration of pdx should already be done.  Use gfsh to check:
 	
 	```
 	gfsh>list disk-stores
 	...
 	gfsh>describe config --member=gem1102-server
 	...
 	```
 	
3. No go ahead and re-create the person region using gfsh. _Be sure to put 
 	the disk store in the `/data` directory._

 	```
 	gfsh>create disk-store --name=person-disk-store --dir=/data/person --auto-compact=true --allow-force-compaction=true --max-oplog-size=10
 	...
 	gfsh>create region --name=person --type=PARTITION_REDUNDANT_PERSISTENT --disk-store=person-disk-store
 	```
 	
4. Finally, get out of gfsh and load some data using people-loader.

	```
	cd /runtime/people-loader
	python peopleloader.py --locator=192.168.1.101[10000] --region=person --count=100000
	```
	
5. Now lets get familiar with gf.py script.  Try the following.

	```
	cd /runtime/gem_cluster_1
	python gf.py stop               #stops all data node (not locators)
	python gf.py start              #starts whatever is not started 
	python gf.py bounce             #performs a rolling restart 
	```
	
	Also inspect the file `cluster.json`.  This file contains all of the 
	settings for the cluster.