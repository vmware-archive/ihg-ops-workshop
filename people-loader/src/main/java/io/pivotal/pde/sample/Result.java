package io.pivotal.pde.sample;

import java.io.Serializable;

public class Result implements Serializable {
	private int count;
	private int total;
	
	public Result(){
		
	}
	
	public Result(int count, int total) {
		super();
		this.count = count;
		this.total = total;
	}
	public int getCount() {
		return count;
	}
	public int getTotal() {
		return total;
	}
	
	
}
