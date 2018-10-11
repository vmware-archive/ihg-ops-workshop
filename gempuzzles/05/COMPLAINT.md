My client program sometimes couldn't retrieve data. I got exceptions
like this. The total load was maybe 10 gets/ second.  I was using 20 threads

Exception in thread "Thread-18" org.apache.geode.cache.client.ServerConnectivityException: Pool unexpected socket timed out on client connection=Pooled Connection to 192.168.43.139:10200: Connection[192.168.43.139:10200]@2027471855 attempt=2). Server unreachable: could not connect after 2 attempts
	at org.apache.geode.cache.client.internal.OpExecutorImpl.handleException(OpExecutorImpl.java:786)
	at org.apache.geode.cache.client.internal.OpExecutorImpl.handleException(OpExecutorImpl.java:611)
	at org.apache.geode.cache.client.internal.OpExecutorImpl.execute(OpExecutorImpl.java:175)
	at org.apache.geode.cache.client.internal.OpExecutorImpl.execute(OpExecutorImpl.java:116)
	at org.apache.geode.cache.client.internal.PoolImpl.execute(PoolImpl.java:774)
	at org.apache.geode.cache.client.internal.GetOp.execute(GetOp.java:91)
	at org.apache.geode.cache.client.internal.ServerRegionProxy.get(ServerRegionProxy.java:113)
	at org.apache.geode.internal.cache.LocalRegion.findObjectInSystem(LocalRegion.java:2771)
	at org.apache.geode.internal.cache.LocalRegion.nonTxnFindObject(LocalRegion.java:1477)
	at org.apache.geode.internal.cache.LocalRegionDataView.findObject(LocalRegionDataView.java:176)
	at org.apache.geode.internal.cache.LocalRegion.get(LocalRegion.java:1366)
	at org.apache.geode.internal.cache.LocalRegion.get(LocalRegion.java:1300)
	at org.apache.geode.internal.cache.LocalRegion.get(LocalRegion.java:1285)
	at org.apache.geode.internal.cache.AbstractRegion.get(AbstractRegion.java:320)
	at io.pivotal.pde.sample.GetPeople$WorkerThread.run(GetPeople.java:178)
Person [lastName=Koelpin, firstName=Richmond, phone=927.346.0125 x9726, address=Address [street=##### Elise Bypass, city=Port Edythefurt, state=SD, zip=17569], gender=F, id=5922, age=80]
