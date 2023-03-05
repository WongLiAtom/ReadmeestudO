package ch.pschatzmann.stocks.utils;

import java.io.Serializable;
import java.util.Random;

import ch.pschatzmann.stocks.Context;

public class Range<N extends Number> implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Random rnd = new Random();
	private N min;
	private N max;

	public Range() {	
	}
	
	public Range(N min, N max) {
		this.min = min;
		this.max = max;
	}

	public N getMin() {
		return min;
	}

	public void setMin(N min) {
		this.min = min;
	}

	public N getMax() {
		return max;
	}

	public void setMax(N max) {
		this.max = max;
	}

	public Double random() {
		Random r 