package ch.pschatzmann.stocks.strategy.optimization;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.pschatzmann.dates.DateRange;
import ch.pschatzmann.stocks.accounting.kpi.KPI;
import ch.pschatzmann.stocks.parameters.ParameterValue;
import ch.pschatzmann.stocks.parameters.StateComparator;
import ch.pschatzmann.stocks.strategy.optimization.annealing.Optimizer;
import ch.pschatzmann.stocks.strategy.optimization.annealing.State;
import ch.pschatzmann.stocks.strategy.selection.TopNSet;

/**
 * Optimizer using SimulatedAnnealing https://en.wikipedia.org/wiki/Simulated_annealing
 * 
 * @author pschatzmann
 *
 */

public class SimulatedAnnealingOptimizer implements IOptimizer, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Logger LOG = LoggerFactory.getLogger(SimulatedAnnealingOptimizer.class);
	private IFitness fitness;
	private long count;
	private KPI optimizationParameter;
	private DateRange optimizationPeriod;

	public SimulatedAnnealingOptimizer(IFitness fitness, KPI targetParameterName) {
		this(fitness, 100, targetParameterName);
	}

	public SimulatedAnnealingOptimizer(IFitness fitness, long count, KPI targetParameterName) {
		this.fitness = fitness;
		this.count = count;
		this.optimizationParameter = targetParameterName;
	}

	@Override
	public synchronized ch.pschatzmann.stocks.parameters.State optimize(IOptimizableTradingStrategy ts, DateRange optimizationPeriod) {
		TopNSet<ch.pschatzmann.stocks.parameters.State> bestValue = new TopNSet(1, new StateComparator(false, optimizationParameter));
		this.fitness.getTrader().getAccount().putStockData(ts.getStockData());

		bestValue.clear();
		// set default parameters as baseline
		bestValue.add(fitness.getFitness(ts, optimizationPeriod));
		
		// run otimization
		this.optimizationPeriod = optimizationPeriod;
		Optimizer o = new Optimizer(new StockState(ts, bestValue));
		StockState s = (StockState) o.search(count);
		bestValue.add(s.getParameters());

		// return best result that we might have ever found
		ch.pschatzmann.stocks.parameters.State result = bestValue.first();
		ts.getParameters().input().setParameters(result.getInput().getParameters());	

		LOG.info("==> " + result.result().getDouble(optimizationParameter)+ " "+ result);				
		return result;
	}

	class StockState implements State {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		TopNSet<ch.pschatzmann.stocks.parameters.State> bestValue;
		IOptimizableTradingStrategy strategy;
		ch.pschatzmann.stocks.parameters.State actualVaules;
		ch.pschatzmann.stocks.parameters.State priorValues;
		int errorCount;

		StockState(IOptimizableTradingStrategy strategy, TopNSet<ch.pschatzmann.stocks.pa