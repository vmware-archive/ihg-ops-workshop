package io.pivotal.pde.sample;

import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientCacheFactory;
import org.apache.geode.cache.client.ClientRegionShortcut;
import org.apache.geode.cache.execute.Execution;
import org.apache.geode.cache.execute.FunctionService;
import org.apache.geode.cache.execute.ResultCollector;
import org.apache.geode.pdx.PdxSerializer;
import org.apache.geode.pdx.ReflectionBasedAutoSerializer;

public class AvgAge {


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
	private static Pattern LOCATOR_PATTERN= Pattern.compile("(\\S+)\\[(\\d{1,5})\\]");

	private static String regionName = "Person";
	private static String locatorHost = "";
	private static int locatorPort = 0;
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
    		} else {
    			System.out.println("unrecognized argument: " + arg);
    			System.exit(1);
    		}
    	}

    	if (locatorHost.length() == 0){
    		System.out.println("--locator argument is required");
    		System.exit(1);
    	}


    	PdxSerializer serializer = new ReflectionBasedAutoSerializer("io.pivotal.pde.sample.*");
    	Properties cacheProps = new Properties();
    	cacheProps.setProperty("security-client-auth-init", "io.pivotal.pde.sample.StaticAuthInit");
    	ClientCache cache = new ClientCacheFactory(cacheProps).setPdxSerializer(serializer).addPoolLocator(locatorHost, locatorPort).create();
//    	ClientCache cache = new ClientCacheFactory().addPoolLocator(locatorHost, locatorPort).create();
		personRegion = cache.<Integer,Person>createClientRegionFactory(ClientRegionShortcut.PROXY).create(regionName);

		Execution exec = FunctionService.onRegion(personRegion).withCollector(new AvgResultCollector());
		ResultCollector<Result,Integer> rc = exec.execute("AVG_FUNCTION");
		
		int avg = rc.getResult();

		System.out.println("Average age is " + avg);
    }

	
}
