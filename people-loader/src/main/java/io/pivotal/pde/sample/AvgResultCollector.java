package io.pivotal.pde.sample;

import java.util.concurrent.TimeUnit;

import org.apache.geode.cache.execute.FunctionException;
import org.apache.geode.cache.execute.ResultCollector;
import org.apache.geode.distributed.DistributedMember;

public class AvgResultCollector implements ResultCollector <Result, Integer>{

	private int ttl = 0;
	private int count = 0;
	
	@Override
	public Integer getResult() throws FunctionException {
		return ttl/count;
	}

	@Override
	public Integer getResult(long timeout, TimeUnit unit) throws FunctionException, InterruptedException {
		return ttl/count;
	}

	@Override
	public void addResult(DistributedMember memberID, Result resultOfSingleExecution) {
		ttl += resultOfSingleExecution.getTotal();
		count += resultOfSingleExecution.getCount();
	}

	@Override
	public void endResults() {		
	}

	@Override
	public void clearResults() {
		ttl = 0;
		count = 0;
	}
	

}
