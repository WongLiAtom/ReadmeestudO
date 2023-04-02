package ch.pschatzmann.stocks.test;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.pschatzmann.stocks.Context;
import ch.pschatzmann.stocks.IStockData;
import ch.pschatzmann.stocks.IStockRecord;
import ch.pschatzmann.stocks.StockData.DateMatching;
import ch.pschatzmann.stocks.StockID;
//import ch.pschatzmann.stocks.cache.HazelcastCache;
import ch.pschatzmann.stocks.input.AlphaVantageReader;
import ch.pschatzmann.stocks.input.FinancialContentReader;
import ch.pschatzmann.stocks.input.IEXReader;
import ch.pschatzmann.stocks.input.InvestopiaReader;
import ch.pschatzmann.stocks.input.MacroTrendsReader;
import ch.pschatzmann.stocks.input.MarketArchiveHttpReader;
import ch.pschatzmann.stocks.input.QuandlBSEReader;
import ch.pschatzmann.stocks.input.QuandlSixReader;
import ch.pschatzmann.stocks.input.QuoteMediaReader;
import ch.pschatzmann.stocks.input.DefaultReader;
import ch.pschatzmann.stocks.input.TiingoReader;
import ch.pschatzmann.stocks.input.WallstreetJournalReader;
import ch.pschatzmann.stocks.input.YahooReader;

public class TestReaders {
	private static final Logger LOG = LoggerFactory.getLogger(TestReaders.class);

	@BeforeClass
	public static void setup() throws Exception {
		System.out.println("*** "+TestReaders.class.getSimpleName()+" ***");
	}

	
	@Test
	public void testYahooApple() {
		StockID apple = new StockID("AAPL", "NASDAQ");
		IStockData sd = Context.getStockData(apple, new YahooReader());
		LOG.info("{}", sd.getHistory().size());
		Assert.assertTrue(sd.getHistory().size()>100);
	}


	@Test
	public void testMarketArchiveApple() {
		StockID apple = new StockID("AAPL", "NASDAQ");
		IStockData sd = Context.getStockData(apple, new MarketArchiveHttpReader());
		LOG.info("{}", sd.getHistory().size());
		Assert.assertTrue(sd.getHistory().size()>100);
	}

	@Test
	public void testSix() {
		StockID apple = new StockID("US0378331005CHF", "SIX");
		IStockData sd = Context.getStockData(apple, new QuandlSixReader());
		LOG.info("{}", sd.getHistory().size());
		Assert.assertTrue(sd.getHistory().size()>100);
	}

	@Test
	public void testBSE() {
		StockID apple = new StockID("BOM531841", "BSE");
		IStockData sd = Context.getStockData(apple, new QuandlBSEReader());
		LOG.info("{}", sd.getHistory().size());
		Assert.assertTrue(sd.getHistory().size()>100);
	}

	@Test
	public void testReadExact() {
		StockID apple = new StockID("AAPL", "NASDAQ");
		IStockData sd = Context.getStockData(apple);
		IStockRecord sr = sd.getValue(Context.date("2017-02-17"), DateMatching.Exact);
		Assert.assertNotNull(sr);
		Assert.assertEquals(Context.date("2017-02-17"), Context.date(sr.getDate()));
	}

	@Test
	public void testReadNext() {
		StockID apple = new StockID("AAPL", "NASDAQ");
		IStockData sd = Context.getStockData(apple);
		IStockRecord sr = sd.getValue(Context.date("2017-02-19"), DateMatching.Next);
		Assert.assertEquals(Context.date("2017-02-21"), Context.date(sr.getDate()));
	}

	@Test
	public void testReadPrior() {
		StockID apple