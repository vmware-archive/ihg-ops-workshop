# SSL 

There will be an in class discussion of SSL configuration for GemFire.
See also the [gemfire documentation](http://gemfire.docs.pivotal.io/latest/geode/managing/security/implementing_ssl.html).  

For this lab we will implement SSL on all GemFire channels except for Pulse. 

As with other settings, there are multiple ways to set the SSL settings.  We will
 place all of the SSL related properties into a `gfsecurity.properties` and then point to it  using `--security-properties-file` setting on the locator and data node start commands.
 
## Lab Instructions

For your convenience, `pssh` has been installed on all servers.  Two hosts 
files, `cluster1_hosts.txt` and `cluster2_hosts.txt` have been provided as 
well.  This will allow you to copy files to all hosts in cluster 1 or 
cluster 2 as needed.

You can perform the whole lab from `gem1111`.

1. stop the cluster using `python gf.py gfsh shutdown --include-locators=true`
2. Now create a slef-signed keystore which will be used as both key store and trust store.

	```ssh
	cd /runtime/gem_cluster_1
	
	$JAVA_HOME/bin/keytool -genkey -alias self -dname "CN=trusted" -validity 3650 -keypass password -keystore ./trusted.keystore -storepass password -storetype JKS 
	```
	
3. Now create a gfsecurity.properties file in the `/runtime/gem_cluster_1` 
	directory with the following contents.  Note that the paths will be 
	interpreted relative to the locator or datanode working directory.
	
 	```
	ssl-enabled-components=cluster,jmx,locator,server
	ssl-keystore=/runtime/gem_cluster_1/trusted.keystore
	ssl-keystore-password=password
	ssl-truststore=/runtime/gem_cluster_1/trusted.keystore
	ssl-truststore-password=password
	ssl-truststore-type=JKS
	ssl-keystore-type=JKS
	ssl-ciphers=any
	ssl-protocols=any
	ssl-default-alias=self
	```

4. Edit cluster.json, add a "security-properties-file" setting in the global 
section.

 	```json
 	{
    "global-properties":{
        "gemfire": "/runtime/gemfire",
        "java-home" : "/runtime/java",
        "locators" : "ec2-54-163-207-134.compute-1.amazonaws.com[10000],ec2-34-229-177-18.compute-1.amazonaws.com[10000]",
        "cluster-home" : "/runtime/gem_cluster_1",
        "security-properties-file" : "/runtime/gem_cluster_1/gfsecurity.properties",
        "distributed-system-id": "1"
    },
    ...
	```

5. Copy the keystore filem the properties file and `cluster.json` to the 
	`/runtime/gem_cluster_1` directory on all servers.
	
	```
	pscp -h ~/cluster1_hosts.txt gfsecurity.properties /runtime/gem_cluster_1 
	pscp -h ~/cluster1_hosts.txt trusted.keystore  /runtime/gem_cluster_1
	pscp -h ~/cluster1_hosts.txt cluster.json  /runtime/gem_cluster_1
	```
	
6. Start the cluster: `python gf.py start`
7. Now connect with gfsh and use a `describe config` command to verify 
	that SSL settings are in place. 
	
	Note that you are only able to connect gfsh to the cluster because 
	of the `gfsecurity.properties' file in the current directory.  If 
	you move it you will not be able to connect to the cluster because 
	it is now secured with SSL.