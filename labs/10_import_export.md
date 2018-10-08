# Export and Import

Export and import are considerably easier to use.  In this lab we'll export 
the person region, then destroy it, then re-create and reload it.

## Lab Instructions

1. First take an export.  Note that the `--member` argument is specifying 
	where to put the export, not which data to back up.  
	
	```
	gfsh>export data --region=person --file=/backup/person.gfd --member=gem1101-server
	```
	
	You may want to go and look on `192.168.1.101` for the file.
	
2. Now drop the region and the disk store.  Confirm it is gone, then 
	recreate it. To make things easier, you can recreate the region 
	without a disk store and without persistence.
	
	```
	gfsh>destroy region --name=person
	...
	gfsh>destroy disk-store --name=person-disk-store
	...
	gfsh>list regions 
	...
	gfsh>create region --name=person --type=PARTITION_REDUNDANT
	```
	
3. Finally, import the data into the newly created region and get the entry 
	with key=1 to verify it is correct.
	
	```
	gfsh>import data --file=/backup/person.gfd --member=gem1101-server --region=person
	...
	gfsh>get --region=person --key=1 --key-class=java.lang.Integer
	```
	


