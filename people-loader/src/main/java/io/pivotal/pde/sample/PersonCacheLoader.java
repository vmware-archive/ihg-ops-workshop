package io.pivotal.pde.sample;

import java.util.Random;

import org.apache.geode.cache.CacheLoader;
import org.apache.geode.cache.CacheLoaderException;
import org.apache.geode.cache.LoaderHelper;

public class PersonCacheLoader implements CacheLoader<Object,Person> {

	private Random rand;
	
	public PersonCacheLoader(){
		rand = new Random();
	}
	
	@Override
	public void close() {
	}

	@Override
	public Person load(LoaderHelper <Object,Person> helper) throws CacheLoaderException {
		if (rand.nextFloat() < 0.2f){
			try {
				Thread.sleep(20000);
			} catch(InterruptedException x){
				//
			}
		}
		
		Person result = Person.fakePerson();
		result.setId(helper.getKey());
		return result;
	}
	
}
