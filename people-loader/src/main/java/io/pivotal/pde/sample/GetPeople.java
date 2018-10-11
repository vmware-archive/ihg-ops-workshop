package io.pivotal.pde.sample;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientCacheFactory;
import org.apache.geode.cache.client.ClientRegionShortcut;
import org.apache.geode.pdx.PdxSerializer;
import org.apache.geode.pdx.ReflectionBasedAutoSerializer;

public class GetPeople {


	private static int parseIntArg(String in, String message){
    	int result = 0;

    	try{
    		result = Integer.parseInt(in);
    	} catch(NumberFormatException nfx){
    		System.err.println(message);
    		System.exit(1);
    	}
		return result;
	}

	private static void printUsage(){
		System.out.println("usage: GetPeople --locator=host[port] --region=RegionName --count=2000 --sleep=100 --threads=10");
		System.out.println("       only --locator and --count are required");
		System.out.println("       --sleep is in milliseconds");
		System.out.println("       --threads may not exceed 64");
	}

	private static String REGION_ARG="--region=";
	private static String LOCATOR_ARG="--locator=";
	private static String COUNT_ARG = "--count=";
	private static String SLEEP_ARG = "--sleep=";
	private static String THREADS_ARG = "--threads=";
	private static String USERNAME_ARG = "--username=";
	private static String PASSWORD_ARG = "--password=";
	private static Pattern LOCATOR_PATTERN= Pattern.compile("(\\S+)\\[(\\d{1,5})\\]");

	private static String regionName = "Person";
	private static String locatorHost = "";
	private static int locatorPort = 0;
	private static int count = 0;
	private static int sleep = 0;
	private static int threads = 1;
	private static Region<Integer,Person> personRegion;
	
	// intentionally package scope
	static String username = "";
	static String password = "";


	public static void main( String[] args )
    {
		if (args.length == 0){
			printUsage();
			System.exit(1);
		}

    	for(String arg:args){
    		if (arg.startsWith(LOCATOR_ARG)){
    			String val = arg.substring(LOCATOR_ARG.length());
    			Matcher m = LOCATOR_PATTERN.matcher(val);
    			if (!m.matches()){
    				System.out.println("argument \"" + val + "\" does not match the locator pattern \"host[port]\"");
    				System.exit(1);
    			} else {
    				locatorHost = m.group(1);
    				locatorPort = parseIntArg(m.group(2), "locator port must be a number");
    			}
    		} else if (arg.startsWith(REGION_ARG)) {
    			regionName = arg.substring(REGION_ARG.length());
    		} else if (arg.startsWith(COUNT_ARG)) {
    			String val = arg.substring(COUNT_ARG.length());
    			count = parseIntArg(val, "count argument must be a number");
    		} else if (arg.startsWith(SLEEP_ARG)){
    			String val = arg.substring(SLEEP_ARG.length());
    			sleep = parseIntArg(val, "sleep argument must be a number");
    		} else if (arg.startsWith(THREADS_ARG)){
    			String val = arg.substring(THREADS_ARG.length());
    			threads = parseIntArg(val, "threads argument must be a number");
    		} else if (arg.startsWith(USERNAME_ARG)){
    			username = arg.substring(USERNAME_ARG.length());
    		} else if (arg.startsWith(PASSWORD_ARG)){
    			password = arg.substring(PASSWORD_ARG.length());
    		} else {
    			System.out.println("unrecognized argument: " + arg);
    			System.exit(1);
    		}
    	}

    	if (locatorHost.length() == 0){
    		System.out.println("--locator argument is required");
    		System.exit(1);
    	}

    	if (count == 0){
    		System.out.println("--count argument is required");
    		System.exit(1);
    	}

    	if (count <= 0 || threads <= 0){
    		System.out.println("count and threads arguments must be strictly positive");
    		System.exit(1);
    	}

    	if (sleep < 0){
    		System.out.println("sleep argument may not be negative");
    		System.exit(1);
    	}

    	PdxSerializer serializer = new ReflectionBasedAutoSerializer("io.pivotal.pde.sample.*");
    	Properties cacheProps = new Properties();
    	cacheProps.setProperty("security-client-auth-init", "io.pivotal.pde.sample.StaticAuthInit");
    	ClientCache cache = new ClientCacheFactory(cacheProps).setPdxSerializer(serializer).addPoolLocator(locatorHost, locatorPort).create();
//    	ClientCache cache = new ClientCacheFactory().addPoolLocator(locatorHost, locatorPort).create();
		personRegion = cache.<Integer,Person>createClientRegionFactory(ClientRegionShortcut.PROXY).create(regionName);

		WorkerThread []workers = new WorkerThread[threads];
		for(int i=0;i<threads; ++i){
			workers[i] = new WorkerThread(i);
			workers[i].start();
		}

		System.out.println("Press enter to stop");
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		try{
			reader.readLine();
		} catch(IOException x){
			//
		}
		
		for(int i=0;i<threads; ++i){
			try {
				workers[i].shutdown();
			} catch(InterruptedException x){
				System.out.println("interrupted while waiting for worker thread to stop ");
			}
		}

		personRegion.close();
		cache.close();

    }

	private static class WorkerThread extends Thread {
		private int slice;
		private AtomicBoolean running;
		private Random rand;

		public WorkerThread(int s){
			super();
			this.setDaemon(false);
			this.slice = s;
			this.running = new AtomicBoolean(true);
			this.rand = new Random();
		}

		public void shutdown() throws InterruptedException {
			this.running.set(false);
			this.join();
		}
		
		@Override
		public void run(){
			while(running.get()){
				int key = rand.nextInt(count);
				Person p = personRegion.get(key);
				if (p!=null) System.out.println(p);
				
				try {
					Thread.sleep(sleep);
				} catch(InterruptedException x){
					
				}
			}
		}
	}
	
}
