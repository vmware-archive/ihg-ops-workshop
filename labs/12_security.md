# Enabling GemFire Security

In the workshop we will discuss:

- the plugin security model 
- the permission model

Also see [the documentation](http://gemfire.docs.pivotal.io/latest/geode/managing/security/chapter_overview.html) here.

In this lab we will use the example security manager that ships with 
GemFire.  Although it is in use by some customers, it is really just 
an example.

## Lab Instructions

You can work from `gem1111` in this lab.

1. First, review the file `/runtime/gem_cluster_1/security.json`.  This 
file defines the roles, the users and passwords. Note that this is just 
the format used by one particular security provider.  Other security providers 
could read this information from another source such as LDAP or a database 
or GemFire itself (see [this project](https://github.com/Pivotal-Data-Engineering/gemfire-dynamic-security) on github.

2. Edit `/runtime/gem_cluster_1/gfsecurity.properties` and add the following 
settings:

	```
	security-manager=org.apache.geode.examples.security.ExampleSecurityManager
	security-username=peer
	security-password=secret
	``` 

	_Notes_
	
	- Even GemFire cluster members must authenticate.  The `security-username` 
		and `security-password` settings control the credentials cluster members 
		use to authenticate to the cluster.  Note that the password matches the 
		one in `security.json`
	- We would normally need to put a jar containing the security manager 
	  on the class path but since we are using the example security manager that 
	  comes with GemFire, it is alread there.
  
3. The `security.json` file does have to be on the class path.  We will put 
   it in `/runtime/gem_cluster_1` on every machine so open up `cluster.json` 
   and add a `classpath` setting in the global section as shown below.
   
   ```json
   {
    "global-properties":{
        "gemfire": "/runtime/gemfire",
        "java-home" : "/runtime/java",
        "locators" : "ec2-18-234-83-1.compute-1.amazonaws.com[10000],ec2-54-88-51-193.compute-1.amazonaws.com[10000]",
        "cluster-home" : "/runtime/gem_cluster_1",
        "classpath" : "/runtime/gem_cluster_1",
        "security-properties-file" : "/runtime/gem_cluster_1/gfsecurity.properties",
	...
   ```
   
4. Now you will need to stop the cluster, distribute the files and then 
	start it again.
	
	```
	gfsh>shutdown --include-locators=true 
	
	# exit gfsh - distribute the files
	pscp -h ~/cluster1_hosts.txt security.json /runtime/gem_cluster_1
	pscp -h ~/cluster1_hosts.txt gfsecurity.properties /runtime/gem_cluster_1
	pscp -h ~/cluster1_hosts.txt cluster.json /runtime/gem_cluster_1
	
	#start the cluster
	python gf.py start 
	```
	
5. Verify the results.  

	- note that you must supply the "admin" password from `security.json` 
		to log in to Pulse
	- gfsh will prompt you for a password when you connect 

6. Configure people-loader as a secure client and load some data into the 
	`person` region.
	
	First you will need to copy `/runtime/gem_cluster_1/gfsecurity.properties`
	to the `/runtime/people-loader` directory.  Now modify that file by 
	removing the `security-*` properties, leaving only `ssl-*` properties.
	This first step is necessary to enable the people-loader to use an 
	SSL connection.
	
	Now run `peopleloader.py` with additional `--username` and `--password` 
	arguments. Just for verification, try to run without the additional 
	arguments as well.
	
	```
	python peopleloader.py --locator=192.168.1.101[10000] --region=person --count=100000 --username=app --password=password
	```
	
_Congratulations!  You have secured your GemFire cluster._