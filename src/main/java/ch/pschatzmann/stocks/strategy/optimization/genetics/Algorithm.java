package ch.pschatzmann.stocks.strategy.optimization.genetics;

import java.io.Serializable;

import ch.pschatzmann.stocks.strategy.optimization.GeneticOptimizer;
import ch.pschatzmann.stocks.utils.Range;

public class Algorithm implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/* GA parameters */
	private static final double uniformRate = 0.5;
	private static final double mutationRate = 0.015;
	private static final int tournamentSize = 5;
	private static final boolean elitism = true;
	private Range[] geneTemplate;
	private String names[];
	private GeneticOptimizer fitnessCalc;

	/* Public methods */
	public Algorithm(String names[], Range geneTemplate[], GeneticOptimizer optimizer) {
		this.geneTemplate = geneTemplate;
		this.fitnessCalc = optimizer;
		this.names = names