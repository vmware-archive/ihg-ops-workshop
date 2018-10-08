# Backup and Restore Lab

In this lab we will do a full and an incremental backup, then stop the 
cluster, remove all of the data and restore from the backup.

The instructions assume you already have a `person` region with data in it.

## Lab Instructions

1. Start by writing down the value of the entry with key=1 and the entry with key=1001 so we can verfify that the backup worked.  An example is below.

	```
	gfsh> Cluster-1 gfsh>get --region=person --key=1 --key-class=java.lang.Integer
	```
	
2. Now run the first backup.

	```
	gfsh>backup disk-store --dir=/backup 
	```
	
3. Now, log in to one of the data nodes and look at the contents of the backup 
	directory.  It will look something like this:
	
	```
	/backup/2016-12-06-20-56-47/     #each backup in a timestamped directory
	             |-192_168_1_101_server101_25117_v2_10981  #each member has its own folder
	             |  |- config       #gemfire config files
	             |  |- user         #additional user defined files
	             |  |- restore.sh   #script to copy disk stores to original location
	             |  |- diskstores
	             |  |  |- pdx-disk-store_c0449b536ab24d3e-bece38b2c50b0d40  #contains op logs for one disk store
	             |  |  |  |- dir0    #each disk-dir in the disk-store has a directory
	             |  |  |  |  |- *.drf/crf/krf/if      
	             |  |  |  |  |- . . . .
	             |  |  |- person-disk-store_9d1cd3d205834971-9b628944a7ef5a98
	             |  |  |- . . . .
	             |  |- README.txt 
	          |- 192_168_1_101_locator101_24889_locator_ec_v0_8542  #locators have disk stores too!
	          |- . . . . 
	```
	
	Take note of the name of the top-level directory that contains the backup. 
	You will need it later.  In this example the directory is
	 `/backup/2016-12-06-20-56-47`.
	 
4. We need to add some data before taking an incremental backup.  Update the first 1000 entries 
	in the person region then make note of the new value of the entry with key = 1.
	
	```bash
	cd /runtime/people-loader
	python peopleloader.py --locator=192.168.1.101[10000] --region=person --count=1000
	$GEMFIRE/bin/gfsh -e "connect --locator=192.168.1.101[10000]" -e "get --region=person --key=1 --key-class=java.lang.Integer"
	```
	
5. Now we'll take an incremental backup to capture the most recent changes.  You will need to provide the directory of a previous backup to use as a baseline for the incremental.

	```
	gfsh>backup disk-store --dir=/backup --baseline-dir=/backup/2016-12-06-20-56-47
	```
	
	
6. Now that that's done, shut everything down and remove all the disk stores in the `/data`  directory.  Also remove the working directories from every 
server. _Do not remove the `/runtime/gem_cluster_1` directory.

	```
	$GEMFIRE/bin/gfsh -e "connect --locator=192.168.1.101[10000]" -e "shutdown --include-locators=true"
	pssh -h ~/cluster1_hosts.txt  rm -rf /data/*
	ssh 192.168.1.101 rm -rf /runtime/gem_cluster_1/gem1101-{locator,server}
	ssh 192.168.1.102 rm -rf /runtime/gem_cluster_1/gem1102-{locator,server}
	ssh 192.168.1.103 rm -rf /runtime/gem_cluster_1/gem1103-server
	```
	
	Log on to one of the cluster members and verify that there is nothing in 
	the `/data` directory and that the working directories in 
	`/runtime/gem_cluster_1` have been removed.
	
7. Now we can run the restore scripts.  Make sure to use the name of the 
	incremental backup in the commands below. Unfortunately, you need to 
	run a restore script for each locator or server.  You can use pssh 
	to automate the task:
	
	```
	pssh -h ~/cluster1_hosts.txt /backup/MY-INCREMENTAL-BACKUP/*locator*/restore.sh   # expecting an error on 192.168.1.103 because it has no locator
	pssh -h ~/cluster1_hosts.txt /backup/MY-INCREMENTAL-BACKUP/*server*/restore.sh   
	```
		
8. Now start the cluster and verify that the region definitions and data 
	have come back.  Use gfsh to retrieve the person with key=1 and verify 
	it matches what you previously recorded.
	
	```
	cd /runtime/gem_cluster_1
	python gf.py start 
	
	# log in to gfsh and check things out
	gfsh>get --region=person --key=1 --key-class=java.lang.Integer
	
	#this should match what it was before
	```
	
	

	
	