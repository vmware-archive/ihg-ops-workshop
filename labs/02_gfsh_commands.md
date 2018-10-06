# gfsh commands every administrator should know

Now that we have some data, take a few minutes to experiment with gfsh.

### From gem1111 (or any server in your environment) 

Run gfsh and connect to the cluster.

```
$GEMFIRE/bin/gfsh
gfsh> connect --locator=192.168.1.101[10000]
```

Now try the following gfsh commands. Feel free to experinment.


```
list members
list regions
list disk-stores
describe member --name=datanode1
describe region --name=person
describe config --member=datanode1
show metrics --member=datanode1
show metrics --region=person
show log --member=datanode2
query --query="select * from /person where address.state='TX'"
```


__Note:__ to successfuly run a query you must either

- have the java class for the entry type on the server class path OR
- the client program must use PDX serialization AND the server must have 
	`pdx-read-serialzed` set to `true`
	
If neither of these two things has been done you will see a `ClassNotFoundException` while attempting to run a query.
	
