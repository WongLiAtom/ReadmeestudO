package ch.pschatzmann.stocks.test;

import java.nio.charset.Charset;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.pschatzmann.stocks.Context;
import ch.pschatzmann.stocks.IStockData;
import ch.pschatzmann.stocks.StockData;
import ch.pschatzmann.stocks.StockID;
import ch.pschatzmann.stocks.accounting.Account;
import ch.pschatzmann.stocks.accounting.BasicAccount;
import ch.pschatzmann.stocks.accounting.kpi.KPI;
import ch.pschatzmann.stocks.accounting.kpi.KPIValue;
import ch.pschatzmann.stocks.execution.ITrader;
import ch.pschatzmann.stocks.execution.NoDelay;
import ch.pschatzmann.stocks.execution.OneDayDelay;
import ch.pschatzmann.stocks.execution.PaperTrader;
import ch.pschatzmann.stocks.execution.StrategyExecutor;
import ch.pschatzmann.stocks.execution.fees.PerTradeFees;
import ch.pschatzmann.stocks.input.MarketArchiveHttpReader;
import ch.pschatzmann.stocks.strategy.CommonTradingStrategy;
import ch.pschatzmann.stocks.strategy.GlobalExtremaStrategy;
import ch.pschatzmann.stocks.strategy.RSI2Strategy;
import ch.pschatzmann.stocks.strategy.SciptStrategy;
import ch.pschatzmann.stocks.strategy.allocation.DistributedAllocationStrategy;
import ch.pschatzmann.stocks.strategy.allocation.IAllocationStrategy;
import ch.pschatzmann.stocks.strategy.optimization.IOptimizableTradingStrategy;
import ch.pschatzmann.stocks.utils.FileUtils;

/**
 * Setup of account and execute automatic trades. At the end we evaluate the trades
 * 
 * @author pschatzmann
 *
 */

public class TestAutomaticTrading {
	private static final Logger LOG = LoggerFactory.getLogger(TestAutomaticTrading.class);
	private Double delta = 0.001;

	
	@BeforeClass 
	public static void setup() throws Exception{
		System.out.println("*** "+TestAutomaticTrading.class.getSimpleName()+" ***");
		Context.setDefaultReader(new MarketArchiveHttpReader());
	}	
	
	@Test
	public void testFullCycleWithOneStock() throws Exception {
		BasicAccount account = new BasicAccount("Simulation","USD", 10000.00, Context.date("2015-01-01"),new PerTradeFees(10.0));	
		Account  ai = new Account(account);
		
		ITrader trader = new PaperTrader(ai, new NoDelay());
		IAllocationStrategy allocationStrategy = new DistributedAllocationStrategy(trader);
		
		StrategyExecutor executor = new StrategyExecutor(trader, allocationStrategy);
		StockData sd = new StockData(new StockID("AAPL", "NASDAQ"), new MarketArchiveHttpReader());

		executor.addStrategy(new RSI2Strategy(sd));
		executor.run(ai.getDateRange());
		
		LOG.info("Realized gain: "+ai.getRealizedGain());		
		LOG.info("Transactions: "+ai.getTransactions().stream().sorted().collect(Collectors.toList()));
		LOG.info("Cashflow: "+ai.getCashFlowHistory());
		LOG.info("Cash History: "+ai.getCashHistory())