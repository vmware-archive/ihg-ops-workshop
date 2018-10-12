package io.pivotal.pde.sample;

import java.util.Set;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.execute.Function;
import org.apache.geode.cache.execute.FunctionContext;
import org.apache.geode.cache.execute.RegionFunctionContext;
import org.apache.geode.cache.partition.PartitionRegionHelper;

public class AvgFunction implements Function {

	@Override
	public boolean hasResult() {
		return true;
	}

	@Override
	public String getId() {
		return "AVG_FUNCTION";
	}

	@Override
	public boolean optimizeForWrite() {
		return true;
	}

	@Override
	public boolean isHA() {
		return true;
	}

	@Override
	public void execute(FunctionContext context) {
		RegionFunctionContext rctx = (RegionFunctionContext) context;
		Region<Integer,Person> myData = PartitionRegionHelper.getLocalDataForContext(rctx);
		
		Set<Integer> keys = myData.keySet();
		
		int ttl = 0; 
		int count = 0;
		
		for(Integer key: keys){
			Person p = myData.get(key);
			ttl += p.getAge();
			count +=1;
		}
		
		
		rctx.getResultSender().lastResult(new Result(count, ttl));
		
	}
	
}
